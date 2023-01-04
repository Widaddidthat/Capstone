package com.example.trackmydooit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout email, password;
    private Button loginBtn;
    private TextView loginQn;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        loginQn = findViewById(R.id.loginQn);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        //if user isn't registered they'll click this sentence, and be directed to the registration Activity
        loginQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        //user will be directed to main activity page
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getEditText().getText().toString();
                String passwordString = password.getEditText().getText().toString();

                if (TextUtils.isEmpty(emailString)){
                    email.setError("Email is required!");
                }
                if (TextUtils.isEmpty(passwordString)){
                    password.setError("Password is required!");
                }

                else{
                    progressDialog.setMessage("Login in progress");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //Intent intent = new Intent (LoginActivity.this, SplashScreenActivity.class);
                                Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                progressDialog.dismiss();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}