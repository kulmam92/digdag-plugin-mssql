package com.github.kulmam92.digdag.plugin.mssql;

import io.digdag.standards.operator.jdbc.AbstractJdbcResultSet;

import java.sql.ResultSet;

public class MssqlResultSet
        extends AbstractJdbcResultSet
{
    MssqlResultSet(ResultSet resultSet)
    {
        super(resultSet);
    }

    @Override
    protected Object serializableObject(Object raw)
    {
        // TODO add more conversion logics here. MSSQL jdbc may return objects that are not serializable using Jackson.
        return raw;
    }

}
