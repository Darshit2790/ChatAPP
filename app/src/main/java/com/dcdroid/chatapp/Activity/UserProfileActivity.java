package com.dcdroid.chatapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dcdroid.chatapp.ApiClient.ApiHandler;
import com.dcdroid.chatapp.Database.ChatDbHelper;
import com.dcdroid.chatapp.Model.UserProfile.UserProfile;
import com.dcdroid.chatapp.R;
import com.dcdroid.chatapp.Uitils.SharedPreferenceManager;
import com.dcdroid.chatapp.Uitils.Utills;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by cpadmin on 24/5/17.
 */
public class UserProfileActivity extends AppCompatActivity {

    ImageView  iv_User_img  , iv_back , iv_right_icon;
    TextView  txt_full_name_detail , txt_email_detail ,  txt_phone_detail  , tv_heading,txt_name,txt_logout;


    boolean isfromChat=false;
    SharedPreferenceManager sharedPreferenceManager;
    Intent intent;
    LinearLayout main_layout;

    ChatDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        sharedPreferenceManager= new SharedPreferenceManager(UserProfileActivity.this);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();
        findViewById();


        db=new ChatDbHelper(this);
        getUserProfileDetail() ;

//            setActions();

    }

    public void findViewById() {
        main_layout=(LinearLayout) findViewById(R.id.main_layout);
        txt_full_name_detail=(TextView) findViewById(R.id.txt_full_name_detail);
        txt_logout=(TextView) findViewById(R.id.txt_logout);
        txt_email_detail=(TextView) findViewById(R.id.txt_email_detail);
        txt_phone_detail=(TextView) findViewById(R.id.txt_phone_detail);
        iv_User_img = (ImageView) findViewById(R.id.iv_User_img);
        iv_right_icon = (ImageView) findViewById(R.id.iv_right_icon);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        txt_name = (TextView) findViewById(R.id.txt_name);
        tv_heading=(TextView) findViewById(R.id.tv_heading);
        iv_back.setVisibility(View.VISIBLE);
        iv_right_icon.setVisibility(View.GONE);
        iv_back .setImageDrawable(getResources().getDrawable(R.drawable.left_aerrow));

        tv_heading.setText("User Profile");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        txt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreferenceManager.clearData();
/*
                db.removeAll();*/

                Intent intent= new Intent(UserProfileActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                UserProfileActivity.this.startActivity(intent);

            }
        });

    }

    private void getUserProfileDetail () {

        if (Utills.isConnectingToInternet(UserProfileActivity.this)) {
            /*Utills.showDialog(UserProfileActivity.this,"Fetching User Profile...");*/
            Utills.showDialog(UserProfileActivity.this,"Loading...");
            Log.e("Api call", "UserProfile");

            HashMap<String,String> user_Profile_param = new HashMap<>();
            user_Profile_param.put("AppsName","IM");


            intent = getIntent();
            if (intent.hasExtra("isFromChat")) {
                isfromChat=intent.getBooleanExtra("isFromChat",false);
                txt_logout.setVisibility(View.GONE);
            }else{
                txt_logout.setVisibility(View.VISIBLE);
            }

            if(isfromChat)
            {
                    user_Profile_param.put("UserName",intent.getStringExtra("userName"));
                    user_Profile_param.put("UserType",intent.getStringExtra("userType"));
                    Log.e("if","if");

            }else
            {
                user_Profile_param.put("UserName",sharedPreferenceManager.getKeyUsername());
                user_Profile_param.put("UserType",sharedPreferenceManager.getKeyUsertype());
                Log.e("else","else");
            }


            ApiHandler.getCommonApiService().UserProfile(user_Profile_param, new Callback<UserProfile>() {
                @Override
                public void success(final UserProfile userProfile, Response response) {
                    Utills.dismissDialog();

                    main_layout.setVisibility(View.VISIBLE);

                    List<UserProfile.Datum> data = userProfile.getData();
                    String userimage="";
                    try{

                         userimage = data.get(0).getPhotoUrl().toString();
                    }catch (Exception e)
                    {

                    }
                  /*  Picasso.with(UserProfileActivity.this).load("atoms.zmallplanet.com/Common/Image/ShowImage.aspx?FileType=StaffPhoto&PkeyNo=app.programming&FileName=4c185e7d-d059-40c6-8f7b-4a195aa7c7a4.jpg").
                            placeholder(getResources().getDrawable(R.drawable.chet_icon)).resize(32, 32).into(iv_User_img);*/


                    if(!userimage.startsWith("http://")||!userimage.startsWith("https://")){
                        userimage="http://"+userimage;
                    }


                    Picasso.with(UserProfileActivity.this).load(userimage.toString()).placeholder(R.drawable.usertwo).resize(150, 100).into(iv_User_img);

                    Log.e("--UserProfile--", "userimage=" + userimage);
                    try{
                        txt_full_name_detail .setText(data.get(0).getFullName());
                    }catch (Exception e)
                    {

                    }

                    try
                    {
                        txt_email_detail .setText(data.get(0).getEmail());

                    }catch (Exception e)
                    {

                    }

                    try
                    {
                        txt_phone_detail .setText(data.get(0).getPhone());
                    }catch (Exception e)
                    {

                    }


                    try
                    {
                        txt_name .setText(data.get(0).getFullName());
                    }catch (Exception e)
                    {

                    }


                }

                @Override
                public void failure(RetrofitError error) {
                    Utills.dismissDialog();
                    Log.e("Failure", "Fail" + error.toString());
                    Toast.makeText(UserProfileActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            Log.e("Failure", "No connection");

            Toast.makeText(UserProfileActivity.this,"No Internet Connection!!!",Toast.LENGTH_LONG).show();

        }
    }


}