package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private ImageView profilePic;
    MyAdapter myAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private TextView userName;
    private View waveView;
    private ImageView logoView;
    String id;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("AdsGrow", Context.MODE_PRIVATE);
        loginButton = findViewById(R.id.login_button);
        profilePic = findViewById(R.id.profile_pic);
        userName = findViewById(R.id.user_nameui);
        waveView = findViewById(R.id.view2);
        logoView = findViewById(R.id.logoImageView);
        recyclerView = findViewById(R.id.rv_page_list);

        profilePic.setVisibility(View.INVISIBLE);
        userName.setVisibility(View.INVISIBLE);

        callbackManager = CallbackManager.Factory.create();

        loginButton.setPermissions(Arrays.asList("pages_show_list", "pages_read_user_content", "pages_manage_ads", "user_friends"));


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                logoView.setVisibility(View.INVISIBLE);
                //waveView.setVisibility(View.INVISIBLE);
                profilePic.setVisibility(View.VISIBLE);
                userName.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        id = sharedPreferences.getString("id", null);
        if (id != null) {
            String path = "/" + id + "/assigned_pages";
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    path,
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            /* handle the result */
                            if (response.getRawResponse() != null) {
                                Log.d("pageResponse", response.getRawResponse());
                            } else {
                                Toast.makeText(MainActivity.this, response.getError().getErrorMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("pageGetError", response.getError().getErrorMessage());
                            }
                        }

                    }
            ).executeAsync();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("activityResultCalled", "called");
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String name = object.getString("name");
                    userName.setText(name);
                    id = object.getString("id");
                    sharedPreferences.edit().putString("id", id).apply();
                    Picasso.get().load("https://graph.facebook.com/" + id + "/picture?type=large").into(profilePic);
                    Log.d("fbId", id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "gender, name, id");

        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();

//        GraphRequest graphRequestFriends = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
//            @Override
//            public void onCompleted(JSONArray objects, GraphResponse response) {
//                Log.d("DemoFriends", objects.toString());
//                ArrayList<FBPage> fbPages = new ArrayList<>();
//
//                for (int i = 0 ; i < objects.length() ; i++) {
//                    try {
//                        JSONObject jsonObject = objects.getJSONObject(i);
//                        fbPages.add(new FBPage(jsonObject.getString("id"), jsonObject.getString("name")));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                layoutManager = new LinearLayoutManager(MainActivity.this);
//                recyclerView.setLayoutManager(layoutManager);
//
//                myAdapter = new MyAdapter(fbPages);
//                recyclerView.setAdapter(myAdapter);
//            }
//        });
//
//        graphRequestFriends.executeAsync();

    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                myAdapter.clear();
                LoginManager.getInstance().logOut();
                userName.setText("");
                profilePic.setImageResource(0);
                logoView.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}