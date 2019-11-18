package com.far.virtualmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.far.virtualmenu.Utils.CODES;


public class MaintenanceFragment extends Fragment {


    ImageView btnFamily, btnGroup, btnMeasures, btnProducts,btnProductsImages, btnUsers, btnControls
    , btnUsersControl, btnClients, btnPrinter;
    LinearLayout llMainScreen, llMaintenanceControls, llMaintenanceUsers, llMaintenanceProducts, llConfiguration;
    public MaintenanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llMainScreen = view.findViewById(R.id.llMainScreen);
        llMaintenanceControls = view.findViewById(R.id.llMaintenanceControls);
        llMaintenanceUsers = view.findViewById(R.id.llMaintenanceUsers);
        llMaintenanceProducts = view.findViewById(R.id.llMaintenanceProducts);
        llConfiguration = view.findViewById(R.id.llConfiguration);

        btnFamily = view.findViewById(R.id.btnFamily);
        btnGroup = view.findViewById(R.id.btnGroups);
        btnMeasures = view.findViewById(R.id.btnMeasures);
        btnProducts = view.findViewById(R.id.btnProducts);
        btnProductsImages = view.findViewById(R.id.btnProductsImages);
        btnUsers = view.findViewById(R.id.btnUsers);
        btnControls = view.findViewById(R.id.btnControls);
        btnUsersControl = view.findViewById(R.id.btnUsersControl);
        btnClients = view.findViewById(R.id.btnClients);
        btnPrinter = view.findViewById(R.id.btnPrinter);

        btnFamily.setOnClickListener(imageClick);
        btnGroup.setOnClickListener(imageClick);
        btnMeasures.setOnClickListener(imageClick);
        btnProducts.setOnClickListener(imageClick);
        btnProductsImages.setOnClickListener(imageClick);

        btnUsers.setOnClickListener(imageClick);

        btnControls.setOnClickListener(imageClick);
        btnUsersControl.setOnClickListener(imageClick);

        btnClients.setOnClickListener(imageClick);

        btnPrinter.setOnClickListener(imageClick);




    }

    public View.OnClickListener imageClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()){
               case R.id.btnFamily:
                    i = new Intent(getActivity(), MaintenanceProductTypes.class);
                    break;
                case R.id.btnMeasures:
                    i = new Intent(getActivity(), MaintenanceUnitMeasure.class);
                    break;
                case R.id.btnUsers:
                    i = new Intent(getActivity(), MaintenanceUsers.class);
                    break;

                case R.id.btnGroups:
                    i = new Intent(getActivity(), MaintenanceProductSubTypes.class);
                     break;
                case R.id.btnProducts:
                    i = new Intent(getActivity(), MaintenanceProducts.class);
                    break;
                case R.id.btnProductsImages:
                    i = new Intent(getActivity(), MainUpload.class);
                    break;

               /* case R.id.btnActualizationCenter:
                    i = new Intent(getActivity(), MainActualizationCenter.class);
                    break;
                case R.id.btnControls:
                    i = new Intent(getActivity(), MaintenanceUsersControl.class);
                    break;
                case R.id.btnUsersControl:
                    i =new Intent(getActivity(), MainAssignation.class);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TABLE, UserControlController.TABLE_NAME);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TARGET, CODES.EXTRA_MAINASSIGNATION_TARGET_USERSCONTROL);
                    break;
                case R.id.btnClients:
                    i = new Intent(getActivity(), MaintenanceClients.class);
                    break;
                case R.id.btnPrinter:
                    i = new Intent(getActivity(), com.example.bluetoothlibrary.BluetoothScan.class);
                    break;*/


            }

            if(i == null){
                return;
            }
            try {
                startActivity(i);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    };
}
