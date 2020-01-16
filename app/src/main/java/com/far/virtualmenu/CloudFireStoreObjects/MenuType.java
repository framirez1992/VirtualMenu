package com.far.virtualmenu.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.virtualmenu.Controllers.MenuTypeController;

import java.util.HashMap;

public class MenuType {
    private String CODE, LAYOUT;
    private int TYPE,ORIENTATION;

    public MenuType(){

    }
    public MenuType(String code, int type, int orientation, String layout){
        this.CODE = code; this.TYPE = type;this.ORIENTATION = orientation;
        this.LAYOUT = layout;
    }

    public MenuType(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(MenuTypeController.CODE));
        this.TYPE = c.getInt(c.getColumnIndex(MenuTypeController.TYPE));
        this.ORIENTATION = c.getInt(c.getColumnIndex(MenuTypeController.ORIENTATION));
        this.LAYOUT = c.getString(c.getColumnIndex(MenuTypeController.LAYOUT));

    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(MenuTypeController.CODE,CODE);
        map.put(MenuTypeController.TYPE,TYPE);
        map.put(MenuTypeController.ORIENTATION,ORIENTATION);
        map.put(MenuTypeController.LAYOUT,LAYOUT);
        return map;

    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getLAYOUT() {
        return LAYOUT;
    }

    public void setLAYOUT(String LAYOUT) {
        this.LAYOUT = LAYOUT;
    }

    public int getTYPE() {
        return TYPE;
    }

    public void setTYPE(int TYPE) {
        this.TYPE = TYPE;
    }

    public int getORIENTATION() {
        return ORIENTATION;
    }

    public void setORIENTATION(int ORIENTATION) {
        this.ORIENTATION = ORIENTATION;
    }
}
