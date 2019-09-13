package com.example.mineproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.InetAddress;

public class OnStart extends AppCompatActivity {


    Button loginbtn,signupbtn;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_on_start);

        loginbtn = findViewById(R.id.lbtn);
        signupbtn=findViewById(R.id.sbtn);
        mAuth=FirebaseAuth.getInstance();


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OnStart.this,LoginActivity.class);
                startActivity(intent);
            }
        });


        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(OnStart.this,SignupActivity.class);
                startActivity(intent);
            }
        });



    }



    @Override
    protected void onStart() {
        super.onStart();

        //to check if user currently login or not

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(isInternetAvailable())
        {

            if (currentUser !=null)
            {
                sendToMain();

            }


        }
        else
        {
            Toast.makeText(OnStart.this, "No internet connection", Toast.LENGTH_SHORT).show();

            Intent mainIntent = new Intent(OnStart.this,NoInternetConnection.class);
            startActivity(mainIntent);
            finish();

        }




    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void sendToMain() {


        Intent mainIntent = new Intent(OnStart.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
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
