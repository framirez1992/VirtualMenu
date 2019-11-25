package com.far.virtualmenu.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.far.virtualmenu.CloudFireStoreObjects.ProductImage;
import com.far.virtualmenu.R;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageHolder> {

    ListableActivity listableActivity;
    ArrayList<ProductImage> objects;
    Context context;
    public ImagesAdapter(Context context, ListableActivity la, ArrayList<ProductImage> objects){
        this.context = context;
        this.objects = objects;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ImageHolder(inflater.inflate(R.layout.image_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, final int position) {

        holder.fillData(objects.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listableActivity.onClick(objects.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }



    public class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvOrder;
        public ImageHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            tvOrder = itemView.findViewById(R.id.tvOrder);
        }

        public void fillData(ProductImage pi){
            tvOrder.setText(pi.getORDEN()+"");
            Picasso.with(context).load(pi.getURL()).into(imageView);
        }
    }
}
