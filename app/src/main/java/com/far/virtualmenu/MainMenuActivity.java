package com.far.virtualmenu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.far.virtualmenu.Model.ItemModel;
import com.far.virtualmenu.interfaces.ListableActivity;

public class MainMenuActivity extends AppCompatActivity implements ListableActivity {

    DetailFragment detailFragment;
    ListFragment listFragment;
    GridFragment gridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        changeMenu(2);
    }

    public void changeFragment(Fragment f, int id) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
    }

    @Override
    public void onClick(Object obj) {
        detailFragment.setItemData((ItemModel) obj);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(detailFragment).attach(detailFragment).commit();
    }

    public void changeMenu(int type){

        if(type == 1){
            findViewById(R.id.menu).setVisibility(View.VISIBLE);
            detailFragment = new DetailFragment();
            detailFragment.setParent(this);

            listFragment = new ListFragment();
            listFragment.setParentActivity(this);
            changeFragment(detailFragment, R.id.details);
            changeFragment(listFragment, R.id.menu);
        }else if(type == 2){
            findViewById(R.id.menu).setVisibility(View.GONE);
            gridFragment = new GridFragment();
            gridFragment.setParentActivity(this);
            changeFragment(gridFragment, R.id.details);
        }
    }

}
