package com.far.virtualmenu.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.far.virtualmenu.Adapters.EditSelectionRowAdapter;
import com.far.virtualmenu.Adapters.Models.EditSelectionRowModel;
import com.far.virtualmenu.CloudFireStoreObjects.Products;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsMeasure;
import com.far.virtualmenu.Controllers.MeasureUnitsController;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Controllers.ProductsMeasureController;
import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Generic.KV;
import com.far.virtualmenu.R;
import com.far.virtualmenu.Utils.CODES;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ProductsDialogfragment extends DialogFragment implements OnFailureListener {

    private Products tempObj;

    LinearLayout llSave, llBack;
    TextInputEditText etCode, etName;
    Spinner spnFamily, spnGroup, spnTime;
    RecyclerView rvMeasures;
    LinearLayout llMeasureScreen, llMainScreen, llNext;
    CheckBox cbActivate;
    EditText etTime, etDescription;

    ProductsController productsController;
    ArrayList<EditSelectionRowModel> selected = new ArrayList<>() ;
    boolean firstTime = true;

    Dialog loadingDialg;
    Dialog errorDialog;

    public  static ProductsDialogfragment newInstance(Products pt) {


        ProductsDialogfragment f = new ProductsDialogfragment();
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
        productsController = ProductsController.getInstance(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.dialog_add_edit_product, container, true);
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
        llMainScreen = view.findViewById(R.id.llMainScreen);
        llMeasureScreen = view.findViewById(R.id.llMeasureScreen);
        llNext = view.findViewById(R.id.llNext);
        llSave = view.findViewById(R.id.llSave);
        llBack = view.findViewById(R.id.llBack);
        etCode = view.findViewById(R.id.etCode);
        etName = view.findViewById(R.id.etName);
        spnFamily = view.findViewById(R.id.spnFamilia);
        spnGroup = view.findViewById(R.id.spnGrupo);
        etTime = view.findViewById(R.id.etTime);
        spnTime = view.findViewById(R.id.spnTime);
        etDescription = view.findViewById(R.id.etDescription);
        rvMeasures = view.findViewById(R.id.rvMeasures);
        rvMeasures.setLayoutManager(new LinearLayoutManager(getActivity()));
        cbActivate = view.findViewById(R.id.cbActivate);

        ProductsTypesController.getInstance(getActivity()).fillSpinner(spnFamily, false);
        ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnGroup, false);

        etCode.setEnabled(false);
        etCode.setText(Funciones.generateCode());

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                selected = ((EditSelectionRowAdapter)rvMeasures.getAdapter()).getSelectedObjects();
                if(tempObj == null){
                    Save();
                }else{
                    //showLoadingDialog();
                    EditProduct();
                }
                llSave.setEnabled(true);
                //closeLoadingDialog();
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMainScreen.setVisibility((llMainScreen.getVisibility() == View.GONE)?View.VISIBLE:View.GONE);
                llMeasureScreen.setVisibility((llMeasureScreen.getVisibility() == View.VISIBLE)?View.GONE:View.VISIBLE);
            }
        });

        llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMainScreen.setVisibility((llMainScreen.getVisibility() == View.VISIBLE)?View.GONE:View.VISIBLE);
                llMeasureScreen.setVisibility((llMeasureScreen.getVisibility() == View.GONE)?View.VISIBLE:View.GONE);
            }
        });
        initSpnTime();

        if(tempObj != null){//EDIT
            setUpToEditUsers();
        }else{//NEW
            cbActivate.setChecked(true);
        }


        fillMeasures();


        spnFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV familia = (KV)spnFamily.getSelectedItem();
                ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnGroup, false, familia.getKey());


                if(firstTime){//para que seleccione el subType del producto automaticamente la primera vez que abra el dialogo.
                    firstTime= false;
                    if(tempObj != null){
                        setSpinnerposition(spnGroup, tempObj.getSUBTYPE());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean validate(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnFamily.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione una familia.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnGroup.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione un grupo.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(selected.size() == 0){
            Snackbar.make(getView(), "Seleccione por lo menos 1 unidad de medida", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public void Save(){

        if(validate()) {
            SaveProduct();
        }else{
            llSave.setEnabled(true);
        }
    }

    public void SaveProduct(){
        try {
            String code = etCode.getText().toString();
            String description = etName.getText().toString();
            String menuDescription = etDescription.getText().toString();
            String productType = ((KV)spnFamily.getSelectedItem()).getKey();
            String productSubType = ((KV)spnGroup.getSelectedItem()).getKey();
            String prepTime = (etTime.getText().toString().isEmpty())?"":(etTime.getText().toString()+"-"+((KV)spnTime.getSelectedItem()).getKey());
            Products product = new Products(code, description,menuDescription, productType, productSubType,prepTime, true, false);

            ArrayList<ProductsMeasure> list = new ArrayList<>();
            for(EditSelectionRowModel ssrm: selected){
                list.add(new ProductsMeasure(Funciones.generateCode(), code, ssrm.getCode(),Double.parseDouble(ssrm.getText()),true, null, null));
            }

            productsController.sendToFireBase(product, list);


            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditProduct(){
        try {
            Products products = ((Products)tempObj);
            products.setDESCRIPTION(etName.getText().toString());
            products.setMENUDESCRIPTION(etDescription.getText().toString());
            products.setTYPE(((KV)spnFamily.getSelectedItem()).getKey());
            products.setSUBTYPE(((KV)spnGroup.getSelectedItem()).getKey());
            products.setENABLED(cbActivate.isChecked());
            String prepTime = (etTime.getText().toString().isEmpty())?"":(etTime.getText().toString()+"-"+((KV)spnTime.getSelectedItem()).getKey());
            products.setPREPTIME(prepTime);
            products.setMDATE(null);

            ArrayList<ProductsMeasure> list = new ArrayList<>();
            for(EditSelectionRowModel ssrm: selected){
                list.add(new ProductsMeasure(Funciones.generateCode(), products.getCODE(), ssrm.getCode(),Double.parseDouble(ssrm.getText()),true, null, null));
            }

            productsController.sendToFireBase(products, list);

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditUsers(){
        Products p = ((Products)tempObj);
        etCode.setText(p.getCODE());
        etCode.setEnabled(false);
        etName.setText(p.getDESCRIPTION());
        etDescription.setText(p.getMENUDESCRIPTION());
        cbActivate.setChecked(tempObj.isENABLED());
        setSpinnerposition(spnFamily, p.getTYPE());
        setSpinnerposition(spnGroup, p.getSUBTYPE());

        if(p.getPREPTIME() != null && p.getPREPTIME().split("-").length>1){
            String[]time = p.getPREPTIME().split("-");
            etTime.setText(time[0]);
            setSpinnerposition(spnTime, time[1]);
        }


    }

    public void setSpinnerposition(Spinner spn, String key){
        for(int i = 0; i< spn.getAdapter().getCount(); i++){
            if(((KV)spn.getAdapter().getItem(i)).getKey().equals(key)){
                spn.setSelection(i);
                break;
            }
        }
    }




    @Override
    public void onFailure(@NonNull Exception e) {
        //Funciones.showNetworkErrorWithText(getView(), e.getMessage());
        llSave.setEnabled(true);
    }

    public void fillMeasures(){

        if(tempObj != null) {
            selected.addAll(ProductsMeasureController.getInstance(getActivity()).getSSRMByCodeProduct((tempObj).getCODE()));
        }
        ArrayList<EditSelectionRowModel> arr = null;
        arr =  MeasureUnitsController.getInstance(getActivity()).getUnitMeasuresSSRM(null, null, null);

        rvMeasures.setAdapter(new EditSelectionRowAdapter(getActivity(),arr, selected));
        rvMeasures.getAdapter().notifyDataSetChanged();
        rvMeasures.invalidate();
    }

    OnFailureListener onFailureSerachProduct = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
        showErrorDialog(e.getMessage());
        llSave.setEnabled(true);
        closeLoadingDialog();
        }
    };

    OnSuccessListener<QuerySnapshot> onSuccessSeachProduct = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {

            if(querySnapshot != null && querySnapshot.getDocuments().size() >0){
            showErrorDialog("No puede editar productos que esten actualmente en ventas (Abiertas o Entregadas)");
            }else{
                EditProduct();
            }

            llSave.setEnabled(true);
            closeLoadingDialog();
        }
    };

    /**
     * validamos que el producto a editar no este en una venta (Abierta, entregada, anulada) es decir no historica
     * @param codeProduct
     */
    public void searchProduct(String codeProduct){
       // SalesController.getInstance(getActivity()).searchProductInSalesDetail(codeProduct,onSuccessSeachProduct, onFailureSerachProduct);
    }

    public void showLoadingDialog(){
        loadingDialg = null;
        loadingDialg = Funciones.getLoadingDialog(getActivity(),"Loading...");
        loadingDialg.show();
    }

    public void closeLoadingDialog(){
        if(loadingDialg != null){
            loadingDialg.dismiss();
        }
    }

    public void showErrorDialog(String msg){
        errorDialog = null;
        errorDialog = Funciones.getCustomDialog(getActivity(),getResources().getColor(R.color.red_700), "Error", msg, R.drawable.ic_error_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.dismiss();
                errorDialog = null;
            }
        });
        errorDialog.setCancelable(false);
        errorDialog.show();
    }

    public void initSpnTime(){
        ArrayList<KV> time = new ArrayList<>();
        time.add(new KV("M", "Minutos"));
        time.add(new KV("H", "Horas"));
        ArrayAdapter<KV>adapter = new ArrayAdapter<KV>(getActivity(), android.R.layout.simple_list_item_1, time);
        spnTime.setAdapter(adapter);
    }
}
