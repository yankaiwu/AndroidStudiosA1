package com.example.shoppingapplication.ButtonController;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.shoppingapplication.CreateAdminPopUp;

public class AddAdminButtonController implements View.OnClickListener {
    private Context appContext;

    public AddAdminButtonController(Context context){
        this.appContext = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.appContext, CreateAdminPopUp.class);
        appContext.startActivity(intent);
    }
}
