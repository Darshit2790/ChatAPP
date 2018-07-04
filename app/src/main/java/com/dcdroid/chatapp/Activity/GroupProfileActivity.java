package com.dcdroid.chatapp.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dcdroid.chatapp.Adapter.GroupProfileUserAdapter;
import com.dcdroid.chatapp.ApiClient.ApiHandler;
import com.dcdroid.chatapp.Model.GroupUsers.GroupUser;
import com.dcdroid.chatapp.Model.GroupUsersList.GroupUsersList;
import com.dcdroid.chatapp.R;
import com.dcdroid.chatapp.Uitils.SharedPreferenceManager;
import com.dcdroid.chatapp.Uitils.Utills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GroupProfileActivity extends AppCompatActivity {

    public TextView tv_group_members_count,tv_group_name,tv_group_transferee_name;

    public GridView grid_group_users;

    private TextView tv_heading;
    private ImageView iv_back,iv_right_icon;

    Intent intent;

    /*old flow*/
    /*ArrayList<GroupUser.Datum> groupmembers;*/

    ArrayList<GroupUser.Datum.UserList> groupmembers;

    SharedPreferenceManager sharedPreferenceManager;

    String groupName,transfereename;

    public GroupProfileUserAdapter groupProfileUserAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);


        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();
        findViewById();
        tv_heading.setText("Members");
        iv_right_icon.setImageResource(R.drawable.usertwo);


        intent= getIntent();

        sharedPreferenceManager= new SharedPreferenceManager(GroupProfileActivity.this);

        groupName=intent.getStringExtra("groupname");
        transfereename=intent.getStringExtra("transfereename");
        /*groupmembers=(ArrayList<GroupUser.Datum>) intent.getSerializableExtra("groupmembers");*/


        groupmembers=(ArrayList<GroupUser.Datum.UserList>) intent.getSerializableExtra("groupmembers");


        tv_group_name.setText(groupName);
        tv_group_transferee_name.setText("TEE: "+transfereename);

        setActions();

//        getGroups();
        /*getGroupUserProfile() ;*/


        tv_group_members_count.setText(groupmembers.size()+"");

                    /*old flow*/
                    /*groupProfileUserAdapter= new GroupProfileUserAdapter(GroupProfileActivity.this ,data,groupmembers);*/
        groupProfileUserAdapter= new GroupProfileUserAdapter(GroupProfileActivity.this ,groupmembers);
        grid_group_users.setAdapter(groupProfileUserAdapter);

           /*         if(groupmembers.size()>5)
                    {
                        grid_group_users.setNumColumns(5);
                    }else
                    {
                        grid_group_users.setNumColumns(groupmembers.size());
                    }*/

        grid_group_users.setNumColumns(3);
    }


    public void findViewById()
    {
        tv_group_name=(TextView) findViewById(R.id.tv_group_name);
        tv_group_transferee_name=(TextView) findViewById(R.id.tv_group_transferee_name);
        tv_group_members_count=(TextView) findViewById(R.id.tv_group_members_count);
        grid_group_users=(GridView) findViewById(R.id.grid_group_users);
        tv_heading = (TextView) findViewById(R.id.tv_heading);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_right_icon = (ImageView) findViewById(R.id.iv_right_icon);
    }
    public void setActions()
    {


        iv_back.setVisibility(View.VISIBLE);
        iv_right_icon.setVisibility(View.VISIBLE);
        iv_right_icon.setImageResource(R.drawable.refresh);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


          iv_right_icon.setVisibility(View.GONE);
    /*    iv_right_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGroupUserProfile() ;
            }
        });*/

    }

/*
    public void getGroups()
    {

        groupProfileUserAdapter= new GroupProfileUserAdapter(GroupProfileActivity.this);
        grid_group_users.setAdapter(groupProfileUserAdapter);


    }
*/

    private void getGroupUserProfile() {

        if (Utills.isConnectingToInternet(GroupProfileActivity.this)) {
            /*Utills.showDialog(GroupProfileActivity.this, "Geting Group Profile...");*/
            Utills.showDialog(GroupProfileActivity.this, "Loading...");
            Log.e("Api call", "UserProfile");

            Map<String,String> group_user_Profile_param = new HashMap<>();

            group_user_Profile_param.put("UserName",sharedPreferenceManager.getKeyUsername());
            group_user_Profile_param.put("UserType",sharedPreferenceManager.getKeyUsertype());

            ApiHandler.getIMApiService().getGroupUsersList(group_user_Profile_param, new Callback<GroupUsersList>() {
                @Override
                public void success(final GroupUsersList groupUsersList, Response response) {
                    Utills.dismissDialog();

                    List<GroupUsersList.Datum> data =  groupUsersList .getData() ;
                    tv_group_members_count.setText(groupmembers.size()+"");

                    /*old flow*/
                    /*groupProfileUserAdapter= new GroupProfileUserAdapter(GroupProfileActivity.this ,data,groupmembers);*/
                    groupProfileUserAdapter = new GroupProfileUserAdapter(GroupProfileActivity.this ,groupmembers);
                    grid_group_users.setAdapter(groupProfileUserAdapter);

           /*         if(groupmembers.size()>5)
                    {
                        grid_group_users.setNumColumns(5);
                    }else
                    {
                        grid_group_users.setNumColumns(groupmembers.size());
                    }*/

                    grid_group_users.setNumColumns(3);

                }

                @Override
                public void failure(RetrofitError error) {
                    Utills.dismissDialog();
                    Log.e("Failure", "Fail" + error.toString());
                    Toast.makeText(GroupProfileActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            Log.e("Failure", "No connection");
            Toast.makeText(GroupProfileActivity.this,"No Internet Connection!!!",Toast.LENGTH_LONG).show();

        }
    }



}
