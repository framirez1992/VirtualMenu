package com.far.virtualmenu.CloudFireStoreObjects;

import android.database.Cursor;


import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Utils.Funciones;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

@IgnoreExtraProperties
public class Products {
    private String CODE, DESCRIPTION,MENUDESCRIPTION, TYPE, SUBTYPE, PREPTIME;
    private boolean ENABLED, COMBO;
    private @ServerTimestamp
    Date DATE, MDATE;
    public Products(){

    }
    public Products(String code, String description,String menuDescription,  String type, String subType,String prepTime, boolean enabled,  boolean combo){
    this.CODE = code; this.DESCRIPTION = description; this.MENUDESCRIPTION = menuDescription; this.TYPE = type;
    this.SUBTYPE = subType;this.PREPTIME = prepTime; this.ENABLED = enabled; this.COMBO = combo;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(ProductsController.CODE,CODE);
        map.put(ProductsController.DESCRIPTION,DESCRIPTION);
        map.put(ProductsController.MENUDESCRIPTION,MENUDESCRIPTION);
        map.put(ProductsController.TYPE,TYPE );
        map.put(ProductsController.SUBTYPE, SUBTYPE);
        map.put(ProductsController.PREPTIME, PREPTIME);
        map.put(ProductsController.ENABLED,ENABLED );
        map.put(ProductsController.COMBO,COMBO );
        map.put(ProductsController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(ProductsController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

        return map;
    }

    public Products(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(ProductsController.CODE));
        this.DESCRIPTION = c.getString(c.getColumnIndex(ProductsController.DESCRIPTION));
        this.MENUDESCRIPTION = c.getString(c.getColumnIndex(ProductsController.MENUDESCRIPTION));
        this.TYPE = c.getString(c.getColumnIndex(ProductsController.TYPE));
        this.SUBTYPE = c.getString(c.getColumnIndex(ProductsController.SUBTYPE));
        this.PREPTIME = c.getString(c.getColumnIndex(ProductsController.PREPTIME));
        this.ENABLED = c.getString(c.getColumnIndex(ProductsController.ENABLED)).equals("1");
        this.COMBO = c.getString(c.getColumnIndex(ProductsController.COMBO)).equals("1");
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsController.MDATE)));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsController.DATE)));
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getMENUDESCRIPTION() {
        return MENUDESCRIPTION;
    }

    public void setMENUDESCRIPTION(String MENUDESCRIPTION) {
        this.MENUDESCRIPTION = MENUDESCRIPTION;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getSUBTYPE() {
        return SUBTYPE;
    }

    public void setSUBTYPE(String SUBTYPE) {
        this.SUBTYPE = SUBTYPE;
    }

    public String getPREPTIME() {
        return PREPTIME;
    }

    public void setPREPTIME(String PREPTIME) {
        this.PREPTIME = PREPTIME;
    }

    public boolean isCOMBO() {
        return COMBO;
    }

    public void setCOMBO(boolean COMBO) {
        this.COMBO = COMBO;
    }

    public boolean isENABLED() {
        return ENABLED;
    }

    public void setENABLED(boolean ENABLED) {
        this.ENABLED = ENABLED;
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
