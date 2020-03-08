package com.example.EEEBuddy;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class JuniorBuddyAdapter extends RecyclerView.Adapter<JuniorBuddyAdapter.MyViewHolder> {

    Context context;
    ArrayList<UserInfo> juniorBuddyList; //Student Info Model
    private CircleImageView profileImage;


    public JuniorBuddyAdapter(Context context, ArrayList<UserInfo> juniorBuddyList) {
        this.context = context;
        this.juniorBuddyList = juniorBuddyList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View view = LayoutInflater.from(context).inflate(R.layout.card_seniorbuddy,parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final String imageUrl = juniorBuddyList.get(position).getProfileImageUrl();
        Picasso.get().load(imageUrl).into(profileImage);
        holder.name.setText(juniorBuddyList.get(position).getName());
        holder.course.setText("Course: " + juniorBuddyList.get(position).getCourse() + ", Year "+ juniorBuddyList.get(position).getYear());
        //holder.experience.setText("Experience: ");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (context, SeniorBuddyInfo.class);
                intent.putExtra("email", juniorBuddyList.get(position).getEmail());
                intent.putExtra("name", juniorBuddyList.get(position).getName());
                intent.putExtra("course", juniorBuddyList.get(position).getCourse());
                intent.putExtra("hall", juniorBuddyList.get(position).getHall());
                intent.putExtra("gender", juniorBuddyList.get(position).getGender());
                intent.putExtra("year", juniorBuddyList.get(position).getYear());
                intent.putExtra("profileImg", imageUrl);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return juniorBuddyList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, course;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.card_name);
            course = (TextView) itemView.findViewById(R.id.card_course);
            profileImage = (CircleImageView) itemView.findViewById(R.id.card_image);

        }

    }


}
