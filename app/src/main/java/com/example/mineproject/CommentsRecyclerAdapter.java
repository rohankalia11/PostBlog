package com.example.mineproject;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String dateString;
    long milliseconds;

    public CommentsRecyclerAdapter(List<Comments> commentsList)
    {
        this.commentsList=commentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item,parent,false);
       context=parent.getContext();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        return new CommentsRecyclerAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        final String currentUserId =firebaseAuth.getCurrentUser().getUid();

        String user_id =commentsList.get(position).getUser_id();





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




        String commentMessage =commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);



      try {

           milliseconds = commentsList.get(position).getTimestamp().getTime();
          dateString = DateFormat.format("dd/MM/yyyy", new Date(milliseconds)).toString();
          holder.setTime(dateString);
        //  Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();

      }
      catch (Exception e)
      {

          Date d=Calendar.getInstance().getTime();
          SimpleDateFormat df =new SimpleDateFormat("dd/MM/yyyy");
          String formattedDate =df.format(d);
          holder.setTime(formattedDate);
        //  Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show();
      }




    }

    @Override
    public int getItemCount() {

        if (commentsList!=null)
        {
            return commentsList.size();
        }
        else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       View mView;
       TextView comment_message;
        ImageView commentImageView;
        TextView commentDate;
        TextView commentUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView =itemView;
        }

        public void setComment_message(String message)
        {
            comment_message=mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
        }


        public void setTime(String date)
        {

            commentDate=mView.findViewById(R.id.comment_date);
            commentDate.setText(date);

        }


        public void setUserData(String name,String image)
        {
            commentImageView=mView.findViewById(R.id.comment_userimage);
            commentUserName=mView.findViewById(R.id.comment_username);

            commentUserName.setText(name);
            RequestOptions placeholderOptions=new RequestOptions();
            placeholderOptions.placeholder(R.drawable.usercard);


            Glide.with(context).applyDefaultRequestOptions(placeholderOptions).load(image).into(commentImageView);
        }


    }
}
