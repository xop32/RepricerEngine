package kissmydisc.repricer.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kissmydisc.repricer.dao.KMDProductionInventoryDAO;

public class ItemNoteGenerator {
    public static final Log log = LogFactory.getLog(ItemNoteGenerator.class);

    public static String getItemNote(final String asin, final String itemNoteFormat, final String region) {
        String itemNote = itemNoteFormat;
        String catalogNumber = "";
        KMDProductionInventoryDAO dao = new KMDProductionInventoryDAO();
        List<String> asinList = new ArrayList<>();
        asinList.add(asin);
        try {
            Map<String, String> mp = dao.getCatalogNumber(asinList);
            if (mp.containsKey(asin)) {
                catalogNumber = mp.get(asin);
            }
        } catch (Exception e) {
            log.error("Error getting ItemNote for " + asin, e);
        }
        itemNote = itemNote.replace("{catalog-number}", catalogNumber);
        return itemNote;
    }
}
