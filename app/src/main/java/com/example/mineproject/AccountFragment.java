package com.example.mineproject;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    FirebaseFirestore firebaseFirestore;
    CircleImageView setupImage;
    TextView setupNamel;
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


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_account, container, false);



        firebaseFirestore =FirebaseFirestore.getInstance();
        setupImage=view.findViewById(R.id.circleImageView);
        setupNamel=view.findViewById(R.id.textView2);

        firebaseAuth =FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();


        storageReference= FirebaseStorage.getInstance().getReference();
        image_path= FirebaseStorage.getInstance().getReference();

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
                        Glide.with(AccountFragment.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);


                    }



                }

                else
                {
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(), "Error:"+error, Toast.LENGTH_SHORT).show();
                }


            }
        });


        return view;
    }

}
