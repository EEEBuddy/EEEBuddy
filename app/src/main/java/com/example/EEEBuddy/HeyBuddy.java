package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;

public class HeyBuddy extends AppCompatActivity {

    private SpaceNavigationView spaceNavigationView;
    private ImageButton microphone, sendQueryBtn;
    private TextView userInput, responseOutput, query_title, response_title;
    private EditText textInput;
    private LinearLayout linearLayout;

    private ImageView backBtn, rightIcon, notificationIcon;
    private TextView title;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference notificationRef;
    private String userNode, userEmail;

    TextToSpeech textToSpeech;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hey_buddy);

        Initialization();
        NavigationSetUp(savedInstanceState);
        UnreadMessageNotification();

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(linearLayout.getVisibility() == View.INVISIBLE){

                    linearLayout.setVisibility(View.VISIBLE);
                    microphone.setVisibility(View.INVISIBLE);
                    rightIcon.setImageResource(R.drawable.microphone);

                }else{

                    linearLayout.setVisibility(View.INVISIBLE);
                    microphone.setVisibility(View.VISIBLE);
                    rightIcon.setImageResource(R.drawable.text);

                }
            }
        });


        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });


        sendQueryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInputText();
            }
        });

    }

    private void getInputText() {

        String input = textInput.getText().toString();

        if(TextUtils.isEmpty(input)){
            Toast.makeText(this, "Write Something", Toast.LENGTH_SHORT).show();

        }else{

            query_title.setVisibility(View.VISIBLE);
            userInput.setVisibility(View.VISIBLE);

            String userQuery = input;
            userInput.setText(userQuery);
            RetrieveFeedTask task = new RetrieveFeedTask();
            task.execute(userQuery);
            textInput.setText("");

        }

    }


    //Showing google speech input dialog
    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask HeyBuddy About NTU");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
        }
    }


    //Receiving speech input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    query_title.setVisibility(View.VISIBLE);
                    userInput.setVisibility(View.VISIBLE);

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String userQuery = result.get(0);
                    userInput.setText(userQuery);
                    RetrieveFeedTask task = new RetrieveFeedTask();
                    task.execute(userQuery);

                }
                break;

            }
        }

    }


    // Create GetText Method
    public String GetText(String query) throws UnsupportedEncodingException {

        String text = "";
        BufferedReader reader = null;

        // Send data
        try {

            // Defined URL  where to send data
            URL url = new URL("https://api.api.ai/v1/query?v=20150910");

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Authorization", "Bearer 9586413b99834b408783ada6242bf1a4");
            conn.setRequestProperty("Content-Type", "application/json");

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            JSONArray queryArray = new JSONArray();
            queryArray.put(query);
            jsonParam.put("query", queryArray);
//            jsonParam.put("name", "order a medium pizza");
            jsonParam.put("lang", "en");
            jsonParam.put("sessionId", "1234567890");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonParam.toString());
            wr.flush();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;


            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                stringBuilder.append(line + "\n");
            }


            text = stringBuilder.toString();


            //get response from json
            JSONObject object1 = new JSONObject(text);
            JSONObject object = object1.getJSONObject("result");
            JSONObject fulfillment = null;
            String speech = null;
            //if (object.has("fulfillment")) {
            fulfillment = object.getJSONObject("fulfillment");

            //if (fulfillment.has("speech")) {
            speech = fulfillment.optString("speech");
            //}
            //}


            return speech;

        } catch (
                Exception ex) {

        } finally {
            try {

                reader.close();
            } catch (Exception ex) {
            }
        }

        return null;
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            String s = null;
            try {

                s = GetText(voids[0]);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            response_title.setVisibility(View.VISIBLE);
            responseOutput.setVisibility(View.VISIBLE);

            if(microphone.getVisibility() == View.VISIBLE){

                responseOutput.setText(s);
                int speech = textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);

            }else{

                responseOutput.setText(s);
                int speech = textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);

            }

        }

    }


    private void Initialization() {

        backBtn = (ImageView) findViewById(R.id.toolbar_back);
        rightIcon = (ImageView) findViewById(R.id.toolbar_right_icon);
        title = (TextView) findViewById(R.id.toolbar_title);

        backBtn.setVisibility(View.GONE);
        title.setText("Hey Buddy");
        rightIcon.setImageResource(R.drawable.text);

        microphone = (ImageButton) findViewById(R.id.heybuddy_microphone);
        userInput = (TextView) findViewById(R.id.heybuddy_userInput);
        responseOutput = (TextView) findViewById(R.id.heybuddy_response);
        query_title = (TextView) findViewById(R.id.heybuddy_query_title);
        response_title = (TextView) findViewById(R.id.heybuddy_response_title);

        textInput = (EditText) findViewById(R.id.heybuddy_textInput);
        sendQueryBtn = (ImageButton) findViewById(R.id.heybuddy_sendQueryBtn);
        linearLayout = (LinearLayout) findViewById(R.id.heybuddy_layout);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status == TextToSpeech.SUCCESS){
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

    }


    private void NavigationSetUp(Bundle savedInstanceState) {

        spaceNavigationView = findViewById(R.id.navigation);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Senior Buddy", R.drawable.ic_buddy_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Study Buddy", R.drawable.ic_msg_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Chat", R.drawable.ic_chat_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Account", R.drawable.ic_person_grey));

        spaceNavigationView.showIconOnly();


        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                spaceNavigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                //Toast.makeText(Account.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                if (itemIndex == 0) {//goto senior buddy page
                    startActivity(new Intent(HeyBuddy.this, SeniorBuddyPage.class));
                }
                if (itemIndex == 1) {//goto study buddy page
                    startActivity(new Intent(HeyBuddy.this, StudyBuddyPage.class));
                }
                if (itemIndex == 2) {//goto chat
                    startActivity(new Intent(HeyBuddy.this, ChatListPage.class));
                }
                if (itemIndex == 3) {//goto profile page
                    startActivity(new Intent(HeyBuddy.this, Account.class));

                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(HeyBuddy.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void UnreadMessageNotification() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        notificationIcon = (ImageView) findViewById(R.id.navigation_notification);

        notificationRef = FirebaseDatabase.getInstance().getReference("Notification");
        notificationRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    int count = 0;

                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                        NotificationModel notificationModel = ds.getValue(NotificationModel.class);
                        String type = notificationModel.getType();


                        if(type.equals("message") || type.equals("group_messages")){

                            count ++;
                        }

                    }

                    if(count>0){

                        notificationIcon.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
