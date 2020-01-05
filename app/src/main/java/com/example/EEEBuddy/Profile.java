package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class Profile extends AppCompatActivity {

    private static final String TAG = Profile.class.getName();
    private static final int CHOOSE_IMAGE =101;

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userID;
    private String userEmail, userNode;
    private TextView profileName, profileMatric, profileEmail, profileCourse;

    private CircleImageView profileImage;
    private Uri uriProfileImage;
    private String profileImageUrl;
    private ProgressBar progressBar;

    private String currentPhotoPath;
    static final int ALBUM = 1, CAMERA = 2;

    private Toolbar toolbar;
    private ImageView backBtn, filter;
    private TextView title;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //customise toolbar
        title = (TextView) findViewById(R.id.toolbar_title);
        backBtn = (ImageView)findViewById(R.id.toolbar_back);
        filter = (ImageView) findViewById(R.id.toolbar_filter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title.setText("Profile");
        filter.setVisibility(View.GONE);

        //initialise attributes
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Student Profile");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        profileName = (TextView) findViewById(R.id. profile_name);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileMatric = (TextView) findViewById(R.id. profile_matric);
        profileCourse = (TextView) findViewById(R.id. profile_course);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);

        progressBar = findViewById(R.id.profile_progressBar);


        // Read from the database student details
        databaseReference.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                profileName.setText(userInfo.getName());
                profileMatric.setText(userInfo.getMatric());
                profileEmail.setText(userInfo.getEmail());
                profileCourse.setText(userInfo.getCourse() + ",Yr " + userInfo.getYear());
                profileImageUrl = userInfo.getProfileImageUrl();
                Picasso.get().load(profileImageUrl).into(profileImage);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        //changing of profile image
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoPreviousActivity();
            }
        });

    }

    private void GotoPreviousActivity() {
        startActivity(new Intent(this,Account.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //get selected image
        if(requestCode == ALBUM && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriProfileImage = data.getData();

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                Picasso.get().load(uriProfileImage).into(profileImage);
                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (requestCode == CAMERA && resultCode == RESULT_OK ){

            //uriProfileImage = data.getData();

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            //Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            //Picasso.get().load(uriProfileImage).into(profileImage);
            profileImage.setImageBitmap(bitmap);
            uriProfileImage = getImageUri(getApplicationContext(), bitmap);
            galleryAddPic();
            uploadImageToFirebaseStorage();
        }
    }

    //get image uri from the photo taken
    private Uri getImageUri(Context applicationContext, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(), bitmap, "profilePic", null);
        return Uri.parse(path);
    }


    private void uploadImageToFirebaseStorage() {

        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + userID + ".jpg");
        System.out.println(uriProfileImage);

        if (uriProfileImage != null){
            progressBar.setVisibility(View.VISIBLE);

            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            profileImageUrl = uri.toString();
                            UserInfo userInfo = new UserInfo();
                            userInfo.setProfileImageUrl(profileImageUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                    //update image url to firebase
                    updateProfileImage();

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Profile.this, "Upload Failed." + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void updateProfileImage() {

        databaseReference.child(userID).child("profileImageUrl").setValue(profileImageUrl);

        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                String newProfileImageUrl = userInfo.getProfileImageUrl();
                Picasso.get().load(newProfileImageUrl).into(profileImage);
                Toast.makeText(Profile.this, "Profile Image Uploaded Successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Profile.this, "Failed to Upload", Toast.LENGTH_LONG).show();
            }
        });

    }

    //open photo album and choose image
    private void showImageFromLibrary(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), ALBUM);
    }


    //show dialog box
    private void showUpdateDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflator = getLayoutInflater();
        final View dialogView = inflator.inflate(R.layout.activity_update_dialog, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final Button buttonCamera = (Button) alertDialog.findViewById(R.id.fromCamera);
        final Button buttonAlbum = (Button) alertDialog.findViewById(R.id.fromAlbum);


        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoragePermissions(Profile.this);
                dispatchTakePictureIntent();
                alertDialog.dismiss();
            }
        });


        buttonAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageFromLibrary();
                alertDialog.dismiss();
            }
        });

    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }




    //to use camera
    private void dispatchTakePictureIntent(){

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) { //make sure the there is an app (activity) can handle the camera intent
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createPhotoFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                currentPhotoPath = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.EEEBuddy.fileprovider", photoFile);
                //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                //startActivityForResult(cameraIntent, CAMERA);
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent.createChooser(intent, "Take Photo"), CAMERA);
            }
        }


    }

    //image naming
    private File createPhotoFile() throws IOException{


        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {

             image = File.createTempFile(imageFileName,".jpg",storageDir);

        }catch (Exception e){
            // if any error occurs
            e.printStackTrace();
        }
        // Save a file: path for use with ACTION_VIEW intents
        return image;

    }


    //save image to gallery. invoke the system's media scanner to add your photo to the Media Provider's database,
    // making it available in the Android Gallery application and to other apps.
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}


