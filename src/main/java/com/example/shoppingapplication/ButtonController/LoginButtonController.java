package com.example.shoppingapplication.ButtonController;

import android.content.Intent;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.exceptions.InvalidRoleException;
import com.b07.exceptions.NullParameterException;
import com.b07.exceptions.UserNotFoundException;
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
        int userId = Integer.parseInt(username);

        if (DatabaseSelectHelper.UserIdExists(userId, appContext)) {
            try {
                if (DatabaseSelectHelper.getUserDetails(userId, appContext).authenticate(password, appContext)) {
                    int userRole = DatabaseSelectHelper.getUserRoleId(userId, appContext);
                    if (userRole == DatabaseSelectHelper.getRoleIdByName("ADMIN", appContext)) {
                        intent = new Intent(this.appContext, AdminActivity.class);
                        intent.putExtra("userId", userId);
                        appContext.startActivity(intent);
                    } else if (userRole == DatabaseSelectHelper.getRoleIdByName(
                            "EMPLOYEE", appContext)) {
                        intent = new Intent(this.appContext, EmployeeActivity.class);
                        intent.putExtra("userId", userId);
                        appContext.startActivity(intent);
                    } else if (userRole == DatabaseSelectHelper.getRoleIdByName(
                            "CUSTOMER", appContext)) {
                        intent = new Intent(this.appContext, CustomerActivity.class);
                        intent.putExtra("userId", userId);
                        appContext.startActivity(intent);
                    }
                } else {
                    loginFailed.setText("Wrong Password");
                }
            } catch (Exception e){
                loginFailed.setText("Error");
            }
        } else {
            loginFailed.setText("Wrong UserId");
        }
    }
}
