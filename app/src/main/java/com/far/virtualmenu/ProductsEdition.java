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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.virtualmenu.Adapters.Models.ProductRowModel;
import com.far.virtualmenu.Adapters.ProductsAdapter;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Generic.KV;
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
    Spinner spnProductType, spnProductSubType;
    ImageView imgSeach, imgHideSearch;
    EditText etSearch;
    LinearLayout llSearch;
    String lastSearch;
    String lastFamilia, lastGrupo;

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

        spnProductType = view.findViewById(R.id.spn);
        spnProductSubType = view.findViewById(R.id.spn2);
        ((TextView)view.findViewById(R.id.spnTitle)).setText("Familia");
        ((TextView)view.findViewById(R.id.spnTitle2)).setText("Grupo");

        ProductsTypesController.getInstance(getActivity()).fillSpinner(spnProductType,true);
        ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnProductSubType,true);


        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(etSearch.getText().toString().trim().equals("")){
                        return false;
                    }
                    lastSearch = etSearch.getText().toString();
                    imgHideSearch.performClick();

                    refreshList();

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


        spnProductType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV familia = (KV)spnProductType.getSelectedItem();
                lastFamilia = (familia.getKey().equals("0"))?null: familia.getKey();
                ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnProductSubType, true, familia.getKey());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnProductSubType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV grupo = (KV)spnProductSubType.getSelectedItem();
                lastGrupo = (grupo.getKey().equals("0"))?null:grupo.getKey() ;
                refreshList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        refreshList();
    }

    public void setParent(Activity activity){
        this.parent = activity;
    }


  /*  public void refreshList(String data){
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
    }*/

    public void refreshList(){
        String where = "1 = 1 ";
        String[] args = null;

        ArrayList<String> x = new ArrayList<>();
        if(lastSearch != null){
            where+=" AND p."+ProductsController.DESCRIPTION+" like  ? ";
            x.add(lastSearch+"%");
        }
        if(lastFamilia != null){
            where+= "AND pt."+ ProductsTypesController.CODE+" = ? ";
            x.add(lastFamilia);
        }

        if(lastGrupo != null){
            where+= "AND pst."+ProductsSubTypesController.CODE+" = ? ";
            x.add(lastGrupo);
        }

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }
        ArrayList<ProductModel> values = ProductsController.getInstance(getActivity()).getProductsRM(where, args, null);
        ProductsAdapter pa = new ProductsAdapter(parent,(ListableActivity)parent, values);
        rvList.setAdapter(pa);
        rvList.invalidate();
    }


}
