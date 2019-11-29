package com.example.shoppingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.shoppingapplication.ButtonController.AddEmployeeButtonController;
import com.example.shoppingapplication.ButtonController.AddUserButtonController;
import com.example.shoppingapplication.ButtonController.LoginButtonController;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button addUserButton = findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new AddUserButtonController(this));

        Button addEmployeeButton = findViewById(R.id.addEmployeeButton);
        addEmployeeButton.setOnClickListener(new AddEmployeeButtonController(this));

        Button logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
