package com.example.EEEBuddy;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class SeniorBuddyAdapter extends RecyclerView.Adapter<SeniorBuddyAdapter.MyViewHolder> {

    Context context;
    ArrayList<SeniorBuddyModel> seniorBuddyList; //Student Info Model
    private CircleImageView profileImage;


    public SeniorBuddyAdapter(Context context, ArrayList<SeniorBuddyModel> seniorBuddyList) {
        this.context = context;
        this.seniorBuddyList = seniorBuddyList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_seniorbuddy,parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final String imageUrl = seniorBuddyList.get(position).getProfileImageUrl();
        Picasso.get().load(imageUrl).into(profileImage);
        holder.name.setText(seniorBuddyList.get(position).getName());
        holder.course.setText("Course: " + seniorBuddyList.get(position).getCourse() + ", Year "+ seniorBuddyList.get(position).getYear());
       // holder.experience.setText("Experience: " + seniorBuddyList.get(position).getExperience());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (context, SeniorBuddyInfo.class);
                intent.putExtra("email", seniorBuddyList.get(position).getEmail());
                intent.putExtra("name", seniorBuddyList.get(position).getName());
                intent.putExtra("course", seniorBuddyList.get(position).getCourse());
                intent.putExtra("hall", seniorBuddyList.get(position).getHall());
                intent.putExtra("gender", seniorBuddyList.get(position).getGender());
                intent.putExtra("year", seniorBuddyList.get(position).getYear());
                intent.putExtra("profileImg", imageUrl);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return seniorBuddyList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, course, year, experience;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.card_name);
            course = (TextView) itemView.findViewById(R.id.card_course);
           // year = (TextView) itemView.findViewById(R.id.card_year);
            profileImage = (CircleImageView) itemView.findViewById(R.id.card_image);
           // experience = (TextView) itemView.findViewById(R.id.card_exp);

        }

    }


}
