package com.far.virtualmenu.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.MenuType;
import com.far.virtualmenu.DataBase.DB;
import com.far.virtualmenu.Globales.Tablas;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MenuTypeController {
    public static final String TABLE_NAME = "MENUTYPE";
    public static String CODE = "code",TYPE ="type", ORIENTATION = "orientation",LAYOUT =  "layout";
    public static String[]columns = new String[]{CODE, TYPE, ORIENTATION, LAYOUT};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+TYPE+" NUMERIC,"+ORIENTATION+" NUMERIC, "+LAYOUT+" TEXT)";

    FirebaseFirestore db;
    Context context;
    private static MenuTypeController instance;
    private MenuTypeController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static MenuTypeController getInstance(Context context){
        if(instance == null){
            instance = new MenuTypeController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersMenuType);
        return reference;
    }

    public long insert(MenuType mt){
        ContentValues cv = new ContentValues();
        cv.put(CODE, mt.getCODE());
        cv.put(TYPE, mt.getTYPE());
        cv.put(ORIENTATION, mt.getORIENTATION());
        cv.put(LAYOUT, mt.getLAYOUT());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(MenuType mt, String where, String[]whereArgs){
        ContentValues cv = new ContentValues();
        cv.put(CODE, mt.getCODE());
        cv.put(TYPE, mt.getTYPE());
        cv.put(ORIENTATION, mt.getORIENTATION());
        cv.put(LAYOUT, mt.getLAYOUT());


        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, whereArgs);
        return result;
    }

    public long delete(String where, String[]whereArgs){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, whereArgs);
        return result;
    }

    public MenuType getMenuType(){
        ArrayList<MenuType> pts = getMenuTypes(null, null, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

    public ArrayList<MenuType> getMenuTypes(String where, String[]args, String orderBy){
        ArrayList<MenuType> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new MenuType(c));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
