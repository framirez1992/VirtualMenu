package com.far.virtualmenu.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.far.virtualmenu.Adapters.Models.ProductRowModel;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.Products;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsMeasure;
import com.far.virtualmenu.DataBase.CloudFireStoreDB;
import com.far.virtualmenu.DataBase.DB;
import com.far.virtualmenu.Generic.KV2;
import com.far.virtualmenu.Globales.Tablas;
import com.far.virtualmenu.Model.ProductModel;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

public class ProductsController {

    public static final String TABLE_NAME ="PRODUCTS";
    public static  String CODE = "code", DESCRIPTION = "description",
            TYPE = "type",SUBTYPE = "subtype", ENABLED = "enabled",  COMBO = "combo", DATE = "date", MDATE="mdate";
    public static String[] columns = new String[]{CODE, DESCRIPTION,TYPE, SUBTYPE, ENABLED, COMBO, DATE, MDATE};

    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+DESCRIPTION+" TEXT, "+TYPE+" TEXT, "+SUBTYPE+" TEXT, "+ENABLED+" NUMERIC, "+
            COMBO+" BOOLEAN, "+DATE+" TEXT, "+MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;
    static ProductsController instance;

    private ProductsController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static ProductsController getInstance(Context context){
        if(instance == null){
            instance = new ProductsController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersProducts);
        return reference;
    }


    public long insert(Products p){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE() );
        cv.put(DESCRIPTION,p.getDESCRIPTION());
        cv.put(TYPE, p.getTYPE());
        cv.put(SUBTYPE,p.getSUBTYPE() );
        cv.put(ENABLED,p.isENABLED() );
        cv.put(COMBO,p.isCOMBO() );
        cv.put(DATE, Funciones.getFormatedDate(p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Products p, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE() );
        cv.put(DESCRIPTION,p.getDESCRIPTION());
        cv.put(TYPE, p.getTYPE());
        cv.put(SUBTYPE,p.getSUBTYPE());
        cv.put(ENABLED,p.isENABLED() );
        cv.put(COMBO,p.isCOMBO() );
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<Products> getProducts(String where, String[]args, String orderBy){
        ArrayList<Products> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new Products(c));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Products getProductByCode(String code){
        String where = CODE+" = ?";
        ArrayList<Products> pts = getProducts(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }



    public ArrayList<ProductRowModel> getProductsPRM(String where, String[] args, String campoOrder){
        ArrayList<ProductRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT p."+CODE+" as CODE, p."+DESCRIPTION+" AS DESCRIPTION, pt."+ProductsTypesController.CODE+" as PTCODE, pt."+ProductsTypesController.DESCRIPTION+" as PTDESCRIPTION, pst."+ProductsSubTypesController.CODE+" AS PSTCODE, pst."+ProductsSubTypesController.DESCRIPTION+" AS PSTDESCRIPTION, p."+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+" p " +
                    "LEFT JOIN "+ProductsTypesController.TABLE_NAME+" pt ON pt."+ProductsTypesController.CODE+" = p."+TYPE+" "+
                    "LEFT JOIN "+ProductsSubTypesController.TABLE_NAME+" pst ON pst."+ProductsSubTypesController.CODE+" = "+SUBTYPE+" "+
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                result.add(new ProductRowModel(c.getString(c.getColumnIndex("CODE")), c.getString(c.getColumnIndex("DESCRIPTION")),c.getString(c.getColumnIndex("PTCODE")) ,c.getString(c.getColumnIndex("PTDESCRIPTION")),c.getString(c.getColumnIndex("PSTCODE")),c.getString(c.getColumnIndex("PSTDESCRIPTION")),c.getString(c.getColumnIndex("MDATE")) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }



    public ArrayList<ProductModel> getProductsRM(String where, String[] args, String campoOrder){
        ArrayList<ProductModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT p."+CODE+" as CODE, p."+DESCRIPTION+" AS DESCRIPTION, pt."+ProductsTypesController.CODE+" as PTCODE, pt."+ProductsTypesController.DESCRIPTION+" as PTDESCRIPTION, pst."+ProductsSubTypesController.CODE+" AS PSTCODE, pst."+ProductsSubTypesController.DESCRIPTION+" AS PSTDESCRIPTION, p."+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+" p " +
                    "LEFT JOIN "+ProductsTypesController.TABLE_NAME+" pt ON pt."+ProductsTypesController.CODE+" = p."+TYPE+" "+
                    "LEFT JOIN "+ProductsSubTypesController.TABLE_NAME+" pst ON pst."+ProductsSubTypesController.CODE+" = "+SUBTYPE+" "+
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){//String code, String description, ArrayList<ProductImage> images, boolean enabled
                String codeProduct = c.getString(c.getColumnIndex("CODE"));
                result.add(new ProductModel(codeProduct,
                        c.getString(c.getColumnIndex("DESCRIPTION")),
                        ProductsImagesController.getInstance(context).getProductImageByCodeProduct(codeProduct),
                        true));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }


    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> products = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersProducts).get();
            products.addOnSuccessListener(onSuccessListener);
            products.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void getAllDataFromFireBase(String key, OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Products object = dc.getDocument().toObject(Products.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCODE()};
                            delete(where, args);
                            insert(object);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendToFireBase(Products product){
        sendToFireBase(product, null);

    }


    public void sendToFireBase(Products product, ArrayList<ProductsMeasure> newMeasures){
        try {
            WriteBatch lote = db.batch();

            if(product.getMDATE() == null){
                lote.set(getReferenceFireStore().document(product.getCODE()), product.toMap());
            }else{
                lote.update(getReferenceFireStore().document(product.getCODE()), product.toMap());
            }

            if (newMeasures != null && !newMeasures.isEmpty()){

                String notIn=" NOT IN ('1'";
                for(ProductsMeasure pm: newMeasures){
                    String where = ProductsMeasureController.CODEMEASURE+" = ? AND "+ProductsMeasureController.CODEPRODUCT+" = ?";
                    String[]args = new String[]{pm.getCODEMEASURE(), pm.getCODEPRODUCT()};
                    ArrayList<ProductsMeasure> existingPM = ProductsMeasureController.getInstance(context).getProductsMeasure(where, args);

                    if(existingPM.size() >0){//ACTUALIZAR
                        pm.setCODE(existingPM.get(0).getCODE());//sustituye el codigo nuevo por el existente en la base de datos
                        pm.setDATE(existingPM.get(0).getDATE());//permanecer la fecha de creacion.
                        pm.setMDATE(null);

                        //ENVIAR A FIRE BASE
                        lote.update(ProductsMeasureController.getInstance(context).getReferenceFireStore().document(pm.getCODE()), pm.toMap());

                        //ACTUALIZAR LOCAL
                        where = ProductsMeasureController.CODE+" = ?";
                        ProductsMeasureController.getInstance(context).update(pm,where, new String[]{pm.getCODE()});

                        notIn+=",'"+pm.getCODE()+"'";
                    }else{//INSERTAR
                        lote.set(ProductsMeasureController.getInstance(context).getReferenceFireStore().document(pm.getCODE()), pm.toMap());
                        ProductsMeasureController.getInstance(context).insert(pm);
                    }
                }

                notIn+=")";
                String where = ProductsMeasureController.CODEPRODUCT+" = ? AND "+ProductsMeasureController.ENABLED+" = ? AND  "+ProductsMeasureController.CODE+notIn;
                ArrayList<ProductsMeasure> toDisable = ProductsMeasureController.getInstance(context).getProductsMeasure(where, new String[]{product.getCODE(), "1"});
                for(ProductsMeasure pm: toDisable){
                    pm.setENABLED(false);
                    pm.setMDATE(null);
                    where = ProductsMeasureController.CODE+" = ?";
                    ProductsMeasureController.getInstance(context).update(pm,where, new String[]{pm.getCODE()});

                    lote.update(ProductsMeasureController.getInstance(context).getReferenceFireStore().document(pm.getCODE()), pm.toMap());
                }
            }

            lote.commit().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void deleteFromFireBase(Products product){
        try {
            WriteBatch lote = db.batch();
            lote.delete(getReferenceFireStore().document(product.getCODE()));
            for(KV2 data: getDependencies(product.getCODE())){
                for(DocumentReference dr : CloudFireStoreDB.getInstance(context, null, null).getDocumentsReferencesByTableName(data)){
                    lote.delete(dr);
                }
            }

            lote.commit().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * retorna un arrayList con todas las  dependencias en otras tablas (llave foranea)
     * @param code
     * @return
     */
    public ArrayList<KV2> getDependencies(String code){
        ArrayList<KV2> tables = new ArrayList<>();
        if(DB.getInstance(context).hasDependencies(ProductsControlController.TABLE_NAME,ProductsControlController.CODEPRODUCT,code))
            tables.add(new KV2(ProductsControlController.TABLE_NAME,ProductsControlController.CODEPRODUCT,code));
        if(DB.getInstance(context).hasDependencies(ProductsMeasureController.TABLE_NAME,ProductsMeasureController.CODEPRODUCT,code))
            tables.add(new KV2(ProductsMeasureController.TABLE_NAME,ProductsMeasureController.CODEPRODUCT,code));


        return tables;
        //priceList,productsControl, productsMeasure,combos
    }



    public void searchChanges(OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){

        Date mdate = DB.getLastMDateSaved(context, TABLE_NAME);
        if(mdate != null){
            getReferenceFireStore().
                    whereGreaterThan(MDATE, mdate).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);
        }else{//TODOS
            getReferenceFireStore().
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);
        }

    }

    public void consumeQuerySnapshot(QuerySnapshot querySnapshot){
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                Products obj = doc.toObject(Products.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }
}
