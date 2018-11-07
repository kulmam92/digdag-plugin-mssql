package com.github.kulmam92.digdag.plugin.mssql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Properties;
import static java.util.Locale.ENGLISH;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;

import io.digdag.client.config.Config;
import io.digdag.spi.SecretProvider;
import io.digdag.standards.operator.jdbc.DatabaseException;
import io.digdag.util.DurationParam;
import org.immutables.value.Value;

import io.digdag.standards.operator.jdbc.AbstractJdbcConnectionConfig;
// logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value.Immutable
public abstract class MssqlConnectionConfig
        extends AbstractJdbcConnectionConfig
{
    // Not defined in AbstractJdbcConnectionConfig.class
    // user, database are not optional
    public abstract Optional<String> instanceName();
    public abstract Optional<String> schema();
    public abstract boolean integratedSecurity();
    public abstract boolean multiSubnetFailover();
    public abstract Optional<String> applicationIntent();
    public abstract Optional<String> failoverPartner();
    // To do options implementations - only accept string not map
    // https://github.com/embulk/embulk-output-jdbc/blob/07b6dfea0c5296c124328d2d17bdc48240f7d159/embulk-output-jdbc/src/main/java/org/embulk/output/jdbc/ToStringMap.java
    public abstract Optional<String> options();

    // logging
    private static Logger logger = LoggerFactory.getLogger(MssqlConnectionConfig.class);
    
    @VisibleForTesting
    public static MssqlConnectionConfig configure(SecretProvider secrets, Config params)
    {
        return ImmutableMssqlConnectionConfig.builder()
            .host(secrets.getSecretOptional("host").or(() -> params.get("host", String.class)))
            .port(secrets.getSecretOptional("port").transform(Integer::parseInt).or(() -> params.get("port", int.class, 1433)))
            .user(secrets.getSecretOptional("user").or(() -> params.get("user", String.class, "N/A")))
            .password(secrets.getSecretOptional("password"))
            .database(secrets.getSecretOptional("database").or(() -> params.get("database", String.class)))
            .ssl(secrets.getSecretOptional("ssl").transform(Boolean::parseBoolean).or(() -> params.get("ssl", boolean.class, false)))
            .connectTimeout(secrets.getSecretOptional("connect_timeout").transform(DurationParam::parse).or(() ->
                params.get("connect_timeout", DurationParam.class, DurationParam.of(Duration.ofSeconds(30)))))
            .socketTimeout(secrets.getSecretOptional("socket_timeout").transform(DurationParam::parse).or(() ->
                params.get("socket_timeout", DurationParam.class, DurationParam.of(Duration.ofSeconds(1800)))))
            .schema(secrets.getSecretOptional("schema").or(params.getOptional("schema", String.class)))
            .instanceName(secrets.getSecretOptional("instanceName").or(params.getOptional("instanceName", String.class)))
            .integratedSecurity(secrets.getSecretOptional("integratedSecurity").transform(Boolean::parseBoolean).or(() -> params.get("integratedSecurity", boolean.class, false)))
            // AG multiSubnetFailover, applicationIntent
            .multiSubnetFailover(secrets.getSecretOptional("multiSubnetFailover").transform(Boolean::parseBoolean).or(() -> params.get("multiSubnetFailover", boolean.class, false)))
            .applicationIntent(secrets.getSecretOptional("applicationIntent").or(params.getOptional("applicationIntent", String.class)))
             // Mirroring failoverPartner
            .failoverPartner(secrets.getSecretOptional("failoverPartner").or(params.getOptional("failoverPartner", String.class)))              
            // options
            .options(secrets.getSecretOptional("options").or(params.getOptional("options", String.class)))
            .build();
    }

    @Override
    public String jdbcDriverName()
    {
        // doesn't support JtdsDriver
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public String jdbcProtocolName()
    {
        return "sqlserver";
    }

    @Override
    public Properties buildProperties()
    {
        // mssql jdbc properties - https://docs.microsoft.com/en-us/sql/connect/jdbc/setting-the-connection-properties?view=sql-server-2017
        Properties props = new Properties();

        if (integratedSecurity()) {
            props.setProperty("integratedSecurity", "true");
        } else {
            props.setProperty("user", user());
            if (password().isPresent()) {
                props.setProperty("password", password().get());
            }
            //props.setProperty("integratedSecurity", "true");
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
        if (multiSubnetFailover()) {
            props.setProperty("multiSubnetFailover", "true");
        }            
        if (applicationIntent().isPresent()) {
            props.setProperty("applicationIntent", applicationIntent().get());
        }  
        if (failoverPartner().isPresent()) {
            props.setProperty("failoverPartner", failoverPartner().get());
        }  
        if (options().isPresent()) {
            String options = options().get();
            String[] arrOptions = options.split(";");
            for (int i=0; i < arrOptions.length; i++) {
                String[] arrOption = arrOptions[i].split("=");
                props.setProperty(arrOption[0], arrOption[1]);
            }
        }
        props.setProperty("applicationName", "digdag");

        return props;
    }

    @Override
    public String toString()
    {
        // Omit credentials in toString output
        return url();
    }

    @Override
    public String url()
    {
        // https://github.com/embulk/embulk-output-jdbc/blob/07b6dfea0c5296c124328d2d17bdc48240f7d159/embulk-output-sqlserver/src/main/java/org/embulk/output/SQLServerOutputPlugin.java
        // jdbc:sqlserver://localhost:1433;databaseName=master;user=sa;password=your_password
        String fullHostname;
        if (instanceName().isPresent()) {
            fullHostname = host()+"//"+instanceName();
        } else {
            fullHostname = host();
        }            
        return String.format(ENGLISH, "jdbc:%s://%s:%d;databaseName=%s", jdbcProtocolName(), fullHostname, port(), database());
    }

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
