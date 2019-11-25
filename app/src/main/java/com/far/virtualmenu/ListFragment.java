package com.far.virtualmenu;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.far.virtualmenu.Adapters.SimpleItemAdapter;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Model.ItemModel;
import com.far.virtualmenu.Model.PriceModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {


    RecyclerView rvList;
    MainMenuActivity parentActivity;
    boolean firstLoad = false;
    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.firstLoad = true;
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(parentActivity));

        fillData();
    }


    public void setParentActivity(MainMenuActivity mainUserMenu){
        this.parentActivity = mainUserMenu;
    }

    public void fillData(){
        ArrayList<ItemModel> list  = ProductsController.getInstance(parentActivity).getItemModelMenu();
        SimpleItemAdapter adapter = new SimpleItemAdapter(parentActivity,parentActivity,list);
        rvList.setAdapter(adapter);
        rvList.invalidate();

        if(firstLoad){
            firstLoad = false;
            //dar click automaticamente al primero producto cuando se llene la lista.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(rvList.findViewHolderForAdapterPosition(1)!= null){
                        rvList.findViewHolderForAdapterPosition(1).itemView.performClick();
                    }
                }
            },200);
        }


    }
}
