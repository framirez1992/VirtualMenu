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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.far.virtualmenu.CloudFireStoreObjects.MeasureUnits;
import com.far.virtualmenu.Controllers.MeasureUnitsController;
import com.far.virtualmenu.R;
import com.far.virtualmenu.Utils.Funciones;
import com.far.virtualmenu.interfaces.DialogCaller;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;


public class MeasureUnitDialogFragment extends DialogFragment implements OnFailureListener {

    private MeasureUnits tempObj;
    DialogCaller dialogCaller;

    LinearLayout llSave, llProgress;
    TextInputEditText etName;


    MeasureUnitsController measureUnitsController;

    public  static MeasureUnitDialogFragment newInstance(MeasureUnits pt, DialogCaller dialogCaller) {

        MeasureUnitDialogFragment f = new MeasureUnitDialogFragment();
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
        measureUnitsController = MeasureUnitsController.getInstance(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
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
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    public void init(View view){
        llProgress = view.findViewById(R.id.llProgress);
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);
        ((EditText)view.findViewById(R.id.etOrden)).setVisibility(View.GONE);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                   EditMeasureUnit();
                }
            }
        });

        if(tempObj != null){//EDIT
                setUpToEditMeasureUnits();
        }
    }

    public boolean validate(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void Save(){

            if(validate()) {
                SaveMeasureUnit();
            }else{
                llSave.setEnabled(true);
            }
    }

    public void SaveMeasureUnit(){
        try {
            String code =Funciones.generateCode();
            String name = etName.getText().toString();
            MeasureUnits pt = new MeasureUnits(code, name);
            pt.setDATE(new Date());
            pt.setMDATE(new Date());
            measureUnitsController.sendToFireBase(pt, this);
            measureUnitsController.searchMeasureUnitFromFireBase(pt.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    MeasureUnits measureUnits =null;
                    if(querySnapshot!= null && querySnapshot.size()>0){
                        measureUnits = querySnapshot.getDocuments().get(0).toObject(MeasureUnits.class);
                    }

                    if(measureUnits!= null){
                        MeasureUnitsController.getInstance(getContext()).insert(measureUnits);
                        dialogCaller.dialogClosed(measureUnits);
                        dismiss();
                    }else{
                        failure("Error guardando medida. Intente nuevamente");
                    }
                }
            }, this);

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditMeasureUnit(){
        try {
            MeasureUnits mu = ((MeasureUnits)tempObj);
            mu.setDESCRIPTION(etName.getText().toString());
            mu.setMDATE(new Date());
            measureUnitsController.sendToFireBase(mu, this);
            measureUnitsController.searchMeasureUnitFromFireBase(mu.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    MeasureUnits measureUnits =null;
                    if(querySnapshot!= null && querySnapshot.size()>0){
                        measureUnits = querySnapshot.getDocuments().get(0).toObject(MeasureUnits.class);
                    }

                    if(measureUnits!= null){
                        MeasureUnitsController.getInstance(getContext()).update(measureUnits);
                        dialogCaller.dialogClosed(measureUnits);
                        dismiss();
                    }else{
                        failure("Error guardando medida. Intente nuevamente");
                    }
                }
            }, this);

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditMeasureUnits(){
        etName.setText(((MeasureUnits)tempObj).getDESCRIPTION());

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
