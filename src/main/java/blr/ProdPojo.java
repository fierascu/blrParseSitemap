package blr;

import static blr.Utils.*;

public class ProdPojo {
    private String id;
    private String siteName;
    private String title;
    private String description;
    private String keywords;
    private String image;
    private String brand;
    private String price;
    private String productLink;
    private String sku;
    private String shortDesc;
    private String cats;
    private String properties;
    private String bulkDesc;
    private String bulkPrice;
    private String originalLink;

    public ProdPojo(String id) {
        this.id = id;
    }

    public ProdPojo() {
        id = getTimestamp().replace("_", "");
    }

    public static String getHeader() {
        return String.join(SEPARATOR, "id", "siteName", "title", "description", "keywords", "image", "brand", "price", "productLink", "sku", "shortDesc", "cats", "properties", "bulkDesc", "bulkPrice", "originalLink");
    }

    public String getBulkDesc() {
        return bulkDesc;
    }

    public void setBulkDesc(String bulkDesc) {
        this.bulkDesc = bulkDesc;
    }

    public String getBulkPrice() {
        return bulkPrice;
    }

    public void setBulkPrice(String bulkPrice) {
        this.bulkPrice = bulkPrice;
    }

    @Override
    public String toString() {
        return "ProdPojo{" +
                "id='" + id + '\'' +
                ", siteName='" + siteName + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", image='" + image + '\'' +
                ", brand='" + brand + '\'' +
                ", price='" + price + '\'' +
                ", productLink='" + productLink + '\'' +
                ", sku='" + sku + '\'' +
                ", shortDesc='" + shortDesc + '\'' +
                ", cats='" + cats + '\'' +
                ", properties='" + properties + '\'' +
                ", bulkDesc='" + bulkDesc + '\'' +
                ", bulkPrice='" + bulkPrice + '\'' +
                ", originalLink='" + originalLink + '\'' +
                '}';
    }

    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public static ProdPojo newInstance(ProdPojo oldProd, String title, String description, String keywords, String cats, String properties, String bulkDesc) {
        // use cleanDesc for text that arives from translating
        return new ProdPojo(
                oldProd.getId(),
                oldProd.getSiteName(),
                cleanDesc(title),
                trim(cleanDesc(description),3000),
                cleanDesc(keywords),
                oldProd.getImage(),
                oldProd.getBrand(),
                oldProd.getPrice(),
                oldProd.getProductLink(),
                oldProd.getSku(),
                trim(cleanDesc(description),3000), //shortDesc,
                cleanDesc(cats),
                cleanDesc(properties),
                cleanDesc(bulkDesc),
                oldProd.getBulkPrice(),
                oldProd.getOriginalLink()
        );
    }

    public ProdPojo(String id, String siteName, String title, String description, String keywords, String image, String brand, String price, String productLink, String sku, String shortDesc, String cats, String properties, String bulkDesc, String bulkPrice, String originalLink) {
        this.id = id;
        this.siteName = siteName;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.image = image;
        this.brand = brand;
        this.price = price;
        this.productLink = productLink;
        this.sku = sku;
        this.shortDesc = shortDesc;
        this.cats = cats;
        this.properties = properties;
        this.bulkDesc = bulkDesc;
        this.bulkPrice = bulkPrice;
        this.originalLink = originalLink;
    }

    public String getRow() {
        return String.join(SEPARATOR, id, siteName, title, description, keywords, image, brand, price, productLink, sku, shortDesc, cats, properties, bulkDesc, bulkPrice, originalLink);
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getSiteName() {

        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = cleanDesc(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = cleanDesc(description);
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = cleanDesc(keywords);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductLink() {
        return productLink;
    }

    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = cleanDesc(shortDesc);
    }

    public String getCats() {
        return cats;
    }

    public void setCats(String cats) {
        this.cats = cats;
    }
}
