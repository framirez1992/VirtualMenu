package com.far.virtualmenu.Adapters.Models;

public class TitleDetailRowModel {
        String id, title, detail;
        boolean inServer, enabled;
    public TitleDetailRowModel(String id, String text,String detail,boolean enabled,  boolean inServer){
        this.id = id;
        this.title = text;
        this.detail =detail;
        this.enabled = enabled;
        this.inServer = inServer;
    }

        public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public boolean isInServer(){
        return inServer;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
