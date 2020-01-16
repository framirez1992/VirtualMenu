package com.far.virtualmenu;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.far.virtualmenu.Adapters.Models.UserDeviceModel;
import com.far.virtualmenu.Adapters.UserDevicesSelectableAdapter;
import com.far.virtualmenu.CloudFireStoreObjects.DownloadRequest;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.UsersDevices;
import com.far.virtualmenu.Controllers.DownloadRequestController;
import com.far.virtualmenu.Controllers.LicenseController;
import com.far.virtualmenu.Controllers.UsersDevicesController;
import com.far.virtualmenu.Globales.Tablas;
import com.far.virtualmenu.Utils.CODES;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class AdminLicenseDevicesActualization extends AppCompatActivity implements ListableActivity {

    Licenses licenses;
    FirebaseFirestore fs;

    ArrayList<UsersDevices> usersDevices;
    RecyclerView rvList;
    LinearLayout llSave;
    CheckBox cbAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_devices_actualization);

        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_ADMIN_LICENSE) ){
            finish();
            return;
        }
        fs = FirebaseFirestore.getInstance();
        licenses = (Licenses) getIntent().getSerializableExtra(CODES.EXTRA_ADMIN_LICENSE);

        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(AdminLicenseDevicesActualization.this));
        llSave = findViewById(R.id.llSave);
        cbAll = findViewById(R.id.cbAll);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                ArrayList<UserDeviceModel> objects = ((UserDevicesSelectableAdapter)rvList.getAdapter()).getSelected();
                if(objects.size() == 0){
                    llSave.setEnabled(true);
                    return;
                }

                ArrayList<DownloadRequest> list = new ArrayList<>();
                for(UserDeviceModel udm: objects){
                    list.add(new DownloadRequest(udm.getCodeDevice(), udm.getCodeDevice(), ""));
                }

                WriteBatch lote = fs.batch();
                for(DownloadRequest dr: list){
                    lote.set(fs.collection(Tablas.generalUsers).document(licenses.getCODE())
                            .collection(Tablas.generalUsersDownloadRequest).document(dr.getCode()), dr.toMap());
                }
                lote.commit().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        failure(e.getMessage());
                    }
                });

                fs.collection(Tablas.generalUsers).document(licenses.getCODE())
                        .collection(Tablas.generalUsersDownloadRequest).whereEqualTo(DownloadRequestController.CODE, list.get(0).getCode()).
                        get().
                        addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {

                                if(querySnapshot!= null && querySnapshot.size() > 0){
                                    Toast.makeText(AdminLicenseDevicesActualization.this, "Enviado", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    failure("Error enviando. Intente nuevamente");
                                }
                            }
                        }).
                        addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                failure(e.getMessage());
                            }
                        });



            }
        });

        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((UserDevicesSelectableAdapter)rvList.getAdapter()).selectAll(isChecked);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpListeners();
    }


    public void refreshList(){

        findViewById(R.id.pb).setVisibility(View.VISIBLE);
        rvList.setAdapter(new UserDevicesSelectableAdapter(AdminLicenseDevicesActualization.this,AdminLicenseDevicesActualization.this, getUserDeviceModels()));
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();
        findViewById(R.id.pb).setVisibility(View.GONE);
    }

    @Override
    public void onClick(Object obj) {

    }

    public void failure(String msg){
        Toast.makeText(AdminLicenseDevicesActualization.this, msg, Toast.LENGTH_LONG).show();
    }



    public void setUpListeners(){

        fs.collection(Tablas.generalUsers).document(licenses.getCODE())
                .collection(Tablas.generalUsersUsersDevices)
                .addSnapshotListener(AdminLicenseDevicesActualization.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        usersDevices = new ArrayList<>();
                        for (DocumentSnapshot ds : querySnapshot) {
                            UsersDevices t = ds.toObject(UsersDevices.class);
                            usersDevices.add(t);
                        }
                        refreshList();

                    }
                });
    }

    public ArrayList<UserDeviceModel> getUserDeviceModels(){
        ArrayList<UserDeviceModel> list = new ArrayList<>();
        for(UsersDevices ud : usersDevices){
            list.add(new UserDeviceModel(ud.getCODE(), ud.getCODEUSER(),
                    ud.getCODEUSER(), ud.getCODEDEVICE()));
        }

        return list;

    }
}
