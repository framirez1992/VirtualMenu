package com.far.virtualmenu.Dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.far.virtualmenu.CloudFireStoreObjects.ProductsTypes;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.R;
import com.far.virtualmenu.Utils.Funciones;
import com.far.virtualmenu.interfaces.DialogCaller;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;


public  class ProductTypeDialogFragment extends DialogFragment implements OnFailureListener {

    ProductsTypes tempObj;
    DialogCaller dialogCaller;

    LinearLayout llSave;
    TextInputEditText etName;
    TextInputEditText etOrden;
    CheckBox cbActivate;
    LinearLayout llProgress;


    ProductsTypesController productsTypesController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static ProductTypeDialogFragment newInstance(ProductsTypes pt, DialogCaller dialogCaller) {

        ProductTypeDialogFragment f = new ProductTypeDialogFragment();
        f.tempObj = pt;
        f.dialogCaller = dialogCaller;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(pt != null) {
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
        productsTypesController = ProductsTypesController.getInstance(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.product_type_fragment_dialog, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        Funciones.showKeyBoard(etName);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        Window window = getDialog().getWindow();
        window.setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    public void init(View view){
        llProgress = view.findViewById(R.id.llProgress);
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);
        etOrden = view.findViewById(R.id.etOrden);
        cbActivate = view.findViewById(R.id.cbActivate);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditProductType();
                }
            }
        });

        if(tempObj != null){//EDIT
                setUpToEditProductType();
        }
    }

    public boolean validateProductType(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateProductType()) {
            SaveProductType();
        }

    }

    public void SaveProductType(){
            String code = Funciones.generateCode();
            String name = etName.getText().toString();
            int orden = etOrden.getText().toString().trim().equals("")?9999:Integer.parseInt(etOrden.getText().toString());
            ProductsTypes pt = new ProductsTypes(code, name, orden, cbActivate.isChecked());
            pt.setDATE(new Date());
            pt.setMDATE(new Date());
            productsTypesController.sendToFireBase(pt);
            productsTypesController.searchProductTypeFromFireBase(pt.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    ProductsTypes pt = null;
                    if(querySnapshot != null && querySnapshot.size() > 0){
                        pt = querySnapshot.getDocuments().get(0).toObject(ProductsTypes.class);
                        dismiss();
                    }

                    if(pt != null){
                        productsTypesController.insert(pt);
                        dialogCaller.dialogClosed(pt);
                        dismiss();
                    }else{
                        failure("Error creando familia. Intente nuevamente");
                    }
                }
            }, this);


    }

    public void EditProductType(){
            int orden = etOrden.getText().toString().trim().equals("")?9999:Integer.parseInt(etOrden.getText().toString());
            tempObj.setDESCRIPTION(etName.getText().toString());
            tempObj.setORDEN(orden);
            tempObj.setENABLED(cbActivate.isChecked());
            tempObj.setMDATE(new Date());
            productsTypesController.sendToFireBase(tempObj);
            productsTypesController.searchProductTypeFromFireBase(tempObj.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    ProductsTypes pt = null;
                    if(querySnapshot != null && querySnapshot.size() > 0){
                        pt = querySnapshot.getDocuments().get(0).toObject(ProductsTypes.class);
                        dismiss();
                    }

                    if(pt != null){
                        productsTypesController.update(pt);
                        dialogCaller.dialogClosed(pt);
                        dismiss();
                    }else{
                        failure("Error editando familia. Intente nuevamente");
                    }
                }
            }, this);

            this.dismiss();


    }



    public void setUpToEditProductType(){
        etName.setText(tempObj.getDESCRIPTION());
        etOrden.setText(tempObj.getORDEN()+"");
        cbActivate.setChecked(tempObj.isENABLED());

    }



    @Override
    public void onFailure(@NonNull Exception e) {
        failure(e.getMessage());
    }

    public void failure(String msg){
        llSave.setEnabled(true);
        llProgress.setVisibility(View.INVISIBLE);
        Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
    }
}
