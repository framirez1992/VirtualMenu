package com.far.virtualmenu.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.far.virtualmenu.Adapters.Models.TitleDetailRowModel;
import com.far.virtualmenu.CloudFireStoreObjects.Attributes;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.Attributes;
import com.far.virtualmenu.DataBase.DB;
import com.far.virtualmenu.Generic.KV;
import com.far.virtualmenu.Globales.Tablas;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

public class AttributesController {
    public static final  String TABLE_NAME = "ATTRIBUTES";
    public static String CODE = "code", DESCRIPTION = "description", ORDER = "orden",ENABLED = "enabled",  DATE="date", MDATE="mdate";
    public static String[]colums = new String[]{CODE, DESCRIPTION, ORDER,ENABLED, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+DESCRIPTION+" TEXT,"+ORDER+" INTEGER, "+ENABLED+" NUMERIC, "+DATE+" TEXT, "+MDATE+" TEXT)";

    Context context;
    FirebaseFirestore db;
    private static AttributesController instance;
    private AttributesController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
    }
    public static AttributesController getInstance(Context context){
        if(instance == null){
            instance = new AttributesController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersAttributes);
        return reference;
    }
    public long insert(Attributes pt){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pt.getCODE());
        cv.put(DESCRIPTION,pt.getDESCRIPTION());
        cv.put(ORDER, pt.getORDEN());
        cv.put(ENABLED, pt.isENABLED());
        cv.put(DATE, Funciones.getFormatedDate(pt.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(pt.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Attributes pt){
        String where = CODE+" = ?";
        return update(pt, where, new String[]{pt.getCODE()});
    }
    public long update(Attributes pt, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pt.getCODE() );
        cv.put(DESCRIPTION,pt.getDESCRIPTION());
        cv.put(ORDER, pt.getORDEN());
        cv.put(ENABLED, pt.isENABLED());
        cv.put(DATE, Funciones.getFormatedDate(pt.getMDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(pt.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(Attributes pt){
        return delete(CODE+" = ?", new String[]{pt.getCODE()});
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<Attributes> getAttributes(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<Attributes> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DESCRIPTION;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new Attributes(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public int getCount(String destiny){
        int result = 0;
        ArrayList<Attributes> pts = getAttributes(null, null, null);
        if(pts != null){
            result =  pts.size();
        }
        return result;
    }
    public Attributes getAtributeByCode(String code){
        ArrayList<Attributes> pts = getAttributes(new String[]{CODE}, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

    public int getNextOrden(){
        int result = 9999;
        /*String sql = "SELECT MAX("+ORDER+" + 1) " +
                "FROM "+TABLE_NAME;
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            if(c.moveToFirst()){
                result = c.getInt(0);
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }*/
        return result;
    }


/*    public ArrayList<SimpleRowModel> getAllProductTypesSRM(String where, String[] args){
        ArrayList<SimpleRowModel> result = new ArrayList<>();
        String orderBy = ORDER+" ASC, "+DESCRIPTION;
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, where, args, null, null, orderBy);
            while(c.moveToNext()){
                result.add(new SimpleRowModel(c.getString(c.getColumnIndex(CODE)), c.getString(c.getColumnIndex(DESCRIPTION)), c.getString(c.getColumnIndex(MDATE)) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }*/


    public ArrayList<TitleDetailRowModel> getAllAttributesTDRM(String where, String[] args){
        ArrayList<TitleDetailRowModel> result = new ArrayList<>();
        String orderBy = ORDER+" ASC, "+DESCRIPTION;
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, where, args, null, null, orderBy);
            while(c.moveToNext()){
                result.add(new TitleDetailRowModel(c.getString(c.getColumnIndex(CODE)),
                        c.getString(c.getColumnIndex(DESCRIPTION)),
                        c.getString(c.getColumnIndex(ORDER)),
                        c.getString(c.getColumnIndex(ENABLED)).equals("1"),
                        c.getString(c.getColumnIndex(MDATE)) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

    /**
     * Simple seleccion row model
     * @param where
     * @param args
     * @param campoOrder
     * @return
     */

    public void sendToFireBase(Attributes pt){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(pt.getCODE()), pt.toMap());
            lote.commit();
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public void deleteFromFireBase(Attributes pt){
        try {
            getReferenceFireStore().document(pt.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> attributes = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersAttributes).get();
            attributes.addOnSuccessListener(onSuccessListener);
            attributes.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void getAllDataFromFireBase(OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Attributes object = dc.getDocument().toObject(Attributes.class);
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




    public void fillSpinner(Spinner spn, boolean addTodos){
        String orderBy = AttributesController.ORDER+" ASC, "+AttributesController.DESCRIPTION;
        ArrayList<Attributes> list = getAttributes(null,null, orderBy);
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("0", "TODOS");
            data.add(obj);
        }
        for(Attributes pt : list){
            data.add(new KV(pt.getCODE(), pt.getDESCRIPTION()));
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }


    /**
     * retorna true si el codigo tiene dependencias en otras tablas (llave foranea)
     * @param code
     * @return
     */
    public String hasDependencies(String code){
        String msg = "";
        ArrayList<String> tables = new ArrayList<>();
        if(DB.getInstance(context).hasDependencies(AttributeTypesController.TABLE_NAME,AttributeTypesController.CODEATTRIBUTE,code))
            tables.add(AttributeTypesController.TABLE_NAME);

        for(String s: tables){
            msg+= s+"\n";
        }
        return msg;
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
                Attributes obj = doc.toObject(Attributes.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }
}
