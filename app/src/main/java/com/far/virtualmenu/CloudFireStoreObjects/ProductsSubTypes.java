package com.far.virtualmenu.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;


@IgnoreExtraProperties
    public class ProductsSubTypes {
        private String CODE,CODETYPE, DESCRIPTION, HEXCOLOR1, HEXCOLOR2;
        private int ORDEN;
        private boolean ENABLED;
        private @ServerTimestamp
        Date DATE, MDATE;
        public ProductsSubTypes(){

        }

        public ProductsSubTypes(String code, String codeType,String description,String hex1, String hex2, int order, boolean enabled){
            this.CODE = code; this.CODETYPE = codeType; this.DESCRIPTION = description; this.ORDEN = order;this.ENABLED = enabled;
            this.HEXCOLOR1 = hex1; this.HEXCOLOR2 = hex2;
        }
        public HashMap<String, Object> toMap(){
            HashMap<String, Object> map = new HashMap<>();
            map.put(ProductsSubTypesController.CODE, CODE);
            map.put(ProductsSubTypesController.CODETYPE, CODETYPE);
            map.put(ProductsSubTypesController.DESCRIPTION, DESCRIPTION);
            map.put(ProductsSubTypesController.HEXCOLOR1, HEXCOLOR1);
            map.put(ProductsSubTypesController.HEXCOLOR2, HEXCOLOR2);
            map.put(ProductsSubTypesController.ORDER, ORDEN);
            map.put(ProductsSubTypesController.ENABLED, ENABLED);
            map.put(ProductsSubTypesController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
            map.put(ProductsSubTypesController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

            return map;

        }
        public ProductsSubTypes(Cursor c){
            this.CODE = c.getString(c.getColumnIndex(ProductsSubTypesController.CODE));
            this.CODETYPE = c.getString(c.getColumnIndex(ProductsSubTypesController.CODETYPE));
            this.DESCRIPTION = c.getString(c.getColumnIndex(ProductsSubTypesController.DESCRIPTION));
            this.HEXCOLOR1 = c.getString(c.getColumnIndex(ProductsSubTypesController.HEXCOLOR1));
            this.HEXCOLOR2 =  c.getString(c.getColumnIndex(ProductsSubTypesController.HEXCOLOR2));
            this.ORDEN = c.getInt(c.getColumnIndex(ProductsSubTypesController.ORDER));
            this.ENABLED = c.getString(c.getColumnIndex(ProductsSubTypesController.ENABLED)).equals("1");
            this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsSubTypesController.DATE)));
            this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsSubTypesController.MDATE)));
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

    public String getHEXCOLOR1() {
        return HEXCOLOR1;
    }

    public void setHEXCOLOR1(String HEXCOLOR1) {
        this.HEXCOLOR1 = HEXCOLOR1;
    }

    public String getHEXCOLOR2() {
        return HEXCOLOR2;
    }

    public void setHEXCOLOR2(String HEXCOLOR2) {
        this.HEXCOLOR2 = HEXCOLOR2;
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

    public String getCODETYPE() {
        return CODETYPE;
    }

    public void setCODETYPE(String CODETYPE) {
        this.CODETYPE = CODETYPE;
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
