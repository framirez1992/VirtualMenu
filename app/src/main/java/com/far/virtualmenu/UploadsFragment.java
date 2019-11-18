package com.far.virtualmenu;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.far.virtualmenu.Model.ProductModel;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadsFragment extends Fragment {


    Activity parent;
    ProductModel productModel;
    private StorageReference mStorageRef;
    TextView tvDescription;
    Button btnSelect, btnUpload;
    ImageView img;
    LinearLayout llProgress;
    TextView tvProgress;
    CardView btnAddImage;


    Dialog dialogProductUpload;
    LinearLayout llProgressBar;
    CardView btnAceptar;
    TextView tvMessageDialog;

    int SEARCH_REQUEST;
    private Uri filePath;

    public UploadsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        return inflater.inflate(R.layout.fragment_uploads, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //img = view.findViewById(R.id.img);
        tvDescription = view.findViewById(R.id.tvDescription);
        btnAddImage = view.findViewById(R.id.btnAddImage);
        btnSelect = view.findViewById(R.id.btnSelect);
        btnUpload = view.findViewById(R.id.btnUpload);
        llProgress = view.findViewById(R.id.llProgress);
        tvProgress = view.findViewById(R.id.tvProgress);

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogProductUpload();
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
                uploadImage();
            }
        });

        if(productModel != null){
            tvDescription.setText(productModel.getDescription());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
        filePath = data.getData();
            Picasso.with(getActivity()).load(filePath).into(img);
        }
    }

    public void setParent(Activity activity){
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

    public void uploadImage(){
        startLoading();
        Uri file = filePath;
        if(file != null){
            StorageReference riversRef = mStorageRef.child(productModel.getCode()+"/"+ Funciones.generateCode() +"jpg");
            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    String x = downloadUrl.toString();
                                }
                            });
                            endLoading();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                           endLoading();
                           setMessageUploadDialog(exception.getLocalizedMessage(), R.color.red_700);
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    tvProgress.setText(progress+"% Uploaded...");

                }
            });
        }else{
            endLoading();
            setMessageUploadDialog("Archivo invalido. Seleccione una imagen");
        }


    }


    public void showDialogProductUpload(){
        try {
            dialogProductUpload = new Dialog(parent);
            dialogProductUpload.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogProductUpload.setContentView(R.layout.dialog_image_btn);
            llProgressBar = dialogProductUpload.findViewById(R.id.llProgress);
            img = dialogProductUpload.findViewById(R.id.img);
            btnAceptar = dialogProductUpload.findViewById(R.id.btnCargaInicial);
            tvMessageDialog = dialogProductUpload.findViewById(R.id.tvMessage);
            btnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   uploadImage();
                }
            });
            dialogProductUpload.show();
            Window window = dialogProductUpload.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void closeDialogProductUpload(){
        if(dialogProductUpload != null){
            dialogProductUpload.dismiss();
            dialogProductUpload = null;
        }
    }

    public void setMessageUploadDialog(String message){
        setMessageUploadDialog(message, android.R.color.black);
    }
    public void setMessageUploadDialog(String message, int color){
        tvMessageDialog.setText(message);
        tvMessageDialog.setTextColor(getResources().getColor(color));
    }




    public void startLoading(){
        tvMessageDialog.setText("");
        llProgressBar.setVisibility(View.VISIBLE);
        dialogProductUpload.setCancelable(false);
        btnAceptar.setEnabled(false);
    }
    public void endLoading(){
        btnAceptar.setEnabled(true);
        llProgressBar.setVisibility(View.INVISIBLE);
        dialogProductUpload.setCancelable(true);
    }


}
