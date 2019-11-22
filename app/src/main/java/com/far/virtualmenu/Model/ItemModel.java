package com.far.virtualmenu.Model;

import java.util.ArrayList;

public class ItemModel {
    private static final String HEADER_TYPE ="h";
    private static final String DETAIL_TYPE="d";
    String code, type, hexBackground;
    String title, description,  time;
    ArrayList<String> urls;
    ArrayList<PriceModel> prices;

    public static ItemModel initHeader(String code, String title, String hexBackground){
        ItemModel lm = new ItemModel();
        lm.code = code;
        lm.type = HEADER_TYPE;
        lm.title = title;
        lm.hexBackground = hexBackground;
        return  lm;
    }

    public static ItemModel initDetail(String code, String title,String description, ArrayList<String> urls, double price){
        ItemModel lm = new ItemModel();
        lm.code = code;
        lm.type = DETAIL_TYPE;
        lm.title = title;
        lm.description = description;
        lm.hexBackground = "#FFFFFF";
        lm.urls = urls;
        lm.prices = new ArrayList<>();
        lm.prices.add(new PriceModel("", price));
        return  lm;
    }

    public static ItemModel initDetail(String code, String title,String description, ArrayList<String> urls, ArrayList<PriceModel> pm){
        ItemModel lm = new ItemModel();
        lm.code = code;
        lm.type = DETAIL_TYPE;
        lm.title = title;
        lm.description = description;
        lm.hexBackground = "#FFFFFF";
        lm.urls = urls;
        lm.prices = pm;
        return  lm;
    }


    public static ItemModel initDetail(String code, String title,String description, String url,double price){
        ItemModel lm = new ItemModel();
        lm.code = code;
        lm.type = DETAIL_TYPE;
        lm.title = title;
        lm.description = description;
        lm.hexBackground = "#FFFFFF";
        lm.urls = new ArrayList<>();
        lm.urls.add(url);
        lm.prices = new ArrayList<>();
        lm.prices.add(new PriceModel("", price));
        return  lm;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHexBackground() {
        return hexBackground;
    }

    public void setHexBackground(String hexBackground) {
        this.hexBackground = hexBackground;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public boolean isHeader(){
        return type.equals(HEADER_TYPE);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public ArrayList<PriceModel> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<PriceModel> prices) {
        this.prices = prices;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
