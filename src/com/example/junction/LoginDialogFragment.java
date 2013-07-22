package com.example.junction;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

@SuppressLint("NewApi")
public class LoginDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("There is no user currently logged in. Would you like to Login or Register as a new user?")
               .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   // FIRE ZE MISSILES!
                	   Intent i = new Intent(getActivity(), LoginActivity.class);
                	   i.putExtra("registering", true); 
                	   startActivity(i);
                   }
               })
               .setNegativeButton("Login", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                	   Intent i = new Intent(getActivity(), LoginActivity.class);
                	   i.putExtra("registering", false); 
                	   startActivity(i);
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}