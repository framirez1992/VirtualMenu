package com.far.virtualmenu.Dialogs;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.far.virtualmenu.CloudFireStoreObjects.ProductImage;
import com.far.virtualmenu.MainUpload;
import com.far.virtualmenu.Model.ProductModel;
import com.far.virtualmenu.R;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadDialog extends DialogFragment implements OnFailureListener {

    MainUpload parent;
    private ProductModel productModel;
    Uri filePath;

    LinearLayout llProgressBar;
    CardView btnAceptar;
    TextView tvMessageDialog;
    ImageView img;
    private StorageReference mStorageRef;

    public  static UploadDialog newInstance(MainUpload parent, ProductModel pm, Uri fileUri) {

        UploadDialog f = new UploadDialog();
        f.productModel = pm;
        f.filePath = fileUri;
        f.parent = parent;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(pm != null) {
            f.setArguments(args);
        }

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.dialog_image_btn, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);

        Picasso.with(parent).load(filePath).into(img);
    }


    public void init(View view){
        llProgressBar = view.findViewById(R.id.llProgress);
        img = view.findViewById(R.id.img);
        btnAceptar = view.findViewById(R.id.btnCargaInicial);
        tvMessageDialog = view.findViewById(R.id.tvMessage);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

    }




    @Override
    public void onFailure(@NonNull Exception e) {

    }

    public void uploadImage(){
        startLoading();
        Uri file = filePath;
        if(file != null){
            StorageReference riversRef = mStorageRef.child(productModel.getCode()+"/"+ Funciones.generateCode() +"."+Funciones.getFileExtension(parent, filePath));
            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    ProductImage productImage = new ProductImage(Funciones.generateCode(), productModel.getCode(), uri.toString());
                                    productModel.getImages().add(productImage);
                                    parent.SaveProductImage(productImage);
                                    endLoading();
                                    dismiss();
                                    Toast.makeText(parent, "Subido satisfactoriamente.", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    endLoading();
                                    setMessageUploadDialog(e.getLocalizedMessage(), R.color.red_700);
                                }
                            });
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
                    setMessageUploadDialog(progress+"% Uploaded...");

                }
            });
        }else{
            endLoading();
            setMessageUploadDialog("Archivo invalido. Seleccione una imagen");
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
        setCancelable(false);
        btnAceptar.setEnabled(false);
    }
    public void endLoading(){
        btnAceptar.setEnabled(true);
        llProgressBar.setVisibility(View.INVISIBLE);
        setCancelable(true);
    }

}
