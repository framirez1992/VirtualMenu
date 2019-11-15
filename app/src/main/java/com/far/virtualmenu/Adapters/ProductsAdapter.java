package com.far.virtualmenu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.far.virtualmenu.Model.ProductModel;
import com.far.virtualmenu.R;
import com.far.virtualmenu.interfaces.ListableActivity;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductHolder> {

    ArrayList<ProductModel> objects;
    ListableActivity listableActivity;
    Activity activity;
    public ProductsAdapter(Activity activity, ListableActivity la, ArrayList<ProductModel> objects){
        this.activity = activity;
        this.objects = objects;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ProductHolder(inflater.inflate(R.layout.product_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, final int position) {

        holder.fillData(objects.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             listableActivity.onClick(objects.get(position));
                activity.registerForContextMenu(v);
                v.showContextMenu();
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }




    public class ProductHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvImages;
        CheckBox cbEnabled;

        public ProductHolder(View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvImages = itemView.findViewById(R.id.tvImages);
            cbEnabled = itemView.findViewById(R.id.cbEnabled);
        }

        public void fillData(ProductModel pm){
            tvDescription.setText(pm.getDescription());
            tvImages.setText(pm.getImages()!=null?pm.getImages().size()+"":"0");
            cbEnabled.setChecked(pm.isEnabled());

        }
    }
}
