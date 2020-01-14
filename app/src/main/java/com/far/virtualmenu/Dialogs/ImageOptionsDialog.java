package com.far.virtualmenu.Dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
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
import com.far.virtualmenu.Controllers.ProductsImagesController;
import com.far.virtualmenu.MainUpload;
import com.far.virtualmenu.R;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ImageOptionsDialog extends DialogFragment implements OnFailureListener {

    MainUpload parent;
    ProductImage productImage;

    CardView  btnDelete, btnEdit,btnBack, btnSave;
    TextView tvMessageDialog, tvOrder;
    //RecyclerView rvList;
    ImageView img;

    LinearLayout llEdition, llMainOptions;
    TextInputEditText etOrden;



    public  static ImageOptionsDialog newInstance(MainUpload parent, ProductImage pi) {

        ImageOptionsDialog f = new ImageOptionsDialog();
        f.productImage = pi;
        f.parent = parent;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(pi != null) {
            f.setArguments(args);
        }

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


        return inflater.inflate(R.layout.image_options, container, true);
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
    }


    public void init(View view){
        //rvList = view.findViewById(R.id.rvList);
        tvMessageDialog = view.findViewById(R.id.tvMessage);
        tvOrder = view.findViewById(R.id.tvOrder);
        img = view.findViewById(R.id.img);
        llEdition = view.findViewById(R.id.llEdition);
        llMainOptions = view.findViewById(R.id.llMainOptions);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnEdit  = view.findViewById(R.id.btnEdit);
        btnBack = view.findViewById(R.id.btnBack);
        btnSave  = view.findViewById(R.id.btnSave);
        etOrden = view.findViewById(R.id.etOrden);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });
        btnEdit.setOnClickListener(changeViewListener);
        btnBack.setOnClickListener(changeViewListener);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    save();
                }
            }
        });

        tvOrder.setText(productImage.getORDEN()+"");
        Picasso.with(parent).load(productImage.getURL()).into(img);

    }


    View.OnClickListener changeViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            llMainOptions.setVisibility(v.getId() == R.id.btnBack?View.VISIBLE:View.GONE);
            llEdition.setVisibility(v.getId()==R.id.btnEdit?View.VISIBLE:View.GONE);
            if(v.getId()==R.id.btnEdit){
                Funciones.showKeyBoard(etOrden);
            }
        }
    };



    @Override
    public void onFailure(@NonNull Exception e) {
        enableAll();
       setMessage(e.getLocalizedMessage(), R.color.red_700);
    }


    public void deleteImage(){
        disableAll();
        startLoading();
        setMessage("Borrando...");
        ProductsImagesController.getInstance(parent).deleteFromStorage(productImage, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                ProductsImagesController.getInstance(parent).deleteFromFireBase(productImage, ImageOptionsDialog.this);
                ProductsImagesController.getInstance(parent).searchProductImage(productImage.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if(querySnapshot == null || querySnapshot.size()==0){
                            parent.deleteProductImage(productImage);//borrado local y refresh
                            dismiss();
                        }else{
                            enableAll();
                            Toast.makeText(parent, "Error eliminando imagen. Intente nuevamente", Toast.LENGTH_LONG).show();
                        }

                    }
                }, ImageOptionsDialog.this);
            }
        }, this);

    }


    public void setMessage(String message){
        setMessage(message, R.color.gray_200);
    }
    public void setMessage(String message, int color){
        tvMessageDialog.setText(message);
        tvMessageDialog.setTextColor(getResources().getColor(color));
    }




    public void startLoading(){
        tvMessageDialog.setText("");
    }
    public void endLoading(){
        setCancelable(true);
    }


    public boolean validate(){
        if(etOrden.getText().toString().isEmpty()){
            Snackbar.make(getView(), "El orden no puede estar vacio", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void save(){
        disableAll();
        startLoading();
        setMessage("Actualizando...");
        productImage.setORDEN(Integer.parseInt(etOrden.getText().toString()));
        ProductsImagesController.getInstance(parent).sendToFireBase(productImage, this);
        ProductsImagesController.getInstance(parent).searchProductImage(productImage.getCODE(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                ProductImage pi = null;
                if(querySnapshot!= null && querySnapshot.size() > 0){
                    pi = querySnapshot.getDocuments().get(0).toObject(ProductImage.class);
                }
                if(pi != null){
                    ProductsImagesController.getInstance(parent).update(pi, ProductsImagesController.CODE+" = ?", new String[]{productImage.getCODE()});
                    parent.refreshImages();
                    dismiss();
                }else{
                    enableAll();
                    Toast.makeText(parent, "Error actualizando Imagen. Intente nuevamente ", Toast.LENGTH_LONG).show();
                }

            }
        }, this);
    }



    public void enableAll(){
        setCancelable(true);
        btnDelete.setEnabled(true);
        btnEdit.setEnabled(true);
        btnBack.setEnabled(true);
        btnSave.setEnabled(true);
    }

    public void disableAll(){
        setCancelable(false);
        btnDelete.setEnabled(false);
        btnEdit.setEnabled(false);
        btnBack.setEnabled(false);
        btnSave.setEnabled(false);
    }

}
