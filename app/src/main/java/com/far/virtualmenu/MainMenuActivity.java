package com.far.virtualmenu;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.far.virtualmenu.CloudFireStoreObjects.Devices;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.MeasureUnits;
import com.far.virtualmenu.CloudFireStoreObjects.MenuType;
import com.far.virtualmenu.CloudFireStoreObjects.ProductImage;
import com.far.virtualmenu.CloudFireStoreObjects.Products;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsMeasure;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsSubTypes;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsTypes;
import com.far.virtualmenu.CloudFireStoreObjects.Users;
import com.far.virtualmenu.CloudFireStoreObjects.UsersDevices;
import com.far.virtualmenu.Controllers.DevicesController;
import com.far.virtualmenu.Controllers.LicenseController;
import com.far.virtualmenu.Controllers.MeasureUnitsController;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Controllers.ProductsImagesController;
import com.far.virtualmenu.Controllers.ProductsMeasureController;
import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Controllers.UsersController;
import com.far.virtualmenu.Controllers.UsersDevicesController;
import com.far.virtualmenu.Model.ItemModel;
import com.far.virtualmenu.Utils.CODES;
import com.far.virtualmenu.Utils.Funciones;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class MainMenuActivity extends AppCompatActivity implements ListableActivity, OnSuccessListener<QuerySnapshot>, OnFailureListener, OnCompleteListener, OnCanceledListener {

    LicenseController licenseController;
    UsersController usersController;
    DevicesController devicesController;
    UsersDevicesController usersDevicesController;

    ProductsTypesController productsTypesController;
    ProductsSubTypesController productsSubTypesController;
    ProductsController productsController;
    ProductsMeasureController productsMeasureController;
    MeasureUnitsController measureUnitsController;
    ProductsImagesController productsImagesController;

    LogoFragment logoFragment;
    DetailFragment detailFragment;
    ListFragment listFragment;
    GridFragment gridFragment;
    Fragment lastFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        licenseController = LicenseController.getInstance(MainMenuActivity.this);
        usersController = UsersController.getInstance(MainMenuActivity.this);
        devicesController = DevicesController.getInstance(MainMenuActivity.this);
        usersDevicesController = UsersDevicesController.getInstance(MainMenuActivity.this);

        productsTypesController = ProductsTypesController.getInstance(MainMenuActivity.this);
        productsSubTypesController = ProductsSubTypesController.getInstance(MainMenuActivity.this);
        productsController = ProductsController.getInstance(MainMenuActivity.this);
        productsMeasureController = ProductsMeasureController.getInstance(MainMenuActivity.this);
        measureUnitsController = MeasureUnitsController.getInstance(MainMenuActivity.this);
        productsImagesController = ProductsImagesController.getInstance(MainMenuActivity.this);



        setLoadingScreen();
    }

    @Override
    protected void onStart() {
        super.onStart();
            licenseController.getReferenceFireStore().addSnapshotListener(MainMenuActivity.this, licenceListener);
            usersController.getReferenceFireStore().addSnapshotListener(MainMenuActivity.this, usersListener);
            devicesController.getReferenceFireStore(licenseController.getLicense()).addSnapshotListener(MainMenuActivity.this, deviceListener);
            usersDevicesController.getReferenceFireStore(licenseController.getLicense()).addSnapshotListener(MainMenuActivity.this, userDevicesListener);

    }

    public void changeFragment(Fragment f, int id) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
    }

    @Override
    public void onClick(Object obj) {
        detailFragment.setItemData((ItemModel) obj);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(detailFragment).attach(detailFragment).commit();
    }

    public void changeMenu(MenuType menuType){

        if(menuType.getTYPE() == CODES.CODE_MENUTYPE_LIST_DETAIL_FRAGMENTS){
            findViewById(R.id.menu).setVisibility(View.VISIBLE);
            detailFragment = new DetailFragment();
            detailFragment.setParent(this);

            listFragment = new ListFragment();
            listFragment.setParentActivity(this);
            changeFragment(detailFragment, R.id.details);
            changeFragment(listFragment, R.id.menu);

            lastFragment = detailFragment;
        }else if(menuType.getTYPE() == CODES.CODE_MENUTYPE_GRID_FRAGMENT){
            findViewById(R.id.menu).setVisibility(View.GONE);
            gridFragment = new GridFragment();
            gridFragment.setParentActivity(this);
            changeFragment(gridFragment, R.id.details);

            lastFragment = gridFragment;
        }
    }

    public void setLoadingScreen(){
        findViewById(R.id.menu).setVisibility(View.GONE);
        logoFragment = new LogoFragment();
        logoFragment.setParentActivity(MainMenuActivity.this);
        changeFragment(logoFragment, R.id.details);
    }



    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(MainMenuActivity.this, e.getMessage()+" - "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCanceled() {
        Toast.makeText(MainMenuActivity.this, "Cancelado", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if(task.getException() != null){
            Toast.makeText(MainMenuActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {


    }


    public EventListener<QuerySnapshot> licenceListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
            Licenses lic = null;
                if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                    lic = querySnapshot.getDocuments().get(0).toObject(Licenses.class);
                    licenseController.delete(null, null);
                    licenseController.insert(lic);
                }
                validateLicence(lic);

        }
    };

    public EventListener<QuerySnapshot> usersListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                usersController.delete(null, null);
                if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                    for(DocumentSnapshot doc: querySnapshot){
                        Users u = doc.toObject(Users.class);
                        if(u.getCODE().equals(Funciones.getCodeuserLogged(MainMenuActivity.this))){
                            usersController.insert(u);
                            break;
                        }
                    }
                }
               validateUser();

        }
    };

    public EventListener<QuerySnapshot> deviceListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

                devicesController.delete(null, null);
                Devices devices=null;
                if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                    for(DocumentSnapshot doc: querySnapshot){
                        Devices d = doc.toObject(Devices.class);
                        if(d.getCODE().equals(Funciones.getPhoneID(MainMenuActivity.this))) {
                            devices = d;
                            devicesController.insert(d);
                            break;
                        }
                    }
                }
                validateDevices(devices);

        }
    };

    public EventListener<QuerySnapshot> userDevicesListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                boolean valid = false;
                if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                    for(DocumentSnapshot doc: querySnapshot){
                        UsersDevices ud = doc.toObject(UsersDevices.class);
                        if(ud.getCODEDEVICE().equals(Funciones.getPhoneID(MainMenuActivity.this))
                                && ud.getCODEUSER().equals(Funciones.getCodeuserLogged(MainMenuActivity.this))){
                            valid = true;
                            break;
                        }
                    }

                   if(!valid){
                       exitWithNoLoginCode(CODES.CODE_DEVICES_NOT_ASSIGNED_TO_USER);
                   }
            }

        }
    };



    public boolean validateLicence(Licenses lic){

        int code = licenseController.validateLicense(lic);
        switch (code){
            //Validando vigencia de la licencia.
            case CODES.CODE_LICENSE_EXPIRED:
            case CODES.CODE_LICENSE_DISABLED:
            case CODES.CODE_LICENSE_NO_LICENSE:
                licenceListener = null;
                    exitWithNoLoginCode(code);
                return false;

        }

        return true;
    }


    public void exitWithNoLoginCode(int code){
            Toast.makeText(MainMenuActivity.this, Funciones.gerErrorMessage(code), Toast.LENGTH_LONG).show();
            Funciones.savePreferences(MainMenuActivity.this, CODES.PREFERENCE_LOGIN_BLOQUED, "1");
            Funciones.savePreferences(MainMenuActivity.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON, code+"");
            startActivityLoginFromBegining(code);
    }

    public void startActivityLoginFromBegining(int code){
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.putExtra(CODES.EXTRA_SECURITY_ERROR_CODE, code);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    public boolean validateUser(){

        int code = usersController.validateUser(usersController.getUserByCode(Funciones.getCodeuserLogged(MainMenuActivity.this)));

        if(code == CODES.CODE_USERS_INVALID || code == CODES.CODE_USERS_DISBLED) {
            exitWithNoLoginCode(code);
            return false;
        }

        return true;
    }

    public boolean validateDevices(Devices d){

        int code = DevicesController.getInstance(MainMenuActivity.this).validateDevice(d);

        if(code == CODES.CODE_DEVICES_UNREGISTERED || code == CODES.CODE_DEVICES_DISABLED) {
            exitWithNoLoginCode(code);
            return false;
        }

        return true;
    }






}
