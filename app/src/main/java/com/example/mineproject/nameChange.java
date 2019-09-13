package com.example.mineproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class nameChange extends AppCompatActivity

{

            EditText setupNamel;
            Button setupBtn;
            ProgressBar progressBar;

            FirebaseFirestore firebaseFirestore;
            FirebaseAuth firebaseAuth;
    String user_id;

    TextView textView;
            String user_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_name_change);

        setupNamel=findViewById(R.id.text_name);

        setupBtn=findViewById(R.id.done_btn);

        firebaseFirestore =FirebaseFirestore.getInstance();
        firebaseAuth =FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();

        textView =findViewById(R.id.textView);
        progressBar=findViewById(R.id.signup_progressBar);





        Intent i=getIntent();
        String name=i.getStringExtra("key");


        setupNamel.setText(name);
        setupNamel.setSelectAllOnFocus(true);

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                user_name=setupNamel.getText().toString();



                if(!TextUtils.isEmpty(user_name)) {

                    firebaseFirestore.collection("Users").document(user_id).update("name", user_name);
                    Toast.makeText(getApplicationContext(), " Name updated", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);

                    Intent i = new Intent(nameChange.this, SetupActivityDumy.class);
                    //i.putExtra("key",user_name);
                    startActivity(i);
                    finish();


                }

                else
                {
                    Toast.makeText(nameChange.this,"Name cannot be empty",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

        });

    }


    @Override
    public void onBackPressed() {

        Intent i =new Intent(nameChange.this,SetupActivityDumy.class);
        startActivity(i);
        finish();

        super.onBackPressed();
    }
}
