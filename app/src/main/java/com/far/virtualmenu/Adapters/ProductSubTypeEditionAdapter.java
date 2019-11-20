package com.far.virtualmenu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.far.virtualmenu.Adapters.Models.ProductSubTypeRowModel;
import com.far.virtualmenu.R;
import com.far.virtualmenu.interfaces.ListableActivity;

import java.util.ArrayList;

public class ProductSubTypeEditionAdapter extends RecyclerView.Adapter<ProductSubTypeEditionAdapter.ProductSubTypeRowHolder> {

    Activity activity;
    ArrayList<ProductSubTypeRowModel> objects;
    ListableActivity listableActivity;
    public ProductSubTypeEditionAdapter(Activity act, ListableActivity la, ArrayList<ProductSubTypeRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public ProductSubTypeRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ProductSubTypeRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.product_sub_type_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductSubTypeRowHolder holder, final int position) {

        holder.fillData(objects.get(position));
        holder.getMenuImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.registerForContextMenu(v);
                v.showContextMenu();
                listableActivity.onClick(objects.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }


    public class ProductSubTypeRowHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetail, tvStatus;
        View vBackground, vTextColor;
        ImageView imgStatus, imgmenu, imgTime;
        public ProductSubTypeRowHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDetail = itemView.findViewById(R.id.tvDetail);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            vBackground = itemView.findViewById(R.id.vBackground);
            vTextColor = itemView.findViewById(R.id.vTextColor);
            imgmenu = itemView.findViewById(R.id.imgMenu);
            imgTime = itemView.findViewById(R.id.imgTime);
        }

        public void fillData(ProductSubTypeRowModel model){
            tvTitle.setText(model.getDescription());
            tvDetail.setText(model.getOrder());
            vBackground.setBackgroundColor(Color.parseColor(model.getHex1()));
            vTextColor.setBackgroundColor(Color.parseColor(model.getHex2()));
            imgStatus.setImageResource((model.isEnabled()?R.drawable.visible:R.drawable.invisible));
            tvStatus.setText((model.isEnabled()?"Activo":"Inactivo"));
            imgTime.setVisibility((model.isInServer())?View.INVISIBLE:View.VISIBLE);
        }

        public ImageView getMenuImage(){
            return imgmenu;
        }
    }

}

