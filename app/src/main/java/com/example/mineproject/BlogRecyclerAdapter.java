package com.example.mineproject;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    public Context context;


    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    public BlogRecyclerAdapter(List<BlogPost> blog_list)
    {

        this.blog_list=blog_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bblog_list_item,parent,false);

        context=parent.getContext();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String blogPostId =blog_list.get(position).BlogPostId;

        final String currentUserId =firebaseAuth.getCurrentUser().getUid();
        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url =blog_list.get(position).getImage_url();
        String thumbUri=blog_list.get(position).getThumb();
        holder.setBlogImage(image_url,thumbUri);

        String user_id =blog_list.get(position).getUser_id();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful())
                {
                    String userName =task.getResult().getString("name");
                    String userImage=task.getResult().getString("image");

                    holder.setUserData(userName,userImage);

                }
                else
                {
                    //Firebase Exception
                }
            }
        });


        long milliseconds =blog_list.get(position).getTimestamp().getTime();
        String dateString= DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();

        holder.setTime(dateString);


        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty())
                {
                    int count =queryDocumentSnapshots.size();

                    holder.udateLikesCount(count);
                }
                else
                {
                    holder.udateLikesCount(0);

                }


            }
        });


        firebaseFirestore.collection("Posts/"+blogPostId+"/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty())
                {
                    int count =queryDocumentSnapshots.size();

                    holder.udateCmntCount(count);
                }
                else
                {
                    holder.udateCmntCount(0);

                }


            }
        });




        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (documentSnapshot.exists())
                {
                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));
                }
                else
                {

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));
                }

            }
        });

        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists())
                        {
                            Map<String,Object> likesMap =new HashMap<>();

                            likesMap.put("timestamp", FieldValue.serverTimestamp());


                            firebaseFirestore.collection("Posts/" + blogPostId +"/Likes").document(currentUserId).set(likesMap);
                        }
                        else
                        {
                            firebaseFirestore.collection("Posts/" + blogPostId +"/Likes").document(currentUserId).delete();
                        }



                    }
                });



            }
        });


        holder.blogCommentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent =new Intent (context,CommentsActivity.class);
                commentIntent.putExtra("blog_post_id",blogPostId);
                context.startActivity(commentIntent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        private TextView descView;
        ImageView blogImageView;
        TextView blogDate;
        TextView blogUserName;
        CircleImageView blogUserImage;
        ImageView blogLikeBtn,blogCommentbtn;
        TextView blogLikeCount;
        TextView blogCmntCount;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

            blogLikeBtn=mView.findViewById(R.id.blog_like_btn);
            blogCommentbtn=mView.findViewById(R.id.blog_comment_icon);
        }

        public void setDescText(String descText)
        {
            descView=mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
        }

        public void setBlogImage(String downloadUri,String thumbUri)
        {
            blogImageView=mView.findViewById(R.id.blog_image);
            RequestOptions requestOptions=new RequestOptions();
            requestOptions.placeholder(R.drawable.imageload);


            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(Glide.with(context).load(thumbUri)).into(blogImageView);

        }


        public void setTime(String date)
        {

            blogDate=mView.findViewById(R.id.blog_date);
            blogDate.setText(date);

        }

        public void setUserData(String name,String image)
        {
            blogUserImage=mView.findViewById(R.id.blog_user_image);
            blogUserName=mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);
            RequestOptions placeholderOptions=new RequestOptions();
            placeholderOptions.placeholder(R.drawable.usercard);


            Glide.with(context).applyDefaultRequestOptions(placeholderOptions).load(image).into(blogUserImage);
        }

        public  void udateLikesCount(int count)
        {
            blogLikeCount=mView.findViewById(R.id.blog_like_count3);
            if (count==1)
            {
                blogLikeCount.setText(count+" Like");
            }
            else
            {
                blogLikeCount.setText(count+" Likes");
            }
        }

        public  void udateCmntCount(int count)
        {
            blogCmntCount=mView.findViewById(R.id.blog_cmnt_count);
            if (count==1)
            {
                blogCmntCount.setText(count+" Comment");
            }
            else
            {
                blogCmntCount.setText(count+" Comments");
            }
        }


    }
}
