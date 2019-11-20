package com.far.virtualmenu.Adapters.Models;

public class ProductSubTypeRowModel {
    String id, description, order, hex1, hex2;
    boolean inServer, enabled;
    public ProductSubTypeRowModel(String id, String description,String hex1, String hex2, String order,boolean enabled,  boolean inServer){
        this.id = id;
        this.description = description;
        this.hex1 = hex1;
        this.hex2 = hex2;
        this.order =order;
        this.enabled = enabled;
        this.inServer = inServer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getHex1() {
        return hex1;
    }

    public void setHex1(String hex1) {
        this.hex1 = hex1;
    }

    public String getHex2() {
        return hex2;
    }

    public void setHex2(String hex2) {
        this.hex2 = hex2;
    }

    public boolean isInServer() {
        return inServer;
    }

    public void setInServer(boolean inServer) {
        this.inServer = inServer;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
