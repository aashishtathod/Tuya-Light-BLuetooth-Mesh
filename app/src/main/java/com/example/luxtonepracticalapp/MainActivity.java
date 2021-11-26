package com.example.luxtonepracticalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.luxtonepracticalapp.databinding.ActivityMainBinding;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        binding.Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = binding.CountyCode.getText().toString().trim();
                String email = binding.Email.getText().toString().trim();
                String password = binding.Password.getText().toString().trim();

                if (!countryCode.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    TuyaHomeSdk.getUserInstance().loginWithEmail(countryCode, email, password, loginCallback);
                }
            }
        });

        binding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this ,RegistrationActivity.class));
            }
        });
    }

    private ILoginCallback loginCallback = new ILoginCallback() {
        @Override
        public void onSuccess(User user) {
            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }

        @Override
        public void onError(String code, String error) {
            Toast.makeText(MainActivity.this, "Login Failed due to" + error, Toast.LENGTH_LONG).show();

        }
    };
}