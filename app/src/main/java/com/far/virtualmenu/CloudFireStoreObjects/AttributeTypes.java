package com.far.virtualmenu.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.virtualmenu.Controllers.AttributeTypesController;
import com.far.virtualmenu.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class AttributeTypes {
    private String CODE,CODEATTRIBUTE, DESCRIPTION;
    private int ORDEN;
    private boolean ENABLED;
    private @ServerTimestamp
    Date DATE, MDATE;
    public AttributeTypes(){

    }
    public AttributeTypes(String code, String codeAttribute,String description, int order, boolean enabled){
        this.CODE = code; this.CODEATTRIBUTE = codeAttribute; this.DESCRIPTION = description; this.ORDEN = order;this.ENABLED = enabled;
    }
    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(AttributeTypesController.CODE, CODE);
        map.put(AttributeTypesController.CODEATTRIBUTE, CODEATTRIBUTE);
        map.put(AttributeTypesController.DESCRIPTION, DESCRIPTION);
        map.put(AttributeTypesController.ORDER, ORDEN);
        map.put(AttributeTypesController.ENABLED, ENABLED);
        map.put(AttributeTypesController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(AttributeTypesController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

        return map;

    }
    public AttributeTypes(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(AttributeTypesController.CODE));
        this.CODEATTRIBUTE = c.getString(c.getColumnIndex(AttributeTypesController.CODEATTRIBUTE));
        this.DESCRIPTION = c.getString(c.getColumnIndex(AttributeTypesController.DESCRIPTION));
        this.ORDEN = c.getInt(c.getColumnIndex(AttributeTypesController.ORDER));
        this.ENABLED = c.getString(c.getColumnIndex(AttributeTypesController.ENABLED)).equals("1");
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(AttributeTypesController.DATE)));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(AttributeTypesController.MDATE)));
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

    public String getCODEATTRIBUTE() {
        return CODEATTRIBUTE;
    }

    public void setCODEATTRIBUTE(String CODEATTRIBUTE) {
        this.CODEATTRIBUTE = CODEATTRIBUTE;
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