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
import com.far.virtualmenu.Adapters.SimpleRowEditionAdapter;
import com.far.virtualmenu.CloudFireStoreObjects.Licenses;
import com.far.virtualmenu.CloudFireStoreObjects.MeasureUnits;
import com.far.virtualmenu.Controllers.LicenseController;
import com.far.virtualmenu.Controllers.MeasureUnitsController;
import com.far.virtualmenu.DataBase.DB;
import com.far.virtualmenu.Dialogs.MeasureUnitDialogFragment;
import com.far.virtualmenu.Generic.KV2;
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

public class MaintenanceUnitMeasure extends AppCompatActivity implements ListableActivity, DialogCaller {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    MeasureUnitsController measureUnitsController;
    MeasureUnits measureUnit;
    Licenses licence;
    String lastSearch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_screen);

        measureUnitsController = MeasureUnitsController.getInstance(MaintenanceUnitMeasure.this);
        licence = LicenseController.getInstance(MaintenanceUnitMeasure.this).getLicense();


        rvList = findViewById(R.id.rvList);
        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceUnitMeasure.this);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        refreshList(lastSearch);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //setUpListeners();
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
        DialogFragment newFragment =  MeasureUnitDialogFragment.newInstance( (isNew)?null:measureUnit, this);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(measureUnit != null){
            description = measureUnit.getDESCRIPTION();
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

                if(measureUnit != null){
                    measureUnitsController.deleteFromFireBase(measureUnit, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            btnAceptar.setEnabled(true);
                            d.findViewById(R.id.btnNegative).setEnabled(true);
                            d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                            Toast.makeText(MaintenanceUnitMeasure.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    measureUnitsController.searchMeasureUnitFromFireBase(measureUnit.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {

                            if(querySnapshot == null || querySnapshot.size()==0){

                                for(KV2 data: MeasureUnitsController.getInstance(MaintenanceUnitMeasure.this).getDependencies(measureUnit.getCODE())){
                                    String sql = "DELETE FROM "+data.getCode()+" WHERE "+data.getDescription()+" = '"+data.getDescription2()+"'";
                                    DB.getInstance(MaintenanceUnitMeasure.this).getWritableDatabase().execSQL(sql);
                                }
                                measureUnitsController.delete(measureUnit);
                                refreshList(lastSearch);
                                d.dismiss();
                            }else{
                                btnAceptar.setEnabled(true);
                                d.findViewById(R.id.btnNegative).setEnabled(true);
                                d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                Toast.makeText(MaintenanceUnitMeasure.this, "Error eliminando medida. Intente nuevamente", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            btnAceptar.setEnabled(true);
                            d.findViewById(R.id.btnNegative).setEnabled(true);
                            d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                            Toast.makeText(MaintenanceUnitMeasure.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        String where = (data!= null)?MeasureUnitsController.DESCRIPTION+" like  ? ":null;
        objects.addAll(measureUnitsController.getMeasureUnitsSRM(where, (data != null)?new String[]{data+"%"}:null, null));


        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        measureUnit = null;
        SimpleRowModel sr = (SimpleRowModel)obj;
        measureUnit = measureUnitsController.getMeasureUnitByCode(sr.getId());


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

    @Override
    public void dialogClosed(Object o) {
        refreshList(lastSearch);
    }
}
