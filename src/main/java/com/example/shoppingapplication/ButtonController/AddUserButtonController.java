package com.example.shoppingapplication.ButtonController;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.shoppingapplication.CreateUserPopUp;

public class AddUserButtonController implements View.OnClickListener {
    private Context appContext;

    public AddUserButtonController(Context context){
        this.appContext = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.appContext, CreateUserPopUp.class);
        appContext.startActivity(intent);
    }
}
