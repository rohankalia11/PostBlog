package com.example.mineproject;

import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmailText;
    EditText loginPassText;
    Button loginButton;
    TextView tvSignup;
    ProgressBar loginprogress;

    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        user =mAuth.getCurrentUser();


        loginEmailText = findViewById(R.id.login_email);
        loginPassText=findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_btn);

        loginprogress=findViewById(R.id.login_progressBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String loginEmail = loginEmailText.getText().toString();
                String loginpass = loginPassText.getText().toString();


                if (!TextUtils.isEmpty(loginEmail)&& !TextUtils.isEmpty(loginpass)) {

                    loginprogress.setVisibility(View.VISIBLE);


                        mAuth.signInWithEmailAndPassword(loginEmail, loginpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (task.isSuccessful()) {

                                    checkEmailVerification();

                                } else {

                                    String errormsg = task.getException().getMessage();
                                    Toast.makeText(LoginActivity.this, "Error : " + errormsg, Toast.LENGTH_LONG).show();

                                }

                                loginprogress.setVisibility(View.INVISIBLE);
                            }
                        });



                  
                }


            }
        });

    }



    private void sendToMain() {


        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this,OnStart.class));
    }




    private void checkEmailVerification()
    {
        FirebaseUser firebaseUser =mAuth.getCurrentUser();
        Boolean emailflag =firebaseUser.isEmailVerified();
        if(emailflag)
        {
            sendToMain();
        }
        else
        {
            Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show();
            mAuth.signOut();

        }
    }



}
