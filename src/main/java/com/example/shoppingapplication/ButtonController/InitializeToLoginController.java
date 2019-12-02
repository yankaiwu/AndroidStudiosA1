package com.example.shoppingapplication.ButtonController;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.b07.database.helper.DatabaseSelectHelper;
import com.example.shoppingapplication.LoginActivity;
import com.example.shoppingapplication.R;

public class InitializeToLoginController implements View.OnClickListener {
    private Context appContext;

    public InitializeToLoginController(Context context){
        this.appContext = context;
    }

    @Override
    public void onClick(View v) {
        if (DatabaseSelectHelper.getUsersByRole(DatabaseSelectHelper.getRoleIdByName(
                "Admin", appContext), appContext).size() < 1){
            TextView errorText = ((AppCompatActivity)appContext).findViewById(
                    R.id.initializeErrorText);
            errorText.setText("Create Admin to Initialize!");
        } else {
            Intent intent = new Intent(this.appContext, LoginActivity.class);
            appContext.startActivity(intent);
        }
    }
}
