package com.far.virtualmenu;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.far.virtualmenu.Adapters.GridAdapter;
import com.far.virtualmenu.Adapters.MenuDetailGridAdapter;
import com.far.virtualmenu.CloudFireStoreObjects.Company;
import com.far.virtualmenu.CloudFireStoreObjects.MenuType;
import com.far.virtualmenu.CloudFireStoreObjects.ProductsControl;
import com.far.virtualmenu.Controllers.CompanyController;
import com.far.virtualmenu.Controllers.DownloadRequestController;
import com.far.virtualmenu.Controllers.MenuTypeController;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Model.ItemMenuDetailModel;
import com.far.virtualmenu.Utils.CODES;
import com.far.virtualmenu.Utils.Funciones;
import com.far.virtualmenu.Utils.Picasso.CircleTransformation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GridFragment extends Fragment {


    MainMenuActivity parentActivity;
    GridView gridView;
    ImageView logo;

    public GridFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = view.findViewById(R.id.grid);
        logo = view.findViewById(R.id.logo);

        setLogo();
        fillGrid();
    }

    @Override
    public void onStart() {
        super.onStart();

        setOrientation();

        DownloadRequestController.getInstance(parentActivity).getReferenceFireStore()
                .document(Funciones.getPhoneID(parentActivity)).addSnapshotListener(parentActivity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                DownloadRequestController.getInstance(parentActivity).delete(null, null);
                if(documentSnapshot.getData()!= null){
                    downloadData();
                }
            }
        });
    }

    public void setParentActivity(MainMenuActivity mainUserMenu){
        this.parentActivity = mainUserMenu;
    }

    public void fillGrid(){

        int lastRow = 0;
        MenuType menuType = MenuTypeController.getInstance(parentActivity).getMenuType();

        if(menuType.getLAYOUT().equals(CODES.CODE_MENUTYPE_LAYOUT_ROW1)){
            lastRow = R.layout.single_item_menu;
            gridView.setNumColumns(GridView.AUTO_FIT);
        }else if(menuType.getLAYOUT().equals(CODES.CODE_MENUTYPE_LAYOUT_ROW2)){
            lastRow = R.layout.single_item_menu2;
            gridView.setNumColumns(1);
        }


        ArrayList<ItemMenuDetailModel> array = ProductsController.getInstance(parentActivity).getItemMenuDetailModels();
        MenuDetailGridAdapter adapter = new MenuDetailGridAdapter(parentActivity,array, lastRow);
        gridView.setAdapter(adapter);
        gridView.invalidate();
    }

    public void downloadData(){
        parentActivity.setLoadingScreen();
    }

    public void setLogo(){
        String url = null;
        ArrayList<Company> list = CompanyController.getInstance(parentActivity).getCompanys(null, null, null);
        for(Company c: list){
            if(c.getLOGO()!= null && !c.getLOGO().isEmpty()){
                url = c.getLOGO();
                break;
            }
        }

        if(url!= null){
            Picasso.with(parentActivity).load(url).transform(new CircleTransformation()).into(logo);
        }


    }


    public void setOrientation(){
        MenuType menuType = MenuTypeController.getInstance(parentActivity).getMenuType();
        if(menuType.getORIENTATION() == CODES.CODE_MENUTYPE_ORIENTATION_LANDSCAPE){
            Funciones.setOrientationLandscape(parentActivity);
        }else if(menuType.getORIENTATION() == CODES.CODE_MENUTYPE_ORIENTATION_PORTRAIT){
            Funciones.setOrientationPortrait(parentActivity);
        }
    }


}
