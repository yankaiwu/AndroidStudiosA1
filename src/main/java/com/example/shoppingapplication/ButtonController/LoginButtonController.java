package com.example.shoppingapplication.ButtonController;

import android.content.Intent;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppingapplication.AdminActivity;
import com.example.shoppingapplication.CustomerActivity;
import com.example.shoppingapplication.EmployeeActivity;
import com.example.shoppingapplication.R;

public class LoginButtonController implements View.OnClickListener {
    private Context appContext;

    public LoginButtonController(Context context){
        this.appContext = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        EditText editUser = ((AppCompatActivity) appContext).findViewById(R.id.usernameInput);
        EditText editPass = ((AppCompatActivity) appContext).findViewById(R.id.passwordInput);
        TextView loginFailed = ((AppCompatActivity) appContext).findViewById(R.id.loginFailed);
        String username = editUser.getText().toString();
        String password = editPass.getText().toString();
        if (username.equalsIgnoreCase("customer")){
            intent = new Intent(this.appContext, CustomerActivity.class);
            appContext.startActivity(intent);
        } else if (username.equalsIgnoreCase("admin")){
            intent = new Intent(this.appContext, AdminActivity.class);
            appContext.startActivity(intent);
        } else if (username.equalsIgnoreCase("employee")){
            intent = new Intent(this.appContext, EmployeeActivity.class);
            appContext.startActivity(intent);
        } else {
            loginFailed.setText("Incorrect Username or Password");
        }
    }
}
