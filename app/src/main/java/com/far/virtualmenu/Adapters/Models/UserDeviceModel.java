package com.far.virtualmenu.Adapters.Models;

public class UserDeviceModel {
    String code, codeUser,userName,  codeDevice;
    boolean selected;

    public UserDeviceModel(String code, String codeUser, String userName, String codeDevice){
        this.code = code; this.codeUser = codeUser; this.userName = userName; this.codeDevice = codeDevice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeUser() {
        return codeUser;
    }

    public void setCodeUser(String codeUser) {
        this.codeUser = codeUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCodeDevice() {
        return codeDevice;
    }

    public void setCodeDevice(String codeDevice) {
        this.codeDevice = codeDevice;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
