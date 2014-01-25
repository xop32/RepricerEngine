package kissmydisc.repricer.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kissmydisc.repricer.dao.AmazonAccessor;
import kissmydisc.repricer.dao.DBException;
import kissmydisc.repricer.dao.ProductDetailDAO;
import kissmydisc.repricer.model.ProductDetail;

public class CreateListingsFilter {

    private Set<String> asins = new HashSet<String>();

    private AmazonAccessor accessor = null;

    private Map<String, ProductDetail> details = null;

    private static final Log log = LogFactory.getLog(CreateListingsFilter.class);

    public CreateListingsFilter(Set<String> asins, AmazonAccessor accessor) {
        this.asins = asins;
        this.accessor = accessor;
    }

    public boolean shouldFilter(String region, String asin, ProductCondition condition) {
        if (region.equals("JP")) {
            if (details == null) {
                details = new HashMap<String, ProductDetail>();
                List<String> reqdId = new ArrayList<String>();
                for (String a : asins) {
                    reqdId.add(a);
                }
                Map<String, ProductDetail> productDetails = new HashMap<String, ProductDetail>();
                try {
                    productDetails = new ProductDetailDAO().getProductDetails(reqdId);
                } catch (DBException e1) {
                    log.error("Error getting product details for " + reqdId, e1);
                }
                List<String> notAvailable = new ArrayList<String>();
                for (String a : reqdId) {
                    if (!productDetails.containsKey(a)) {
                        notAvailable.add(a);
                    } else {
                        ProductDetail detail = productDetails.get(a);
                        if (detail.getProductType() == null) {
                            notAvailable.add(a);
                        } else {
                            details.put(a, detail);
                        }
                    }
                }
                try {
                    Map<String, ProductDetail> obtainedFromAmazon = accessor.getMatchingCatalog(notAvailable);
                    if (obtainedFromAmazon != null) {
                        details.putAll(obtainedFromAmazon);
                    }
                    new ProductDetailDAO().addProductDetails(obtainedFromAmazon);
                } catch (Exception e) {
                    // Error
                }
            }
            if (details != null && details.containsKey(asin)) {
                ProductDetail detail = details.get(asin);
                if (detail.getProductType() != null && "book".equalsIgnoreCase(detail.getProductType())
                        && condition == ProductCondition.NEW) {
                    return true;
                }
            }
        }
        return false;
    }
}
