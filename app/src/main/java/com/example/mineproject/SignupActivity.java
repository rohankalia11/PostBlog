package com.example.mineproject;

import android.content.Intent;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText text_email,text_pass,text_cnf;
    Button sign_btn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_signup);

        mAuth=FirebaseAuth.getInstance();

        text_email=findViewById(R.id.signup_email);
        text_pass=findViewById(R.id.signup_password);
        text_cnf=findViewById(R.id.signup_confirm_password);
        sign_btn=findViewById(R.id.signup_btn);
        progressBar =findViewById(R.id.signup_progressBar);

        sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = text_email.getText().toString();
                String password = text_pass.getText().toString();
                String cnfpass = text_cnf.getText().toString();


                    String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";





                if (!TextUtils.isEmpty(email) && (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(cnfpass))) {


                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
                    java.util.regex.Matcher m = p.matcher(email);
                    if (m.matches())
                    {

                        if (password.equals(cnfpass)) {

                            Intent intent = new Intent(SignupActivity.this, SetupActivity.class);
                            intent.putExtra("Email", email);
                            intent.putExtra("Password", password);
                            startActivity(intent);
                            finish();


                        } else {
                            Toast.makeText(SignupActivity.this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
                        }

                }

                else
                    {
                        Toast.makeText(SignupActivity.this, "Enter valid Email", Toast.LENGTH_SHORT).show();
                    }

            }

            }
        });


    }


}
