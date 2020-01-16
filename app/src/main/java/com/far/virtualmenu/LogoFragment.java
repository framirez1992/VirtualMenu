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

import com.far.virtualmenu.CloudFireStoreObjects.Company;
import com.far.virtualmenu.CloudFireStoreObjects.DownloadRequest;
import com.far.virtualmenu.CloudFireStoreObjects.MenuType;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsControl;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsTypes;
import com.far.virtualmenu.CloudFireStoreObjects.UserControl;
import com.far.virtualmenu.Controllers.CompanyController;
import com.far.virtualmenu.Controllers.DownloadRequestController;
import com.far.virtualmenu.Controllers.MeasureUnitsController;
import com.far.virtualmenu.Controllers.MenuTypeController;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Controllers.ProductsImagesController;
import com.far.virtualmenu.Controllers.ProductsMeasureController;
import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Controllers.UserControlController;
import com.far.virtualmenu.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogoFragment extends Fragment implements OnSuccessListener<QuerySnapshot>, OnFailureListener {

    MainMenuActivity parent;

    LinearLayout llLoading;
    TextView tvLoading, tvErrorMsg;
    Button btnRetry;
    MenuType menuType = null;
    int index = 0;



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
                if(menuType == null){
                    getMenuType();
                }else{
                    searchDownloadRequest();
                }

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
        MenuTypeController.getInstance(getContext()).getReferenceFireStore().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                MenuType mt = null;
                MenuTypeController.getInstance(getContext()).delete(null, null);
                if(querySnapshot!= null && querySnapshot.size() > 0){
                    mt = querySnapshot.getDocuments().get(0).toObject(MenuType.class);
                }

                if(mt != null){
                    MenuTypeController.getInstance(getContext()).insert(mt);
                    menuType =mt;
                   searchDownloadRequest();
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

    public void searchDownloadRequest(){
        showLoading();
        DownloadRequestController.getInstance(parent).getReferenceFireStore().document(Funciones.getPhoneID(parent)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //PRIMERA VEZ
                if(documentSnapshot.getData() == null && ProductsController.getInstance(parent).getProducts(null, null, null).size() == 0){
                    endLoading();
                    setErrorMessage("No hay carga de datos para el dispositivo. Comuniquese con el administrador");
                    btnRetry.setVisibility(View.VISIBLE);
                    btnRetry.setEnabled(true);
                }else if(documentSnapshot.getData()!= null){//SI HAY DATA SI SE ENVIA UNA CARGA DE DATOS ON Demand
                    DownloadRequest dr = documentSnapshot.toObject(DownloadRequest.class);
                    loadData();
                }else{
                    parent.changeMenu(menuType);
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

    public void loadData(){
        switch (index){
            case 0:
                ProductsTypesController.getInstance(parent).searchChanges(true,this, this); break;//familias
            case 1:
                ProductsSubTypesController.getInstance(parent).searchChanges(true,this, this); break;//grupos
            case 2:
                MeasureUnitsController.getInstance(parent).searchChanges(true, this, this); break;//medidas
            case 3:
                ProductsMeasureController.getInstance(parent).searchChanges(true, this, this); break;//products measure
            case 4:
                ProductsController.getInstance(parent).searchChanges(true, this, this); break;//products
            case 5:
                ProductsImagesController.getInstance(parent).searchChanges(true, this, this);  break;//productsImages
            case 6:
                CompanyController.getInstance(parent).searchChanges(true, this, this); break;//company
            default:
                index = 0;
                DownloadRequestController.getInstance(parent).getReferenceFireStore().document(Funciones.getPhoneID(parent)).delete().addOnFailureListener(this);
                searchDownloadRequest();
                return;
        }

    }


    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {
        switch (index){
            case 0:
                ProductsTypesController.getInstance(parent).consumeQuerySnapshot(true,querySnapshot); break;//familias
            case 1:
                ProductsSubTypesController.getInstance(parent).consumeQuerySnapshot(true,querySnapshot); break;//grupos
            case 2:
                MeasureUnitsController.getInstance(parent).consumeQuerySnapshot(true,querySnapshot); break;//medidas
            case 3:
                ProductsMeasureController.getInstance(parent).consumeQuerySnapshot(true,querySnapshot); break;//products measure
            case 4:
                ProductsController.getInstance(parent).consumeQuerySnapshot(true,querySnapshot); break;//products
            case 5:
                ProductsImagesController.getInstance(parent).consumeQuerySnapshot(true,querySnapshot); break;//productsImages
            case 6:
                CompanyController.getInstance(parent).consumeQuerySnapshot(true,querySnapshot); break;//company
        }

        index++;
        loadData();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        endLoading();
        setErrorMessage(e.getMessage());
        btnRetry.setVisibility(View.VISIBLE);
        btnRetry.setEnabled(true);
        index = 0;
    }
}
