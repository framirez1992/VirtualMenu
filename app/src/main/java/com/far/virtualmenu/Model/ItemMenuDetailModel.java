package com.far.virtualmenu.Model;

import java.text.NumberFormat;
import java.util.Locale;

public class ItemMenuDetailModel {
    String title, description, url;
    double price;

    public ItemMenuDetailModel(String title, String description, double price, String url){
        this.title = title;
        this.description = description;
        this.price = price;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrice() {
        return  NumberFormat.getCurrencyInstance(new Locale("en", "US"))
                .format(price);
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
