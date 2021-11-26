package com.example.luxtonepracticalapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.INeedLoginListener;

public class TuyaSmartAppSDK extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        TuyaHomeSdk.init(TuyaSmartAppSDK.this , "emy59mrgqrrry3vvthh4" ,"vsh3wxu5xmp3mxte9h7xa5vs47f9mmct");


        TuyaHomeSdk.setDebugMode(true);

        TuyaHomeSdk.setOnNeedLoginListener(new INeedLoginListener() {
            @Override
            public void onNeedLogin(Context context) {
                startActivity(new Intent(TuyaSmartAppSDK.this, MainActivity.class));
            }
        });
    }
}


