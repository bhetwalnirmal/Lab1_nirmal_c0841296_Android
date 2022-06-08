package com.nirmalbhetwal.lab1_nirmal_c0841296_android;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

public class CustomColorPickerDialog extends Dialog implements View.OnClickListener {
    Button setColor, cancel;
    Activity activity;
    Dialog dialog;

    public CustomColorPickerDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onClick(View view) {

    }
}
