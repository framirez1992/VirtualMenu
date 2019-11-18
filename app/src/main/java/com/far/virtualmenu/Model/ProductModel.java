package com.far.virtualmenu.Model;

import com.far.virtualmenu.CloudFireStoreObjects.ProductImage;

import java.util.ArrayList;

public class ProductModel {
    String code, description;
    boolean enabled;
    ArrayList<ProductImage> images;

    public ProductModel(){

    }

    public ProductModel(String code, String description, ArrayList<ProductImage> images, boolean enabled){
        this.code = code;
        this.description = description;
        this.enabled = enabled;
        this.images = images;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ArrayList<ProductImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<ProductImage> images) {
        this.images = images;
    }

}
