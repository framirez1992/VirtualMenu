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
import com.far.virtualmenu.Model.ItemMenuDetailModel;

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
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //parentActivity.changeMenu(1);
            }
        });

        fillGrid();
    }

    public void setParentActivity(MainMenuActivity mainUserMenu){
        this.parentActivity = mainUserMenu;
    }

    public void fillGrid(){

        ArrayList<ItemMenuDetailModel> array = new ArrayList<>();
        array.add(new ItemMenuDetailModel("Combo 1","Amburguesa + papas pequena + refresco 12oz", 200, "https://i0.wp.com/enzos.gofoodpng.biz/wp-content/uploads/2018/03/Chips-and-a-330ml.png?fit=484%2C368&ssl=1"));
        array.add(new ItemMenuDetailModel("Combo 2","2 Tacos de pollo + Papas fritas + Refresco 12oz", 250, "https://www.deltaco.com/files/menu/item/thumb_1.jpg?v=2.6"));
        array.add(new ItemMenuDetailModel("Combo 3","Hot dog + papas mediana + refresco 12oz", 180, "https://defia.es/291-medium_default/combo-hot-dog.jpg"));
        array.add(new ItemMenuDetailModel("Combo 4","2 Pizza medianas + Papotas + Vegetales + Refresco 60oz", 1200, "https://5.imimg.com/data5/VE/GG/MY-53040683/combo-for-family-28just-499-2f-29-500x500.jpg"));
        array.add(new ItemMenuDetailModel("Banana Split","Banana split + toping + mermelada", 120, "https://www.tasteofhome.com/wp-content/uploads/2017/10/exps37953_Page_23.jpg"));

        MenuDetailGridAdapter adapter = new MenuDetailGridAdapter(parentActivity,array);
        gridView.setAdapter(adapter);
        gridView.invalidate();
    }

    public void refresh(){

    }

}
