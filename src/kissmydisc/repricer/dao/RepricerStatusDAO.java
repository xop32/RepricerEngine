package kissmydisc.repricer.dao;

import java.sql.Connection;
import java.sql.Statement;

public class RepricerStatusDAO extends DBAccessor {

    public RepricerStatusDAO(final Connection conn) {
        super(conn);
    }

    public RepricerStatusDAO() {
        super();
    }

    public void cancelRunning() throws DBException {
        String query = "update repricer_configuration set repricer_status = 'CANCELED' where repricer_status = 'RUNNING'";
        Connection conn = null;
        Statement st = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(query);
            st.executeUpdate(query);
        } catch (Exception e) {
            throw new DBException("Error updating..", e);
        } finally {
            releaseStatement(st);
            releaseConnection();
        }
    }

}
