package kissmydisc.repricer.dao;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kissmydisc.repricer.model.InventoryLoaderConfiguration;
import kissmydisc.repricer.utils.AppConfig;

public class ListingConfigurationDAO extends DBAccessor {
    public ListingConfigurationDAO() {
        super();
    }

    public InventoryLoaderConfiguration getListingConfiguration(String region) throws DBException {
        InventoryLoaderConfiguration config = null;
        String query = "select * from listing_configuration where region = ?";
        PreparedStatement st = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(query);
            st.setString(1, region);
            rs = st.executeQuery();
            if (rs.next()) {
                config = new InventoryLoaderConfiguration();
                config.setExpeditedShipping(rs.getString("EXPEDITED_SHIPPING"));
                config.setItemIsMarketplace(rs.getString("ITEM_IS_MARKETPLACE"));
                // config.setItemNoteNew(new
                // String(rs.getBytes("ITEM_NOTE_NEW")));
                config.setItemNoteNew(rs.getString("ITEM_NOTE_NEW"));
                config.setItemNoteObi(rs.getString("ITEM_NOTE_OBI"));
                config.setItemNoteUsed(rs.getString("ITEM_NOTE_USED"));
                config.setWillShipInternationally(rs.getString("WILL_SHIP_INTERNATIONALLY"));
            }
            return config;
        } catch (Exception e) {
            throw new DBException("Error in GetListingConfiguration", e);
        } finally {
            releaseResultSet(rs);
            releaseStatement(st);
            releaseConnection();
        }
    }

    private static void printBytes(String string) {
        byte[] by = string.getBytes();
        System.out.println(by.length);
        for (byte b : by) {
            System.out.print(b);
            System.out.print(" ");
        }
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        AppConfig.initialize("Repricer.properties");
        FileOutputStream output = new FileOutputStream("utf8-output-test.txt");
        InventoryLoaderConfiguration config = new ListingConfigurationDAO().getListingConfiguration("JP");
        String string = "{catalog-number} オリジナル/本物の新品。　International orders & English service OK. ^_^ お届けには七日から十四日掛かります。そのため至急商品が必要な場合には購入をお控えください。日本の商品は日本支店から発送され、その他の商品は海外支店から発送されす。そのため、海外支店から送られた商品がお手元に届くまで時間を要することもありますので、あらかじめご留意ください。";
        if (config
                .getItemNoteNew()
                .equals("{catalog-number} オリジナル/本物の新品。　International orders & English service OK. ^_^ お届けには七日から十四日掛かります。そのため至急商品が必要な場合には購入をお控えください。日本の商品は日本支店から発送され、その他の商品は海外支店から発送されす。そのため、海外支店から送られた商品がお手元に届くまで時間を要することもありますので、あらかじめご留意ください。")) {
            System.out.println("Yes");
        } else {
            System.out.println("No");
        }
        //"{catalog-number} オリジナル/本物の新品。　International orders & English service OK. ^_^ お届けには七日から十四日掛かります。そのため至急商品が必要な場合には購入をお控えください。日本の商品は日本支店から発送され、その他の商品は海外支店から発送されす。そのため、海外支店から送られた商品がお手元に届くまで時間を要することもありますので、あらかじめご留意ください。";
        printBytes(string);
        printBytes(config.getItemNoteNew());
        output.write("{catalog-number} オリジナル/本物の新品。　International orders & English service OK. ^_^ お届けには七日から十四日掛かります。そのため至急商品が必要な場合には購入をお控えください。日本の商品は日本支店から発送され、その他の商品は海外支店から発送されす。そのため、海外支店から送られた商品がお手元に届くまで時間を要することもありますので、あらかじめご留意ください。"
                .getBytes("Shift_JIS"));
        output.write("\n".getBytes());
        output.write(config.getItemNoteNew().getBytes("Shift_JIS"));
        output.write("\n".getBytes());
        output.write(config.getItemNoteObi().getBytes("Shift_JIS"));
        output.write("\n".getBytes());
        output.write(config.getItemNoteUsed().getBytes("Shift_JIS"));
        output.close();

    }
}
