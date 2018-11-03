package com.github.kulmam92.digdag.plugin.mssql;

import com.google.common.annotations.VisibleForTesting;
import io.digdag.standards.operator.jdbc.AbstractJdbcConnection;
import io.digdag.standards.operator.jdbc.AbstractPersistentTransactionHelper;
import io.digdag.standards.operator.jdbc.DatabaseException;
import io.digdag.standards.operator.jdbc.JdbcResultSet;
import io.digdag.standards.operator.jdbc.LockConflictException;
import io.digdag.standards.operator.jdbc.NotReadOnlyException;
import io.digdag.standards.operator.jdbc.TableReference;
import io.digdag.standards.operator.jdbc.TransactionHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Locale.ENGLISH;
import static org.postgresql.core.Utils.escapeIdentifier;

public class MssqlConnection
    extends AbstractJdbcConnection
{
    @VisibleForTesting
    public static MssqlConnection open(MssqlConnectionConfig config)
    {
        return new MssqlConnection(config.openConnection());
    }

    protected MssqlConnection(Connection connection)
    {
        super(connection);
    }

    @Override
    public String buildCreateTableStatement(String selectSql, TableReference targetTable)
    {
        String escapedRef = escapeTableReference(targetTable);
        return String.format(ENGLISH,
                "IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '%s') \n" +
                "BEGIN \n" +
                "DROP TABLE %s; \n" +
                "END \n" +
                "SELECT * INTO %s \n" +
                "FROM ( \n" +
                "%s \n" +
                ") t ",
                escapedRef, escapedRef, selectSql);                
    }

    @Override
    public String buildInsertStatement(String selectSql, TableReference targetTable)
    {
        String escapedRef = escapeTableReference(targetTable);
        return String.format(ENGLISH,
                "INSERT INTO %s\n%s",
                escapedRef, selectSql);
    }

    // To do - make it compatable with MS    
    @Override
    public SQLException validateStatement(String sql)
    {
        // Here uses nativeSQL() instead of Connection#prepareStatement because
        // prepareStatement() validates a SQL by creating a server-side prepared statement
        // and RDBMS wrongly decides that the SQL is broken in this case:
        //   * the SQL includes multiple statements
        //   * a statement creates a table and a later statement uses it in the SQL
        try {
            connection.nativeSQL(sql);
            return null;
        }
        catch (SQLException ex) {
            if (ex.getSQLState().startsWith("42")) { // to do
                // SQL error class 42
                return ex;
            }
            throw new DatabaseException("Failed to validate statement", ex);
        }
    }

    // To do - implement sqlcmd support
    // https://github.com/embulk/embulk-output-jdbc/blob/07b6dfea0c5296c124328d2d17bdc48240f7d159/embulk-output-sqlserver/src/test/java/org/embulk/output/sqlserver/SQLServerTests.java

    @Override
    public void executeReadOnlyQuery(String sql, Consumer<JdbcResultSet> resultHandler)
            throws NotReadOnlyException
    {
        try {
            // Need to check if this is really necessary
            execute("SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED");
            try (Statement stmt = connection.createStatement()) {
                // https://docs.microsoft.com/en-us/sql/connect/jdbc/reference/executequery-method-sqlserverstatement?view=sql-server-2017
                // Runs the given SQL statement and returns a single SQLServerResultSet object.
                ResultSet rs = stmt.executeQuery(sql);  // executeQuery throws exception if given query includes multiple statements
                resultHandler.accept(new MssqlResultSet(rs));
            }
            execute("SET TRANSACTION ISOLATION LEVEL READ COMMITTED");
        }
        catch (SQLException ex) {
            throw new DatabaseException("Failed to execute given SELECT statement", ex);
        }
    }

    @Override
    public String escapeIdent(String ident)
    {
        try {
            StringBuilder buf = new StringBuilder();
            escapeIdentifier(buf, ident);
            return buf.toString();
        }
        catch (SQLException ex) {
            throw new IllegalArgumentException(
                    String.format(ENGLISH,
                            "Invalid identifier name (%s): %s",
                            ex.getMessage(),
                            ident));
        }
    }

    @Override
    public TransactionHelper getStrictTransactionHelper(String statusTableSchema, String statusTableName, Duration cleanupDuration)
    {
        return new MssqlConnection.MssqlPersistentTransactionHelper(statusTableSchema, statusTableName, cleanupDuration);
    }

    private class MssqlPersistentTransactionHelper
            extends AbstractPersistentTransactionHelper
    {
        private final TableReference statusTableReference;

        MssqlPersistentTransactionHelper(String statusTableSchema, String statusTableName, Duration cleanupDuration)
        {
            super(cleanupDuration);
            if (statusTableSchema != null) {
                statusTableReference = TableReference.of(statusTableSchema, statusTableName);
            }
            else {
                statusTableReference = TableReference.of(statusTableName);
            }
        }

        TableReference statusTableReference()
        {
            return statusTableReference;
        }

        String buildCreateTable()
        {
            return String.format(ENGLISH,
                    "IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '%s')  \n" +
                    "BEGIN  \n" +
                    "CREATE TABLE %s  \n" +
                    "(query_id varchar(50) NOT NULL UNIQUE, created_at datetime2 NOT NULL, completed_at datetime2)  \n" +
                    "END",
                    statusTableReference.getName(),
                    statusTableReference.getName());
        }

        @Override
        public void prepare(UUID queryId)
        {
            String sql = buildCreateTable();
            executeStatement("create a status table " + escapeTableReference(statusTableReference()) + ".\n"
                            + "hint: if you don't have permission to create tables, "
                            + "please try one of these options:\n"
                            + "1. ask system administrator to create this table using the following command "
                            + "and grant INSERT privilege to this user: " + sql + ";\n"
                            + "2. ask system administrator to create a schema that this user can create a table "
                            + "and set 'status_table_schema' option to it\n"
                    , sql);
        }

        @Override
        public void cleanup()
        {
            executeStatement("delete old query status rows from " + statusTableReference().getName() + " table",
                    String.format(ENGLISH,
                            "DELETE FROM %s WHERE query_id in ( " +
                                "SELECT query_id FROM %s " +
                                "WHERE completed_at < dateadd(s, -%d, SYSDATETIME()))",
                            statusTableReference().getName(),
                            statusTableReference().getName(),
                            cleanupDuration.getSeconds())
            );
        }

        @Override
        public boolean lockedTransaction(UUID queryId, TransactionAction action)
                throws LockConflictException
        {
            boolean completed = beginTransactionAndLockStatusRow(queryId);
    
            // status row is locked here until this transaction is committed or aborted.
    
            if (completed) {
                // query was completed successfully before. skip the action.
                abortTransaction();
                return false;
            }
            else {
                // query is not completed. run the action.
                action.run();
                updateStatusRowAndCommit(queryId);
                return true;
            }
        }

        private boolean beginTransactionAndLockStatusRow(UUID queryId)
        throws LockConflictException
        {
            do {
                beginTransaction();

                String status = lockStatusRowString(queryId);
                switch (status) {
                case "LOCKED_COMPLETED":
                    return true;
                case "LOCKED_NOT_COMPLETED":
                    return false;
                case "NOT_EXISTS":
                    // status row doesn't exist. insert one.
                    insertStatusRowAndCommit(queryId);
                }
            } while (true);
        }

        private void beginTransaction()
        {
            executeStatement("begin a transaction", "BEGIN TRANSACTION");
        }
    
        @Override
        protected void abortTransaction()
        {
            executeStatement("rollback a transaction", "ROLLBACK TRANSACTION");
        }

        // Referenced by AbstractPersistentTransactionHelper
        @Override
        protected StatusRow lockStatusRow(UUID queryId)
                throws LockConflictException
        {
            try (Statement stmt = connection.createStatement()) {
                // Sql Server doesn't support select for update -- (updlock)
                ResultSet rs = stmt.executeQuery(String.format(ENGLISH,
                        "SELECT completed_at FROM %s WHERE query_id = '%s'",
                        statusTableReference().getName(),
                        queryId.toString())
                );
                if (rs.next()) {
                    // status row exists and locked. get status of it.
                    rs.getTimestamp(1);
                    if (rs.wasNull()) {
                        return StatusRow.LOCKED_NOT_COMPLETED;
                    }
                    else {
                        return StatusRow.LOCKED_COMPLETED;
                    }
                }
                else {
                    return StatusRow.NOT_EXISTS;
                }
            }
            catch (SQLException ex) {
                throw new DatabaseException("Failed to lock a status row", ex);
            }
        }

        // Did this since enum StatusRow is not public
        protected String lockStatusRowString(UUID queryId)
        throws LockConflictException
        {
            try (Statement stmt = connection.createStatement()) {
                // Sql Server doesn't support select for update -- (updlock)
                ResultSet rs = stmt.executeQuery(String.format(ENGLISH,
                        "SELECT completed_at FROM %s WHERE query_id = '%s'",
                        statusTableReference().getName(),
                        queryId.toString())
                );
                if (rs.next()) {
                    // status row exists and locked. get status of it.
                    rs.getTimestamp(1);
                    if (rs.wasNull()) {
                        return "LOCKED_NOT_COMPLETED";
                    }
                    else {
                        return "LOCKED_COMPLETED";
                    }
                }
                else {
                    return "NOT_EXISTS";
                }
            }
            catch (SQLException ex) {
                throw new DatabaseException("Failed to lock a status row", ex);
            }
        }

        @Override
        protected void updateStatusRowAndCommit(UUID queryId)
        {
            executeStatement("update status row",
                    String.format(ENGLISH,
                            "UPDATE %s SET completed_at = SYSDATETIME() WHERE query_id = '%s'",
                            statusTableReference().getName(),
                            queryId.toString())
            );
            executeStatement("commit updated status row", "COMMIT");
        }

        @Override
        protected void insertStatusRowAndCommit(UUID queryId)
        {
            try {
                execute(String.format(ENGLISH,
                        "INSERT INTO %s (query_id, created_at) VALUES ('%s', SYSDATETIME())",
                        statusTableReference().getName(), queryId.toString()));
                // succeeded to insert a status row.
                execute("COMMIT");
            }
            catch (SQLException ex) {
                if (isConflictException(ex)) {
                    // another node inserted a status row after BEGIN call.
                    // skip insert since it already exists.
                    abortTransaction();
                }
                else {
                    throw new DatabaseException("Failed to insert a status row", ex);
                }
            }
        }

        boolean isConflictException(SQLException ex)
        {
            return "2627".equals(ex.getSQLState()); // UK violation
        }

        @Override
        protected void executeStatement(String desc, String sql)
        {
            try {
                execute(sql);
            }
            catch (SQLException ex) {
                throw new DatabaseException("Failed to " + desc, ex);
            }
        }
    }
}
