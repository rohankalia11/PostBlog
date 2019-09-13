package com.example.mineproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class SetupActivityDumy extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    CircleImageView setupImage;
    EditText setupNamel;
    Button setupBtn;
    FirebaseAuth firebaseAuth;
    String user_id;
    ProgressBar progressBar;
    Uri mainImageURI=null;
    StorageReference image_path;
    String url,user_name;
    StorageReference storageReference;
    String name;


    String shared;
    String image;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_setup_dumy);



        firebaseFirestore =FirebaseFirestore.getInstance();
        setupImage=findViewById(R.id.circleImageView);
        setupNamel=findViewById(R.id.text_name);
        setupBtn=findViewById(R.id.btn_done);
        firebaseAuth =FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressBar=findViewById(R.id.login_progressBar1);

        storageReference= FirebaseStorage.getInstance().getReference();
        image_path= FirebaseStorage.getInstance().getReference();


       // Intent i=getIntent();
       //user_name= i.getStringExtra("key");




        setupNamel.setInputType(InputType.TYPE_NULL);
        setupNamel.setFocusable(false);
        setupNamel.setClickable(true);


        setupBtn.setEnabled(false);



        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {


                         name = task.getResult().getString("name");
                        image = task.getResult().getString("image");


                        setupNamel.setText(name);
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.user);
                        Glide.with(SetupActivityDumy.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);


                    }



                }

                else
                {
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivityDumy.this, "Error:"+error, Toast.LENGTH_SHORT).show();
                }


            }
        });



        setupNamel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int D_left=0;
                final int D_rigth=2;
                final int D_top=1;
                final int D_bottom=3;

                if (event.getAction()==MotionEvent.ACTION_UP)
                {
                    if (event.getRawX()>=(setupNamel.getRight()-setupNamel.getCompoundDrawables()[D_rigth].getBounds().width()))
                    {
                        Intent i=new Intent(getApplicationContext(),nameChange.class);

                        ActivityOptions options =  ActivityOptions.makeSceneTransitionAnimation(SetupActivityDumy.this,setupNamel,"sharedtext");

                        i.putExtra("key",name);

                        startActivity(i,options.toBundle());

                        finish();


                        return true;
                    }
                }


                return false;
            }
        });


        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupBtn.setEnabled(true);

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivityDumy.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {

                        Toast.makeText(SetupActivityDumy.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivityDumy.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    }else
                    {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)

                                .start(SetupActivityDumy.this);
                    }

                }

            }
        });






        setupBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                progressBar.setVisibility(View.VISIBLE);



                user_id = firebaseAuth.getCurrentUser().getUid();
                image_path = storageReference.child("profile_images").child(user_id + ".jpg");


                image_path.putFile(mainImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                url = uri.toString();

                                //Map<String, String> userMap = new HashMap<>();

                               // userMap.put("image", url);
                              //  userMap.put("name", user_name);



                                firebaseFirestore.collection("Users").document(user_id).update("image",url);
                                Toast.makeText(getApplicationContext(),"Profile image updated",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);

                            }

                        });


                    }


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Not Updated",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });



            }



        });


            }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI=result.getUri();

                setupImage.setImageURI(mainImageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {


                Exception error = result.getError();
                Toast.makeText(getApplicationContext(),"Error"+error,Toast.LENGTH_LONG).show();
            }
        }
    }


  /*  private void storeinfo(String Url,String Name)
    {
        SharedPreferences sharedPreferences=getSharedPreferences("File",MODE_PRIVATE);
        SharedPreferences.Editor mEditor=sharedPreferences.edit();
        mEditor.putString("Url",Url);
        mEditor.putString("Name",Name);
        mEditor.apply();
    }


    private String Url_info()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("File",MODE_PRIVATE);
        String url1=sharedPreferences.getString("Url",null);
        return url1;
    }

    private String Name_info()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("File",MODE_PRIVATE);
        String name1=sharedPreferences.getString("Name",null);
        return name1;
    }*/

}
