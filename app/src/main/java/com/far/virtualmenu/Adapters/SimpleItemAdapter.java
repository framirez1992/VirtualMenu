package com.far.virtualmenu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.far.virtualmenu.Model.ItemModel;
import com.far.virtualmenu.R;
import com.far.virtualmenu.interfaces.ListableActivity;

import java.util.ArrayList;


public class SimpleItemAdapter extends RecyclerView.Adapter<SimpleItemAdapter.ItemHolder> {

    ArrayList<ItemModel> objects;
    Context context;
    ListableActivity listActivity;
    boolean newAdapter =false;//
    int lastPos=0;
    public SimpleItemAdapter(Context context, ListableActivity act, ArrayList<ItemModel> objects){
        this.context = context;
        this.listActivity = act;
        this.objects = objects;
        this.newAdapter = true;
    }
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ItemHolder(inflater.inflate(R.layout.simple_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, final int position) {

        holder.fillData(objects.get(position));
        if(!objects.get(position).isHeader()){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected(position);
                    listActivity.onClick(objects.get(position));
                }
            });
        }else{
            holder.itemView.setOnClickListener(null);
        }


    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void selected(int pos){
        for(ItemModel it: objects){
            if(!it.isHeader()){
                it.setHexBackground("#FFFFFF");
            }
        }

      objects.get(pos).setHexBackground("#9E9E9E");
      notifyDataSetChanged();
    }

    public int getLastPos(){
        return lastPos;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        TextView text;
        CardView cvParent;
        public ItemHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tvText);
            cvParent = itemView.findViewById(R.id.cvParent);
        }

        public void fillData(ItemModel im){
            text.setText(im.getTitle());
            cvParent.setCardBackgroundColor(Color.parseColor(im.getHexBackground()));
            if(im.isHeader()){
                text.setTextSize(20);
                text.setTextColor(Color.WHITE);
                text.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) cvParent.getLayoutParams();
                layoutParams.setMargins(0, 20,0, 0);
                cvParent.requestLayout();
            }else{
                text.setTextSize(16);
                text.setTextColor(Color.BLACK);
                text.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) cvParent.getLayoutParams();
                layoutParams.setMargins(0, 0,0, 0);
                cvParent.requestLayout();
            }

        }
    }
}
