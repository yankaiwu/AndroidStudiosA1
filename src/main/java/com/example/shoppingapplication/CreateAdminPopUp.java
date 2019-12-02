package com.example.shoppingapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;

import com.b07.database.helper.DatabaseSelectHelper;
import com.example.shoppingapplication.ButtonController.CreateUserButtonController;

public class CreateAdminPopUp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_add_admin);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.8));

        Button createButton = findViewById(R.id.createAdminButton);
        int adminRoleId = DatabaseSelectHelper.getRoleIdByName("Admin", this);
        createButton.setOnClickListener(new CreateUserButtonController(this, adminRoleId));
    }
}