package com.far.virtualmenu;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.far.virtualmenu.Adapters.Models.SimpleRowModel;
import com.far.virtualmenu.Adapters.Models.TitleDetailRowModel;
import com.far.virtualmenu.Adapters.SimpleRowEditionAdapter;
import com.far.virtualmenu.Adapters.TitleDetailRowEditionAdapter;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsTypes;
import com.far.virtualmenu.Controllers.LicenseController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Dialogs.ProductTypeDialogFragment;
import com.far.virtualmenu.Utils.CODES;
import com.far.virtualmenu.Utils.Funciones;
import com.far.virtualmenu.interfaces.DialogCaller;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MaintenanceProductTypes extends AppCompatActivity implements ListableActivity, DialogCaller {

    RecyclerView rvList;
    ArrayList<TitleDetailRowModel> objects;
    TitleDetailRowEditionAdapter adapter;
    ProductsTypesController productsTypesController;

    ProductsTypes productsType = null;
    Licenses licence;
    String lastSearch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w_spinner);


        productsTypesController = ProductsTypesController.getInstance(MaintenanceProductTypes.this);
        licence = LicenseController.getInstance(MaintenanceProductTypes.this).getLicense();

        findViewById(R.id.cvSpinner).setVisibility(View.GONE);

        rvList = findViewById(R.id.rvList);
        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceProductTypes.this);
        rvList.setLayoutManager(manager);
        adapter = new TitleDetailRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        refreshList(lastSearch);



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try{
            getMenuInflater().inflate(R.menu.search_menu, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView search = (SearchView) searchItem.getActionView();

            search.setOnQueryTextListener(searchListener);
        }catch(Exception e){
            e.printStackTrace();
        }
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new:
                callAddDialog(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_delete, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionEdit:
                callAddDialog(false);
                return true;
            case R.id.actionDelete:
                callDeleteConfirmation();
                return  true;

            default:return super.onContextItemSelected(item);
        }
    }

    public void callAddDialog(boolean isNew){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;
        if(isNew){
            newFragment = ProductTypeDialogFragment.newInstance(null, this);
        }else {
            newFragment = ProductTypeDialogFragment.newInstance(productsType, this);
        }

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(productsType != null){
            description = productsType.getDESCRIPTION();
        }

        String msg = "Esta seguro que desea eliminar \'"+description+"\' permanentemente?";
        final Dialog d = Funciones.getCustomDialog2Btn(this,getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        final CardView btnAceptar = d.findViewById(R.id.btnPositive);
        final CardView btnCancelar = d.findViewById(R.id.btnNegative);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
                btnAceptar.setEnabled(false);
                btnCancelar.setEnabled(false);

                String msgDependency = getMsgDependency();
                if(!msgDependency.isEmpty()) {
                    Funciones.showAlertDependencies(MaintenanceProductTypes.this, msgDependency);
                    d.dismiss();
                    return;
                }

                if(productsType != null){
                        productsTypesController.deleteFromFireBase(productsType,  new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btnAceptar.setEnabled(true);
                                btnCancelar.setEnabled(true);
                                d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                Toast.makeText(MaintenanceProductTypes.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        productsTypesController.searchProductTypeFromFireBase(productsType.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                if(querySnapshot == null || querySnapshot.size() ==0){
                                    productsTypesController.delete(productsType);
                                    refreshList(lastSearch);
                                    d.dismiss();
                                }else{
                                    btnAceptar.setEnabled(true);
                                    btnCancelar.setEnabled(true);
                                    d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                    Toast.makeText(MaintenanceProductTypes.this, "Error borrando Familia. Intente nuevamente", Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btnAceptar.setEnabled(true);
                                btnCancelar.setEnabled(true);
                                d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                Toast.makeText(MaintenanceProductTypes.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                }

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
        Window window = d.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);

    }

    public void refreshList(String data){
        objects.clear();
        String where = " 1 = 1 ";
        ArrayList<String> values = new ArrayList<>();
        String[] args = null;

            if(data != null){
                where += " AND "+ProductsTypesController.DESCRIPTION+" like ?";
                values.add(data+"%");
            }
            args = values.toArray(new String[values.size()]);

            objects.addAll(productsTypesController.getAllProductTypesTDRM(where, args));

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        productsType = null;
        TitleDetailRowModel sr = (TitleDetailRowModel)obj;
        productsType = productsTypesController.getProductTypeByCode(sr.getId());


    }



    public SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")) {
                lastSearch = query;
                refreshList(lastSearch);
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                lastSearch = null;
                refreshList(lastSearch);
                return true;
            }
            return false;
        }
    };


    public String getMsgDependency(){
        String msgDependency ="";
        if(productsType != null){
                msgDependency = productsTypesController.hasDependencies(productsType.getCODE());


        }
        return msgDependency;
    }

    @Override
    public void dialogClosed(Object o) {
        refreshList(lastSearch);
    }
}
