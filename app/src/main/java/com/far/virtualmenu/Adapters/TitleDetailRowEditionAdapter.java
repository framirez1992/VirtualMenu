package com.far.virtualmenu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.far.virtualmenu.Adapters.Models.TitleDetailRowModel;
import com.far.virtualmenu.R;
import com.far.virtualmenu.interfaces.ListableActivity;

import java.util.ArrayList;

public class TitleDetailRowEditionAdapter extends RecyclerView.Adapter<TitleDetailRowEditionAdapter.TitleDetailHolder> {

    Activity activity;
    ArrayList<TitleDetailRowModel> objects;
    ListableActivity listableActivity;
    public TitleDetailRowEditionAdapter(Activity act, ListableActivity la, ArrayList<TitleDetailRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public TitleDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new TitleDetailHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.title_detail_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TitleDetailHolder holder, final int position) {

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


    public class TitleDetailHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetail, tvStatus;
        ImageView imgStatus, imgmenu, imgTime;
        public TitleDetailHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDetail = itemView.findViewById(R.id.tvDetail);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            imgmenu = itemView.findViewById(R.id.imgMenu);
            imgTime = itemView.findViewById(R.id.imgTime);
        }

        public void fillData(TitleDetailRowModel model){
            tvTitle.setText(model.getTitle());
            tvDetail.setText(model.getDetail());
            imgTime.setVisibility((model.isInServer())?View.INVISIBLE:View.VISIBLE);
            imgStatus.setImageResource((model.isEnabled()?R.drawable.visible:R.drawable.invisible));
            tvStatus.setText((model.isEnabled()?"Activo":"Inactivo"));
        }

        public ImageView getMenuImage(){
            return imgmenu;
        }
    }

}
