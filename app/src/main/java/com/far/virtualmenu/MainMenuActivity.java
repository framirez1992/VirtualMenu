package com.far.virtualmenu;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.far.virtualmenu.Controllers.MeasureUnitsController;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Controllers.ProductsImagesController;
import com.far.virtualmenu.Controllers.ProductsMeasureController;
import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Model.ItemModel;
import com.far.virtualmenu.interfaces.ListableActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class MainMenuActivity extends AppCompatActivity implements ListableActivity, OnSuccessListener<QuerySnapshot>, OnFailureListener, OnCompleteListener, OnCanceledListener {

    LogoFragment logoFragment;
    DetailFragment detailFragment;
    ListFragment listFragment;
    GridFragment gridFragment;
    int currentindex =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        setLoadingScreen();
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

    public void setLoadingScreen(){
        findViewById(R.id.menu).setVisibility(View.GONE);
        logoFragment = new LogoFragment();
        changeFragment(logoFragment, R.id.details);
        loadData();
    }




    public void loadData(){
        switch (currentindex){
            //case 1:CompanyController.getInstance(MainActualizationCenter.this).searchChanges(this, this, this); break;
            //case 7:ProductsControlController.getInstance(MainActualizationCenter.this).searchChanges(this, this, this); break;
            case 0:ProductsTypesController.getInstance(MainMenuActivity.this).searchChanges(this, this, this); break;
            case 1:ProductsSubTypesController.getInstance(MainMenuActivity.this).searchChanges(this, this, this); break;
            case 2:ProductsController.getInstance(MainMenuActivity.this).searchChanges(this, this, this); break;
            case 3:ProductsMeasureController.getInstance(MainMenuActivity.this).searchChanges(this, this, this); break;
            case 4:MeasureUnitsController.getInstance(MainMenuActivity.this).searchChanges(this, this, this); break;
            case 5:ProductsImagesController.getInstance(MainMenuActivity.this).searchChanges(this, this, this); break;
            default:
                currentindex=0;
                //tvMessage.setText("Finalizado Correctamente");
                //pb.setVisibility(View.INVISIBLE);
                //btnExit.setVisibility(View.VISIBLE);
                changeMenu(1);
                break;
        }
    }



    @Override
    public void onFailure(@NonNull Exception e) {
        currentindex=0;
        Toast.makeText(MainMenuActivity.this, e.getMessage()+" - "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        //pb.setVisibility(View.INVISIBLE);
        //btnExit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCanceled() {
        currentindex=0;
        Toast.makeText(MainMenuActivity.this, "Cancelado", Toast.LENGTH_LONG).show();
        //pb.setVisibility(View.INVISIBLE);
        //btnExit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if(task.getException() != null){
            currentindex=0;
            Toast.makeText(MainMenuActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
            //pb.setVisibility(View.INVISIBLE);
            //btnExit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {
        switch (currentindex){
            case 0:ProductsTypesController.getInstance(MainMenuActivity.this).consumeQuerySnapshot(querySnapshot); break;
            case 1:ProductsSubTypesController.getInstance(MainMenuActivity.this).consumeQuerySnapshot(querySnapshot); break;
            case 2:ProductsController.getInstance(MainMenuActivity.this).consumeQuerySnapshot(querySnapshot); break;
            case 3:ProductsMeasureController.getInstance(MainMenuActivity.this).consumeQuerySnapshot(querySnapshot); break;
            case 4:MeasureUnitsController.getInstance(MainMenuActivity.this).consumeQuerySnapshot(querySnapshot); break;
            case 5:ProductsImagesController.getInstance(MainMenuActivity.this).consumeQuerySnapshot(querySnapshot); break;
            default:break;
        }
        currentindex++;
        loadData();

    }

}
