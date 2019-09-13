package com.example.mineproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import id.zelory.compressor.Compressor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {
    ImageView newPostImage;
    EditText  newPostDesc;
    Button    newPostBtn;
    static final int MAX_LENGTH=100;
    Uri postImageUri=null;
    ProgressBar newPostProgress;

    StorageReference storageReference,filepath;
    String url,desc,current_user_id,thumbUrl;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Bitmap compressedImageFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_new_post);

        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        current_user_id=firebaseAuth.getCurrentUser().getUid();

        newPostImage=findViewById(R.id.new_post_image);
        newPostDesc=findViewById(R.id.new_post_desc);
        newPostBtn=findViewById(R.id.new_post_btn);
        newPostProgress=findViewById(R.id.new_post_progress);
        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(NewPostActivity.this);
            }
        });


        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  desc =newPostDesc.getText().toString();

                if (!TextUtils.isEmpty(desc) && postImageUri!=null)
                {
                   newPostProgress.setVisibility(View.VISIBLE);

                   final String randomName = random();
                    filepath =storageReference.child("post_image").child(randomName+".jpg");
                   filepath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                           if (task.isSuccessful())
                           {

                               File newImageFile =new File(postImageUri.getPath());
                               try {
                                   compressedImageFile = new Compressor(NewPostActivity.this)
                                           .setMaxHeight(100)
                                           .setMaxWidth(100)
                                           .setQuality(2)
                                           .compressToBitmap(newImageFile);
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }


                               ByteArrayOutputStream baos =new ByteArrayOutputStream();
                               compressedImageFile.compress(Bitmap.CompressFormat.JPEG,100,baos);
                               byte[] thumbData=baos.toByteArray();

                               final UploadTask uploadTask =storageReference.child("post_images/thumbs").child(randomName+".jpg").putBytes(thumbData);

                               uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                   @Override
                                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                       storageReference.child("post_images/thumbs").child(randomName+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                           @Override
                                           public void onSuccess(Uri uri) {

                                               thumbUrl=uri.toString();

                                               filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                   @Override
                                                   public void onSuccess(Uri uri) {
                                                       url = uri.toString();

                                                       Map<String,Object > postMap = new HashMap<>();
                                                       postMap.put("image_url",url);
                                                       postMap.put("thumb",thumbUrl);
                                                       postMap.put("desc",desc);
                                                       postMap.put("user_id",current_user_id);
                                                       postMap.put("timestamp",FieldValue.serverTimestamp());

                                                       firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<DocumentReference> task) {

                                                               if (task.isSuccessful())
                                                               {
                                                                   Toast.makeText(NewPostActivity.this,"Post was added",Toast.LENGTH_LONG).show();
                                                                   Intent mainIntent=new Intent(NewPostActivity.this,MainActivity.class);
                                                                   startActivity(mainIntent);
                                                                   finish();

                                                               }
                                                               else
                                                               {
                                                                   String error = task.getException().getMessage();
                                                                   Toast.makeText(NewPostActivity.this,"Error : "+error,Toast.LENGTH_LONG).show();

                                                               }

                                                               newPostProgress.setVisibility(View.INVISIBLE);
                                                           }
                                                       });


                                                   }});

                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {

                                           }
                                       });



                                           }
                                       });
                           }

                           else
                           {
                               newPostProgress.setVisibility(View.INVISIBLE);
                           }



                       }
                   });


                }

                else
                {
                    Toast.makeText(NewPostActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
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

                postImageUri =result.getUri();
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {


                Exception error = result.getError();
            }
        }
    }

    public static String random() {
      /*  Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();*/
        String id = UUID.randomUUID().toString();
        return id;
    }


}
