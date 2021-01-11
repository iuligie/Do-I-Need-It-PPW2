package emanuelg.ppw2.doineedit.util;

import android.app.Application;

import emanuelg.ppw2.doineedit.model.Product;

public class ProductApi extends Application {

    private String username;
    private String userId;
    private static ProductApi instance;


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
}
