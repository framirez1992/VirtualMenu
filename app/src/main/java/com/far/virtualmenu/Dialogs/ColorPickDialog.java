package com.far.virtualmenu.Dialogs;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.far.virtualmenu.Adapters.ColorsAdapter;
import com.far.virtualmenu.Adapters.Models.ColorModel;
import com.far.virtualmenu.Adapters.Models.SimpleRowModel;
import com.far.virtualmenu.Generic.KV;
import com.far.virtualmenu.R;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class ColorPickDialog extends DialogFragment implements ListableActivity {
    String white = "#FFFFFF";
    String black="#000000";
   /* Rojo
            #E57373 300
            #EF5350 red 400
            #F44336 red 500
            #E53935 600
            #D32F2F 700
            #B71C1C 900
            #FF5252 coral
            #D50000 ladrillo*/
    String[]rojos =new String[]{"#E57373","#EF5350", "#F44336", "#E53935", "#D32F2F", "#B71C1C", "#FF5252", "#D50000"};



   /* Rosa
            #F8BBD0 100
            #F48FB1 200
            #F06292 300
            #EC407A 400
            #E91E63 500
            #AD1457 800
            #880E4F 900
            #EA80FC fuxia
            #E040FB a200
            #D500F9 a400
            #AA00FF A700*/
   String[]rosas =new String[]{"#EC407A", "#E91E63", "#AD1457", "#880E4F", "#E040FB", "#D500F9", "#AA00FF"};

   /* Morado
            #CE93D8 200
            #AB47BC 400
            #9C27B0 500
            #6A1B9A 800
            #4A148C 900

            #B39DDB deep200
            #673AB7 deep 500
            #4527A0 deep 800
            #311B92 deep 900
            #6200EA A700*/
    String[]morados =new String[]{"#AB47BC", "#9C27B0", "#6A1B9A", "#4A148C",  "#673AB7", "#4527A0","#311B92", "#6200EA" };

   /* Indigo
            #9FA8DA 200
            #5C6BC0 400
            #3F51B5 500
            #283593 800
            #1A237E 900

            #8C9EFF a100
            #536DFE a200
            #3D5AFE a400
            #304FFE a700*/
    String[]indigos =new String[]{"#5C6BC0", "#3F51B5", "#283593", "#1A237E", "#8C9EFF", "#536DFE", "#3D5AFE", "#304FFE"};

   /* blue
            #BBDEFB 100
            #90CAF9 200
            #42A5F5 400
            #2196F3 500
            #1E88E5 600
            #1565C0 800
            #0D47A1 900
            #2979FF a400
            #2962FF a700*/
    String[]azules =new String[]{ "#42A5F5", "#2196F3", "#1E88E5", "#1565C0", "#0D47A1", "#2979FF", "#2962FF"};

    /*
     light blue
            #B3E5FC 100
            #81D4FA 200
            #4FC3F7 300
            #29B6F6 400
            #03A9F4 500
            #0288D1 700
            #0277BD 800
            #01579B 900
            #00B0FF a400
            #0091EA a700
     */
    String[]azules_claros =new String[]{"#0288D1", "#0277BD", "#01579B", "#00B0FF", "#0091EA"};

    /*
    cyan
            #B2EBF2 100
            #80DEEA 200
            #4DD0E1 300
            #26C6DA 400
            #00ACC1 600
            #00838F 800
            #006064 900
     */
    String[]cyan =new String[]{"#B2EBF2","#80DEEA", "#4DD0E1", "#26C6DA", "#00ACC1", "#00838F", "#006064"};

    /*
     teal
            #B2DFDB 100
            #80CBC4 200
            #4DB6AC 300
            #26A69A 400
            #009688 500
            #00796B 700
            #004D40 900
            #64FFDA a200
            #1DE9B6 a400
            #00BFA5 a700
     */
    String[]teals =new String[]{"#26A69A", "#009688", "#00796B", "#004D40", "#64FFDA", "#1DE9B6", "#00BFA5"};

     /*   green
            #4CAF50
            #2E7D32
            #1B5E20
            #8BC34A
            #558B2F
            #33691E
            #76FF03
            #64DD17
            #CDDC39
            #9E9D24
            #827717*/
     String[]green =new String[]{"#4CAF50", "#2E7D32", "#1B5E20", "#8BC34A", "#558B2F", "#33691E", "#76FF03", "#64DD17", "#CDDC39", "#9E9D24", "#827717"};

     /*yellow
            #FFEB3B
            #F9A825
            #F57F17
            #FFFF00*/

    String[]yellow =new String[]{"#FFEB3B", "#F9A825", "#F57F17", "#FFFF00"};
        /*    orange
            #FF8F00
            #E65100
            #FF5722
            #D84315
            #FF3D00
            #DD2C00
*/
    String[]orange =new String[]{"#FF8F00", "#E65100", "#FF5722", "#D84315", "#FF3D00", "#DD2C00"};
         /*   brown
            #795548
            #4E342E
            #3E2723*/
    String[]brown =new String[]{"#795548", "#4E342E", "#3E2723"};
    /*gray
            #BDBDBD
            #9E9E9E
            #616161
            #424242
            #212121*/
    String[]gray =new String[]{"#BDBDBD", "#9E9E9E", "#616161", "#424242", "#212121"};


    Activity parent;
    View viewToApply;
    RecyclerView rvList;
    CardView btnOK, btnCancel;
    ColorModel selectedColor;

    public  static ColorPickDialog newInstance(Activity parent, View viewToApply)  {

        ColorPickDialog f = new ColorPickDialog();
        f.parent = parent;
        f.viewToApply = viewToApply;

        // Supply num input as an argument.
        Bundle args = new Bundle();
       /* if(pi != null) {
            f.setArguments(args);
        }*/

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.dialog_list_2btn, container, true);
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
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }


    public void init( View view){
        //rvList = view.findViewById(R.id.rvList);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new GridLayoutManager(parent, 5));
        btnOK = view.findViewById(R.id.btnOk);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedColor != null){
                    viewToApply.setBackgroundColor(Color.parseColor(selectedColor.getHexColor()));
                    dismiss();
                }else{
                    Snackbar.make(getView(), "Seleccione un color", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });

        fillList();



    }


    @Override
    public void onClick(Object obj) {
        selectedColor = (ColorModel)obj;
    }




    public void fillList(){
        ArrayList<ColorModel> arr = new ArrayList<>();
        arr.add(new ColorModel(white, false));

        for(String color: rojos){
            arr.add(new ColorModel(color, false));
        }
        for(String color: rosas){
            arr.add(new ColorModel(color, false));
        }
        for(String color: morados){
            arr.add(new ColorModel(color, false));
        }
        for(String color: indigos){
            arr.add(new ColorModel(color, false));
        }
        for(String color: azules){
            arr.add(new ColorModel(color, false));
        }
        for(String color: azules_claros){
            arr.add(new ColorModel(color, false));
        }
        for(String color: cyan){
            arr.add(new ColorModel(color, false));
        }

        for(String color: teals){
            arr.add(new ColorModel(color, false));
        }

        for(String color: green){
            arr.add(new ColorModel(color, false));
        }
        for(String color: yellow){
            arr.add(new ColorModel(color, false));
        }
        for(String color: orange){
            arr.add(new ColorModel(color, false));
        }
        for(String color: brown){
            arr.add(new ColorModel(color, false));
        }
        for(String color: gray){
            arr.add(new ColorModel(color, false));
        }
        arr.add(new ColorModel(black, false));

        rvList.setAdapter(new ColorsAdapter(parent,ColorPickDialog.this, arr));
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();
    }
}
