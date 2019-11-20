package com.far.virtualmenu.Dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.far.virtualmenu.CloudFireStoreObjects.ProductsSubTypes;
import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Generic.KV;
import com.far.virtualmenu.MaintenanceProductSubTypes;
import com.far.virtualmenu.R;
import com.far.virtualmenu.Utils.Funciones;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.google.android.gms.tasks.OnFailureListener;

public class ProductSubTypeDialogFragment extends DialogFragment implements OnFailureListener {

    MaintenanceProductSubTypes parent;
    ProductsSubTypes tempObj;
    LinearLayout llFamilia;
    Spinner spnFamilia;
    LinearLayout llSave;
    TextInputEditText etName, etOrden;
    CheckBox cbActivate;
    View vTextColor, vBackground;
    LinearLayout llBackground, llTextColor;

    ProductsSubTypesController productsSubTypesController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static ProductSubTypeDialogFragment newInstance(MaintenanceProductSubTypes parent,  ProductsSubTypes pt) {

        ProductSubTypeDialogFragment f = new ProductSubTypeDialogFragment();
        f.parent = parent;
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


        return inflater.inflate(R.layout.add_edit_product_sub_type, container, true);
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
        cbActivate = view.findViewById(R.id.cbActivate);
        vBackground = view.findViewById(R.id.vBackground);
        vTextColor = view.findViewById(R.id.vTextColor);
        llBackground = view.findViewById(R.id.llBackground);
        llTextColor = view.findViewById(R.id.llTextColor);

        llBackground.setOnClickListener(colorChangeListener);
        llTextColor.setOnClickListener(colorChangeListener);

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
            String hex1 = Funciones.convertToHexColor(((ColorDrawable)vBackground.getBackground()).getColor());
            String hex2 = Funciones.convertToHexColor(((ColorDrawable)vTextColor.getBackground()).getColor());
            ProductsSubTypes pst = new ProductsSubTypes(code,codeProductType,name,hex1, hex2, orden, cbActivate.isChecked());
            productsSubTypesController.sendToFireBase(pst);


            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void EditProductSubType(){
        try {
            ProductsSubTypes pst = tempObj;
            String hex1 = Funciones.convertToHexColor(((ColorDrawable)vBackground.getBackground()).getColor());
            String hex2 = Funciones.convertToHexColor(((ColorDrawable)vTextColor.getBackground()).getColor());
            int orden = (etOrden.getText().toString().trim().equals(""))?9999:Integer.parseInt(etOrden.getText().toString());
            pst.setDESCRIPTION(etName.getText().toString());
            pst.setCODETYPE(((KV)spnFamilia.getSelectedItem()).getKey());
            pst.setHEXCOLOR1(hex1);
            pst.setHEXCOLOR2(hex2);
            pst.setMDATE(null);
            pst.setORDEN(orden);
            pst.setENABLED(cbActivate.isChecked());
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
        cbActivate.setChecked(tempObj.isENABLED());
        vBackground.setBackgroundColor(Color.parseColor(tempObj.getHEXCOLOR1()));
        vTextColor.setBackgroundColor(Color.parseColor(tempObj.getHEXCOLOR2()));
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

    View.OnClickListener colorChangeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View vColor = (v.getId() == R.id.llBackground)?vBackground:vTextColor;
           parent.callColorDialog(vColor);
        }
    };
}
