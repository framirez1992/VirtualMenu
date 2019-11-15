package com.far.virtualmenu.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.far.virtualmenu.CloudFireStoreObjects.Company;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.DataBase.DB;
import com.far.virtualmenu.Generic.KV;
import com.far.virtualmenu.Globales.Tablas;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;



public class CompanyController {
    public static final String TABLE_NAME ="COMPANY";
    public static  String CODE = "code", NAME = "name" ,
            RNC = "rnc",PHONE = "phone", PHONE2="phone2",
            ADDRESS="address", ADDRESS2="address2", DATE = "date", MDATE = "mdate";
    String[] columns = new String[]{CODE, NAME, RNC, PHONE, PHONE2, ADDRESS, ADDRESS2, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+NAME+" TEXT, "+RNC+" TEXT, "+PHONE+" TEXT, "+PHONE2+" TEXT, "+ADDRESS+" TEXT," +
            ADDRESS2+" TEXT, "+DATE+" TEXT, "+MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;
    private static CompanyController instance;
    private CompanyController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static CompanyController getInstance(Context context){
        if(instance == null){
            instance = new CompanyController(context);
        }
        return instance;
    }
    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersCompany);
        return reference;
    }

    public long insert(Company c){
        ContentValues cv = new ContentValues();
        cv.put(CODE,c.getCODE() );
        cv.put(NAME,c.getNAME());
        cv.put(RNC, c.getRNC());
        cv.put(PHONE,c.getPHONE() );
        cv.put(PHONE2,c.getPHONE2() );
        cv.put(ADDRESS,c.getADDRESS2() );
        cv.put(ADDRESS2,c.getADDRESS2() );
        cv.put(DATE, Funciones.getFormatedDate((Date) c.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate((Date) c.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Company c, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,c.getCODE() );
        cv.put(NAME,c.getNAME());
        cv.put(RNC, c.getRNC());
        cv.put(PHONE,c.getPHONE() );
        cv.put(PHONE2,c.getPHONE2() );
        cv.put(ADDRESS,c.getADDRESS2() );
        cv.put(ADDRESS2,c.getADDRESS2() );
        cv.put(MDATE, Funciones.getFormatedDate((Date)c.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                     OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> combos = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersCompany).get();
            combos.addOnSuccessListener(onSuccessListener);
            combos.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getAllDataFromFireBase(String key, OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> company =getReferenceFireStore().get();
            company.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Company object = dc.getDocument().toObject(Company.class);
                            delete(null, null);
                            insert(object);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Company> getCompanys(String where, String[]args, String orderBy){
        ArrayList<Company> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new Company(c));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public void fillSpnCompany(Spinner spn, boolean addTodos){
        ArrayList<Company> result = getCompanys(null, null, NAME);
        ArrayList<KV> spnList = new ArrayList<>();
        if(addTodos){
            spnList.add(new KV("0", "TODOS"));
        }
        for(Company u : result){
            spnList.add(new KV(u.getCODE(), u.getNAME()));
        }
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }


}
