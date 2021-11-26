package com.example.luxtonepracticalapp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.luxtonepracticalapp.databinding.ActivityHomeBinding;
import com.tuya.smart.android.blemesh.api.ITuyaBlueMeshActivatorListener;
import com.tuya.smart.android.blemesh.api.ITuyaBlueMeshSearch;
import com.tuya.smart.android.blemesh.api.ITuyaBlueMeshSearchListener;
import com.tuya.smart.android.blemesh.bean.SearchDeviceBean;
import com.tuya.smart.android.blemesh.builder.SearchBuilder;
import com.tuya.smart.android.blemesh.builder.TuyaSigMeshActivatorBuilder;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.bluemesh.ITuyaBlueMeshActivator;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.bean.SigMeshBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private static final String TAG = "Log Tag";

    private HomeBean currentHomeBean;
    List<SigMeshBean> meshList;
    private final List<SearchDeviceBean> searchDeviceBeanList = new ArrayList<>();
    private ITuyaBlueMeshSearch mMeshSearch;
    private ITuyaBlueMeshActivator iTuyaBlueMeshActivator;


    private String homeName = "My Home";
    private String[] rooms = new String[]{"Kitchen", "BedRoom", "LivingRoom"};
    private ArrayList<String> roomList;
    private String ssId = "Luxtone";
    private String password = "Luxtone123";


   /* private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT};*/

    ProgressDialog dialog;

    // private final int PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Scanning Devices");
        dialog.setMessage("Wait a minute");
        dialog.setCancelable(false);

        roomList = new ArrayList<>();
        roomList.addAll(Arrays.asList(rooms));
        checkPermission();
        createHome(homeName, roomList);


        binding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDevices();
            }
        });

    }


    private void createHome(String homeName, List<String> roomList) {
        TuyaHomeSdk.getHomeManagerInstance().createHome(homeName, 0, 0, "", roomList, new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                currentHomeBean = bean;
                Toast.makeText(HomeActivity.this, "Home Created Successfully", Toast.LENGTH_SHORT).show();
                createBluetoothMesh();
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(HomeActivity.this, "Error Occurred while creating Home, Please Restart the App", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void createBluetoothMesh() {
        TuyaHomeSdk.newHomeInstance(currentHomeBean.getHomeId()) // homeId
                .createSigMesh(new ITuyaResultCallback<SigMeshBean>() {

                    @Override
                    public void onError(String errorCode, String errorMsg) {
                    }

                    @Override
                    public void onSuccess(SigMeshBean sigMeshBean) {
                        Toast.makeText(HomeActivity.this, "Mesh Created Successfully", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    private void searchDevices() {
        dialog.show();
        searchDeviceBeanList.clear();

        UUID[] MESH_PROVISIONING_UUID = {UUID.fromString("00001827-0000-1000-8000-00805f9b34fb")};
        SearchBuilder searchBuilder = new SearchBuilder()
                .setServiceUUIDs(MESH_PROVISIONING_UUID)
                .setTimeOut(100)        // timeout: s
                .setTuyaBlueMeshSearchListener(new ITuyaBlueMeshSearchListener() {

                    @Override
                    public void onSearched(SearchDeviceBean bean) {
                        searchDeviceBeanList.add(bean);
                        stopScan();
                        startActivator();
                    }

                    @Override
                    public void onSearchFinish() {
                        Toast.makeText(HomeActivity.this, "search finish", Toast.LENGTH_SHORT).show();
                        stopScan();
                        dialog.dismiss();
                    }
                }).build();

        mMeshSearch = TuyaHomeSdk.getTuyaBlueMeshConfig().newTuyaBlueMeshSearch(searchBuilder);
        mMeshSearch.startSearch();
    }

    private void stopScan() {
        if (mMeshSearch != null) {
            mMeshSearch.stopSearch();
        }
        return;
    }


    private void startActivator() {
        TuyaSigMeshActivatorBuilder tuyaSigMeshActivatorBuilder = new TuyaSigMeshActivatorBuilder()
                .setSearchDeviceBeans(searchDeviceBeanList)
                .setSigMeshBean(TuyaHomeSdk.getSigMeshInstance().getSigMeshList().get(0))
                .setTimeOut(100)  // timeout: s
                .setTuyaBlueMeshActivatorListener(new ITuyaBlueMeshActivatorListener() {

                    @Override
                    public void onSuccess(String mac, DeviceBean deviceBean) {
                        Log.d(TAG, "activator success:" + deviceBean.getName());
                        Toast.makeText(HomeActivity.this, "activator success:" + deviceBean.getName(), Toast.LENGTH_SHORT).show();

                        binding.name.setText(deviceBean.getName());
                        binding.productId.setText(deviceBean.productId);
                        binding.deviceId.setText(deviceBean.getDevId());
                        dialog.dismiss();

                        Intent intent = new Intent(HomeActivity.this , DeviceControlActivity.class);
                        intent.putExtra("deviceId" , deviceBean.getDevId());
                        startActivity(intent);

                    }

                    @Override
                    public void onError(String mac, String errorCode, String errorMsg) {
                        Log.d(TAG, "activator error:" + errorMsg);
                        Toast.makeText(HomeActivity.this, "activator error:" + errorMsg, Toast.LENGTH_SHORT).show();
                        stopActivator();
                        dialog.dismiss();


                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "activator finish");
                        stopActivator();
                        dialog.dismiss();
                    }
                });

        iTuyaBlueMeshActivator = TuyaHomeSdk.getTuyaBlueMeshConfig().newSigActivator(tuyaSigMeshActivatorBuilder);
        iTuyaBlueMeshActivator.startActivator();

    }

    private void stopActivator() {
        if (iTuyaBlueMeshActivator != null) {
            iTuyaBlueMeshActivator.stopActivator();
        }
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != 0 ||
                ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {

            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 1001);
        }
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Does not support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            AlertDialog builder = new AlertDialog.Builder(this)
                    .setMessage("Please Turn On Bluetooth to connect Nearby Devices")
                    .setPositiveButton("OK", (dialog, which) -> finish())
                    .create();

            builder.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length != 0 && grantResults[0] == 0) {
                Log.i(TAG, "onRequestPermissionsResult: agree");
            } else {
                Log.i(TAG, "onRequestPermissionsResult: denied");
            }
        } //else {
        //   throw new IllegalStateException("Unexpected value: " + requestCode);
        // }
    }




}