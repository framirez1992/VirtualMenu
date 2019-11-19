package com.far.virtualmenu;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.far.virtualmenu.Adapters.ImagesAdapter;
import com.far.virtualmenu.CloudFireStoreObjects.ProductImage;
import com.far.virtualmenu.Controllers.ProductsImagesController;
import com.far.virtualmenu.Dialogs.ImageOptionsDialog;
import com.far.virtualmenu.Dialogs.UploadDialog;
import com.far.virtualmenu.Model.ProductModel;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadsFragment extends Fragment implements OnSuccessListener<QuerySnapshot>, OnFailureListener, OnCompleteListener, OnCanceledListener {


    MainUpload parent;
    ProductModel productModel;
    ProgressBar pb;
    RecyclerView rvList;
    TextView tvDescription;
    Button btnSelect, btnUpload;
    LinearLayout llProgress;
    TextView tvProgress;
    CardView btnAddImage;


    int SEARCH_REQUEST;
    private Uri filePath;

    public UploadsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_uploads, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //img = view.findViewById(R.id.img);
        pb = view.findViewById(R.id.pb);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new GridLayoutManager(parent,3));
        tvDescription = view.findViewById(R.id.tvDescription);
        btnAddImage = view.findViewById(R.id.btnAddImage);
        btnSelect = view.findViewById(R.id.btnSelect);
        btnUpload = view.findViewById(R.id.btnUpload);
        llProgress = view.findViewById(R.id.llProgress);
        tvProgress = view.findViewById(R.id.tvProgress);

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               searchImage();
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadImage();
            }
        });

        if(productModel != null){
            tvDescription.setText(productModel.getDescription());
        }

        searchImages();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
         filePath = data.getData();
         callAddDialog();
        }else{
            Snackbar.make(getView(), "No selecciono ningun archivo",Snackbar.LENGTH_LONG ).show();
        }
    }


    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(parent, e.getMessage()+" - "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        pb.setVisibility(View.GONE);
    }

    @Override
    public void onCanceled() {
        Toast.makeText(parent, "Canceled", Toast.LENGTH_LONG).show();
        pb.setVisibility(View.GONE);
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if(task.getException() != null){
            Toast.makeText(parent, task.getException().toString(), Toast.LENGTH_LONG).show();
        }
        pb.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {
        productModel.getImages().clear();
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                ProductImage obj = doc.toObject(ProductImage.class);
                productModel.getImages().add(obj);

            }
        }
        refreshImages();
    }


    public void setParent(MainUpload activity){
        this.parent = activity;
    }
    public void setProductModel(ProductModel pm){
        this.productModel = pm;
    }
    public void searchImage(){
        Intent i = new Intent();
        i.setType("Image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Selecciona una imagen"), SEARCH_REQUEST);

    }

    public void callAddDialog(){
        FragmentTransaction ft = ((AppCompatActivity)parent).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((AppCompatActivity)parent).getSupportFragmentManager().findFragmentByTag("upload");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  UploadDialog.newInstance(parent, productModel, filePath);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }


    public void callOptionDialog(ProductImage pi){
        FragmentTransaction ft = ((AppCompatActivity)parent).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((AppCompatActivity)parent).getSupportFragmentManager().findFragmentByTag("options");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  ImageOptionsDialog.newInstance(parent, pi);
        // Create and show the dialog.
        newFragment.show(ft, "options");
    }

    public void refreshImages(){

        ImagesAdapter adapter = new ImagesAdapter(parent,parent, productModel.getImages());
        rvList.setAdapter(adapter);
        rvList.invalidate();

    }

    public void searchImages(){
        pb.setVisibility(View.VISIBLE);
        ProductsImagesController.getInstance(parent).searchProductsImages(productModel.getCode(), this, this, this);
    }


}
