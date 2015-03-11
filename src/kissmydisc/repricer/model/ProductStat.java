package kissmydisc.repricer.model;

public class ProductStat {
    private String productId;
    private int salesRank = -1;
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public int getSalesRank() {
        return salesRank;
    }
    public void setSalesRank(int salesRank) {
        this.salesRank = salesRank;
    }
}
