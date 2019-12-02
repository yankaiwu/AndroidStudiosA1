package com.example.shoppingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.users.Roles;
import com.example.shoppingapplication.ButtonController.AddAdminButtonController;
import com.example.shoppingapplication.ButtonController.AddEmployeeButtonController;
import com.example.shoppingapplication.ButtonController.InitializeToLoginController;

import java.sql.SQLException;

public class AppInitializeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DatabaseSelectHelper.getRoleIds(AppInitializeActivity.this).size() == 3){
            int adminRoleId = DatabaseSelectHelper.getRoleIdByName("Admin",
                    AppInitializeActivity.this);
            if (adminRoleId != -1) {
                if (DatabaseSelectHelper.getUsersByRole(adminRoleId,
                        AppInitializeActivity.this).size() >= 1) {
                    Intent intent = new Intent(AppInitializeActivity.this, LoginActivity.class);
                    AppInitializeActivity.this.startActivity(intent);
                }
            }
        } else {
            if (DatabaseSelectHelper.getRoleIds(AppInitializeActivity.this).size() < 3){
                for (Roles role: Roles.values()){
                    try {
                        DatabaseInsertHelper.insertRole(role.toString(),
                                AppInitializeActivity.this);
                    } catch (DatabaseInsertException e) {
                        e.printStackTrace();
                    }
                }
            }
            setContentView(R.layout.activity_app_initialize);

            Button createNewAdminButton = findViewById(R.id.createAdminButton);
            createNewAdminButton.setOnClickListener(new AddAdminButtonController(this));

            Button createNewEmployeeButton = findViewById(R.id.createEmployeeButton);
            createNewEmployeeButton.setOnClickListener(new AddEmployeeButtonController(this));

            Button goToLoginButton = findViewById(R.id.goToLoginButton);
            goToLoginButton.setOnClickListener(new InitializeToLoginController(this));
        }

    }
}
