package emanuelg.ppw2.doineedit.util;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import emanuelg.ppw2.doineedit.PostProductActivity;
import emanuelg.ppw2.doineedit.model.Product;

public class ProductApi extends Application {

    private String username;
    private String userId;
    private List<Product> productList;
    private String currentItemPos;

    public String getCurrentItemPos() {
        return currentItemPos;
    }

    public void setCurrentItemPos(String currentItemPos) {
        this.currentItemPos = currentItemPos;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    private static ProductApi instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static ProductApi getInstance(){
        if (instance == null)
            instance = new ProductApi();

            return instance;

    }

    public ProductApi(){}


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void deleteItem(String itemId)
    {
        db.collection("Reflection").document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DELETE-LOG", "DocumentSnapshot successfully deleted!");
                    //result=true;
                })
                .addOnFailureListener(e -> {
                    Log.w("DELETE-LOG", "Error deleting document", e);
                    //result=false;
                });
    }

    public void editItem(Product item) {


    }

}
