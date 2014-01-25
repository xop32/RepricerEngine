package kissmydisc.repricer.model;

import kissmydisc.repricer.engine.ProductCondition;

public class Product {
    public Product(String sku, String asin, ProductCondition condition, String region) {
        super();
        this.sku = sku;
        this.asin = asin;
        this.condition = condition;
        this.region = region;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public ProductCondition getCondition() {
        return condition;
    }

    public void setCondition(ProductCondition condition) {
        this.condition = condition;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    private String sku;
    private String asin;
    private ProductCondition condition;
    private String region;

}
