package com.far.virtualmenu.Model;

import java.util.ArrayList;

public class ProductModel {
    String code, description;
    boolean enabled;
    ArrayList<String> images;

    public ProductModel(){

    }

    public ProductModel(String code, String description, boolean enabled){
        this.code = code;
        this.description = description;
        this.enabled = enabled;
        images = new ArrayList<>();
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

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

}
