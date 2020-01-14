package com.far.virtualmenu.Adapters.Models;

public class MenuTypeModel {
    String key,value,  title, description;
    boolean selected;

    public MenuTypeModel(String key, String value, String title, String description, boolean selected){
        this.key = key; this.value = value; this.title = title; this.description = description;
        this.selected = selected;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
