package com.example.EEEBuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    Context context;
    ArrayList<CommentModel> commentList; //StudyEvent Model
    String commentedByID, commentedByName;

    DatabaseReference studentProfileRef;

    public CommentAdapter(Context context, ArrayList<CommentModel> commentList) {

        this.context = context;
        this.commentList = commentList;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_comment_layout,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        commentedByID = commentList.get(position).getCommentedBy();

        studentProfileRef = FirebaseDatabase.getInstance().getReference("Student Profile");
        studentProfileRef.child(commentedByID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                commentedByName = dataSnapshot.getValue(UserInfo.class).getName();
                String profileImgUrl = dataSnapshot.getValue(UserInfo.class).getProfileImageUrl();
                holder.name.setText(commentedByName);
                holder.date.setText(commentList.get(position).getDate());
                holder.comment.setText(commentList.get(position).getComment());
                Picasso.get().load(profileImgUrl).into(holder.profileImgHolder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImgHolder;
        TextView name, date, comment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImgHolder = (CircleImageView) itemView.findViewById(R.id.card_comment_profileImg);
            name = (TextView) itemView.findViewById(R.id.card_comment_name);
            date = (TextView) itemView.findViewById(R.id.card_comment_date);
            comment = (TextView) itemView.findViewById(R.id.card_comment);

        }
    }


}
