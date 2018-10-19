package com.github.kulmam92.digdag.plugin.mssql;

import io.digdag.client.config.Config;
import io.digdag.spi.Operator;
import io.digdag.spi.OperatorContext;
import io.digdag.spi.OperatorFactory;
import io.digdag.spi.SecretProvider;
import io.digdag.spi.TemplateEngine;
import io.digdag.standards.operator.jdbc.AbstractJdbcJobOperator;

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MssqlOperatorFactory
        implements OperatorFactory
{
    private static final String OPERATOR_TYPE = "mssql";

    private final TemplateEngine templateEngine;
    private final Config systemConfig;

    private static Logger logger = LoggerFactory.getLogger(MssqlOperatorFactory.class);

    public MssqlOperatorFactory(Config systemConfig,TemplateEngine templateEngine)
    {
        this.templateEngine = templateEngine;
        this.systemConfig = systemConfig;
    }

    @Override
    public String getType()
    {
        return OPERATOR_TYPE;
    }

    @Override
    public Operator newOperator(OperatorContext context)
    {
        return new MssqlOperator(systemConfig,context,templateEngine);
    }

    static class MssqlOperator
            extends AbstractJdbcJobOperator<MssqlConnectionConfig>
    {
        MssqlOperator(Config systemConfig,OperatorContext context, TemplateEngine templateEngine)
        {
            super(systemConfig,context, templateEngine);
        }

        @Override
        protected MssqlConnectionConfig configure(SecretProvider secrets, Config params)
        {
            return MssqlConnectionConfig.configure(secrets, params);
        }

        @Override
        protected MssqlConnection connect(MssqlConnectionConfig connectionConfig)
        {
            return MssqlConnection.open(connectionConfig);
        }

        @Override
        protected String type()
        {
            return OPERATOR_TYPE;
        }

        @Override
        protected SecretProvider getSecretsForConnectionConfig()
        {
            return context.getSecrets().getSecrets(type());
        }
    }
}
