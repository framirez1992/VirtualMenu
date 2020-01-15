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
import com.far.virtualmenu.CloudFireStoreObjects.UserControl;
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
    ArrayList<UserControl> userControls;
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
        //refreshList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpListeners();
    }


    public void setUpListeners(){
       /* fs.collection(Tablas.generalLicencias).document(license.getCODE())
                .collection(Tablas.generalUsersProductsControl)
                .addSnapshotListener(AdminLicenseMenuSelection.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        menuTypes = new ArrayList<>();
                        for (DocumentSnapshot ds : querySnapshot) {
                            Devices t = ds.toObject(Devices.class);
                            t.setDocumentReference(ds.getReference());
                            devices.add(t);
                        }
                        refreshList();

                    }
                });*/

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
                //String code, String target, String targetCode, String control, String value, boolean active
                UserControl uc = new UserControl(model.getKey(), "0", "0", "MENUTYPE", model.getValue(), true);
                fs.collection(Tablas.generalUsers).document(license.getCODE())
                        .collection(Tablas.generalUsersUserControl).document(uc.getCONTROL()).set(uc.toMap());
                searchMenuType();

            }
        });
    }

    public void searchMenuType(){
        fs.collection(Tablas.generalUsers).document(license.getCODE())
                .collection(Tablas.generalUsersUserControl).whereEqualTo(UserControlController.CONTROL, "MENUTYPE")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                userControls = new ArrayList<>();
                if(querySnapshot != null && querySnapshot.size()>0){
                    for(DocumentSnapshot ds: querySnapshot){
                        userControls.add(ds.toObject(UserControl.class));
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
        if(userControls.size()>0){
            key = userControls.get(0).getCODE();
        }

        ArrayList<MenuTypeModel> data= new ArrayList();
        data.add(new MenuTypeModel("1", "1", "Carta", ((key.equals("1"))?"(Activo)":"")+"Muestra los items en una lista y el detalle con imagenes en carrusel", key.equals("1")));
        data.add(new MenuTypeModel("2", "2", "Grid", ((key.equals("2"))?"(Activo)":"")+"Muestra los items en un Grid", key.equals("2")));
        return  data;
    }
}
