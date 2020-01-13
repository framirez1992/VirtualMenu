package com.far.virtualmenu.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;


@IgnoreExtraProperties
public class ProductsTypes {
    private String CODE, DESCRIPTION;
    private int ORDEN;
    private boolean ENABLED;
    private @ServerTimestamp
    Date DATE;
    private @ServerTimestamp
    Date MDATE;

    public ProductsTypes(){

    }
    public ProductsTypes(String code, String description, int order, boolean enabled){
        this.CODE = code; this.DESCRIPTION = description; this.ORDEN = order;this.ENABLED = enabled;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(ProductsTypesController.CODE, CODE);
        map.put(ProductsTypesController.DESCRIPTION, DESCRIPTION);
        map.put(ProductsTypesController.ORDER, ORDEN);
        map.put(ProductsTypesController.ENABLED, ENABLED);
        map.put(ProductsTypesController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(ProductsTypesController.MDATE,  (MDATE == null)? FieldValue.serverTimestamp():MDATE);
        return map;

    }
    public ProductsTypes(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(ProductsTypesController.CODE));
        this.DESCRIPTION = c.getString(c.getColumnIndex(ProductsTypesController.DESCRIPTION));
        this.ORDEN = c.getInt(c.getColumnIndex(ProductsTypesController.ORDER));
        this.ENABLED = c.getString(c.getColumnIndex(ProductsTypesController.ENABLED)).equals("1");
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsTypesController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsTypesController.MDATE)));
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

    public int getORDEN() {
        return ORDEN;
    }

    public void setORDEN(int ORDEN) {
        this.ORDEN = ORDEN;
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
