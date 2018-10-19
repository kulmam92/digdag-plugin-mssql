package com.github.kulmam92.digdag.plugin.mssql;

import io.digdag.client.config.Config;
import io.digdag.client.config.ConfigFactory;

import static io.digdag.client.DigdagClient.objectMapper;

public class ConfigUtils
{
    private ConfigUtils()
    { }

    public static final ConfigFactory configFactory = new ConfigFactory(objectMapper());

    public static Config newConfig()
    {
        return configFactory.create();
    }

}
