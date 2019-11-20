package com.far.virtualmenu.Adapters.Models;

public class ColorModel {
    boolean selected;
    String hexColor;
    public  ColorModel(String hexColor, boolean selected){
        this.hexColor = hexColor;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }
}
