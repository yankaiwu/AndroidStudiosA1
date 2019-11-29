package com.example.shoppingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.shoppingapplication.ButtonController.LoginButtonController;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImageView shoppingAppIcon = findViewById(R.id.shoppingAppLogo);
        shoppingAppIcon.setImageResource(R.drawable.normal_rabbit);
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new LoginButtonController(this));
    }
}
