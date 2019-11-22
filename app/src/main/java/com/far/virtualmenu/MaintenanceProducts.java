package com.far.virtualmenu;

import android.app.Dialog;
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

import com.far.virtualmenu.Adapters.Models.ProductRowModel;
import com.far.virtualmenu.Adapters.ProductRowEditionAdapter;
import com.far.virtualmenu.CloudFireStoreObjects.Products;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsMeasure;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Controllers.ProductsMeasureController;
import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Dialogs.ProductsDialogfragment;
import com.far.virtualmenu.Generic.KV;
import com.far.virtualmenu.Utils.Funciones;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MaintenanceProducts extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    Spinner spnProductType, spnProductSubType;
    ArrayList<ProductRowModel> objects;
    ProductRowEditionAdapter adapter;
    ProductsController productsController;
    ProductsMeasureController productsMeasureController;
    Products products;
    String lastSearch = null;
    String lastFamilia;
    String lastGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w2_spinner);

        productsController = ProductsController.getInstance(MaintenanceProducts.this);
        productsMeasureController = ProductsMeasureController.getInstance(MaintenanceProducts.this);

        rvList = findViewById(R.id.rvList);
        spnProductType = findViewById(R.id.spn);
        spnProductSubType = findViewById(R.id.spn2);
        ((TextView)findViewById(R.id.spnTitle)).setText("Familia");
        ((TextView)findViewById(R.id.spnTitle2)).setText("Grupo");

        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceProducts.this);
        rvList.setLayoutManager(manager);
        adapter = new ProductRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        ProductsTypesController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductType,true);
        ProductsSubTypesController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductSubType,true);



        spnProductType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV familia = (KV)spnProductType.getSelectedItem();
                lastFamilia = (familia.getKey().equals("0"))?null: familia.getKey();
                ProductsSubTypesController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductSubType, true, familia.getKey());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnProductSubType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV grupo = (KV)spnProductSubType.getSelectedItem();
                lastGrupo = (grupo.getKey().equals("0"))?null:grupo.getKey() ;
                refreshList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        refreshList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpListeners();
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


    public void setUpListeners(){
            productsMeasureController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    productsMeasureController.delete(null, null);//limpia la tabla

                    for (DocumentSnapshot ds : querySnapshot) {
                        ProductsMeasure mu = ds.toObject(ProductsMeasure.class);
                        productsMeasureController.insert(mu);
                    }

                    //refreshList();
                }
            });


            productsController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    productsController.delete(null, null);//limpia la tabla

                    for (DocumentSnapshot ds : querySnapshot) {

                        Products mu = ds.toObject(Products.class);
                        productsController.insert(mu);
                    }

                    refreshList();
                }
            });

    }
    public void callAddDialog(boolean isNew){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  ProductsDialogfragment.newInstance((isNew)?null:products);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(products != null){
            description = products.getDESCRIPTION();
        }
        final Dialog d = Funciones.getAlertDeleteAllDependencies(MaintenanceProducts.this,description,
                productsController.getDependencies(products.getCODE()));
        CardView btnAceptar = d.findViewById(R.id.btnPositive);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(products != null){
                    productsController.deleteFromFireBase(products);

                }
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
        if(lastSearch != null){
            where+=" AND p."+ProductsController.DESCRIPTION+" like  ? ";
            x.add(lastSearch+"%");
        }
        if(lastFamilia != null){
            where+= "AND pt."+ ProductsTypesController.CODE+" = ? ";
            x.add(lastFamilia);
        }

        if(lastGrupo != null){
            where+= "AND pst."+ProductsSubTypesController.CODE+" = ? ";
            x.add(lastGrupo);
        }

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }
            objects.addAll(productsController.getProductsPRM(where, args, null));

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        products = null;
        ProductRowModel sr = (ProductRowModel)obj;
        products = productsController.getProductByCode(sr.getCode());

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
}

