package com.far.virtualmenu.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.far.virtualmenu.Adapters.Models.UserDeviceModel;
import com.far.virtualmenu.R;
import com.far.virtualmenu.interfaces.ListableActivity;

import java.util.ArrayList;

public class UserDevicesSelectableAdapter  extends RecyclerView.Adapter<UserDevicesSelectableAdapter.UserDeviceHolder> {

    ListableActivity listableActivity;
    ArrayList<UserDeviceModel> objects;
    Context context;
    public UserDevicesSelectableAdapter(Context context, ListableActivity la, ArrayList<UserDeviceModel> objects){
        this.context = context;
        this.objects = objects;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public UserDeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new UserDeviceHolder(inflater.inflate(R.layout.user_device_selectable_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserDeviceHolder holder, final int position) {

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
        objects.get(pos).setSelected(true);
    }

    public ArrayList<UserDeviceModel> getSelected(){
        ArrayList<UserDeviceModel> selected = new ArrayList<>();
        for(UserDeviceModel cm: objects){
            if(cm.isSelected()){
               selected.add(cm);
            }
        }
        return selected;
    }

    public void selectAll(boolean select){
        for(UserDeviceModel cm: objects){
            cm.setSelected(select);
        }
        notifyDataSetChanged();
    }

    public class UserDeviceHolder extends RecyclerView.ViewHolder {
        RadioButton rb;
        TextView tvUser, tvDevice;
        public UserDeviceHolder(View itemView) {
            super(itemView);
            rb = itemView.findViewById(R.id.rb);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvDevice  = itemView.findViewById(R.id.tvDevice);
        }

        public void fillData(UserDeviceModel cm){
            rb.setChecked(cm.isSelected());
            tvUser.setText("User:"+cm.getUserName());
            tvDevice.setText("Device:"+cm.getCodeDevice());
        }
    }
}
