package com.far.virtualmenu.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.virtualmenu.Controllers.AttributesController;
import com.far.virtualmenu.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class Attributes {
    private String CODE, DESCRIPTION;
    private int ORDEN;
    private boolean ENABLED;
    private @ServerTimestamp
    Date DATE;
    private @ServerTimestamp
    Date MDATE;

    public Attributes(){

    }
    public Attributes(String code, String description, int order, boolean enabled){
        this.CODE = code; this.DESCRIPTION = description; this.ORDEN = order;this.ENABLED = enabled;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(AttributesController.CODE, CODE);
        map.put(AttributesController.DESCRIPTION, DESCRIPTION);
        map.put(AttributesController.ORDER, ORDEN);
        map.put(AttributesController.ENABLED, ENABLED);
        map.put(AttributesController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(AttributesController.MDATE,  (MDATE == null)? FieldValue.serverTimestamp():MDATE);
        return map;

    }
    public Attributes(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(AttributesController.CODE));
        this.DESCRIPTION = c.getString(c.getColumnIndex(AttributesController.DESCRIPTION));
        this.ORDEN = c.getInt(c.getColumnIndex(AttributesController.ORDER));
        this.ENABLED = c.getString(c.getColumnIndex(AttributesController.ORDER)).equals("1");
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(AttributesController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(AttributesController.MDATE)));
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
