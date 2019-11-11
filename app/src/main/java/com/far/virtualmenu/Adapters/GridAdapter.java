package com.far.virtualmenu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.far.virtualmenu.Model.PriceModel;
import com.far.virtualmenu.R;
import com.far.virtualmenu.interfaces.ListableActivity;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    Context context;
    ListableActivity listableActivity;

    ArrayList<PriceModel> objects;

    public GridAdapter(Context context, ListableActivity la, ArrayList<PriceModel> priceModels){
        this.context = context;
        this.listableActivity = la;
        this.objects = priceModels;
    }

    @Override
    public int getCount() {
        return objects.size();
    }


    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }


    @Override
    public long getItemId(int position) {
        return Long.parseLong(position+"");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            v = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.icon_text,null);
        }

        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvPrice =  v.findViewById(R.id.tvPrice);

        tvTitle.setText(objects.get(position).getDescription());
        tvPrice.setText(objects.get(position).getAmount());

        return v;
    }
}
