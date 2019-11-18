package com.far.virtualmenu.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.ProductImage;
import com.far.virtualmenu.DataBase.DB;
import com.far.virtualmenu.Globales.Tablas;
import com.far.virtualmenu.Utils.Funciones;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProductsImagesController {
    public static final String TABLE_NAME ="PRODUCTSIMAGES";
    public static  String CODE = "code",CODEPRODUCT = "codeproduct", URL = "url", DATE = "date", MDATE="mdate";
    public static String[] columns = new String[]{CODE, CODEPRODUCT,URL,  DATE, MDATE};

    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+CODEPRODUCT+" TEXT, "+URL+" TEXT,  "+DATE+" TEXT, "+MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;
    static ProductsImagesController instance;

    private ProductsImagesController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static ProductsImagesController getInstance(Context context){
        if(instance == null){
            instance = new ProductsImagesController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersProductsImages);
        return reference;
    }


    public long insert(ProductImage p){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE() );
        cv.put(CODEPRODUCT,p.getCODEPRODUCT());
        cv.put(URL, p.getURL());
        cv.put(DATE, Funciones.getFormatedDate(p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(ProductImage p, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE() );
        cv.put(CODEPRODUCT,p.getCODEPRODUCT());
        cv.put(URL, p.getURL());
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<ProductImage> getProductsImages(String where, String[]args, String orderBy){
        ArrayList<ProductImage> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new ProductImage(c));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public ProductImage getProductImageByCode(String code){
        String where = CODE+" = ?";
        ArrayList<ProductImage> pts = getProductsImages(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

    public ArrayList<ProductImage> getProductImageByCodeProduct(String codeProduct){
        String where = CODEPRODUCT+" = ?";
        ArrayList<ProductImage> pi = getProductsImages(where, new String[]{codeProduct}, null);
        return pi;
    }

}
