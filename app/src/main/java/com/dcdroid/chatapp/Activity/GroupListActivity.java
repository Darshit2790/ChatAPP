package com.dcdroid.chatapp.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.dcdroid.chatapp.Adapter.GroupListAdapter;
import com.dcdroid.chatapp.ApiClient.ApiHandler;
import com.dcdroid.chatapp.Configuration.Config;
import com.dcdroid.chatapp.Database.ChatDbHelper;
import com.dcdroid.chatapp.Model.BadKeyWord.BadKeyWord;
import com.dcdroid.chatapp.Model.ChatModel;

import com.dcdroid.chatapp.Model.GroupUsers.GroupUser;
import com.dcdroid.chatapp.Model.UserProfile.UserProfile;
import com.dcdroid.chatapp.R;
import com.dcdroid.chatapp.Uitils.SharedPreferenceManager;
import com.dcdroid.chatapp.Uitils.SmackUtils;
import com.dcdroid.chatapp.Uitils.Utills;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GroupListActivity extends AppCompatActivity {

    public TextView tv_heading;
    private ImageView iv_back,iv_right_icon , iv_back_User_img;

    private EditText edt_search;
    GroupListAdapter listAdapter;
    public static ExpandableListView list_group;
    //List<GroupList.Datum> groups= new ArrayList<>();   old flow

    BadKeyWord badKeyWords=null;

    public static List<GroupUser.Datum> groups = new ArrayList();
    public static HashMap<String, ArrayList<GroupUser.Datum.UserList>> listDataChild=new HashMap<>();


    private String UserProfilePicture;

/*    private int STORAGE_PERMISSION_CODE = 23;
    private int CAMERA_PERMISSION_CODE = 24;
    private int RECORD_AUDIO_CODE= 25;*/
    String currentDateTimeString="";

    private int RUNTIME_PERSMISSION_CODE= 0000;
    SharedPreferenceManager sharedPreferenceManager;

    LinearLayout layout_main;

    Context _context;

    ChatDbHelper db;


    public static boolean isFirstTime=false;


    public String myMUCfullName="";

    public MultiUserChatManager manager=null;
    public MultiUserChat mMultiUserChat=null;

    /*public static  List<GroupUsersList.Datum> userData;*/

    @Override
    protected void onResume() {
        super.onResume();
        layout_main.setVisibility(View.VISIBLE);

        list_group.setAdapter(listAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();
        _context=GroupListActivity.this;

        sharedPreferenceManager= new SharedPreferenceManager(GroupListActivity.this);
        list_group=(ExpandableListView) findViewById(R.id.group_list);

        tv_heading=(TextView) findViewById(R.id.tv_heading);

        layout_main=(LinearLayout) findViewById(R.id.layout_main);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back_User_img =  (ImageView) findViewById(R.id.iv_back_User_img);
        iv_right_icon = (ImageView) findViewById(R.id.iv_right_icon);
        edt_search=(EditText) findViewById(R.id.edt_search);
        tv_heading.setText("Group");
        iv_back.setVisibility(View.VISIBLE);
        iv_right_icon.setVisibility(View.VISIBLE);

        iv_right_icon.setImageResource(R.drawable.refresh);
        UserProfilePicture=sharedPreferenceManager.getKEY_PROFILEPICTURE();


        isFirstTime=true;

        Log.e("Profile Picture : ",UserProfilePicture.toString());

        if(UserProfilePicture!=null)

        {

            if(!UserProfilePicture.startsWith("http://")||!UserProfilePicture.startsWith("https://")){
                UserProfilePicture="http://"+UserProfilePicture;
            }


            Picasso.with(GroupListActivity.this).load(UserProfilePicture.toString()).resize(150, 100).into(iv_back_User_img);

            Log.e("--UserProfile--", "userimage=" + UserProfilePicture);
        }


        //getGroupList(); old flow
        loadGroupList();


        list_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        /*       Intent intent = new Intent(GroupListActivity.this,MainActivity.class);
                intent.putExtra("name",friends.get(position).getName());
                GroupListActivity.this.startActivity(intent);*/

            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                /*old flow*/
                /*List<GroupList.Datum> search_groups= new ArrayList<GroupList.Datum>();*/

                List<GroupUser.Datum> search_groups= new ArrayList<GroupUser.Datum>();

                /*old flow*/
                /*HashMap<String, ArrayList<GroupUser.Datum>> searched_listDataChild=new HashMap<>();*/


                HashMap<String, ArrayList<GroupUser.Datum.UserList>> searched_listDataChild=new HashMap<>();


                search_groups.clear();
                for(int i=0;i<groups.size();i++)
                {

                    /*old flow*/
                 /*   for (int j=0;j<listDataChild.get(groups.get(i).getGroupNo()).size();j++)
                    {
                        *//*if(listDataChild.get(groups.get(i).getGroupNo()).get(j).getUserName().equalsIgnoreCase(s.toString()))*//*
                        if(listDataChild.get(groups.get(i).getGroupNo()).get(j).getUserName().contains(s))
                        {
                            search_groups.add(groups.get(i));
                            searched_listDataChild.put(groups.get(i).getGroupNo(),listDataChild.get(groups.get(i).getGroupNo()));
                            break;
                        }
                    }*/

                    for (int j=0;j<groups.get(i).getUserList().size();j++)
                    {
                        /*if(listDataChild.get(groups.get(i).getGroupNo()).get(j).getUserName().equalsIgnoreCase(s.toString()))*/

                        /*Compare username,loginname,tee name, groupname*/
                        if(groups.get(i).getUserList().get(j).getUserName().toLowerCase().contains(s.toString().toLowerCase())||groups.get(i).getUserList().get(j).getLoginName().toLowerCase().contains(s.toString().toLowerCase())||groups.get(i).getUserList().get(j).getEmail().toLowerCase().contains(s.toString().toLowerCase())||groups.get(i).getUserList().get(j).getFullName().toLowerCase().contains(s.toString().toLowerCase())||groups.get(i).getGroupNo().toLowerCase().contains(s.toString().toLowerCase())||groups.get(i).getTransfereeName().toLowerCase().contains(s.toString().toLowerCase()))
                        {
                            search_groups.add(groups.get(i));
                            break;
                        }
                    }
                }


                if(s.length()>0)
                {

                    /*old flow*/
                    /*listAdapter = new GroupListAdapter(GroupListActivity.this, search_groups, searched_listDataChild,userData);*/

                    listAdapter = new GroupListAdapter(GroupListActivity.this, search_groups);

                    // setting list adapter
                    list_group.setAdapter(listAdapter);

                }else
                {

                    /*old flow*/
/*
                    listAdapter = new GroupListAdapter(GroupListActivity.this, search_groups, searched_listDataChild,userData);
*/


                    listAdapter = new GroupListAdapter(GroupListActivity.this, search_groups);

                    // setting list adapter
                    list_group.setAdapter(listAdapter);

                }




            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        iv_back_User_img.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.GONE);

        iv_back_User_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this,UserProfileActivity.class);
//                intent.putExtra("name",friends.get(position).getName());
                GroupListActivity.this.startActivity(intent);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


        iv_right_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getGroupList(); old flow



                getUserProfileDetail();


            }
        });

        if(isReadStorageAllowed()){
            //If permission is already having then showing the toast
//            Toast.makeText(ActivityGetRol.this,"You already have the permission", Toast.LENGTH_LONG).show();
            //Existing the method with return
            Log.e("already permission","You already have the permission");
            return;
        }



        requestStoragePermission();






    }


    /*old flow*/
   /* private void getGroupList() {

        if (Utills.isConnectingToInternet(GroupListActivity.this)) {
            *//*Utills.showDialog(GroupListActivity.this,"Fetching Groups...");*//*
            Utills.showDialog(GroupListActivity.this,"Loading...");
            Log.e("Api call", "Login");

            HashMap<String,String> group_list_param = new HashMap<>();
            group_list_param.put("UserName",sharedPreferenceManager.getKeyUsername());
            group_list_param.put("UserType",sharedPreferenceManager.getKeyUsertype());

            ApiHandler.getIMApiService().getGroupList(group_list_param,new Callback<GroupList>() {
                @Override
                public void success(final GroupList groupList, Response response) {
                    *//*Utills.dismissDialog();*//*
                    groups=groupList.getData();
                    if(groups.size()==1)
                    {layout_main.setVisibility(View.GONE);
                    }else
                    {
                        layout_main.setVisibility(View.VISIBLE);
                    }

                    listDataChild = new HashMap<String, ArrayList<GroupUser.Datum>>();
                    getGroupUsers();

                }

                @Override
                public void failure(RetrofitError error) {
                    *//*Utills.dismissDialog();*//*
                    Log.e("Failure", "Fail" + error.toString());

                    Toast.makeText(GroupListActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            Log.e("Failure", "No connection");

            Toast.makeText(GroupListActivity.this,"No Internet Connection!!!",Toast.LENGTH_LONG).show();

        }
    }
*/


    private void loadGroupList() {

        if (Utills.isConnectingToInternet(GroupListActivity.this)) {
            /*Utills.showDialog(GroupListActivity.this,"Fetching Group Users...");*/
            Utills.showDialog(GroupListActivity.this,"Loading...");
            Log.e("Api call", "Group Users");

            HashMap<String,String> group_user_param = new HashMap<>();
            group_user_param.put("UserName",sharedPreferenceManager.getKeyUsername());
            group_user_param.put("UserType",sharedPreferenceManager.getKeyUsertype());

            ApiHandler.getIMApiService().getGroupUsers(group_user_param,new Callback<GroupUser>() {
                @Override
                public void success(final GroupUser groupUser, Response response) {
                    Utills.dismissDialog();
                    /*old flow*/
                    /*groups=groupList.getData();*/

                    groups=groupUser.getData();

                /*    if(groups.size()==1)
                    {layout_main.setVisibility(View.GONE);
                    }else
                    {
                        layout_main.setVisibility(View.VISIBLE);
                    }*/

                    /*old flow*/
                    /*listDataChild = new HashMap<String, ArrayList<GroupUser.Datum>>();*/

/*

                    listDataChild = new HashMap<String, ArrayList<GroupUser.Datum.UserList>>();

                    //fill groups
                    for(int i=0;i<groups.size();i++)
                    {

                        ArrayList<GroupUser.Datum.UserList> childs = new ArrayList<>();

                        //file list of childs
                        for(int j=0;j<groupUser.getData().size();j++)
                        {
                            if(groups.get(i).getGroupNo().equals(groupUser.getData().get(j).getGroupNo()))
                            {
                                childs.add(groupUser.getData().get(j).getUserList());
                            }
                        }


                        Log.e("Group : "+groups.get(i).getGroupNo(),"   -- -- -- "+"Childs :"+childs.size());

                        listDataChild.put(groups.get(i).getGroupNo(),childs);

                    }*/


                    /*old flow*/
                      /*  getGroupUserPictures();*/


                    //If only Single group available then jump to group chat screen
                    if(groups.size()==1&&isFirstTime)
                    {Utills.dismissDialog();
                        listAdapter = new GroupListAdapter(GroupListActivity.this, groups);
                        list_group.setAdapter(listAdapter);

                        ArrayList<GroupUser.Datum.UserList> users=groups.get(0).getUserList();
                        createOrJoinGroup(groups.get(0).getGroupNo(),groups.get(0).getTransfereeName(),users);
                    }
                    //If Multiple groups available then show the expandable list pf groups
                    else
                    {

                        //Visible Layout
                        layout_main.setVisibility(View.VISIBLE);

                        HashMap<String,String> images= new HashMap();

                        /*old flow*/
                        /*listAdapter = new GroupListAdapter(GroupListActivity.this, groups, listDataChild,userData);*/

                        listAdapter = new GroupListAdapter(GroupListActivity.this, groups);
                        list_group.setAdapter(listAdapter);
                        isFirstTime=false;

                        //fetch badkeywords and fetch latest messages

                        if(badKeyWords==null)
                        {

                            getBadkeywords();
                        }else
                        {
                            new fetchAllGroupsLatestMessage().execute();
                        }

                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Utills.dismissDialog();
                    Log.e("Failure", "Fail" + error.toString());

                    Toast.makeText(GroupListActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                    //fetch badkeywords and fetch latest messages

                    if(badKeyWords==null)
                    {

                        getBadkeywords();
                    }else
                    {
                        new fetchAllGroupsLatestMessage().execute();
                    }
                }
            });
        } else {

            Log.e("Failure", "No connection");

            Toast.makeText(GroupListActivity.this,"No Internet Connection!!!",Toast.LENGTH_LONG).show();
            //fetch badkeywords and fetch latest messages

            if(badKeyWords==null)
            {

                getBadkeywords();
            }else
            {
                new fetchAllGroupsLatestMessage().execute();
            }

        }
    }


    private void getUserProfileDetail () {

        if (Utills.isConnectingToInternet(GroupListActivity.this)) {

/*Utills.showDialog(GroupListActivity.this,"Fetching User Icon...");*/

            Log.e("Api call", "UserProfile");

            HashMap<String,String> user_Profile_param = new HashMap<>();
            user_Profile_param.put("AppsName","IM");
            user_Profile_param.put("UserName",sharedPreferenceManager.getKeyUsername());
            user_Profile_param.put("UserType",sharedPreferenceManager.getKeyUsertype());

            ApiHandler.getCommonApiService().UserProfile(user_Profile_param, new Callback<UserProfile>() {
                @Override
                public void success(final UserProfile userProfile, Response response) {
                    Utills.dismissDialog();
                    List<UserProfile.Datum> data = userProfile.getData();
                    String userimage="";


                    try{
                        userimage = data.get(0).getPhotoUrl();
                    }catch (Exception ew)
                    {

                    }


                    if(userimage!=null)

                    {

                        if(!userimage.startsWith("http://")||!userimage.startsWith("https://")){
                            userimage="http://"+userimage;
                        }


                        Picasso.with(GroupListActivity.this).load(userimage.toString()).resize(150, 100).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                iv_back_User_img.setImageBitmap(bitmap);

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {


                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

                        Log.e("--UserProfile--", "userimage=" + userimage);
                    }

                    loadGroupList();

                }

                @Override
                public void failure(RetrofitError error) {
                    Utills.dismissDialog();
                    Log.e("Failure", "Fail" + error.toString());
                    Toast.makeText(GroupListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    loadGroupList();
                }
            });
        } else {

            Log.e("Failure", "No connection");

            Toast.makeText(GroupListActivity.this,"No Internet Connection!!!",Toast.LENGTH_LONG).show();

        }
    }

    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int result3 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED&& result2 == PackageManager.PERMISSION_GRANTED&& result3 == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)){

        }

        //And finally ask for the permission
        /*ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},RUNTIME_PERSMISSION_CODE);*/
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},RUNTIME_PERSMISSION_CODE);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},RUNTIME_PERSMISSION_CODE);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RUNTIME_PERSMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == RUNTIME_PERSMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Log.e("permission granted","Permission granted now you can read the storage");
//                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
//                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
                Log.e("permission denied","Oops you just denied the permission of storage");
            }
        }
       /* else if(requestCode == CAMERA_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Log.e("permission granted","Permission granted now you can use Camera");
//                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
//                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
                Log.e("permission denied","Oops you just denied the permission of camera");
            }
        }

        //Checking the request code of our request
       else if(requestCode == RECORD_AUDIO_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Log.e("permission granted","Permission granted now you can record audio");
//                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
//                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
                Log.e("permission denied","Oops you just denied the permission for recording");
            }
        }*/

    }


    /*old flow*/
/*
    private void getGroupUserPictures() {

        if (Utills.isConnectingToInternet(GroupListActivity.this)) {
            *//*Utills.showDialog(GroupListActivity.this,"Fetching Pictures...");*//*
            Log.e("Api call", "Pictures");

            Map<String,String> group_user_Profile_param = new HashMap<>();

            group_user_Profile_param.put("UserName",sharedPreferenceManager.getKeyUsername());
            group_user_Profile_param.put("UserType",sharedPreferenceManager.getKeyUsertype());

            ApiHandler.getIMApiService().getGroupUsersList(group_user_Profile_param, new Callback<GroupUsersList>() {
                @Override
                public void success(final GroupUsersList groupUsersList, Response response) {
                    *//*Utills.dismissDialog();*//*


                    userData = groupUsersList.getData();


                    //If only Single group available then jump to group chat screen
                    if(groups.size()==1)
                    {Utills.dismissDialog();
                        createOrJoinGroup(groups.get(0).getGroupNo(),groups.get(0).getTransfereeName());
                    }
                    //If Multiple groups available then show the expandable list pf groups
                    else
                    {

                        //Visible Layout
                        layout_main.setVisibility(View.VISIBLE);

                        HashMap<String,String> images= new HashMap();

                        listAdapter = new GroupListAdapter(GroupListActivity.this, groups, listDataChild,userData);
                        list_group.setAdapter(listAdapter);

                        getUserProfileDetail();
                    }




                }

                @Override
                public void failure(RetrofitError error) {
                    *//*Utills.dismissDialog();*//*
                    getUserProfileDetail() ;
                    Log.e("Failure", "Fail" + error.toString());
                    Toast.makeText(GroupListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {Utills.dismissDialog();

            Log.e("Failure", "No connection");
            Toast.makeText(GroupListActivity.this,"No Internet Connection!!!",Toast.LENGTH_LONG).show();

        }
    }*/
    public void createOrJoinGroup(final String groupName,final String transferee_name, final ArrayList<GroupUser.Datum.UserList> users)
    {

        if (Utills.isConnectingToInternet(_context)) {
            /*Utills.showDialog(_context,"Checking Group on Openfire...");*/
            Utills.showDialog(_context,"Loading...");


            ApiHandler.getOpenfireApiService().createGroupOnOpenfire("application/json", Config.openfire_host_server_key, createOpenfireGroupJson(groupName),new Callback<String>() {
                @Override
                public void success(final String response_string, Response response) {
                    Utills.dismissDialog();
                    addUserToOpenFireRoom(groupName,sharedPreferenceManager.getKEY_LOGINNAME(),transferee_name,users);
                }

                @Override
                public void failure(RetrofitError error) {
                    Utills.dismissDialog();
                    Log.e("Failure", "Fail in openfire API " + error.toString());
                }
            });
        } else {
            Log.e("Failure", "No connection");
        }
    }

    public void addUserToOpenFireRoom(final String groupName,String username,final String transfereename,final ArrayList<GroupUser.Datum.UserList> userLists)
    {

        if (Utills.isConnectingToInternet(_context)) {
            /*Utills.showDialog(_context,"Adding user to Group on Openfire...");*/
            Utills.showDialog(_context,"Loading...");


            ApiHandler.getOpenfireApiService().addUserToOpenFireRoom("application/json", Config.openfire_host_server_key,groupName,username,new Callback<String>() {
                @Override
                public void success(final String response_string, Response response) {
                    Utills.dismissDialog();


                    if(response.getStatus()==201)
                    {
                        Intent intent = new Intent(_context, GroupChatActivity.class);
                        intent.putExtra("name",groupName);
                        intent.putExtra("transfereename",transfereename);
                        intent.putExtra("groupmembers",userLists);
                        _context.startActivity(intent);
                    }else
                    {
                        Toast.makeText(_context,"Unable to add user in group in openfire!",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Utills.dismissDialog();
                    Log.e("Failure", "Fail in openfire API " + error.toString());

                    Toast.makeText(_context,"Unable to add user in group in openfire!",Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("Failure", "No connection");
        }
    }


    public JsonObject createOpenfireGroupJson(String groupName)
    {

        JsonObject gsonObject = new JsonObject();

        try {
            JSONObject jsonObj_createorder = new JSONObject();
            jsonObj_createorder.put("roomName", groupName);
            jsonObj_createorder.put("naturalName", groupName);
            jsonObj_createorder.put("description", groupName);
            jsonObj_createorder.put("subject", groupName);
            jsonObj_createorder.put("persistent", "true");
            jsonObj_createorder.put("publicRoom", "true");
            jsonObj_createorder.put("registrationEnabled", "true");
            jsonObj_createorder.put("canAnyoneDiscoverJID", "true");
            jsonObj_createorder.put("canOccupantsChangeSubject", "true");
            jsonObj_createorder.put("canOccupantsInvite", "true");
            jsonObj_createorder.put("canChangeNickname", "true");
            jsonObj_createorder.put("logEnabled", "true");
            /*jsonObj_createorder.put("loginRestrictedToNickname", "false");*/
            jsonObj_createorder.put("membersOnly", "true");
            jsonObj_createorder.put("moderated", "false");

            JSONObject jsonObj_broadcastPresenceRoles = new JSONObject();
            JSONArray jsonArr_broadcastPresenceRole = new JSONArray();

            jsonArr_broadcastPresenceRole.put("moderator");
            jsonArr_broadcastPresenceRole.put("participant");
            jsonArr_broadcastPresenceRole.put("visitor");

            jsonObj_broadcastPresenceRoles.put("broadcastPresenceRole",jsonArr_broadcastPresenceRole);

            jsonObj_createorder.put("broadcastPresenceRoles", jsonObj_broadcastPresenceRoles);


            JSONObject jsonObj_Owners = new JSONObject();

            /*jsonObj_Owners.put("owner","admin@"+Config.openfire_host_server_SERVICE);*/

            jsonObj_Owners.put("owner","$innoways@"+Config.openfire_host_server_SERVICE);

            jsonObj_createorder.put("owners",jsonObj_Owners);
            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(jsonObj_createorder.toString());

        }catch (JSONException e)
        {

        }

        return gsonObject;
    }


    /*Fetch All group message and save in db*/
    public class  fetchAllGroupsLatestMessage extends AsyncTask<Void,Void,Void>
    {


        @Override
        protected Void doInBackground(Void... params) {


            Log.e("Fetching messages","-------------");
            db = new ChatDbHelper(GroupListActivity.this);
            for(int i=0;i<groups.size();i++)
            {
                final String groupName=groups.get(i).getGroupNo();

                myMUCfullName=groupName.toLowerCase() + "@" + Config.openfire_host_server_CONFERENCE_SERVICE;
                try {
                manager= MultiUserChatManager.getInstanceFor(Config.conn1);
                mMultiUserChat=manager.getMultiUserChat(myMUCfullName);




                /*yyyy-MM-dd'T'HH:mm:ss.SSS'Z'*/

                    DiscussionHistory history = new DiscussionHistory();

                    //fetch since the last messafe date received from db

                /*check latest date from db,if null take today's date*/

                    String latest_date = db.getLatestMessageDateFromgroupChat(groupName.toLowerCase());

                    Log.e("database date : ",latest_date+" ------" +
                            "");


                    Date since_history_date= new Date();



                    if(latest_date!=null)
                    {

                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try{
                            since_history_date = df.parse(latest_date);
                        }catch (ParseException e)
                        {
                            Log.e("Exception ",e.toString());
                        }


                    }else
                    {

                        Calendar cal = GregorianCalendar.getInstance();
                        cal.setTime(new Date());
                        cal.add(Calendar.DAY_OF_YEAR, -7);
                        since_history_date = cal.getTime();

                    }

                    Log.e("--------","Fetching history of group  "+groupName.toLowerCase()+" since "+since_history_date.toString());
                    history.setSince(since_history_date);


                /*mMultiUserChat.join(sharedPreferenceManager.getKEY_LOGINNAME());*/

                    mMultiUserChat.join(sharedPreferenceManager.getKEY_LOGINNAME(), null, history, SmackConfiguration.getDefaultPacketReplyTimeout());
                } catch (SmackException.NoResponseException e) {

                    Log.e("Fetch messsages : ",e.toString());
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                    Log.e("Fetch messsages : ",e.toString());
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    Log.e("Fetch messsages : ",e.toString());
                } catch (RuntimeException e)
                {

                }


                //Receiving Group Chat message
                mMultiUserChat.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(final Message message) {

                        if(message.getBody()!=null){
                            if (message.getBody().length() > 0) {


                                if(badKeyWords!=null)
                                {

                                    runOnUiThread(new Runnable() {


                                        @Override
                                        public void run() {

                                            String filteredMessage = message.getBody();


                                            for (int i = 0; i < badKeyWords.getData().size(); i++) {

                                                String badkeyWord = badKeyWords.getData().get(i).getItemName();

                                                filteredMessage = filteredMessage.replaceAll("(?i)" + badkeyWord, "*");
                                            }


                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTimeInMillis(System.currentTimeMillis());
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            String name = SmackUtils.parseResource(message.getFrom());

                                            DelayInformation inf = null;
                                            try {
                                                inf = (DelayInformation)message.getExtension("delay","urn:xmpp:delay");
                                            } catch (Exception e) {

                                                Log.e("DELAY in Timestamp ",e.toString());
                                            }
// get offline message timestamp


                                            String messageTimeStamp="";
                                            String timeinMillis="";
                                            if(inf!=null) {
                                                Date date = inf.getStamp();
                                                currentDateTimeString = DateFormat.getDateTimeInstance().format(date);
                                                messageTimeStamp = dateFormat.format(date);
                                                timeinMillis=date.getTime()+"";


                                                Log.e("if statement : ",currentDateTimeString+"   --  " +timeinMillis+"");

                                            }else {

                                                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                                messageTimeStamp = dateFormat.format(calendar.getTime());
                                                timeinMillis=System.currentTimeMillis()+"";
                                                Log.e("else statement : ",currentDateTimeString+"   --  " +timeinMillis+"");
                                            }
                                            final ChatModel chatModel = new ChatModel();
                                            chatModel.setName(name);
                                            chatModel.setMessage(filteredMessage);
                                            chatModel.setConfId(groupName.toLowerCase());
                                            chatModel.setTimeStamp(currentDateTimeString);
                                            chatModel.setTimeinMillis(timeinMillis+"");

                                            chatModel.setTime(messageTimeStamp + "");

                                            chatModel.setFile_name("");
                                            chatModel.setImage_height("");
                                            chatModel.setImage_width("");
                                            chatModel.setFile_url("");

                                            if (SmackUtils.parseResource(message.getFrom()).equals(sharedPreferenceManager.getKEY_LOGINNAME())) {
                                                chatModel.setMyMessage(1);
                                            } else {
                                                chatModel.setMyMessage(0);
                                            }

                                            chatModel.setMessage_id(message.getStanzaId()+"");


                                            //Multimedia or Text Message

                                            final DefaultExtensionElement extPhotoChat= (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:photochat");

                                            final DefaultExtensionElement extAudiourl = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:audiourl");

                                            final DefaultExtensionElement extFileName = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:extfilename");


                                            final DefaultExtensionElement extTypeOfChat = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:exttypeofchat");

                                            final DefaultExtensionElement extImageWidth = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:extimageWidth");

                                            final DefaultExtensionElement extImageHeight = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:extimageHeight");

                                            final DefaultExtensionElement extchatTime = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:extchatTime");


                                            final DefaultExtensionElement extuserProfilePicture = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:extuserProfilePicture");

                                            if(extTypeOfChat!=null)
                                            {

                                                String dir = Environment.getExternalStorageDirectory()+ File.separator+"IMCHATAPP";
                                                //create folder
                                                File folder = new File(dir); //folder name
                                                folder.mkdirs();

                                                        /*NEEDS TO RUN IN BACKGROUND*/

                                                        try {

                                                            if(extTypeOfChat.getValue("typeofchat").equals("image"))
                                                            {
                                                                try
                                                                { chatModel.setFile_url(extPhotoChat.getValue("PhotoChat"));


                                                                    chatModel.setImage_height(extImageHeight.getValue("imageHeight"));
                                                                    chatModel.setImage_width(extImageWidth.getValue("imageWidth"));

                                                                }catch(NullPointerException e)
                                                                {

                                                                }

                                                                try{
                                                                    chatModel.setMessage("[Picture]");
                                                                }catch (Exception e)
                                                                {

                                                                }



                                                            }else if(extTypeOfChat.getValue("typeofchat").equals("audio"))
                                                            {
                                                                try{
                                                                    chatModel.setFile_url(extAudiourl.getValue("audiourl"));
                                                                    chatModel.setImage_height("");
                                                                    chatModel.setImage_width("");
                                                                }catch(NullPointerException e)
                                                                {

                                                                }
                                                                try{
                                                                    chatModel.setMessage("[Audio]");
                                                                }catch (Exception e)
                                                                {

                                                                }

                                                            }




                                                            chatModel.setMultimedia(1);
                                                    /*chatModel.setFile(incommingFile);*/
                                                            chatModel.setFile_name(extFileName.getValue("FileName"));
                                                            /****Save message in Database SQLITE*****/

                                                            db.insertGroupChat(chatModel);

                                                            /****************************************/

                                                        }catch (NullPointerException e1)
                                                        {
                                                            e1.printStackTrace();
                                                        }


                                            }
                                            else
                                            {
                                                chatModel.setMessage(filteredMessage);
                                                chatModel.setMultimedia(0);
                                        /*chatModel.setFile(null);*/
                                                chatModel.setFile_name("");
                                                chatModel.setImage_height("");
                                                chatModel.setImage_width("");
                                                chatModel.setFile_url("");
                                                /****Save message in Database SQLITE*****/

                                                db.insertGroupChat(chatModel);

                                                /****************************************/
                                            }





                                        }
                                    });
                                }else
                                {

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {

                                            String name = SmackUtils.parseResource(message.getFrom());

                                            DelayInformation inf = null;
                                            try {
                                                inf = (DelayInformation)message.getExtension("delay","urn:xmpp:delay");
                                            } catch (Exception e) {

                                                Log.e("DELAY in Timestamp ",e.toString());
                                            }
// get offline message timestamp
                                            if(inf!=null) {
                                                Date date = inf.getStamp();
                                                currentDateTimeString = DateFormat.getDateTimeInstance().format(date);
                                            }else {

                                                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                            }
                                            String messageBody = message.getBody();

                                            final ChatModel chatModel = new ChatModel();
                            /*chatModel.setName(delayInformation.getFrom().replace("@","#"));*/
                                            chatModel.setName(name);
                                            chatModel.setMessage(messageBody);
                                            chatModel.setTimeinMillis(System.currentTimeMillis()+"");
                                            chatModel.setConfId(groupName.toLowerCase());
                                            chatModel.setTimeStamp(currentDateTimeString);
                                            chatModel.setTimeinMillis(System.currentTimeMillis()+"");
                                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                            Calendar cal = Calendar.getInstance();
                                            Date date = cal.getTime();


                                            String date1 = format1.format(date);


                                            chatModel.setTime(date1 + "");


                                            if (SmackUtils.parseResource(message.getFrom()).equals(sharedPreferenceManager.getKEY_LOGINNAME())) {
                                                chatModel.setMyMessage(1);
                                            } else {
                                                chatModel.setMyMessage(0);
                                            }

                                            chatModel.setMessage_id(message.getStanzaId()+"");

                                            //Multimedia or Text Message

                                            final DefaultExtensionElement extPhotoChat= (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:photochat");

                                            final DefaultExtensionElement extAudiourl = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:audiourl");

                                            final DefaultExtensionElement extFileName = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:extfilename");


                                            final DefaultExtensionElement extTypeOfChat = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:exttypeofchat");

                                            final DefaultExtensionElement extImageWidth = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:extimageWidth");

                                            final DefaultExtensionElement extImageHeight = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:extimageHeight");

                                            final DefaultExtensionElement extchatTime = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:extchatTime");


                                            final DefaultExtensionElement extuserProfilePicture = (DefaultExtensionElement) message
                                                    .getExtension("urn:xmpp:extuserProfilePicture");

                                            if(extTypeOfChat!=null)
                                            {

                                                String dir = Environment.getExternalStorageDirectory()+ File.separator+"IMCHATAPP";
                                                //create folder
                                                File folder = new File(dir); //folder name
                                                folder.mkdirs();

                                                final File  incommingFile = new File(dir, extFileName.getValue("FileName"));
                                                        /*NEEDS TO RUN IN BACKGROUND*/


                                                        try {

                                                            if(extTypeOfChat.getValue("typeofchat").equals("image"))
                                                            {
                                                                try
                                                                { chatModel.setFile_url(extPhotoChat.getValue("PhotoChat"));


                                                                    chatModel.setImage_height(extImageHeight.getValue("imageHeight"));
                                                                    chatModel.setImage_width(extImageWidth.getValue("imageWidth"));

                                                                }catch(NullPointerException e)
                                                                {

                                                                }


                                                                try{
                                                                    chatModel.setMessage("[Picture]");
                                                                }catch (Exception e)
                                                                {

                                                                }



                                                            }else if(extTypeOfChat.getValue("typeofchat").equals("audio"))
                                                            {
                                                                try{
                                                                    chatModel.setFile_url(extAudiourl.getValue("audiourl"));

                                                                    chatModel.setImage_height("");
                                                                    chatModel.setImage_width("");
                                                                }catch(NullPointerException e)
                                                                {

                                                                }
                                                                try{
                                                                    chatModel.setMessage("[Audio]");
                                                                }catch (Exception e)
                                                                {

                                                                }

                                                            }




                                                            chatModel.setMultimedia(1);
                                                    /*chatModel.setFile(incommingFile);*/
                                                            chatModel.setFile_name(extFileName.getValue("FileName"));




                                                        } catch (NullPointerException e1)
                                                        {
                                                            e1.printStackTrace();
                                                        }

                                            }
                                            else
                                            {
                                                chatModel.setMessage(messageBody);
                                                chatModel.setMultimedia(0);
                                        /*chatModel.setFile(null);*/
                                                chatModel.setFile_name("");
                                                chatModel.setImage_height("");
                                                chatModel.setImage_width("");
                                                chatModel.setFile_url("");
                                            }


                                            db.insertGroupChat(chatModel);

                                        }
                                    });

                                }


                            }

                        }


                    }
                });




            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            listAdapter = new GroupListAdapter(GroupListActivity.this, groups);
            list_group.setAdapter(listAdapter);


            Log.e("Processing, ","grouplist Latest message !");

        }



    }

    public void getBadkeywords()
    {

        Map<String, String> group_bad_keyword_param = new HashMap<>();


        group_bad_keyword_param.put("UserType", sharedPreferenceManager.getKeyUsertype());

        ApiHandler.getIMApiService().checkBadKeyWords(group_bad_keyword_param, new Callback<BadKeyWord>() {
            @Override
            public void success(final BadKeyWord badKeyWord, Response response) {
                                    /*Utills.dismissDialog();*/
                badKeyWords=badKeyWord;
                new fetchAllGroupsLatestMessage().execute();

            }

            @Override
            public void failure(RetrofitError error) {
                Utills.dismissDialog();
                Log.e("Failure", "Fail" + error.toString());
                badKeyWords=null;
                new fetchAllGroupsLatestMessage().execute();

            }
        });


    }

}