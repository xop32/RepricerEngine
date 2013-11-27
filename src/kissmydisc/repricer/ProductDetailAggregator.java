package kissmydisc.repricer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import kissmydisc.repricer.dao.AmazonAccessor;
import kissmydisc.repricer.dao.InventoryItemDAO;
import kissmydisc.repricer.dao.LatestInventoryDAO;
import kissmydisc.repricer.dao.ProductDetailDAO;
import kissmydisc.repricer.model.InventoryFeedItem;
import kissmydisc.repricer.model.ProductDetail;
import kissmydisc.repricer.utils.AppConfig;
import kissmydisc.repricer.utils.Pair;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

public class ProductDetailAggregator {

    private static Map<String, AmazonAccessor> amazonAccessors = new Hashtable<String, AmazonAccessor>();

    private static ThreadPoolExecutor usExecutor;

    private static ThreadPoolExecutor ukExecutor;

    private static ThreadPoolExecutor deExecutor;

    private static ThreadPoolExecutor itExecutor;

    private static ThreadPoolExecutor caExecutor;

    private static ThreadPoolExecutor esExecutor;

    private static Long usSubmitted = 0L;

    private static Long ukSubmitted = 0L;

    private static Long caSubmitted = 0L;

    private static Long itSubmitted = 0L;

    private static Long deSubmitted = 0L;

    private static Long esSubmitted = 0L;

    private static Long totalCompleted = 0L;

    private static Log log;

    private static Map<String, Long> latestInventories = new Hashtable<String, Long>();

    public static void main(String[] args) throws Exception {
        ukExecutor = new ThreadPoolExecutor(4, 4, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000));
        usExecutor = new ThreadPoolExecutor(4, 4, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000));
        caExecutor = new ThreadPoolExecutor(4, 4, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000));
        deExecutor = new ThreadPoolExecutor(2, 2, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000));
        itExecutor = new ThreadPoolExecutor(1, 1, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000));
        esExecutor = new ThreadPoolExecutor(1, 1, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000));
        PropertyConfigurator.configure("detail-cache-log4j.properties");
        AppConfig.initialize("Translator.Properties");
        log = LogFactory.getLog(ProductDetailAggregator.class);
        AmazonAccessor.initialize();
        long startTime = System.currentTimeMillis() - 1000;
        amazonAccessors.put("US", new AmazonAccessor("US", "ATVPDKIKX0DER", "A2239UYQWWA9KK"));
        amazonAccessors.put("UK", new AmazonAccessor("UK", "A1F83G8C2ARO7P", "A4F79GX8B8ONH"));
        amazonAccessors.put("DE", new AmazonAccessor("DE", "A1PA6795UKMFR9", "A1Y6IU4BLRQHTK"));
        amazonAccessors.put("IT", new AmazonAccessor("IT", "APJ6JRA9NG5V4", "A1Y6IU4BLRQHTK"));
        amazonAccessors.put("CA", new AmazonAccessor("CA", "A2EUQ1WTGCTBG2", "A1CGLT0YJKNWXZ"));
        amazonAccessors.put("ES", new AmazonAccessor("ES", "A1RKKUPIHCS9HS", "A1Y6IU4BLRQHTK"));
        String moreToken = null;
        int MAX_BATCH_SIZE = AppConfig.getInteger("maxBatchSize-translator", 3);
        String region = null;
        region = args[0];
        if (args.length > 1) {
            moreToken = args[1];
        }
        long latestInventory = new LatestInventoryDAO().getLatestInventory(region);
        latestInventories.put("US", new LatestInventoryDAO().getLatestInventory("US"));
        latestInventories.put("CA", new LatestInventoryDAO().getLatestInventory("CA"));
        latestInventories.put("UK", new LatestInventoryDAO().getLatestInventory("UK"));
        latestInventories.put("DE", new LatestInventoryDAO().getLatestInventory("DE"));
        latestInventories.put("IT", new LatestInventoryDAO().getLatestInventory("IT"));
        latestInventories.put("ES", new LatestInventoryDAO().getLatestInventory("ES"));
        Map<String, Boolean> lastIteration = new HashMap<String, Boolean>();
        List<InventoryFeedItem> itemPair = new ArrayList<InventoryFeedItem>();
        int submitted = 0;
        do {
            try {
                Map<String, Boolean> thisIteration = new HashMap<String, Boolean>();
                Pair<List<InventoryFeedItem>, String> itemsMoreToken = new InventoryItemDAO().getMatchingItems(
                        latestInventory, region, moreToken, 500);
                List<InventoryFeedItem> items = itemsMoreToken.getFirst();
                for (InventoryFeedItem item : items) {
                    if (item.isValid()) {
                        if (!thisIteration.containsKey(item.getProductId())
                                && !lastIteration.containsKey(item.getProductId())) {
                            String[] regionsAvailable = { "US", "UK", "CA", "DE", "IT", "ES"};
                            List<String> itemsToRequest = new ArrayList<String>();
                            boolean available = false;
                            for (String availableRegion : regionsAvailable) {
                                List<InventoryFeedItem> itemsInDB = new InventoryItemDAO().getMatchingItems(
                                        latestInventories.get(availableRegion), availableRegion, item.getProductId());
                                if (itemsInDB != null && itemsInDB.size() > 0) {
                                    available = true;
                                }
                            }
                            if (available) {
                                itemPair.add(item);
                            }
                            if (itemPair.size() == MAX_BATCH_SIZE) {
                                List<String> productIds = new ArrayList<String>();
                                for (InventoryFeedItem newItem : itemPair) {
                                    productIds.add(newItem.getProductId());
                                }
                                int random = (int) (Math.random() * 100000000);
                                log.info("ItemsToRequest: " + productIds);
                                List<String> regionOrder = new ArrayList<String>() {
                                    {
                                        add("CA");
                                        add("DE");
                                        add("IT");
                                        add("ES");
                                    }
                                };
                                if (random % 2 == 0) {
                                    usExecutor.submit(new DetailAggregator("US", productIds, 1, regionOrder));
                                    submitted += productIds.size();
                                    synchronized (usSubmitted) {
                                        usSubmitted++;
                                    }
                                } else {
                                    ukExecutor.submit(new DetailAggregator("UK", productIds, 2, regionOrder));
                                    synchronized (ukSubmitted) {
                                        ukSubmitted++;
                                    }
                                    submitted += productIds.size();
                                }
                                itemPair = new ArrayList<InventoryFeedItem>();
                            }
                        }
                    }
                    thisIteration.put(item.getProductId(), true);
                }
                lastIteration = thisIteration;
                while (usExecutor.getQueue().size() > 500 || ukExecutor.getQueue().size() > 500
                        || caExecutor.getQueue().size() > 500 || itExecutor.getQueue().size() > 100
                        || deExecutor.getQueue().size() > 100 || esExecutor.getQueue().size() > 100) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        // Do nothing.
                    }
                    String info = "Waiting for Queue to drain - US: [" + usSubmitted + ", "
                            + usExecutor.getCompletedTaskCount() + "], ";
                    info += " UK: [" + ukSubmitted + ", " + ukExecutor.getCompletedTaskCount() + "] ";
                    info += " CA: [" + caSubmitted + ", " + caExecutor.getCompletedTaskCount() + "] ";
                    info += " IT: [" + itSubmitted + ", " + itExecutor.getCompletedTaskCount() + "] ";
                    info += " DE: [" + deSubmitted + ", " + deExecutor.getCompletedTaskCount() + "] ";
                    info += " ES: [" + esSubmitted + ", " + esExecutor.getCompletedTaskCount() + "] ";
                    info += " [totalSubmitted: " + submitted + " totalCompleted: " + totalCompleted + " ] ";
                    info += " [rate: "
                            + (totalCompleted / (1.0 * (System.currentTimeMillis() - startTime) / (1000 * 60 * 60)))
                            + "] ";
                    log.info(info);
                }
                String info = "Status: [" + usSubmitted + ", " + usExecutor.getCompletedTaskCount() + "], ";
                info += " UK: [" + ukSubmitted + ", " + ukExecutor.getCompletedTaskCount() + "] ";
                info += " CA: [" + caSubmitted + ", " + caExecutor.getCompletedTaskCount() + "] ";
                info += " IT: [" + itSubmitted + ", " + itExecutor.getCompletedTaskCount() + "] ";
                info += " DE: [" + deSubmitted + ", " + deExecutor.getCompletedTaskCount() + "] ";
                info += " ES: [" + esSubmitted + ", " + esExecutor.getCompletedTaskCount() + "] ";
                info += " [totalSubmitted: " + submitted + " totalCompleted: " + totalCompleted + " ] ";
                info += " [rate: "
                        + (totalCompleted / (1.0 * (System.currentTimeMillis() - startTime) / (1000 * 60 * 60))) + "] ";
                log.info(info);
                moreToken = itemsMoreToken.getSecond();
                if (moreToken == null && itemPair.size() > 0) {
                    List<String> productIds = new ArrayList<String>();
                    for (InventoryFeedItem newItem : itemPair) {
                        productIds.add(newItem.getProductId());
                    }
                    List<String> regionOrder = new ArrayList<String>() {
                        {
                            add("CA");
                            add("DE");
                            add("IT");
                            add("ES");
                        }
                    };
                    int random = (int) (Math.random() * 100000000);
                    if (random % 2 == 0) {
                        usExecutor.submit(new DetailAggregator("US", productIds, 1, regionOrder));
                        submitted += productIds.size();
                        synchronized (usSubmitted) {
                            usSubmitted++;
                        }
                    } else {
                        ukExecutor.submit(new DetailAggregator("UK", productIds, 2, regionOrder));
                        synchronized (ukSubmitted) {
                            ukSubmitted++;
                        }
                        submitted += productIds.size();
                    }
                    itemPair = new ArrayList<InventoryFeedItem>();
                }
            } catch (Exception e) {
                log.error("Error while processing " + moreToken, e);
                try {
                    Thread.sleep(1000);
                } catch (Exception e1) {
                    // Do nothing;
                }
            }
        } while (moreToken != null);
        while (usExecutor.getCompletedTaskCount() < usSubmitted || ukExecutor.getCompletedTaskCount() < ukSubmitted
                || caExecutor.getCompletedTaskCount() < caSubmitted || itExecutor.getCompletedTaskCount() < itSubmitted
                || deExecutor.getCompletedTaskCount() < deSubmitted || esExecutor.getCompletedTaskCount() < esSubmitted) {
            String info = "Waiting for completion US: [" + usSubmitted + ", " + usExecutor.getCompletedTaskCount()
                    + "], ";
            info += " UK: [" + ukSubmitted + ", " + ukExecutor.getCompletedTaskCount() + "] ";
            info += " CA: [" + caSubmitted + ", " + caExecutor.getCompletedTaskCount() + "] ";
            info += " IT: [" + itSubmitted + ", " + itExecutor.getCompletedTaskCount() + "] ";
            info += " DE: [" + deSubmitted + ", " + deExecutor.getCompletedTaskCount() + "] ";
            info += " ES: [" + esSubmitted + ", " + esExecutor.getCompletedTaskCount() + "] ";
            info += " [totalSubmitted: " + submitted + " totalCompleted: " + totalCompleted + " ] ";
            info += " [rate: " + (totalCompleted / (1.0 * (System.currentTimeMillis() - startTime) / (1000 * 60 * 60)))
                    + "] ";
            log.info(info);
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                // Do nothing.
            }
        }
        usExecutor.shutdownNow();
        ukExecutor.shutdownNow();
        caExecutor.shutdownNow();
        deExecutor.shutdownNow();
        itExecutor.shutdownNow();
        esExecutor.shutdownNow();
    }

    private static class DetailAggregator implements Runnable {

        private String region;

        private List<String> productIds;

        private int order;

        private List<String> regionOrder;

        public DetailAggregator(String region, List<String> productIds, int order, List<String> regionOrder) {
            this.region = region;
            this.productIds = productIds;
            this.order = order;
            this.regionOrder = regionOrder;
        }

        @Override
        public void run() {
            try {
                log.info("Processing " + productIds + " for " + region + " regions: " + regionOrder);
                if (productIds != null && productIds.size() > 0) {
                   // Map<String, ProductDetail> details = new ProductDetailDAO().getProductDetails(productIds);
                    Map<String, ProductDetail> details = new HashMap<String, ProductDetail>();
                    List<String> toRequest = new ArrayList<String>();
                    for (String productId : productIds) {
                        if (!details.containsKey(productId)) {
                            toRequest.add(productId);
                        }
                    }
                    if (toRequest.size() > 0) {
                        Map<String, ProductDetail> detailsFromAmazon = new HashMap<String, ProductDetail>();
                        int retry = 0;
                        boolean done = false;
                        do {
                            try {
                                detailsFromAmazon = amazonAccessors.get(region).getMatchingCatalog(toRequest);
                                done = true;
                            } catch (Exception e) {
                                log.error("Exception getting data from Amazon for " + productIds, e);
                                try {
                                    Thread.sleep(1000);
                                } catch (Exception e2) {
                                    // ignore
                                }
                            }
                        } while (!done && retry++ < 3);
                        //new ProductDetailDAO().addProductDetails(detailsFromAmazon);
                        List<String> leftOver = new ArrayList<String>();
                        for (String productId : toRequest) {
                            if (!detailsFromAmazon.containsKey(productId)) {
                                leftOver.add(productId);
                            }
                        }
                        if (leftOver.size() > 0) {
                            if (order == 1) {
                                DetailAggregator aggr = new DetailAggregator("UK", leftOver, 3, regionOrder);
                                ukExecutor.submit(aggr);
                                synchronized (ukSubmitted) {
                                    ukSubmitted++;
                                }
                            } else if (order == 2) {
                                DetailAggregator aggr = new DetailAggregator("US", leftOver, 3, regionOrder);
                                usExecutor.submit(aggr);
                                synchronized (usSubmitted) {
                                    usSubmitted++;
                                }
                            } else if (order == 3) {
                                boolean seen = false;
                                String next = null;
                                for (String r : regionOrder) {
                                    if (seen) {
                                        next = r;
                                        break;
                                    }
                                    if (r.equals(region)) {
                                        seen = true;
                                    }
                                }
                                if (!seen) {
                                    next = regionOrder.get(0);
                                }
                                if (next != null) {
                                    DetailAggregator aggr = new DetailAggregator(next, leftOver, 3, regionOrder);
                                    if (next.equals("ES")) {
                                        while (esExecutor.getQueue().size() > 100) {
                                            log.info("Waiting for Queue of ES to drain.."
                                                    + esExecutor.getQueue().size() + " Current: " + productIds
                                                    + ", region: " + region);
                                            try {
                                                Thread.sleep(1000);
                                            } catch (Exception e) {

                                            }
                                        }
                                        esExecutor.submit(aggr);
                                        synchronized (esSubmitted) {
                                            esSubmitted++;
                                        }
                                    } else if (next.equals("DE")) {
                                        while (deExecutor.getQueue().size() > 200) {
                                            log.info("Waiting for Queue of DE to drain.."
                                                    + deExecutor.getQueue().size() + ", Current: " + productIds
                                                    + ", region: " + region);
                                            try {
                                                Thread.sleep(1000);
                                            } catch (Exception e) {

                                            }
                                        }
                                        deExecutor.submit(aggr);
                                        synchronized (deSubmitted) {
                                            deSubmitted++;
                                        }
                                    } else if (next.equals("IT")) {
                                        while (itExecutor.getQueue().size() > 100) {
                                            log.info("Waiting for Queue of IT to drain.."
                                                    + itExecutor.getQueue().size() + " Current: " + productIds
                                                    + ", region: " + region);
                                            try {
                                                Thread.sleep(1000);
                                            } catch (Exception e) {

                                            }
                                        }
                                        itExecutor.submit(aggr);
                                        synchronized (itSubmitted) {
                                            itSubmitted++;
                                        }

                                    } else if (next.equals("CA")) {
                                        while (caExecutor.getQueue().size() > 500) {
                                            log.info("Waiting for Queue of CA to drain.."
                                                    + caExecutor.getQueue().size() + " Current: " + productIds
                                                    + ", region: " + region);
                                            try {
                                                Thread.sleep(1000);
                                            } catch (Exception e) {

                                            }
                                        }
                                        caExecutor.submit(aggr);
                                        synchronized (caSubmitted) {
                                            caSubmitted++;
                                        }
                                    }
                                } else {
                                    log.info("Unable to find details for the productIds = " + leftOver);
                                    //new ProductDetailDAO().addProductUnavailable(leftOver);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error processing details " + productIds + " for " + region, e);
            }
        }
    }

}
