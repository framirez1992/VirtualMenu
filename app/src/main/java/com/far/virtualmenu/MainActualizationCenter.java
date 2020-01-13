package com.far.virtualmenu;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.far.virtualmenu.Controllers.CompanyController;
import com.far.virtualmenu.Controllers.MeasureUnitsController;
import com.far.virtualmenu.Controllers.ProductsControlController;
import com.far.virtualmenu.Controllers.ProductsController;
import com.far.virtualmenu.Controllers.ProductsImagesController;
import com.far.virtualmenu.Controllers.ProductsMeasureController;
import com.far.virtualmenu.Controllers.ProductsSubTypesController;
import com.far.virtualmenu.Controllers.ProductsTypesController;
import com.far.virtualmenu.Controllers.RolesController;
import com.far.virtualmenu.Controllers.UsersController;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActualizationCenter extends AppCompatActivity implements OnSuccessListener<QuerySnapshot>, OnFailureListener, OnCompleteListener, OnCanceledListener {

    ProgressBar pb;
    TextView tvMessage;
    Button btnExit;
    UsersController usersController;
    int currentindex =0;

    //TABLAS QUE SE BAJAN SIMEPRE POR LOS LISTENER DEL MAIN Usuarios, Devices, Licencias
    String[]Tables = {RolesController.TABLE_NAME, CompanyController.TABLE_NAME,
            ProductsTypesController.TABLE_NAME, ProductsSubTypesController.TABLE_NAME, ProductsController.TABLE_NAME, ProductsMeasureController.TABLE_NAME, ProductsControlController.TABLE_NAME,
            ProductsImagesController.TABLE_NAME, MeasureUnitsController.TABLE_NAME};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_actualization_center);
        usersController = UsersController.getInstance(MainActualizationCenter.this);



        pb = findViewById(R.id.pb);
        tvMessage = findViewById(R.id.tvMessage);
        btnExit = findViewById(R.id.btnExit);

        tvMessage.setText("Por favor espere mietras se actualizan los datos...");
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pb.setVisibility(View.VISIBLE);
        //currentindex=15;
        loadData();


    }

    @Override
    public void onBackPressed() {

    }


    public void loadData(){
        switch (currentindex){
            case 0:CompanyController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
            case 1:ProductsTypesController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
            case 2:ProductsSubTypesController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
            case 3:ProductsController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
            case 4:ProductsMeasureController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
            case 5:ProductsControlController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
            case 6: ProductsImagesController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
            case 7:MeasureUnitsController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
            default:
                currentindex=0;
                tvMessage.setText("Finalizado Correctamente");
                pb.setVisibility(View.INVISIBLE);
                btnExit.setVisibility(View.VISIBLE);
                break;
        }
    }



    @Override
    public void onFailure(@NonNull Exception e) {
        currentindex=0;
        tvMessage.setText(e.getMessage()+" - "+e.getLocalizedMessage());
        pb.setVisibility(View.INVISIBLE);
        btnExit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCanceled() {
        currentindex=0;
        tvMessage.setText("Canceled");
        pb.setVisibility(View.INVISIBLE);
        btnExit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if(task.getException() != null){
            currentindex=0;
            tvMessage.setText(task.getException().toString());
            pb.setVisibility(View.INVISIBLE);
            btnExit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {

        switch (currentindex){
            case 0:CompanyController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 1:ProductsTypesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 2:ProductsSubTypesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 3:ProductsController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 4:ProductsMeasureController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 5:ProductsControlController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 6: ProductsImagesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;//ALL
            case 7:MeasureUnitsController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            default:break;
        }
        currentindex++;
        loadData();

    }
}
