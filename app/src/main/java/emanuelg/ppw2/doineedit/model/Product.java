package emanuelg.ppw2.doineedit.model;

import com.google.firebase.Timestamp;

public class Product {

    private String title;
    private String price;
    private String imageUrl;
    private String userId;
    private Timestamp timeAdded;
    private String userName;

    //empty constructor for firestore // must have

    public Product() {


    }

    public Product(String title, String price, String imageUrl, String userId, Timestamp timeAdded, String userName) {
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timeAdded = timeAdded;
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
