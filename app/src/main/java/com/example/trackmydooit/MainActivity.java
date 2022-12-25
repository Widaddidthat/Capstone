package com.example.trackmydooit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button CreateSaving;
    TextView forgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));

        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        //go to CreateSavings
        CreateSaving=findViewById(R.id.CreateSaving);
        CreateSaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CreateSavingsGoal.class);
                startActivity(intent);
            }
        });

        //go to ForgotPassword
        forgotPassword = (TextView)findViewById(R.id.PasswordForgot);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    //testing this againnnn
    //hi it's homu
    //hi it's ED
    //hi it's Kell
    //hi its annei
    //annei test again
    //homu test again
    //hellloooo
    //syaza finally -.-
}