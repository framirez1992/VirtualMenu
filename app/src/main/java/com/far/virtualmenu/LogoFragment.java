package com.far.virtualmenu;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.far.virtualmenu.CloudFireStoreObjects.UserControl;
import com.far.virtualmenu.Controllers.UserControlController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogoFragment extends Fragment {

    MainMenuActivity parent;

    LinearLayout llLoading;
    TextView tvLoading, tvErrorMsg;
    Button btnRetry;



    public LogoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llLoading = view.findViewById(R.id.llLoading);
        tvLoading = view.findViewById(R.id.tvLoading);
        tvErrorMsg = view.findViewById(R.id.tvErrorMsg);
        btnRetry = view.findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMenuType();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getMenuType();
    }

    public void setParentActivity(MainMenuActivity parent){
        this.parent = parent;
    }

    public void getMenuType(){
        showLoading();
        UserControlController.getInstance(getContext()).getReferenceFireStore()
                .whereEqualTo(UserControlController.CONTROL, "MENUTYPE").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                UserControl uc = null;
                UserControlController.getInstance(getContext()).delete(null, null);
                if(querySnapshot!= null && querySnapshot.size() > 0){
                    uc = querySnapshot.getDocuments().get(0).toObject(UserControl.class);
                }

                if(uc != null){
                    UserControlController.getInstance(getContext()).insert(uc);
                    parent.changeMenu(uc.getVALUE());
                }else{
                endLoading();
                setErrorMessage("No se ha configurado un tipo de menu.");
                btnRetry.setVisibility(View.VISIBLE);
                btnRetry.setEnabled(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                endLoading();
                setErrorMessage(e.getMessage());
                btnRetry.setVisibility(View.VISIBLE);
                btnRetry.setEnabled(true);
            }
        });
    }

    public void showLoading(){
        tvErrorMsg.setText("");
        tvLoading.setText("Cargando datos...");
        btnRetry.setVisibility(View.INVISIBLE);
        btnRetry.setEnabled(false);
        llLoading.setVisibility(View.VISIBLE);
    }

    public void endLoading(){
        llLoading.setVisibility(View.INVISIBLE);
    }
    public void setLoadingMessage(String msg){
        tvLoading.setText(msg);
    }

    public void setErrorMessage(String msg){
        tvErrorMsg.setText(msg);
        tvErrorMsg.setVisibility(View.VISIBLE);
    }


}
