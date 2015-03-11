package kissmydisc.repricer.dao;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import kissmydisc.repricer.utils.AppConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class KMDProductionDBAccessor {
    private Connection conn = null;

    private boolean myConnection = true;

    public KMDProductionDBAccessor(Connection conn) {
        this.conn = conn;
        this.myConnection = false;
    }

    public KMDProductionDBAccessor() {
    }

    protected void releaseResultSet(ResultSet rs) throws DBException {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new DBException(e);
            }
        }
    }

    protected Connection getConnection() throws DBException {
        if (conn == null) {
            conn = KMDDBConnectionFactory.getConnection();
            return conn;
        } else {
            return conn;
        }
    }

    protected void releaseStatement(Statement st) throws DBException {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                throw new DBException(e);
            }
        }
    }

    protected void releaseConnection() throws DBException {
        if (myConnection) {
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception e) {
                    throw new DBException("Error closing the connection", e);
                }
                conn = null;
            }
        }
    }

    static class KMDDBConnectionFactory {
        private static ComboPooledDataSource cpds = null;
        private static boolean initialized = false;

        static {
            if (!initialized) {
                try {
                    cpds = new ComboPooledDataSource();
                    cpds.setDriverClass("com.mysql.jdbc.Driver");
                    String host = AppConfig.getString("KMDProductionDBHost");
                    String database = AppConfig.getString("KMDProductionDBName");
                    int port = AppConfig.getInteger("KMDProductionDBPort", 3306);
                    cpds.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database
                            + "?useServerPrepStmts=false&rewriteBatchedStatements=true");
                    cpds.setUser(AppConfig.getString("KMDProductionDBUsername"));
                    cpds.setPassword(AppConfig.getString("KMDProductionDBPassword"));
                    cpds.setMaxPoolSize(2);
                    cpds.setMinPoolSize(1);
                    cpds.setNumHelperThreads(4);
                    cpds.setAcquireIncrement(1);
                    cpds.setIdleConnectionTestPeriod(3600);
                    cpds.setMaxConnectionAge(3 * 3600);
                    initialized = true;
                } catch (PropertyVetoException ex) {
                    // handle exception...not important.....
                }
            }
        }

        public synchronized static Connection getConnection() throws DBException {
            if (!initialized) {
                throw new DBException("KMDDBConnectionFactory not initialized!!");
            }
            try {
                return cpds.getConnection();
            } catch (SQLException e) {
                throw new DBException("Error while creating connection to KMD", e);
            }
        }
    }
}
