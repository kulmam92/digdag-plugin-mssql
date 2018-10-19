package com.github.kulmam92.digdag.plugin.mssql;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import io.digdag.client.config.Config;
import io.digdag.spi.SecretProvider;
import io.digdag.standards.operator.jdbc.DatabaseException;
import io.digdag.util.DurationParam;
import org.immutables.value.Value;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Properties;
import io.digdag.standards.operator.jdbc.AbstractJdbcConnectionConfig;

@Value.Immutable
public abstract class MssqlConnectionConfig
        extends AbstractJdbcConnectionConfig
{
//    public abstract Optional<String> schema();

    @VisibleForTesting
    public static MssqlConnectionConfig configure(SecretProvider secrets, Config params)
    {
        return ImmutableMssqlConnectionConfig.builder()
                .host(secrets.getSecretOptional("host").or(() -> params.get("host", String.class)))
                .port(secrets.getSecretOptional("port").transform(Integer::parseInt).or(() -> params.get("port", int.class, 3306)))
                .user(secrets.getSecretOptional("user").or(() -> params.get("user", String.class)))
                .password(secrets.getSecretOptional("password"))
                .database(secrets.getSecretOptional("database").or(() -> params.get("database", String.class)))
                .ssl(secrets.getSecretOptional("ssl").transform(Boolean::parseBoolean).or(() -> params.get("ssl", boolean.class, false)))
                .connectTimeout(secrets.getSecretOptional("connect_timeout").transform(DurationParam::parse).or(() ->
                        params.get("connect_timeout", DurationParam.class, DurationParam.of(Duration.ofSeconds(30)))))
                .socketTimeout(secrets.getSecretOptional("socket_timeout").transform(DurationParam::parse).or(() ->
                        params.get("socket_timeout", DurationParam.class, DurationParam.of(Duration.ofSeconds(1800)))))
//                .schema(secrets.getSecretOptional("schema").or(params.getOptional("schema", String.class)))
//                // Not defined in AbstractJdbcConnectionConfig.class
//                // Instance name
//                .instanceName(secrets.getSecretOptional("instanceName").or(params.getOptional("instanceName", String.class)))
//                // integratedSecurity
//                .integratedSecurity(secrets.getSecretOptional("integratedSecurity").or(params.getOptional("integratedSecurity", String.class)))
//                // AG multiSubnetFailover, applicationIntent
//                .multiSubnetFailover(secrets.getSecretOptional("multiSubnetFailover").transform(Boolean::parseBoolean).or(params.getOptional("multiSubnetFailover", boolean.class)))
//                .applicationIntent(secrets.getSecretOptional("applicationIntent").or(params.getOptional("applicationIntent", String.class)))
//                // Mirroring failoverPartner
//                .failoverPartner(secrets.getSecretOptional("failoverPartner").or(params.getOptional("failoverPartner", String.class)))
                .build();
    }

    @Override
    public String jdbcDriverName()
    {
//        return "com.mysql.jdbc.Driver";
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public String jdbcProtocolName()
    {
        return "mssql";
    }

    @Override
    public Properties buildProperties()
    {
        // mssql jdbc properties - https://docs.microsoft.com/en-us/sql/connect/jdbc/setting-the-connection-properties?view=sql-server-2017
        Properties props = new Properties();
  
        props.setProperty("user", user());
        if (password().isPresent()) {
            props.setProperty("password", password().get());
        }

        // convert 0000-00-00 to NULL to avoid this exceptoin:
        //   java.sql.SQLException: Value '0000-00-00' can not be represented as java.sql.Date
        props.setProperty("zeroDateTimeBehavior", "convertToNull");

        props.setProperty("loginTimeout", String.valueOf(connectTimeout().getDuration().getSeconds()));
        props.setProperty("socketTimeout", String.valueOf(socketTimeout().getDuration().getSeconds()));
        // https://docs.microsoft.com/en-us/sql/connect/jdbc/connecting-with-ssl-encryption?view=sql-server-2017
        if (ssl()) {
            props.setProperty("encrypt", "true");
            props.setProperty("trustServerCertificate", "true");
        } else {
            props.setProperty("encrypt", "false");
        }
//        Not defined in AbstractJdbcConnectionConfig.class        
//        if (user().isPresent()) {
//            props.setProperty("user", user().get());
//        } 
//        if (instanceName().isPresent()) {
//            props.setProperty("instanceName", instanceName().get());
//        }                        
//        if (integratedSecurity().isPresent()) {
//            props.setProperty("integratedSecurity", integratedSecurity().get());
//        }         
//        if (multiSubnetFailover().isPresent() && multiSubnetFailover()) {
//            props.setProperty("multiSubnetFailover", "true");
//        }         
//        if (applicationIntent().isPresent()) {
//            props.setProperty("applicationIntent", applicationIntent().get());
//        }  
//        if (failoverPartner().isPresent()) {
//            props.setProperty("failoverPartner", failoverPartner().get());
//        }                         
        props.setProperty("applicationName", "digdag");

        return props;
    }

    @Override
    public String toString()
    {
        // Omit credentials in toString output
        return url();
    }

    //
    // Class.forName("com.mysql.jdbc.Driver") does not work as I expected.
    // That's why I override this method.
    //
    @Override
    public Connection openConnection()
    {
        Driver driver;
        try {
            driver = new com.microsoft.sqlserver.jdbc.SQLServerDriver();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }

        try {
            return DriverManager.getConnection(url(), buildProperties());
        }
        catch (SQLException ex) {
            throw new DatabaseException("Failed to connect to the database", ex);
        }
    }

}
