package emanuelg.ppw2.doineedit.model;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Product {

    private String title;
    private String price;
    private String imageUrl;
    private String userId;
    private Timestamp timeAdded;
    private String itemId;

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    private boolean owned;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //empty constructor for firestore // must have

    public Product() {


    }

    public Product(String title, String price, String imageUrl, String userId, Timestamp timeAdded, String itemId, boolean owned) {
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timeAdded = timeAdded;
        this.itemId = itemId;
        this.owned=owned;
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

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Serializable toMap() {
        Map<String,String> map = new HashMap<>();
        map.put("title",getTitle());
        map.put("price",getPrice());
        map.put("image",getImageUrl());

        return (Serializable) map;
    }
}
