package com.far.virtualmenu.Adapters.Models;

import com.far.virtualmenu.Controllers.MenuTypeController;

public class MenuTypeModel {
    String code,layout,  title, description;
    int type,orientation;
    boolean selected;

    public MenuTypeModel(int type,int orientation, String layout, String title, String description, boolean selected){
        this.code = MenuTypeController.TABLE_NAME; this.type = type;this.orientation = orientation; this.layout = layout;
        this.title = title; this.description = description; this.selected = selected;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
