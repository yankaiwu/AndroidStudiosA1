package com.example.shoppingapplication.ButtonController;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.example.shoppingapplication.R;

public class CreateUserButtonController implements View.OnClickListener {
    private Context appContext;
    private int roleId;

    public CreateUserButtonController (Context context, int roleId){
        this.appContext = context;
        this.roleId = roleId;
    }

    @Override
    public void onClick(View v) {
        TextView creationText = ((Activity)appContext).findViewById(R.id.creationText);
        EditText usernameEdit = ((Activity)appContext).findViewById(R.id.usernameEdit);
        String username = usernameEdit.getText().toString();
        EditText passwordEdit = ((Activity)appContext).findViewById(R.id.passwordEdit);
        String password = passwordEdit.getText().toString();
        EditText ageEdit = ((Activity)appContext).findViewById(R.id.ageEdit);
        int age = Integer.parseInt(ageEdit.getText().toString());
        EditText addressEdit = ((Activity)appContext).findViewById(R.id.addressEdit);
        String address = addressEdit.getText().toString();
        if (roleId == DatabaseSelectHelper.getRoleIdByName("Admin", appContext)){
            try {
                int userId = DatabaseInsertHelper.insertNewUser(
                        username, age, address, password, "", appContext);
                DatabaseInsertHelper.insertUserRole(userId, DatabaseSelectHelper.getRoleIdByName(
                        "ADMIN", appContext), appContext);
                creationText.setText(Integer.toString(userId));
            } catch (Exception e) {
                creationText.setText("Error in user creation");
            }
        } else if (roleId == DatabaseSelectHelper.getRoleIdByName("Employee", appContext)){
            try {
                int userId = DatabaseInsertHelper.insertNewUser(
                        username, age, address, password, "", appContext);
                DatabaseInsertHelper.insertUserRole(userId, DatabaseSelectHelper.getRoleIdByName(
                        "EMPLOYEE", appContext), appContext);
                creationText.setText(Integer.toString(userId));
            } catch (Exception e) {
                creationText.setText("Error in user creation");
            }
        } else if (roleId == DatabaseSelectHelper.getRoleIdByName("Customer", appContext)) {
            try {
                int userId = DatabaseInsertHelper.insertNewUser(
                        username, age, address, password, "", appContext);
                DatabaseInsertHelper.insertUserRole(userId, DatabaseSelectHelper.getRoleIdByName(
                        "CUSTOMER", appContext), appContext);
                creationText.setText(Integer.toString(userId));
            } catch (Exception e) {
                creationText.setText("Error in user creation");
            }
        } else {
            creationText.setText("Error");
        }
    }
}
