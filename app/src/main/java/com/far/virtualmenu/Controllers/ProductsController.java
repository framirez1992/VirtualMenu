package com.far.virtualmenu.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.far.virtualmenu.Adapters.Models.ProductRowModel;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.ProductImage;
import com.far.virtualmenu.CloudFireStoreObjects.Products;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsControl;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsMeasure;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsSubTypes;
import com.far.virtualmenu.DataBase.CloudFireStoreDB;
import com.far.virtualmenu.DataBase.DB;
import com.far.virtualmenu.Generic.KV2;
import com.far.virtualmenu.Globales.Tablas;
import com.far.virtualmenu.Model.ItemMenuDetailModel;
import com.far.virtualmenu.Model.ItemModel;
import com.far.virtualmenu.Model.PriceModel;
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

import io.grpc.internal.FailingClientStream;

public class ProductsController {

    public static final String TABLE_NAME ="PRODUCTS";
    public static  String CODE = "code", DESCRIPTION = "description",MENUDESCRIPTION = "menudescription",
            TYPE = "type",SUBTYPE = "subtype",PREPTIME="preptime", ENABLED = "enabled",  COMBO = "combo", DATE = "date", MDATE="mdate";
    public static String[] columns = new String[]{CODE, DESCRIPTION,MENUDESCRIPTION, TYPE, SUBTYPE,PREPTIME, ENABLED, COMBO, DATE, MDATE};

    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+DESCRIPTION+" TEXT,"+MENUDESCRIPTION+" TEXT,  "+TYPE+" TEXT, "+SUBTYPE+" TEXT,"+PREPTIME+" TEXT,  "+ENABLED+" NUMERIC, "+
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
        cv.put(MENUDESCRIPTION,p.getMENUDESCRIPTION());
        cv.put(TYPE, p.getTYPE());
        cv.put(SUBTYPE,p.getSUBTYPE() );
        cv.put(PREPTIME,p.getPREPTIME() );
        cv.put(ENABLED,p.isENABLED() );
        cv.put(COMBO,p.isCOMBO() );
        cv.put(DATE, Funciones.getFormatedDate(p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Products p){
        return  update(p, CODE+" = ?", new String[]{p.getCODE()});
    }

    public long update(Products p, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE() );
        cv.put(DESCRIPTION,p.getDESCRIPTION());
        cv.put(MENUDESCRIPTION,p.getMENUDESCRIPTION());
        cv.put(TYPE, p.getTYPE());
        cv.put(SUBTYPE,p.getSUBTYPE());
        cv.put(PREPTIME,p.getPREPTIME() );
        cv.put(ENABLED,p.isENABLED() );
        cv.put(COMBO,p.isCOMBO() );
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(Products p){
        return delete(CODE+" = ?", new String[]{p.getCODE()});
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

            String sql = "SELECT p."+CODE+" as CODE, p."+DESCRIPTION+" AS DESCRIPTION,p."+ENABLED+" as ENABLED,  pt."+ProductsTypesController.CODE+" as PTCODE, pt."+ProductsTypesController.DESCRIPTION+" as PTDESCRIPTION, pst."+ProductsSubTypesController.CODE+" AS PSTCODE, " +
                    "pst."+ProductsSubTypesController.DESCRIPTION+" AS PSTDESCRIPTION, p."+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+" p " +
                    "LEFT JOIN "+ProductsTypesController.TABLE_NAME+" pt ON pt."+ProductsTypesController.CODE+" = p."+TYPE+" "+
                    "LEFT JOIN "+ProductsSubTypesController.TABLE_NAME+" pst ON pst."+ProductsSubTypesController.CODE+" = "+SUBTYPE+" "+
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                result.add(new ProductRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("DESCRIPTION")),
                        c.getString(c.getColumnIndex("PTCODE")) ,
                        c.getString(c.getColumnIndex("PTDESCRIPTION")),
                        c.getString(c.getColumnIndex("PSTCODE")),
                        c.getString(c.getColumnIndex("PSTDESCRIPTION")),
                        c.getString(c.getColumnIndex("ENABLED")).equals("1"),
                        c.getString(c.getColumnIndex("MDATE")) != null));
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

    public void sendToFireBase(Products product, OnFailureListener failureListener){
        sendToFireBase(product, null, failureListener);

    }


    public void sendToFireBase(Products product, ArrayList<ProductsMeasure> newMeasures, OnFailureListener failureListener){
            WriteBatch lote = db.batch();

            //if(product.getMDATE() == null){
                lote.set(getReferenceFireStore().document(product.getCODE()), product.toMap());
            //}else{
                //lote.update(getReferenceFireStore().document(product.getCODE()), product.toMap());
            //}

            String notIn=" NOT IN ('1'";
            if (newMeasures != null && !newMeasures.isEmpty()){

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
                        //where = ProductsMeasureController.CODE+" = ?";
                        //ProductsMeasureController.getInstance(context).update(pm,where, new String[]{pm.getCODE()});
                    }else{//INSERTAR
                        lote.set(ProductsMeasureController.getInstance(context).getReferenceFireStore().document(pm.getCODE()), pm.toMap());
                        //ProductsMeasureController.getInstance(context).insert(pm);
                    }
                    notIn+=",'"+pm.getCODE()+"'";
                }


            }

            notIn+=")";
            String where = ProductsMeasureController.CODEPRODUCT+" = ? AND "+ProductsMeasureController.ENABLED+" = ? AND  "+ProductsMeasureController.CODE+notIn;
            ArrayList<ProductsMeasure> toDisable = ProductsMeasureController.getInstance(context).getProductsMeasure(where, new String[]{product.getCODE(), "1"});
            for(ProductsMeasure pm: toDisable){
                pm.setENABLED(false);
                pm.setMDATE(null);
                //where = ProductsMeasureController.CODE+" = ?";
                //ProductsMeasureController.getInstance(context).update(pm,where, new String[]{pm.getCODE()});

                lote.update(ProductsMeasureController.getInstance(context).getReferenceFireStore().document(pm.getCODE()), pm.toMap());
            }

            lote.commit().addOnFailureListener(failureListener);

    }

    public void searchProductFromFireBase(String code, OnSuccessListener<QuerySnapshot> success,  OnFailureListener failure){

        getReferenceFireStore().
                whereEqualTo(CODE, code).
                get().addOnSuccessListener(success).
                addOnFailureListener(failure);

    }

    public void deleteFromFireBase(Products product, OnFailureListener failureListener){
            boolean hasimages=false;
            WriteBatch lote = db.batch();
            lote.delete(getReferenceFireStore().document(product.getCODE()));
            for(KV2 data: getDependencies(product.getCODE())){
                if(data.getCode().equals(ProductsImagesController.TABLE_NAME)){
                    hasimages = true;
                }
                for(DocumentReference dr : CloudFireStoreDB.getInstance(context, null, null).getDocumentsReferencesByTableName(data)){
                    lote.delete(dr);
                }
            }

            lote.commit().addOnFailureListener(failureListener);

            if(hasimages){
                for(ProductImage pi : ProductsImagesController.getInstance(context).getProductImageByCodeProduct(product.getCODE())){
                    ProductsImagesController.getInstance(context).deleteFromStorage(pi.getURL(),null, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
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
        if(DB.getInstance(context).hasDependencies(ProductsImagesController.TABLE_NAME,ProductsImagesController.CODEPRODUCT,code))
            tables.add(new KV2(ProductsImagesController.TABLE_NAME,ProductsImagesController.CODEPRODUCT,code));


        return tables;
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
                Products obj = doc.toObject(Products.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }

    public ArrayList<ItemModel> getItemModelMenu(){
        ArrayList<ItemModel> list  = new ArrayList<>();

        String sql = "SELECT pt."+ProductsTypesController.CODE+" as FCODE,pst."+ProductsSubTypesController.CODE+" as GCODE,  pst."+ProductsSubTypesController.DESCRIPTION+" as GDESCRIPTION, pst."+ProductsSubTypesController.HEXCOLOR1+" as GHEX1, " +
                "p."+ProductsController.CODE+" as PCODE, p."+ ProductsController.DESCRIPTION+" as PDESCRIPTION, p."+ProductsController.MENUDESCRIPTION+" as  PMENUDESCRIPTION,p."+ProductsController.PREPTIME+" as PREPTIME, " +
                "pt."+ ProductsTypesController.ORDER+ ", pst."+ProductsSubTypesController.ORDER+" "+
                "FROM "+ProductsController.TABLE_NAME+" p "+
                "INNER JOIN "+ProductsTypesController.TABLE_NAME+" pt on pt."+ProductsTypesController.CODE+" = p."+ProductsController.TYPE+" AND pt." +ProductsTypesController.ENABLED+" = '1'  "+
                "INNER JOIN "+ProductsSubTypesController.TABLE_NAME+" pst on pst."+ProductsSubTypesController.CODE+" = p."+ProductsController.SUBTYPE+" AND pst." +ProductsSubTypesController.ENABLED+" = '1' " +
                "WHERE p."+ProductsController.ENABLED+" = '1' " +
                "ORDER BY pt."+ ProductsTypesController.ORDER+", pst."+ProductsSubTypesController.ORDER+", p."+DESCRIPTION;
    try {
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        ItemModel lastHeader = ItemModel.initHeader("", "", "");
        while (c.moveToNext()) {
            String codeHeader = c.getString(c.getColumnIndex("GCODE"));
            if (!lastHeader.getCode().equals(codeHeader)) {
                lastHeader = null;
                lastHeader = ItemModel.initHeader(codeHeader, c.getString(c.getColumnIndex("GDESCRIPTION")), c.getString(c.getColumnIndex("GHEX1")));
                list.add(lastHeader);
            }

            String codeProduct = c.getString(c.getColumnIndex("PCODE"));
            ArrayList<String> urls = new ArrayList<>();
            for (ProductImage pi : ProductsImagesController.getInstance(context).getProductImageByCodeProduct(codeProduct)) {
                urls.add(pi.getURL());
            }

            ArrayList<PriceModel> prices = ProductsMeasureController.getInstance(context).getPriceModelsByCodeProduct(codeProduct);
            list.add(ItemModel.initDetail(codeProduct, c.getString(c.getColumnIndex("PDESCRIPTION")),c.getString(c.getColumnIndex("PMENUDESCRIPTION")),
                    c.getString(c.getColumnIndex("PREPTIME")), urls, prices));
        }
        c.close();
    }catch (Exception e){
        e.printStackTrace();
    }

        return list;

    }


    public ArrayList<ItemMenuDetailModel> getItemMenuDetailModels(){
        ArrayList<ItemMenuDetailModel> list = new ArrayList<>();

        String sql ="SELECT p."+CODE+" as CODE,  pt."+ProductsTypesController.ORDER+",pst."+ProductsSubTypesController.ORDER+", " +
                "p."+DESCRIPTION+" as DESCRIPTION, p."+MENUDESCRIPTION+" as MENUDESCRIPTION, MIN(pm."+ProductsMeasureController.PRICE+") as PRICE  " +
                "FROM "+TABLE_NAME+" p " +
                "INNER JOIN "+ProductsMeasureController.TABLE_NAME+" pm on pm."+ProductsMeasureController.CODEPRODUCT+" = p."+CODE+" AND pm."+ProductsMeasureController.ENABLED+" = '1' " +
                "INNER JOIN "+ProductsTypesController.TABLE_NAME+" pt on pt."+ProductsTypesController.CODE+" = p."+TYPE+" " +
                "INNER JOIN "+ProductsSubTypesController.TABLE_NAME+" pst on pst."+ProductsSubTypesController.CODE+" = p."+SUBTYPE+" "+
                "WHERE p."+ENABLED+" = ? " +
                "GROUP BY p."+CODE+", pt."+ProductsTypesController.ORDER+",pst."+ProductsSubTypesController.ORDER+",p."+DESCRIPTION+", p."+MENUDESCRIPTION+" "+
                "ORDER BY pt."+ProductsTypesController.ORDER+" ASC, pst."+ProductsSubTypesController.ORDER+" ASC, p."+DESCRIPTION+" ASC ";


        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{"1"});
        while (c.moveToNext()) {
            String codeProduct = c.getString(c.getColumnIndex("CODE"));
            String url = null;
            for (ProductImage pi : ProductsImagesController.getInstance(context).getProductImageByCodeProduct(codeProduct)) {
                url =pi.getURL();
                break;
            }
            if(url != null){
                list.add(new ItemMenuDetailModel(c.getString(c.getColumnIndex("DESCRIPTION")),
                        c.getString(c.getColumnIndex("MENUDESCRIPTION")),
                        c.getDouble(c.getColumnIndex("PRICE")), url));
            }
        }c.close();


            return list;
    }
}
