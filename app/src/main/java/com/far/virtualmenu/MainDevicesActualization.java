package com.far.virtualmenu;

import android.support.annotation.NonNull;
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
import com.far.virtualmenu.Controllers.UsersController;
import com.far.virtualmenu.Controllers.UsersDevicesController;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MainDevicesActualization extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    LinearLayout llSave;
    CheckBox cbAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_devices_actualization);

        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(MainDevicesActualization.this));
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

                DownloadRequestController.getInstance(MainDevicesActualization.this).sendToFireBase(list, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    failure(e.getMessage());
                    }
                });
                DownloadRequestController.getInstance(MainDevicesActualization.this).searchDownloadRequest(list.get(0).getCode(), new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        if(querySnapshot!= null && querySnapshot.size() > 0){
                            Toast.makeText(MainDevicesActualization.this, "Enviado", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            failure("Error enviando. Intente nuevamente");
                        }
                    }
                }, new OnFailureListener() {
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
        Licenses l = LicenseController.getInstance(MainDevicesActualization.this).getLicense();
        UsersDevicesController.getInstance(MainDevicesActualization.this).getReferenceFireStore(l).addSnapshotListener(MainDevicesActualization.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                UsersDevicesController.getInstance(MainDevicesActualization.this).delete(null, null);
                if(querySnapshot!= null){
                    for(DocumentSnapshot ds : querySnapshot){
                        UsersDevices ud = ds.toObject(UsersDevices.class);
                        UsersDevicesController.getInstance(MainDevicesActualization.this).insert(ud);
                    }
                }
                refreshList();

            }
        });


    }


    public void refreshList(){
        findViewById(R.id.pb).setVisibility(View.VISIBLE);
        rvList.setAdapter(new UserDevicesSelectableAdapter(MainDevicesActualization.this,MainDevicesActualization.this, UsersDevicesController.getInstance(MainDevicesActualization.this).getUDM()));
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();
        findViewById(R.id.pb).setVisibility(View.GONE);
    }

    @Override
    public void onClick(Object obj) {

    }

    public void failure(String msg){
        Toast.makeText(MainDevicesActualization.this, msg, Toast.LENGTH_LONG).show();
    }
}
