package com.far.virtualmenu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.far.virtualmenu.CloudFireStoreObjects.Products;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Model.ProductModel;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class MainUpload extends AppCompatActivity implements ListableActivity {


    String lastSearch = null;
    UploadsFragment uploadsFragment;
    ProductsEdition productsEdition;
    ProductsController productsController;


    ProductModel pm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_upload);

        productsController = ProductsController.getInstance(this);

        uploadsFragment = new UploadsFragment();
        productsEdition = new ProductsEdition();

        uploadsFragment.setParent(this);
        productsEdition.setParent(this);

        changeFragment(productsEdition, R.id.details);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       /* try{
            getMenuInflater().inflate(R.menu.search_menu, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            search = (SearchView) searchItem.getActionView();

            search.setOnQueryTextListener(searchListener);
        }catch(Exception e){
            e.printStackTrace();
        }*/
        return (super.onCreateOptionsMenu(menu));
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_photo_options, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.photo:
                addNewPhoto();
                return  true;

            default:return super.onContextItemSelected(item);
        }
    }



    public SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")) {
                lastSearch = query;
                productsEdition.refreshList();
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                lastSearch = null;
                productsEdition.refreshList();
                return true;
            }
            return false;
        }
    };



    @Override
    public void onClick(Object obj) {
        pm = (ProductModel)obj;
    }


    public void changeFragment(Fragment f, int id) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
    }

    public void addNewPhoto(){
        uploadsFragment.setProductModel(pm);
        changeFragment(uploadsFragment, R.id.details);
    }


    public void setUpListeners(){
        productsController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                //productsController.delete(null, null);//limpia la tabla

                for (DocumentSnapshot ds : querySnapshot) {
                    Products p = ds.toObject(Products.class);
                    if(productsController.update(p, ProductsController.CODE+"= ?", new String[]{p.getCODE()})<=0){
                        productsController.insert(p);
                    }

                }

                if(productsEdition!= null){
                    productsEdition.refreshList();
                }

            }
        });

    }
}
