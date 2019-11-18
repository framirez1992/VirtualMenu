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
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.far.virtualmenu.CloudFireStoreObjects.ProductsSubTypes;
import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Generic.KV;
import com.far.virtualmenu.R;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;

public class ProductSubTypeDialogFragment extends DialogFragment implements OnFailureListener {

    ProductsSubTypes tempObj;
    LinearLayout llFamilia;
    Spinner spnFamilia;
    LinearLayout llSave;
    TextInputEditText etName, etOrden;

    ProductsSubTypesController productsSubTypesController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static ProductSubTypeDialogFragment newInstance( ProductsSubTypes pt) {

        ProductSubTypeDialogFragment f = new ProductSubTypeDialogFragment();
        f.tempObj = pt;

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
        productsSubTypesController = ProductsSubTypesController.getInstance(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.dialog_spn_save, container, true);
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
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    public void init(View view){
        llFamilia = view.findViewById(R.id.llFamilia);
        spnFamilia = view.findViewById(R.id.spnFamilia);
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);
        etOrden = view.findViewById(R.id.etOrden);
        view.findViewById(R.id.tilOrden).setVisibility(View.VISIBLE);

        ProductsTypesController.getInstance(getActivity()).fillSpinner(spnFamilia, false);


        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditProductSubType();
                }
            }
        });



        if(tempObj != null) {//EDIT
            prepareForProductSubType();
        }
    }


    public boolean validateProductSubType(){
        if(spnFamilia.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione una familia", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void Save(){
            if(validateProductSubType()){
                SaveProductSubType();
            }else{
                llSave.setEnabled(true);
            }

    }

    public void SaveProductSubType(){
        try {
            String code = Funciones.generateCode();
            String name = etName.getText().toString();
            String codeProductType = ((KV)spnFamilia.getSelectedItem()).getKey();
            int orden = (etOrden.getText().toString().trim().equals(""))?9999:Integer.parseInt(etOrden.getText().toString());
            ProductsSubTypes pst = new ProductsSubTypes(code,codeProductType,name, orden);
            productsSubTypesController.sendToFireBase(pst);


            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void EditProductSubType(){
        try {
            ProductsSubTypes pst = tempObj;
            int orden = (etOrden.getText().toString().trim().equals(""))?9999:Integer.parseInt(etOrden.getText().toString());
            pst.setDESCRIPTION(etName.getText().toString());
            pst.setCODETYPE(((KV)spnFamilia.getSelectedItem()).getKey());
            pst.setMDATE(null);
            pst.setORDEN(orden);
            productsSubTypesController.sendToFireBase(pst);


            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void prepareForProductSubType(){
        setFamilia();
        etName.setText(tempObj.getDESCRIPTION());
        etOrden.setText(tempObj.getORDEN()+"");
    }
    public void setFamilia(){
        for(int i = 0; i< spnFamilia.getAdapter().getCount(); i++){
            if(((KV)spnFamilia.getAdapter().getItem(i)).getKey().equals(tempObj.getCODETYPE())){
                spnFamilia.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }
}
