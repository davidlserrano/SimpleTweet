package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    EditText etCompose;
    Button btnTweet;
    TextView tvCounter;
    public static final int MAX_TWEET_LENGTH = 140;
    public static final String TAG = "ComposeActivity";

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCounter = findViewById(R.id.tvCounter);

        client = TwitterApp.getRestClient(this);

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(MAX_TWEET_LENGTH - s.toString().length() < 0){
                    tvCounter.setTextColor(Color.parseColor("#FF0000"));
                    tvCounter.setText(s.toString().length() + "/" + MAX_TWEET_LENGTH);
                }else{
                    tvCounter.setTextColor(Color.parseColor("#000000"));
                }
                tvCounter.setText(s.toString().length() + "/" + MAX_TWEET_LENGTH);

            }
        });



        //Set click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etCompose.getText().toString().isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                } else if(etCompose.getText().toString().length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String tweet = etCompose.getText().toString();
                // Make API call to twitter to publish tweet
                client.publishTweet(tweet, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try{
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // set result code & bundle data for response
                            setResult(RESULT_OK, intent);
                            // closes activity, passes data back to parent
                            finish();

                        } catch(JSONException e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });

            }
        });
    }
}