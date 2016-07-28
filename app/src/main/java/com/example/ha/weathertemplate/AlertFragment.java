package com.example.ha.weathertemplate;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Ha on 7/26/2016.
 */
public class AlertFragment extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
              .setTitle(context.getString(R.string.Error_title))
                .setMessage(context.getString(R.string.error_message))
                .setPositiveButton(context.getString(R.string.error_ok_button), null);


    AlertDialog dialog = builder.create();
        return dialog;
    }

}
