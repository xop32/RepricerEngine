package kissmydisc.repricer.model;

public class ProductDetail {

    public ProductDetail() {
    }

    public ProductDetail(String productId, String artist, String author, String title) {
        // TODO Auto-generated constructor stub
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public Float getWeight() {
        return weight;
    }
    
    public void setWeight(Float weight) {
        this.weight = weight;
    }
    
    public String getProductType() {
        return this.productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }

    private String productId;
    private String artist;
    private String author;
    private String title;
    private Float weight = -1F;
    private String productType;
}
