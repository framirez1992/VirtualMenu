package com.far.virtualmenu.CloudFireStoreObjects;

import com.far.virtualmenu.Controllers.DownloadRequestController;

import java.util.HashMap;

public class DownloadRequest {
    String code, codedevice, tablecodes;

    public DownloadRequest(){

    }

    public DownloadRequest(String code, String codedevice, String tablecodes){
        this.code = code; this.codedevice = codedevice; this.tablecodes = tablecodes;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(DownloadRequestController.CODE, code);
        map.put(DownloadRequestController.CODEDEVICE, codedevice);
        map.put(DownloadRequestController.TABLECODES, tablecodes);

        return map;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodedevice() {
        return codedevice;
    }

    public void setCodedevice(String codedevice) {
        this.codedevice = codedevice;
    }

    public String getTablecodes() {
        return tablecodes;
    }

    public void setTablecodes(String tablecodes) {
        this.tablecodes = tablecodes;
    }
}
