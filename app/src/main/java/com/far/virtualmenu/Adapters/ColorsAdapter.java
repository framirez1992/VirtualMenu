package com.far.virtualmenu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.far.virtualmenu.Adapters.Models.ColorModel;
import com.far.virtualmenu.R;
import com.far.virtualmenu.interfaces.ListableActivity;

import java.util.ArrayList;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ColorHolder> {

    ListableActivity listableActivity;
    ArrayList<ColorModel> objects;
    Context context;
    public ColorsAdapter(Context context, ListableActivity la, ArrayList<ColorModel> objects){
        this.context = context;
        this.objects = objects;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public ColorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ColorHolder(inflater.inflate(R.layout.color_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorHolder holder, final int position) {

        holder.fillData(objects.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(position);
                listableActivity.onClick(objects.get(position));
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    private void setSelected(int pos){
        for(ColorModel cm: objects){
            cm.setSelected(false);
        }
        objects.get(pos).setSelected(true);
    }


    public class ColorHolder extends RecyclerView.ViewHolder {
        CardView cvColor;
        ImageView img;
        public ColorHolder(View itemView) {
            super(itemView);
            cvColor = itemView.findViewById(R.id.cvColor);
            img = itemView.findViewById(R.id.img);
        }

        public void fillData(ColorModel cm){
           cvColor.setCardBackgroundColor(Color.parseColor(cm.getHexColor()));
           img.setVisibility(cm.isSelected()?View.VISIBLE:View.GONE);
        }
    }
}
