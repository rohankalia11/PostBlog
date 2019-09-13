package com.example.mineproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class SetupActivity extends AppCompatActivity {

    CircleImageView setupImage;

    Uri mainImageURI=null;

    EditText setupNamel;
    Button setupBtn;

    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    StorageReference image_path ;

    String url,user_name;

    String user_id;

    FirebaseFirestore firebaseFirestore;
    String Email,Password;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_setup);

       // Toolbar toolbar =findViewById(R.id.setupToolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Account Setting");


        progressBar=findViewById(R.id.login_progressBar1);
        firebaseAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        image_path= FirebaseStorage.getInstance().getReference();

        user =firebaseAuth.getCurrentUser();
        setupImage=findViewById(R.id.circleImageView);
        setupNamel=findViewById(R.id.text_name);
        setupBtn=findViewById(R.id.btn_done);

        firebaseFirestore =FirebaseFirestore.getInstance();

        Email =getIntent().getExtras().getString("Email");
        Password=getIntent().getExtras().getString("Password");

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 user_name =setupNamel.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(user_name)&& mainImageURI !=null)
                {


                    firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful())
                            {

                                user_id = firebaseAuth.getCurrentUser().getUid();


                                image_path = storageReference.child("profile_images").child(user_id+".jpg");
                                image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        if (task.isSuccessful())
                                        {

                                            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    url = uri.toString();

                                                    Map<String,String> userMap =new HashMap<>();
                                                    userMap.put("name",user_name);
                                                    userMap.put("image",url);


                                                    firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            sendEmailVerification();

                                                            progressBar.setVisibility(View.INVISIBLE);



                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            Toast.makeText(SetupActivity.this, "error ", Toast.LENGTH_LONG).show();
                                                        }
                                                    });



                                                }
                                            });



                                        }else
                                        { String error = task.getException().getMessage();
                                            Toast.makeText(SetupActivity.this,"Error : "+error,Toast.LENGTH_LONG).show();
                                        } }});
                            }
                            else
                            {
                                String error = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this, "Error:"+error, Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(getApplicationContext(),SignupActivity.class);
                                startActivity(i);
                                finish();
                                } }});



                }

                else
                {
                    Toast.makeText(SetupActivity.this, "Image and Name field cannot be empty", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });



        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {

                        Toast.makeText(SetupActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    }else
                    {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)

                                .start(SetupActivity.this);
                    }

                }

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
            }
        }
    }



    private void sendEmailVerification()
    {
        final FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();
        if (firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        Toast.makeText(SetupActivity.this,"Singnup success,Verification Email send to :"+firebaseUser.getEmail(),Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        finish();
                        Intent intent =new Intent(SetupActivity.this,LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(SetupActivity.this, "Verification Email not Sent", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
    }




}
