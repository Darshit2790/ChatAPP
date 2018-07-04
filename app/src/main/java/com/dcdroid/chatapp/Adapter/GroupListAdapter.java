package com.dcdroid.chatapp.Adapter;

/**
 * Created by darshit on 16/5/17.
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.dcdroid.chatapp.Activity.GroupChatActivity;
import com.dcdroid.chatapp.Activity.GroupListActivity;
import com.dcdroid.chatapp.Activity.IndividualChatActivity;
import com.dcdroid.chatapp.ApiClient.ApiHandler;
import com.dcdroid.chatapp.Configuration.Config;
import com.dcdroid.chatapp.Database.ChatDbHelper;
import com.dcdroid.chatapp.Model.ChatModel;
import com.dcdroid.chatapp.Model.GroupUsers.GroupUser;
import com.dcdroid.chatapp.R;
import com.dcdroid.chatapp.Uitils.SharedPreferenceManager;
import com.dcdroid.chatapp.Uitils.Utills;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GroupListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<GroupUser.Datum> _listDataHeader; // header titles
    // child data in format of header title, child title


    /*old flow*/
    /*private HashMap<String, ArrayList<GroupUser.Datum.UserList>> _listDataChild;*/

    /*old flow*/
    /*List<GroupUsersList.Datum> data;*/
    SharedPreferenceManager sharedPreferenceManager;

    ChatDbHelper db;

    /*old flow*/
/*
    public GroupListAdapter(Context context, List<GroupUser.Datum> listDataHeader,
                            HashMap<String, ArrayList<GroupUser.Datum.UserList>> listChildData, List<GroupUsersList.Datum> data) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.data = data;
        sharedPreferenceManager= new SharedPreferenceManager(this._context);
                Log.e("child",""+this._listDataChild.size());
                Log.e("header",""+this._listDataHeader.size());


        this.db= new ChatDbHelper(context);
    }*/


    public GroupListAdapter(Context context, List<GroupUser.Datum> listDataHeader) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        sharedPreferenceManager= new SharedPreferenceManager(this._context);
        this.db= new ChatDbHelper(context);
    }



    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        /*return this._listDataChild.get(this._listDataHeader.get(groupPosition).getGroupNo())
                .get(childPosititon);*/

        return this._listDataHeader.get(groupPosition).getUserList().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String userName = ((GroupUser.Datum.UserList) getChild(groupPosition, childPosition)).getUserName();
        final String loginName = ((GroupUser.Datum.UserList) getChild(groupPosition, childPosition)).getLoginName();

        final String userType = ((GroupUser.Datum.UserList) getChild(groupPosition, childPosition)).getUserType();
        final String fullName = ((GroupUser.Datum.UserList) getChild(groupPosition, childPosition)).getFullName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);


        TextView tv_individual_latest_message = (TextView) convertView
                .findViewById(R.id.tv_individual_latest_message);

        TextView tv_time = (TextView) convertView
                .findViewById(R.id.tv_time);


        ImageView iv_user = (ImageView) convertView
                .findViewById(R.id.iv_user);

        txtListChild.setText(fullName);


//        old flow
       /* for(int i=0;i<data.size();i++)
        {
            if(userName.equals(data.get(i).getUserName()))
            {

                if (!data.get(i).getPhotoUrl().trim().equalsIgnoreCase("")) {
                    if (data.get(i).getPhotoUrl().startsWith("http://")) {
                        Picasso.with(convertView.getContext()).load(data.get(i).getPhotoUrl()).
                                placeholder(convertView.getResources().getDrawable(R.drawable.usertwo)).into(iv_user);
                    } else {

                        String picUrl = "";
                        picUrl = "http://" + data.get(i).getPhotoUrl();
                        Picasso.with(convertView.getContext()).load(picUrl).
                                placeholder(convertView.getResources().getDrawable(R.drawable.usertwo)).into(iv_user);

                    }

                }

            Log.e(i+"",data.get(i).getPhotoUrl());
            }


        }*/


        final ArrayList<GroupUser.Datum.UserList> usersList = _listDataHeader.get(groupPosition).getUserList();


        if (!usersList.get(childPosition).getPhotoUrl().trim().equalsIgnoreCase("")) {
            if (usersList.get(childPosition).getPhotoUrl().startsWith("http://")) {
                Picasso.with(convertView.getContext()).load(usersList.get(childPosition).getPhotoUrl()).
                        placeholder(convertView.getResources().getDrawable(R.drawable.usertwo)).into(iv_user);
            } else {

                String picUrl = "";
                picUrl = "http://" + usersList.get(childPosition).getPhotoUrl();
                Picasso.with(convertView.getContext()).load(picUrl).
                        placeholder(convertView.getResources().getDrawable(R.drawable.usertwo)).into(iv_user);

            }

        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(_context, IndividualChatActivity.class);
                intent.putExtra("name",userName);
                intent.putExtra("LoginName",loginName);
                intent.putExtra("full_name",fullName);
                intent.putExtra("user_type",userType);
                intent.putExtra("groupmembers",usersList);
                _context.startActivity(intent);
            }
        });



        //Fetch Latest Message From Individual Chat
        String receiverName=loginName.replace("@","#").toLowerCase(),sendername=sharedPreferenceManager.getKEY_LOGINNAME().toLowerCase();



        Log.i("Receiver",receiverName);
        Log.i("sendername",sendername);


        ChatModel latest_record=db.getLatestIndividualChatMessage(sendername,receiverName);


        if(latest_record!=null)
        {
            if (latest_record.getName()!=null && latest_record.getTime()!=null && latest_record.getMessage()!=null)
            {
                tv_individual_latest_message.setVisibility(View.VISIBLE);
                tv_time.setVisibility(View.VISIBLE);

                if(latest_record.getMessage().length()>10)
                {
                    tv_individual_latest_message.setText(latest_record.getName().replace("#","@")+" : "+latest_record.getMessage().substring(0,10)+"...");
                }else
                {
                    tv_individual_latest_message.setText(latest_record.getName().replace("#","@")+" : "+latest_record.getMessage());
                }
                try {
                    /*Jul 12, 2017 5:53:56 PM*/
                    /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");*/
                    DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm:ss a");//1


                    SimpleDateFormat dateFormat_ = new SimpleDateFormat("HH:mm");
                    Date messageTimeStamp = dateFormat.parse(latest_record.getTime());
                    String time=dateFormat_.format(messageTimeStamp)+"";
                    if(time.length()>0)
                        tv_time.setText(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Date messageTimeStamp = null;
                    try {
                        //                        2 Aug 2017 4:31:25 pm

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");//2
                        SimpleDateFormat dateFormat_ = new SimpleDateFormat("HH:mm");
                        messageTimeStamp = dateFormat.parse(latest_record.getTime());
                        String time=dateFormat_.format(messageTimeStamp)+"";
                        if(time.length()>0)
                            tv_time.setText(time);
                    } catch (ParseException e1) {
                        e1.printStackTrace();

                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//3
                            SimpleDateFormat dateFormat_ = new SimpleDateFormat("HH:mm");
                            messageTimeStamp = dateFormat.parse(latest_record.getTime());

                        String time=dateFormat_.format(messageTimeStamp)+"";
                        if(time.length()>0)
                            tv_time.setText(time);

                        } catch (ParseException e2) {
                            e2.printStackTrace();
                            try {
//                        Aug 7, 2017 15:31:04
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");///4
                            SimpleDateFormat dateFormat_ = new SimpleDateFormat("HH:mm");

                                messageTimeStamp = dateFormat.parse(latest_record.getTime());


                            String time=dateFormat_.format(messageTimeStamp)+"";
                            if(time.length()>0)
                                tv_time.setText(time);
                            } catch (ParseException e3) {
                                e3.printStackTrace();
                            }
                        }
                    }


                }
            }else
            {
                tv_individual_latest_message.setVisibility(View.GONE);
                tv_time.setVisibility(View.INVISIBLE);

            }
        }



        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        /*old flow*/
        /*return this._listDataChild.get(this._listDataHeader.get(groupPosition).getGroupNo()).size();*/
        return this._listDataHeader.get(groupPosition).getUserList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {


        /*old flow*/
        /*GroupList.Datum group = (GroupList.Datum) getGroup(groupPosition);*/

        final GroupUser.Datum group = (GroupUser.Datum) getGroup(groupPosition);

        final String headerTitle = group.getGroupNo();
        final String transferee_name = group.getTransfereeName();

         String group_image = group.getPhotoUrl();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);

        TextView tv_transferee_name = (TextView) convertView
                .findViewById(R.id.tv_transferee_name);

        TextView tv_group_latest_message = (TextView) convertView
                .findViewById(R.id.tv_group_latest_message);

        TextView tv_time = (TextView) convertView
                .findViewById(R.id.tv_time);



        /*GRDIVIEW for head potraits*/
      /* final GridView iv_group = (GridView) convertView
                .findViewById(R.id.iv_group);


        GroupUserPictures groupUserPictures = new GroupUserPictures(_context,_listDataChild.get(headerTitle),data);
        iv_group.setAdapter(groupUserPictures);


        iv_group.setEnabled(false);

        if(_listDataChild.get(headerTitle).size()%2==0) {

            iv_group.setNumColumns(2);
        }else
        {
            iv_group.setNumColumns(3);
        }*/


        final ImageView iv_group = (ImageView) convertView
                .findViewById(R.id.iv_group);



        if(group_image!=null)

        {

      /*      if(!userimage.startsWith("http://")||!userimage.startsWith("https://")){
                userimage="http://"+userimage;
            }*/


            Log.e("GroupImage",group_image.toString());

            Picasso.with(_context).load(group_image.toString()).placeholder(R.drawable.group_icon_round).into(iv_group);

            Log.e("--groupProfile--", "groupimage=" + group_image);
        }

        LinearLayout layout_right = (LinearLayout) convertView.findViewById(R.id.layout_right);

        lblListHeader.setText(headerTitle);
        tv_transferee_name.setText("TEE: "+transferee_name);


        ImageView iv_expand_collapse = (ImageView) convertView.findViewById(R.id.iv_expand_collapse);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

       //Fetch Latest Message From Group

        ChatModel latest_record=db.getLatestMessageRoomWise(headerTitle.toLowerCase());


        if(latest_record!=null)
        {
            if (latest_record.getName()!=null && latest_record.getTime()!=null && latest_record.getMessage()!=null)
            {
                tv_group_latest_message.setVisibility(View.VISIBLE);
                tv_time.setVisibility(View.VISIBLE);

                if(latest_record.getMessage().length()>10)
                {
                    tv_group_latest_message.setText(latest_record.getName().replace("#","@")+" : "+latest_record.getMessage().substring(0,10)+"...");
                }else
                {
                    tv_group_latest_message.setText(latest_record.getName().replace("#","@")+" : "+latest_record.getMessage());
                }
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//1
                    SimpleDateFormat dateFormat_ = new SimpleDateFormat("HH:mm");
                    Date messageTimeStamp = dateFormat.parse(latest_record.getTime());

                    String time=dateFormat_.format(messageTimeStamp)+"";

                    if(time.length()>0)
                    tv_time.setText(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                    try {
                        /*Aug 2, 2017 3:01:52 PM*/
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");//2
                    SimpleDateFormat dateFormat_ = new SimpleDateFormat("HH:mm");
                    Date messageTimeStamp = null;

                    messageTimeStamp = dateFormat.parse(latest_record.getTime());


                    String time=dateFormat_.format(messageTimeStamp)+"";

                    if(time.length()>0)
                        tv_time.setText(time);
                    } catch (ParseException e1) {
                        e1.printStackTrace();


//                        2 Aug 2017 4:31:25 pm


                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");///3
                            SimpleDateFormat dateFormat_ = new SimpleDateFormat("HH:mm");
                            Date messageTimeStamp = null;
                            messageTimeStamp = dateFormat.parse(latest_record.getTime());
                            String time=dateFormat_.format(messageTimeStamp)+"";

                            if(time.length()>0)
                                tv_time.setText(time);
                        } catch (ParseException e2) {
                            e2.printStackTrace();
//                        Aug 7, 2017 15:31:04


                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");///4
                                SimpleDateFormat dateFormat_ = new SimpleDateFormat("HH:mm");
                                Date messageTimeStamp = null;
                                messageTimeStamp = dateFormat.parse(latest_record.getTime());
                                String time=dateFormat_.format(messageTimeStamp)+"";

                                if(time.length()>0)
                                    tv_time.setText(time);
                            } catch (ParseException e3) {
                                e3.printStackTrace();
                            }

                        }
                    }



                }
            }else
            {
                tv_group_latest_message.setVisibility(View.GONE);
                tv_time.setVisibility(View.INVISIBLE);

            }
        }
        if(isExpanded)
        {
            iv_expand_collapse.setImageResource(R.drawable.up_aerrow);
        }else
        {
            iv_expand_collapse.setImageResource(R.drawable.down_green_aerrow);
        }


        layout_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isExpanded)
                {
                    GroupListActivity.list_group.collapseGroup(groupPosition);

                }else
                {
                    GroupListActivity.list_group.expandGroup(groupPosition);
                }

            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*old flow*/
                /*createOrJoinGroup(headerTitle,transferee_name);*/


                ArrayList<GroupUser.Datum.UserList> users=group.getUserList();

                createOrJoinGroup(headerTitle,transferee_name,users);

            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public void createOrJoinGroup(final String groupName, final String transferee_name, final ArrayList<GroupUser.Datum.UserList> users)
    {

        if (Utills.isConnectingToInternet(_context)) {
            /*Utills.showDialog(_context,"Checking Group on Openfire...");*/
            /*Utills.showDialog(_context,"Loading...");*/


            ApiHandler.getOpenfireApiService().createGroupOnOpenfire("application/json", Config.openfire_host_server_key, createOpenfireGroupJson(groupName),new Callback<String>() {
                @Override
                public void success(final String response_string, Response response) {
                    Utills.dismissDialog();

                    /*old flow*/
                    /*addUserToOpenFireRoom(groupName,sharedPreferenceManager.getKEY_LOGINNAME(),transferee_name);*/

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

    public void addUserToOpenFireRoom(final String groupName, String username, final String transfereename, final ArrayList<GroupUser.Datum.UserList> userLists)
    {

        if (Utills.isConnectingToInternet(_context)) {
            /*Utills.showDialog(_context,"Adding user to Group on Openfire...");*/
            /*Utills.showDialog(_context,"Loading...");*/


            ApiHandler.getOpenfireApiService().addUserToOpenFireRoom("application/json", Config.openfire_host_server_key,groupName,username,new Callback<String>() {
                @Override
                public void success(final String response_string, Response response) {
                    Utills.dismissDialog();


                    if(response.getStatus()==201)
                    {
                        Intent intent = new Intent(_context, GroupChatActivity.class);
                        intent.putExtra("name",groupName);
                        intent.putExtra("transfereename",transfereename);
                        /*intent.putExtra("groupmembers",_listDataChild.get(groupName));*/
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

}
