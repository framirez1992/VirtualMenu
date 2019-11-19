package com.far.virtualmenu.Dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.far.virtualmenu.CloudFireStoreObjects.ProductImage;
import com.far.virtualmenu.MainUpload;
import com.far.virtualmenu.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ImageOptionsDialog extends DialogFragment implements OnFailureListener {

    MainUpload parent;
    ProductImage productImage;

    CardView  btnDelete;
    TextView tvMessageDialog;
    //RecyclerView rvList;
    ImageView img;

    private StorageReference mStorageRef;

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
        img = view.findViewById(R.id.img);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });

        Picasso.with(parent).load(productImage.getURL()).into(img);

    }




    @Override
    public void onFailure(@NonNull Exception e) {
       setMessage(e.getLocalizedMessage(), R.color.red_700);
    }


    public void deleteImage(){
        startLoading();
        setMessage("Borrando...");
        StorageReference storageReference = mStorageRef.getStorage().getReferenceFromUrl(productImage.getURL());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                endLoading();
                parent.deleteProductImage(productImage);
                dismiss();
            }
        }).addOnFailureListener(this);

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
        //llProgressBar.setVisibility(View.VISIBLE);
        setCancelable(false);
        btnDelete.setEnabled(false);
    }
    public void endLoading(){
        btnDelete.setEnabled(true);
        //llProgressBar.setVisibility(View.INVISIBLE);
        setCancelable(true);
    }

}
