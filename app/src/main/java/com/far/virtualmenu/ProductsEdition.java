package com.far.virtualmenu;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.far.virtualmenu.Adapters.ProductsAdapter;
import com.far.virtualmenu.Model.ProductModel;
import com.far.virtualmenu.Utils.Funciones;
import com.far.virtualmenu.interfaces.ListableActivity;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsEdition extends Fragment {

    Activity parent;
    RecyclerView rvList;
    ImageView imgSeach, imgHideSearch;
    EditText etSearch;
    LinearLayout llSearch;
    String lastSerach;

    public ProductsEdition() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.products_edition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(parent);
        rvList.setLayoutManager(manager);

        imgHideSearch = view.findViewById(R.id.imgHideSearch);
        imgSeach = view.findViewById(R.id.imgSearch);
        llSearch = view.findViewById(R.id.llSearch);
        etSearch = view.findViewById(R.id.etSearch);

        imgSeach.setVisibility(View.VISIBLE);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(etSearch.getText().toString().trim().equals("")){
                        return false;
                    }
                    lastSerach = etSearch.getText().toString();
                    imgHideSearch.performClick();

                    refreshList(lastSerach);

                    return true;

                }
                return false;
            }
        });
        imgSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgSeach.setVisibility(View.GONE);
                llSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
                etSearch.setText("");
                Funciones.showKeyBoard(etSearch);
            }
        });

        imgHideSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Funciones.hideKeyBoard(etSearch, parent);
                imgSeach.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
            }
        });

        refreshList("");
    }

    public void setParent(Activity activity){
        this.parent = activity;
    }


    public void refreshList(String data){
      /*  objects.clear();
        String where = " 1 = 1 ";
        ArrayList<String> values = new ArrayList<>();
        String[] args = null;

            if(data != null){
                where += " AND "+ProductsTypesController.DESCRIPTION+" like ?";
                values.add(data+"%");
            }
            args = values.toArray(new String[values.size()]);

            objects.addAll(productsTypesController.getAllProductTypesSRM(where, args));

        adapter.notifyDataSetChanged();*/
        ArrayList<ProductModel> values = new ArrayList<>();
        values.add(new ProductModel("0", "Pulpo en salsa", true));
        values.add(new ProductModel("1", "Salmon ahumado", true));
        values.add(new ProductModel("2", "Cerdo Agridulce", true));
        values.add(new ProductModel("3", "Bistec encebollado", true));
        values.add(new ProductModel("4", "Coctel de frutas", true));
        values.add(new ProductModel("5", "Helado friito", true));
        values.add(new ProductModel("6", "Helado de coco", true));
        ProductsAdapter pa = new ProductsAdapter(parent,(ListableActivity)parent, values);
        rvList.setAdapter(pa);
        rvList.invalidate();
    }

}
