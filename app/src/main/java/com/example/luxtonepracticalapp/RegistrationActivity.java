package com.example.luxtonepracticalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.luxtonepracticalapp.databinding.ActivityRegistrationBinding;
import com.tuya.smart.android.user.api.IRegisterCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

public class RegistrationActivity extends AppCompatActivity {
    ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.linearLayout.setVisibility(View.VISIBLE);




        binding.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = binding.CountyCode.getText().toString().trim();
                String email = binding.Email.getText().toString().trim();
                String password = binding.Password.getText().toString().trim();
                if (!countryCode.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Creating Account ", Toast.LENGTH_SHORT).show();
                    TuyaHomeSdk.getUserInstance().getRegisterEmailValidateCode(countryCode, email, validateCallback);

                }
            }
        });

        binding.btnVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = binding.CountyCode.getText().toString().trim();
                String email = binding.Email.getText().toString().trim();
                String password = binding.Password.getText().toString().trim();
                String verificationCode = binding.edtVerify.getText().toString().trim();
                TuyaHomeSdk.getUserInstance().registerAccountWithEmail(countryCode, email, password, verificationCode, registerCallback);
                Toast.makeText(RegistrationActivity.this, "Verifying Code", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));

            }
        });
    }




    private void getVerificationCode(String countryCode, String email) {
    }





    private final IResultCallback validateCallback = new IResultCallback() {
        @Override
        public void onError(String code, String error) {
            Toast.makeText(RegistrationActivity.this, "SignUp Failed due to " + error, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess() {
            Toast.makeText(RegistrationActivity.this, "Verification Code Sent.", Toast.LENGTH_SHORT).show();
            binding.linearLayout.setVisibility(View.VISIBLE);
        }
    };



    private final IRegisterCallback registerCallback = new IRegisterCallback() {
        @Override
        public void onSuccess(User user) {
            Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegistrationActivity.this , MainActivity.class));
        }

        @Override
        public void onError(String code, String error) {
            Toast.makeText(RegistrationActivity.this, "Login Failed due to " + error, Toast.LENGTH_SHORT).show();

        }
    };

}


