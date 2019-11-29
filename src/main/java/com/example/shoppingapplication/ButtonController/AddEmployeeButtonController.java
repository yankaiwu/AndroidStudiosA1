package com.example.shoppingapplication.ButtonController;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.shoppingapplication.CreateEmployeePopUp;

public class AddEmployeeButtonController implements View.OnClickListener {
    private Context appContext;

    public AddEmployeeButtonController(Context context){
        this.appContext = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.appContext, CreateEmployeePopUp.class);
        appContext.startActivity(intent);
    }
}
