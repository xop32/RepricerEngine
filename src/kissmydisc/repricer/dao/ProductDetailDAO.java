package kissmydisc.repricer.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kissmydisc.repricer.model.ProductDetail;

public class ProductDetailDAO extends DBAccessor {

    public ProductDetailDAO() {

    }

    public Map<String, ProductDetail> getProductDetails(List<String> productIds) throws DBException {
        String selectQuery = "select * from product_info where PRODUCT_ID in ( ? ";
        if (productIds.size() > 1) {
            for (int i = 0; i < productIds.size() - 1; i++) {
                selectQuery += ", ? ";
            }
        }
        selectQuery += " ) ";
        PreparedStatement st = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(selectQuery);
            int index = 1;
            for (String sku : productIds) {
                st.setString(index++, sku);
            }
            rs = st.executeQuery();
            Map<String, ProductDetail> map = new HashMap<String, ProductDetail>();
            while (rs.next()) {
                String productId = rs.getString("PRODUCT_ID");
                String artist = rs.getString("ARTIST");
                String author = rs.getString("AUTHOR");
                String title = rs.getString("TITLE");
                ProductDetail detail = new ProductDetail(productId, artist, author, title);
                map.put(productId, detail);
            }
            return map;
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            releaseResultSet(rs);
            releaseStatement(st);
            releaseConnection();
        }
    }

    public void addProductDetails(Map<String, ProductDetail> map) throws DBException {
        String insertQuery = "insert into product_info (PRODUCT_ID, ARTIST, TITLE, AUTHOR) values (?, ?, ?, ?) ON DUPLICATE KEY UPDATE ARTIST = values(ARTIST), AUTHOR=values(AUTHOR), TITLE=values(TITLE)";
        PreparedStatement st = null;
        Connection conn = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(insertQuery);
            for (Map.Entry<String, ProductDetail> entry : map.entrySet()) {
                st.setString(1, entry.getKey());
                st.setString(2, entry.getValue().getArtist());
                st.setString(3, entry.getValue().getTitle());
                st.setString(4, entry.getValue().getAuthor());
                st.addBatch();
            };
            st.executeBatch();
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            releaseStatement(st);
            releaseConnection();
        }
    }

    public void addProductUnavailable(List<String> leftOver) throws DBException {
        String insertQuery = "insert into product_info (PRODUCT_ID, UNAVAILABLE) values (?, ?) ON DUPLICATE KEY UPDATE UNAVAILABLE=values(UNAVAILABLE)";
        PreparedStatement st = null;
        Connection conn = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(insertQuery);
            for (String id : leftOver) {
                st.setString(1, id);
                st.setBoolean(2, true);
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            releaseStatement(st);
            releaseConnection();
        }

    }

}
