package com.far.virtualmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.far.virtualmenu.Adapters.MenuTypeRowAdapter;
import com.far.virtualmenu.Adapters.Models.MenuTypeModel;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.MenuType;
import com.far.virtualmenu.CloudFireStoreObjects.UserControl;
import com.far.virtualmenu.Controllers.MenuTypeController;
import com.far.virtualmenu.Controllers.UserControlController;
import com.far.virtualmenu.Globales.Tablas;
import com.far.virtualmenu.Utils.CODES;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminLicenseMenuSelection extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    FirebaseFirestore fs;
    Licenses license;
    LinearLayout llSave;
    ArrayList<MenuType> menuTypes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_license_menu_selection);

        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_ADMIN_LICENSE) ){
            finish();
            return;
        }
        fs = FirebaseFirestore.getInstance();
        license = (Licenses) getIntent().getSerializableExtra(CODES.EXTRA_ADMIN_LICENSE);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpListeners();
    }


    public void setUpListeners(){

       searchMenuType();
    }


    public void init(){
        llSave = findViewById(R.id.llSave);
        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(AdminLicenseMenuSelection.this));

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuTypeModel model = ((MenuTypeRowAdapter)rvList.getAdapter()).getSelected();
                if(model == null){
                    Toast.makeText(AdminLicenseMenuSelection.this, "Seleccione un tipo de menu", Toast.LENGTH_SHORT).show();
                   return;
                }

                MenuType uc = new MenuType(model.getCode(), model.getType(), model.getOrientation(), model.getLayout());
                fs.collection(Tablas.generalUsers).document(license.getCODE())
                        .collection(Tablas.generalUsersMenuType).document(uc.getCODE()).set(uc.toMap());
                searchMenuType();

            }
        });
    }

    public void searchMenuType(){
        fs.collection(Tablas.generalUsers).document(license.getCODE())
                .collection(Tablas.generalUsersMenuType)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                menuTypes = new ArrayList<>();
                if(querySnapshot != null && querySnapshot.size()>0){
                    for(DocumentSnapshot ds: querySnapshot){
                        menuTypes.add(ds.toObject(MenuType.class));
                    }
                }
                refreshList();
            }
        });
    }

    public void refreshList(){
        findViewById(R.id.pb).setVisibility(View.VISIBLE);
        rvList.setAdapter(new MenuTypeRowAdapter(AdminLicenseMenuSelection.this,AdminLicenseMenuSelection.this, getList()));
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();
        findViewById(R.id.pb).setVisibility(View.GONE);
    }

    @Override
    public void onClick(Object obj) {

    }

    private ArrayList<MenuTypeModel> getList(){
        String key = "";
        if(menuTypes.size()>0){
            for(MenuType mt: menuTypes){
                if(mt.getTYPE() == CODES.CODE_MENUTYPE_LIST_DETAIL_FRAGMENTS && mt.getLAYOUT().equals("")){
                    key = "1";
                }else if(mt.getTYPE() == CODES.CODE_MENUTYPE_GRID_FRAGMENT && mt.getLAYOUT().equals(CODES.CODE_MENUTYPE_LAYOUT_ROW1)){
                    key = "2";
                }else if(mt.getTYPE() == CODES.CODE_MENUTYPE_GRID_FRAGMENT && mt.getLAYOUT().equals(CODES.CODE_MENUTYPE_LAYOUT_ROW2)){
                    key = "3";
                }
            }
        }

        ArrayList<MenuTypeModel> data= new ArrayList();
        //int type,int orientation, String layout, String title, String description, boolean selected
        data.add(new MenuTypeModel(CODES.CODE_MENUTYPE_LIST_DETAIL_FRAGMENTS, CODES.CODE_MENUTYPE_ORIENTATION_LANDSCAPE, "", "Carta", ((key.equals("1"))?"(Activo)":"")+"Muestra los items en una lista y el detalle con imagenes en carrusel", key.equals("1")));
        data.add(new MenuTypeModel(CODES.CODE_MENUTYPE_GRID_FRAGMENT, CODES.CODE_MENUTYPE_ORIENTATION_PORTRAIT, CODES.CODE_MENUTYPE_LAYOUT_ROW1, "Grid", ((key.equals("2"))?"(Activo)":"")+"Muestra los items en un Grid", key.equals("2")));
        data.add(new MenuTypeModel(CODES.CODE_MENUTYPE_GRID_FRAGMENT, CODES.CODE_MENUTYPE_ORIENTATION_PORTRAIT,  CODES.CODE_MENUTYPE_LAYOUT_ROW2, "Line", ((key.equals("3"))?"(Activo)":"")+"Muestra los items en una lista vertical.", key.equals("3")));
        return  data;
    }
}
