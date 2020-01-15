package com.far.virtualmenu.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.far.virtualmenu.Adapters.Models.UserDeviceModel;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.UsersDevices;
import com.far.virtualmenu.DataBase.DB;
import com.far.virtualmenu.Globales.Tablas;
import com.far.virtualmenu.Utils.CODES;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class UsersDevicesController {
    public static final  String TABLE_NAME = "USERDEVICES";
    public static String CODE = "code", CODEUSER= "codeuser", CODEDEVICE = "codedevice",  DATE = "date", MDATE = "mdate";
    public static String[]colums = new String[]{CODE, CODEUSER, CODEDEVICE, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+CODEUSER+" TEXT,  "+CODEDEVICE+" TEXT,"+DATE+" TEXT, "+MDATE+" TEXT)";



    FirebaseFirestore db;
    Context context;
    private static UsersDevicesController instance;
    private UsersDevicesController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static UsersDevicesController getInstance(Context context){
        if(instance == null){
            instance = new UsersDevicesController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(Licenses l){
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersUsersDevices);
        return reference;
    }

    public long insert(UsersDevices dr){
        ContentValues cv = new ContentValues();
        cv.put(CODE,dr.getCODE());
        cv.put(CODEUSER,dr.getCODEUSER());
        cv.put(CODEDEVICE,dr.getCODEDEVICE());
        cv.put(DATE, Funciones.getFormatedDate(dr.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(dr.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(UsersDevices dr){
        String where = CODE+" = ?";
        return update(dr, where, new String[]{dr.getCODE()});
    }
    public long update(UsersDevices dr, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,dr.getCODE());
        cv.put(CODEUSER,dr.getCODEUSER());
        cv.put(CODEDEVICE,dr.getCODEDEVICE());
        cv.put(DATE, Funciones.getFormatedDate(dr.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(dr.getMDATE()));
        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(UsersDevices dr){
        return delete(CODE+" = ?", new String[]{dr.getCODE()});
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }


    public void getQueryusersDevices(Licenses l, String codeUser, String deviceID, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failute){
        getReferenceFireStore(l).
                whereEqualTo(CODEUSER, codeUser).
                whereEqualTo(CODEDEVICE, deviceID).get().
                addOnSuccessListener(success).
                addOnCompleteListener(complete).
                addOnFailureListener(failute);

    }

    public ArrayList<UserDeviceModel> getUDM(){
        ArrayList<UserDeviceModel> list = new ArrayList<>();

        String sql = "SELECT ud."+CODE+" AS CODE, ud."+CODEUSER+" AS CODEUSER, ud."+CODEDEVICE+" AS CODEDEVICE, u."+UsersController.USERNAME+" as USERNAME " +
                "FROM "+TABLE_NAME+" ud " +
                "INNER JOIN "+UsersController.TABLE_NAME+" u  ON ud."+CODEUSER+" = u."+UsersController.CODE+" " +
                "WHERE "+UsersController.SYSTEMCODE+" = ?";

        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{CODES.USER_SYSTEM_CODE_USER});
        while(c.moveToNext()){
            //String code, String codeUser, String userName, String codeDevice
            list.add(new UserDeviceModel(c.getString(c.getColumnIndex("CODE")), c.getString(c.getColumnIndex("CODEUSER")),
                     c.getString(c.getColumnIndex("USERNAME")), c.getString(c.getColumnIndex("CODEDEVICE"))));
        }c.close();

        return list;
    }

    public Task<QuerySnapshot> getUserDeviceFromFireBase(Licenses license, String codeUser, String codeDevice, OnSuccessListener onSuccessListener, OnFailureListener failureListener){
        // Create a query against the collection.
        Query query = getReferenceFireStore(license).whereEqualTo(CODEUSER, codeUser).whereEqualTo(CODEDEVICE, codeDevice);
        // retrieve  query results asynchronously using query.get()
        return query.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(failureListener);
    }


}
