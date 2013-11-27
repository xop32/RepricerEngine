package kissmydisc.repricer.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KMDProductionInventoryDAO extends KMDProductionDBAccessor {
    public KMDProductionInventoryDAO() {
        super();
    }
    public Map<String, String> getCatalogNumber(final List<String> asin) throws DBException {
        String query = "select asin, catnumber from catalog where asin in (?";
        if (asin.size() > 1) {
            for (int i = 0; i < asin.size() - 1; i++) {
                query += ", ? ";
            }
        }
        query += ")";
        PreparedStatement st = null;
        Connection conn = null;
        ResultSet rs = null;
        Map<String, String> result = new HashMap<String, String>();
        try {
            conn = getConnection();
            st = conn.prepareStatement(query);
            int index = 1;
            for (String a : asin) {
                st.setString(index++, a);
            }
            rs = st.executeQuery();
            while (rs.next()) {
                String prdId = rs.getString("asin");
                String catNum = rs.getString("catnumber");
                if (!result.containsKey(prdId) && (catNum != null && !catNum.equals(""))) {
                    result.put(prdId, catNum);
                }
            }
            return result;
        } catch(Exception e) {
            throw new DBException("Error getting catnumber", e);
        } finally {
            releaseResultSet(rs);
            releaseStatement(st);
            releaseConnection();
        }
    }
}
