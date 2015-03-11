package kissmydisc.repricer.utils;

public class PriceUtils {

    private static double round(float price) {
        double price1 = Math.round((double) price + 0.5F);
        return price1;
    }

    private static String truncateDecimal(String price) {
        int d = price.indexOf('.');
        if (d == -1) {
            price += ".00";
        } else {
            d++; // converting to 1 based.
            int i = Math.min(d + 2, price.length());
            price = price.substring(0, i);
            if (i - d == 1) {
                price += "0";
            }
        }
        return price;
    }

    public static String getPrice(float price, String region) {
        if (price > 0.0) {
            String priceInStr = price + "";
            if (region.equals("JP") || region.equals("JP-1")) {
                priceInStr = round(price) + "";
                int index = priceInStr.indexOf(".");
                if (index == -1) {
                    index = priceInStr.length();
                }
                priceInStr = priceInStr.substring(0, index);
            } else {
                priceInStr = truncateDecimal(priceInStr);
                if (region.equals("IT") || region.equals("ES") || region.equals("DE") || region.equals("FR")) {
                    priceInStr = priceInStr.replace('.', ',');
                }
            }
            return priceInStr;
        }
        return null;
    }
}
