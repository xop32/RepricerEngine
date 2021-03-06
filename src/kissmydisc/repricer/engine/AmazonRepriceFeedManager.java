package kissmydisc.repricer.engine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;

import kissmydisc.repricer.RepricerMainThreadPool;
import kissmydisc.repricer.dao.AmazonAccessor;
import kissmydisc.repricer.dao.FeedsDAO;
import kissmydisc.repricer.feeds.AmazonFeed;
import kissmydisc.repricer.feeds.PriceQuantityFeed;
import kissmydisc.repricer.model.AmazonSubmission;
import kissmydisc.repricer.utils.AppConfig;

public class AmazonRepriceFeedManager implements RepriceFeedManager {

    private static final String directory = "feeds/";

    static {
        new File(directory).mkdir();
    }

    private List<PriceQuantityFeed> feeds = new ArrayList<PriceQuantityFeed>();

    private static final int MAX_ITEMS = AppConfig.getInteger("MaxItemsPerAmazonFeed", 30000);

    private String region;

    private long id;

    private AmazonAccessor amazonAccessor;

    private int MAX_ITEMS_PER_REGION;

    private int MAX_ITEMS_TO_BUFFER_REGION;

    private boolean onlyBuffer = false;

    public AmazonRepriceFeedManager(final long id, final String region, final AmazonAccessor amazonAccessor,
            boolean onlyBuffer) {
        this.id = id;
        this.region = region;
        this.amazonAccessor = amazonAccessor;
        MAX_ITEMS_PER_REGION = AppConfig.getInteger(region + "_MaxItemsPerAmazonFeed", MAX_ITEMS);
        MAX_ITEMS_TO_BUFFER_REGION = Math.min(MAX_ITEMS_PER_REGION / 10, 1000);
    }

    private DigestOutputStream currentOutputStream;

    private String currentFeedFile;

    private int rowsWritten = 0;

    private int submitted = 0;

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(AmazonRepriceFeedManager.class);

    public synchronized void writeToFeedFile(AmazonFeed amzFeed) throws Exception {
        if (amzFeed instanceof PriceQuantityFeed) {
            PriceQuantityFeed feed = (PriceQuantityFeed) amzFeed;
            if (feed.getPrice() < 0 && feed.getQuantity() < 0) {
                // Some error in feed, drop it.
                return;
            }
            if (feeds.size() < MAX_ITEMS_TO_BUFFER_REGION) {
                feeds.add(feed);
            }
            if (feeds.size() == MAX_ITEMS_TO_BUFFER_REGION) {
                writeToFile();
            }
        }
    }

    public synchronized void flush() throws Exception {
        if (feeds.size() > 0) {
            writeToFile();
            byte[] md5 = currentOutputStream.getMessageDigest().digest();
            currentOutputStream.close();
            rowsWritten = 0;
            feeds = new ArrayList<PriceQuantityFeed>();
            currentOutputStream = null;
            submitToAmazon(currentFeedFile, md5);
        }
    }

    private synchronized void writeToFile() throws Exception {
        if (currentOutputStream == null) {
            currentFeedFile = directory + region + "-" + System.currentTimeMillis();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(currentFeedFile));
            currentOutputStream = new DigestOutputStream(bos, MessageDigest.getInstance("MD5"));
            currentOutputStream.write((PriceQuantityFeed.getHeader() + "\n").getBytes());
        }
        for (PriceQuantityFeed f : feeds) {
            String feedStr = f.toString();
            if (feedStr != null) {
                currentOutputStream.write((feedStr + "\n").getBytes());
            }
            rowsWritten++;
        }
        currentOutputStream.flush();
        feeds = new ArrayList<PriceQuantityFeed>();
        if (rowsWritten >= MAX_ITEMS_PER_REGION) {
            byte[] md5 = currentOutputStream.getMessageDigest().digest();
            currentOutputStream.close();
            rowsWritten = 0;
            currentOutputStream = null;
            submitToAmazon(currentFeedFile, md5);
        }
    }

    private static String getCharsetForRegion(String region) {
        if ("JP".equals(region)) {
            return "Shift_JIS";
        } else if ("CN".equals(region)) {
            return "UTF-8";
        } else {
            return "ISO8859-1";
        }
    }

    private void submitToAmazon(String feedFile, byte[] md5) {
        // Submit the feed to Amazon in the background.
        if (region.startsWith("N-") == false && !onlyBuffer) {
            try {
                String md5Str = new String(Base64.encodeBase64(md5), "UTF-8");
                if (log.isDebugEnabled()) {
                    log.debug("MD5 of the generated feed file: " + feedFile + " is " + md5Str);
                }
                RepricerMainThreadPool.getInstance().submit(new FeedSubmitter(feedFile, md5Str, region));
                submitted++;
            } catch (UnsupportedEncodingException e) {
                log.error("Unable to submit feeds to amazon", e);
            }
        }
    }

    private class FeedSubmitter implements Runnable {

        private String feedFile;
        private String md5;

        public FeedSubmitter(final String feedFile, final String md5, final String region) {
            this.feedFile = feedFile;
            this.md5 = md5;
        }

        @Override
        public void run() {
            try {
                log.info("Submitting " + feedFile + " to amazon.");
                String submissionId = amazonAccessor.sendFeed(feedFile, md5);
                new FeedsDAO().addNewFeedSubmission(id, feedFile, submissionId);
                log.info("Submitted " + feedFile + " to amazon, submission id: " + submissionId);
            } catch (Exception e) {
                log.error("Error processing the feed file", e);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(Charset.availableCharsets());
        System.out.println(Charset.isSupported("UTF8"));
        System.out.println(Charset.isSupported("Shift_JIS"));
        System.out.println(Charset.isSupported("ISO8859-1"));
    }

    @Override
    public List<AmazonSubmission> getSubmissions() {
        // TODO Auto-generated method stub
        return null;
    }
}
