package com.example.shoppingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.shoppingapplication.ButtonController.AddEmployeeButtonController;
import com.example.shoppingapplication.ButtonController.AddUserButtonController;

public class EmployeeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

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
