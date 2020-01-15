package com.far.virtualmenu.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.ProductImage;
import com.far.virtualmenu.DataBase.DB;
import com.far.virtualmenu.Globales.Tablas;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class ProductsImagesController {
    public static final String TABLE_NAME ="PRODUCTSIMAGES";
    public static  String CODE = "code",CODEPRODUCT = "codeproduct", URL = "url",ORDER = "orden", DATE = "date", MDATE="mdate";
    public static String[] columns = new String[]{CODE, CODEPRODUCT,URL,ORDER,  DATE, MDATE};

    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+CODEPRODUCT+" TEXT, "+URL+" TEXT,"+ORDER+" INTEGER, "+DATE+" TEXT, "+MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;
    StorageReference mStorageRef;
    static ProductsImagesController instance;

    private ProductsImagesController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();//Base de datos
        mStorageRef = FirebaseStorage.getInstance().getReference();//Archivos
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


    public void sendToFireBase(ProductImage pm, OnFailureListener failureListener){
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(pm.getCODE()), pm.toMap());
            lote.commit().addOnFailureListener(failureListener);

    }

    public void searchProductsImages(String codeProduct, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){


            getReferenceFireStore().
                    whereEqualTo(CODEPRODUCT, codeProduct).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);

    }

    public void searchProductImage(String code, OnSuccessListener<QuerySnapshot> success,  OnFailureListener failure){


        getReferenceFireStore().
                whereEqualTo(CODE, code).
                get().
                addOnSuccessListener(success).
                addOnFailureListener(failure);

    }

    public void deleteFromFireBase(ProductImage pi,OnFailureListener failureListener){
            WriteBatch lote = db.batch();
            lote.delete(getReferenceFireStore().document(pi.getCODE()));
            lote.commit().addOnFailureListener(failureListener);
    }

    public long insert(ProductImage p){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE() );
        cv.put(CODEPRODUCT,p.getCODEPRODUCT());
        cv.put(URL, p.getURL());
        cv.put(ORDER, p.getORDEN());
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
        cv.put(ORDER, p.getORDEN());
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
        ArrayList<ProductImage> pi = getProductsImages(where, new String[]{codeProduct}, ORDER+" ASC, "+MDATE+" DESC");
        return pi;
    }



    public void searchChanges(boolean all, OnSuccessListener<QuerySnapshot> success,  OnFailureListener failure){

        Date mdate = all?null: DB.getLastMDateSaved(context, TABLE_NAME);
        if(mdate != null){
            getReferenceFireStore().
                    whereGreaterThan(MDATE, mdate).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                    get().
                    addOnSuccessListener(success).
                    addOnFailureListener(failure);
        }else{//TODOS
            getReferenceFireStore().
                    get().
                    addOnSuccessListener(success).
                    addOnFailureListener(failure);
        }

    }

    public void consumeQuerySnapshot(boolean clear, QuerySnapshot querySnapshot){
        if(clear){
            delete(null, null);
        }
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                ProductImage obj = doc.toObject(ProductImage.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }


    public ArrayList<DocumentReference> getReferences(String field, String value){
        ArrayList<DocumentReference> references = new ArrayList<>();
        ArrayList<ProductImage> objs = getProductsImages(field+" = ? ", new String[]{value}, null);
        if(objs != null){
            for(ProductImage c: objs){
                references.add(getReferenceFireStore().document(c.getCODE()));
            }
        }
        return references;
    }


    public void deleteFromStorage(ProductImage productImage,OnSuccessListener successListener, OnFailureListener failureListener){
        StorageReference storageReference = mStorageRef.getStorage().getReferenceFromUrl(productImage.getURL());
        storageReference.delete().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }
    public void deleteFromStorage(String url,OnSuccessListener successListener,  OnFailureListener failureListener){
        StorageReference storageReference = mStorageRef.getStorage().getReferenceFromUrl(url);
        storageReference.delete().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

}
