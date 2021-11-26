package com.example.luxtonepracticalapp;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.luxtonepracticalapp.databinding.ActivityDeviceControlBinding;
import com.tuya.smart.centralcontrol.TuyaLightDevice;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.centralcontrol.api.ILightListener;
import com.tuya.smart.sdk.centralcontrol.api.ITuyaLightDevice;
import com.tuya.smart.sdk.centralcontrol.api.bean.LightDataPoint;


public class DeviceControlActivity extends AppCompatActivity {
    ActivityDeviceControlBinding binding;
    String devId;
    ITuyaLightDevice lightDevice;
    SwitchCompat lightswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeviceControlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        lightswitch = binding.bulbSwitch ;


        devId = getIntent().getStringExtra("deviceId");
        DeviceBean deviceBean= TuyaHomeSdk.getDataInstance().getDeviceBean(devId);



        boolean online = deviceBean.getIsOnline();
        boolean localOnline = deviceBean.getIsLocalOnline();


        if (online || localOnline) {
            lightDevice = new TuyaLightDevice(devId);
            lightDevice.registerLightListener(new ILightListener() {
                @Override
                public void onDpUpdate(LightDataPoint lightDataPoint) {
                }

                @Override
                public void onRemoved() {
                }

                @Override
                public void onStatusChanged(boolean b) {
                }

                @Override
                public void onNetworkStatusChanged(boolean b) {
                }

                @Override
                public void onDevInfoUpdate() {
                }
            });

            initSwitch();

        }
    }

    private void initSwitch() {
        lightswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (lightDevice != null) {
                    lightDevice.powerSwitch(b, new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Toast.makeText(DeviceControlActivity.this, "Light Change Failed", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onSuccess() {
                            Toast.makeText(DeviceControlActivity.this, "Light Change SUCCESSFUL", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}