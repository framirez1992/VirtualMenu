package com.far.virtualmenu;

import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.far.virtualmenu.CloudFireStoreObjects.ProductImage;
import com.far.virtualmenu.CloudFireStoreObjects.Products;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Controllers.ProductsImagesController;
import com.far.virtualmenu.Model.ProductModel;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainUpload extends AppCompatActivity implements ListableActivity, OnFailureListener {


    String lastSearch = null;
    ProductsEdition productsEdition;
    UploadsFragment uploadsFragment;
    ProductsController productsController;
    Fragment lastFragment;

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
        //setUpListeners();
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
        if(obj instanceof ProductModel){
            pm = (ProductModel)obj;
        }else if(obj instanceof ProductImage){
            uploadsFragment.callOptionDialog((ProductImage)obj);
        }

    }


    public void changeFragment(Fragment f, int id) {
        lastFragment = f;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if(lastFragment == uploadsFragment){
            showProductsearch();
        }else {
            super.onBackPressed();
        }
    }

    public void addNewPhoto(){
        uploadsFragment.setProductModel(pm);
        changeFragment(uploadsFragment, R.id.details);
    }

    public void showProductsearch(){
        changeFragment(productsEdition,  R.id.details);
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


    public void SaveProductImage(ProductImage pi){
        ProductsImagesController productsImagesController = ProductsImagesController.getInstance(this);

        productsImagesController.sendToFireBase(pi, this);
        productsImagesController.searchProductImage(pi.getCODE(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {

                ProductImage pi = null;
                if(querySnapshot!= null && querySnapshot.size()>0){
                    pi = querySnapshot.getDocuments().get(0).toObject(ProductImage.class);
                }

                if(pi != null){
                    ProductsImagesController.getInstance(MainUpload.this).insert(pi);
                    if(lastFragment instanceof UploadsFragment){
                        uploadsFragment.productModel.setImages(ProductsImagesController.getInstance(MainUpload.this)
                                .getProductImageByCodeProduct(uploadsFragment.productModel.getCode()));
                    }
                    refreshImages();
                }else{
                    Toast.makeText(MainUpload.this, "Error creando imagen, intente nuevamente.", Toast.LENGTH_LONG).show();
                }
            }
        }, this);

    }

    public void deleteProductImage(final ProductImage pi){
        ProductsImagesController.getInstance(MainUpload.this).delete(ProductsImagesController.CODE+" = ?", new String[]{ pi.getCODE()});
        refreshImages();

    }

    public void refreshImages(){
        if(lastFragment == uploadsFragment){
            uploadsFragment.refreshImages();
        }
    }




    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(MainUpload.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }
}
