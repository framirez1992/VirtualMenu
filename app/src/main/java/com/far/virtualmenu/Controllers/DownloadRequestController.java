package com.far.virtualmenu.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.far.virtualmenu.CloudFireStoreObjects.DownloadRequest;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.DataBase.DB;
import com.far.virtualmenu.Globales.Tablas;
import com.far.virtualmenu.Utils.CODES;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class DownloadRequestController {
    public static final  String TABLE_NAME = "DOWNLOADREQUEST";
    public static String CODE = "code", CODEDEVICE = "description", TABLECODES = "tablecodes";
    public static String[]colums = new String[]{CODE, CODEDEVICE, TABLECODES};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+CODEDEVICE+" TEXT,"+TABLECODES+" TEXT)";

    Context context;
    FirebaseFirestore db;
    private static DownloadRequestController instance;
    private DownloadRequestController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
    }
    public static DownloadRequestController getInstance(Context context){
        if(instance == null){
            instance = new DownloadRequestController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersDownloadRequest);
        return reference;
    }

    public long insert(DownloadRequest dr){
        ContentValues cv = new ContentValues();
        cv.put(CODE,dr.getCode());
        cv.put(CODEDEVICE,dr.getCodedevice());
        cv.put(TABLECODES, dr.getTablecodes());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(DownloadRequest dr){
        String where = CODE+" = ?";
        return update(dr, where, new String[]{dr.getCode()});
    }
    public long update(DownloadRequest dr, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,dr.getCode());
        cv.put(CODEDEVICE,dr.getCodedevice());
        cv.put(TABLECODES, dr.getTablecodes());
        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(DownloadRequest dr){
        return delete(CODE+" = ?", new String[]{dr.getCode()});
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public boolean existDownloadRequest(){

        boolean result = false;
       String sql = "SELECT  "+CODEDEVICE+" as CODEDEVICE " +
               "FROM "+TABLE_NAME+" " +
               "GROUP BY "+CODEDEVICE;
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            result = c.moveToFirst();
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }



    public void sendToFireBase(ArrayList<DownloadRequest> list, OnFailureListener failureListener){
            WriteBatch lote = db.batch();
            for(DownloadRequest dr: list){
                lote.set(getReferenceFireStore().document(dr.getCode()), dr.toMap());
            }
            lote.commit().addOnFailureListener(failureListener);

    }


    public void searchDownloadRequest(String code, OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failure){

            getReferenceFireStore().
                    whereEqualTo(CODE, code).
                    get().
                    addOnSuccessListener(successListener).
                    addOnFailureListener(failure);

    }


}
