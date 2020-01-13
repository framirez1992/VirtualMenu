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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.far.virtualmenu.Adapters.Models.ProductRowModel;
import com.far.virtualmenu.Adapters.Models.ProductSubTypeRowModel;
import com.far.virtualmenu.Adapters.Models.SimpleRowModel;
import com.far.virtualmenu.Adapters.ProductSubTypeEditionAdapter;
import com.far.virtualmenu.Adapters.SimpleRowEditionAdapter;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsSubTypes;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsTypes;
import com.far.virtualmenu.Controllers.LicenseController;
import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Dialogs.ColorPickDialog;
import com.far.virtualmenu.Dialogs.ProductSubTypeDialogFragment;
import com.far.virtualmenu.Generic.KV;
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

public class MaintenanceProductSubTypes extends AppCompatActivity implements ListableActivity, DialogCaller {

    RecyclerView rvList;
    ArrayList<ProductSubTypeRowModel> objects;
    ProductSubTypeEditionAdapter adapter;
    ProductsTypesController productsTypesController;
    ProductsSubTypesController productsSubTypesController;
    ProductsSubTypes productsSubType = null;
    Licenses licence;
    String lastSearch = null;
    Spinner spnFamily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w_spinner);

        productsTypesController = ProductsTypesController.getInstance(MaintenanceProductSubTypes.this);
        productsSubTypesController = ProductsSubTypesController.getInstance(MaintenanceProductSubTypes.this);

        licence = LicenseController.getInstance(MaintenanceProductSubTypes.this).getLicense();


        rvList = findViewById(R.id.rvList);
        spnFamily = findViewById(R.id.spn);
        ((TextView)findViewById(R.id.spnTitle)).setText("Familia");

        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceProductSubTypes.this);
        rvList.setLayoutManager(manager);
        adapter = new ProductSubTypeEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        productsTypesController.fillSpinner(spnFamily, true);

        spnFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV obj = ((KV)spnFamily.getSelectedItem());
                if(obj.getKey().equals("0"))
                    refreshList();
                else
                    refreshList();//DESCRIPCION
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
        if(isNew)
            newFragment = ProductSubTypeDialogFragment.newInstance(this, null, this);
        else
            newFragment = ProductSubTypeDialogFragment.newInstance(this, productsSubType, this);


        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(productsSubType != null){
            description = productsSubType.getDESCRIPTION();
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
                    Funciones.showAlertDependencies(MaintenanceProductSubTypes.this, msgDependency);
                    d.dismiss();
                    return;
                }
                if(productsSubType != null){
                    productsSubTypesController.deleteFromFireBase(productsSubType, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            btnAceptar.setEnabled(true);
                            btnCancelar.setEnabled(true);
                            d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                            Toast.makeText(MaintenanceProductSubTypes.this, e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });
                    productsSubTypesController.searchProductSubTypeFromFireBase(productsSubType.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {

                            if(querySnapshot== null || querySnapshot.size()==0){
                                productsSubTypesController.delete(productsSubType);
                                refreshList();
                                d.dismiss();
                            }else{
                                btnAceptar.setEnabled(true);
                                btnCancelar.setEnabled(true);
                                d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                Toast.makeText(MaintenanceProductSubTypes.this, "Error eliminando grupo. Intente nuevamente", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            btnAceptar.setEnabled(true);
                            btnCancelar.setEnabled(true);
                            d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                            Toast.makeText(MaintenanceProductSubTypes.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                d.dismiss();
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

    public void refreshList(){

        objects.clear();
        String where = "1 = 1 ";
        String[] args = null;
        ArrayList<String> x = new ArrayList<>();
        String order = null;

        if(lastSearch != null){
            where+=" AND pst."+ProductsSubTypesController.DESCRIPTION+" like  ? ";
            x.add(lastSearch+"%");
        }

        if(spnFamily.getSelectedItem() != null && !((KV)spnFamily.getSelectedItem()).getKey().equals("0")){
            where+= "AND pt."+ ProductsTypesController.CODE+" = ? ";
            x.add(((KV)spnFamily.getSelectedItem()).getKey());
        }else{
            order = "pst."+ProductsTypesController.DESCRIPTION;
        }

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }

            objects.addAll(productsSubTypesController.getAllProductSubTypesRM(where, args, order));

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        productsSubType = null;
        ProductSubTypeRowModel sr = (ProductSubTypeRowModel)obj;
        productsSubType = productsSubTypesController.getProductTypeByCode(sr.getId());

    }


    public SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")) {
                lastSearch = query;
                refreshList();
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                lastSearch = null;
                refreshList();
                return true;
            }
            return false;
        }
    };

    public String getMsgDependency(){
        String msgDependency ="";
        if(productsSubType != null){
            msgDependency = productsSubTypesController.hasDependencies(productsSubType.getCODE());


        }
        return msgDependency;
    }



    public void callColorDialog(View view){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("colorDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  ColorPickDialog.newInstance(this, view);
        // Create and show the dialog.
        newFragment.show(ft, "colorDialog");
    }


    @Override
    public void dialogClosed(Object o) {
        refreshList();
    }
}

