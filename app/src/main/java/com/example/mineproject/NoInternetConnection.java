package com.example.mineproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NoInternetConnection extends AppCompatActivity {

    Button button;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_no_internet_connection);

        mAuth=FirebaseAuth.getInstance();

        button =findViewById(R.id.intenet_btn);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser currentUser = mAuth.getCurrentUser();

                if(isInternetAvailable()) {

                    if (currentUser !=null) {

                        Intent intent = new Intent(NoInternetConnection.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    else
                    {
                        Intent intent = new Intent(NoInternetConnection.this, OnStart.class);
                        startActivity(intent);
                        finish();

                    }
                }

                else
                {
                    Toast.makeText(NoInternetConnection.this, "Please,turn on your internet connection", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    public boolean isInternetAvailable()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;

        } else {
            return false;
        }
    }
}
