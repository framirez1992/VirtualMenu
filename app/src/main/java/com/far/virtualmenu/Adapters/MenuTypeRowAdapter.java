package com.far.virtualmenu.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.far.virtualmenu.Adapters.Models.MenuTypeModel;
import com.far.virtualmenu.R;
import com.far.virtualmenu.interfaces.ListableActivity;

import java.util.ArrayList;

public class MenuTypeRowAdapter extends RecyclerView.Adapter<MenuTypeRowAdapter.MenuTypeHolder> {

    ListableActivity listableActivity;
    ArrayList<MenuTypeModel> objects;
    Context context;
    public MenuTypeRowAdapter(Context context, ListableActivity la, ArrayList<MenuTypeModel> objects){
        this.context = context;
        this.objects = objects;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public MenuTypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new MenuTypeHolder(inflater.inflate(R.layout.menu_type_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuTypeHolder holder, final int position) {

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
        for(MenuTypeModel cm: objects){
            cm.setSelected(false);
        }
        objects.get(pos).setSelected(true);
    }

    public MenuTypeModel getSelected(){
        for(MenuTypeModel cm: objects){
           if(cm.isSelected()){
               return cm;
           }
        }
        return null;
    }

    public class MenuTypeHolder extends RecyclerView.ViewHolder {
        RadioButton rb;
        TextView tvTitle, tvDescription;
        ImageView img;
        public MenuTypeHolder(View itemView) {
            super(itemView);
            rb = itemView.findViewById(R.id.rb);
            img = itemView.findViewById(R.id.img);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription  = itemView.findViewById(R.id.tvDescription);
        }

        public void fillData(MenuTypeModel cm){
            rb.setChecked(cm.isSelected());
            tvTitle.setText(cm.getTitle());
            tvDescription.setText(cm.getDescription());
        }
    }
}
