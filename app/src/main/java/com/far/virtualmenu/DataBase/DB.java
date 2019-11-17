package com.far.virtualmenu.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.far.virtualmenu.Controllers.CompanyController;
import com.far.virtualmenu.Controllers.DevicesController;
import com.far.virtualmenu.Controllers.LicenseController;
import com.far.virtualmenu.Controllers.ProductsControlController;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Globales.Tablas;
import com.far.virtualmenu.Utils.Funciones;

import java.util.Date;

public class DB extends SQLiteOpenHelper {
    private static DB instance;
    public static DB getInstance(Context c){
    if(instance == null){
        instance = new DB(c, Tablas.DB_NAME,null,1);
    }
    return instance;
    }

    private DB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.beginTransaction();
            db.execSQL(LicenseController.QUERY_CREATE);
            //db.execSQL(CombosController.QUERY_CREATE);
            db.execSQL(CompanyController.QUERY_CREATE);
            db.execSQL(DevicesController.QUERY_CREATE);
            //db.execSQL(MeasureUnitsController.QUERY_CREATE);
           // db.execSQL(PriceListController.QUERY_CREATE);
            db.execSQL(ProductsController.QUERY_CREATE);
            db.execSQL(ProductsTypesController.QUERY_CREATE);
            db.execSQL(ProductsSubTypesController.QUERY_CREATE);
            //db.execSQL(ProductsMeasureController.QUERY_CREATE);
            db.execSQL(ProductsControlController.QUERY_CREATE);
            //db.execSQL(UserControlController.QUERY_CREATE);


            db.setTransactionSuccessful();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createStructure(){

    }

    public static String getWhereFormat(String[] campos){
        String result ="";
        for(int i = 0; i< campos.length; i++){
            result+=(i == 0)?campos[i]+" = ? ":","+campos[i]+" = ?";
        }
        return result;
    }
    public boolean hasDependencies(String table, String field, String code){
        boolean resutl= false;
        String sql ="SELECT "+field+" from "+table+" WHERE "+field+" = ? ";
        Cursor c = getReadableDatabase().rawQuery(sql, new String[]{code});
        if(c.moveToFirst()){
            resutl = true;
        }c.close();
        return resutl;
    }

    /**
     * obtiene el ultimo MDATE guardada en la base de datos local en la tabla especificada.
     * @return
     */
    public static Date getLastMDateSaved(Context context, String table){
        Date date = null;
        String sql = "SELECT mdate as MDATE " +
                "FROM "+table+" " +
                "ORDER BY mdate DESC " +
                "LIMIT 1 ";
        Cursor c = getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
            date = Funciones.parseStringToDate(c.getString(c.getColumnIndex("MDATE")));
        }c.close();
        return date;
    }
}
