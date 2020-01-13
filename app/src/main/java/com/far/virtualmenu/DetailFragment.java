package com.far.virtualmenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.far.virtualmenu.Adapters.GridAdapter;
import com.far.virtualmenu.Model.ItemModel;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;



public class DetailFragment extends Fragment {


    MainMenuActivity parentActivity;
    GridView gvPrices;
    TextView tvTitle,tvDescription, tvTime;
    CarouselView carouselView;
    LinearLayout llTime;
    ItemModel itemModel;

    public DetailFragment() {
        // Required empty public constructor
    }

    public void setParent(MainMenuActivity parent){
        this.parentActivity = parent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.detail_fragment, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        carouselView = view.findViewById(R.id.carouselView);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvDescription = view.findViewById(R.id.tvDescription);
        gvPrices = view.findViewById(R.id.gvPrices);
        llTime = view.findViewById(R.id.llTime);
        tvTime = view.findViewById(R.id.tvTime);

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.changeMenu(2);
            }
        });
        if(itemModel != null){

            tvTitle.setText(itemModel.getTitle());
            tvDescription.setText(itemModel.getDescription());

            if(itemModel.getTime() != null && itemModel.getTime().split("-").length>1){
                llTime.setVisibility(View.VISIBLE);
                tvTime.setText(itemModel.getTime().replace("-M", " Minutos").replace("-H", " Horas"));
            }else{
                llTime.setVisibility(View.GONE);
                tvTime.setText("");
            }

            setPriceAdapter();
            try {
                carouselView.setImageListener(new ImageListener() {
                    @Override
                    public void setImageForPosition(int position, ImageView imageView) {
                        // imageView.setImageResource(sampleImages[position]);
                        Picasso.with(parentActivity).load(itemModel.getUrls().get(position)).into(imageView);

                    }
                });
                carouselView.setPageCount(itemModel.getUrls().size());
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    public void setItemData(ItemModel item){

            this.itemModel = item;
    }

    public void setPriceAdapter(){

        GridAdapter adapter = new GridAdapter(parentActivity, parentActivity,itemModel.getPrices());
        gvPrices.setAdapter(adapter);
        gvPrices.invalidate();
    }

    public void refresh(){

    }


}
