package com.far.virtualmenu.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.virtualmenu.Controllers.ProductsImagesController;
import com.far.virtualmenu.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class ProductImage {
    private String CODE, CODEPRODUCT,URL;
    private @ServerTimestamp
    Date DATE, MDATE;
    public ProductImage(){

    }
    public ProductImage(String code, String codeProduct, String url){
        this.CODE = code; this.CODEPRODUCT = codeProduct; this.URL = url;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(ProductsImagesController.CODE,CODE);
        map.put(ProductsImagesController.CODEPRODUCT,CODEPRODUCT);
        map.put(ProductsImagesController.URL,URL );
        map.put(ProductsImagesController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(ProductsImagesController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

        return map;
    }

    public ProductImage(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(ProductsImagesController.CODE));
        this.CODEPRODUCT = c.getString(c.getColumnIndex(ProductsImagesController.CODEPRODUCT));
        this.URL = c.getString(c.getColumnIndex(ProductsImagesController.URL));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsImagesController.MDATE)));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsImagesController.DATE)));
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getCODEPRODUCT() {
        return CODEPRODUCT;
    }

    public void setCODEPRODUCT(String CODEPRODUCT) {
        this.CODEPRODUCT = CODEPRODUCT;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public Date getDATE() {
        return DATE;
    }

    public void setDATE(Date DATE) {
        this.DATE = DATE;
    }

    public Date getMDATE() {
        return MDATE;
    }

    public void setMDATE(Date MDATE) {
        this.MDATE = MDATE;
    }
}
