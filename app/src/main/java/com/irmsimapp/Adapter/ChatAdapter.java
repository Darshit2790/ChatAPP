package com.irmsimapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.irmsimapp.Activity.GroupChatActivity;
import com.irmsimapp.Activity.IndividualChatActivity;
import com.irmsimapp.Activity.UserProfileActivity;
import com.irmsimapp.Model.ChatModel;
import com.irmsimapp.Model.GroupUsers.GroupUser;
import com.irmsimapp.R;
import com.irmsimapp.Uitils.SharedPreferenceManager;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by darshit on 25/4/17.
 */
public class ChatAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    List<ChatModel> list;
    Context context;
    SharedPreferenceManager sharedPreferenceManager;
    boolean isGroupChat;
    ArrayList<GroupUser.Datum.UserList> groupmembers;


    public ChatAdapter(Context context, List<ChatModel> list, boolean isGroupChat,    ArrayList<GroupUser.Datum.UserList> groupmembers) {

        this.list = list;
        this.context = context;
        sharedPreferenceManager = new SharedPreferenceManager(context);
        this.isGroupChat = isGroupChat;
        this.groupmembers = groupmembers;
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = convertView;


        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.chat_screen, null);
        }

        TextView tv_sender_right = (TextView) v.findViewById(R.id.tv_sender_right);
        TextView tv_message_right = (TextView) v.findViewById(R.id.tv_message_right);
        TextView tv_time_stamp_right = (TextView) v.findViewById(R.id.tv_time_stamp_right);
        final ImageView iv_chat_member = (ImageView) v.findViewById(R.id.iv_chat_member);

        RelativeLayout messageLayout_right = (RelativeLayout) v.findViewById(R.id.messageLayoutRight);
        TextView tv_sender_left = (TextView) v.findViewById(R.id.tv_sender_left);
        TextView tv_message_left = (TextView) v.findViewById(R.id.tv_message_left);
        ImageView iv_message_left = (ImageView) v.findViewById(R.id.iv_message_left);
        ImageView iv_message_right = (ImageView) v.findViewById(R.id.iv_message_right);
        final ImageView iv_chat_member_left = (ImageView) v.findViewById(R.id.iv_chat_member_left);
        TextView tv_time_stamp_left = (TextView) v.findViewById(R.id.tv_time_stamp_left);

        RelativeLayout messageLayout_left = (RelativeLayout) v.findViewById(R.id.messageLayoutleft);
        RelativeLayout inner_left = (RelativeLayout) v.findViewById(R.id.inner_left);
        RelativeLayout inner_right = (RelativeLayout) v.findViewById(R.id.inner_right);
        RelativeLayout layout_main = (RelativeLayout) v.findViewById(R.id.layout_main);

        try {

            DateFormat originalFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("h:mm a");
            Date date = null;
            date = originalFormat.parse(list.get(position).getTimeStamp());
            String time_stamp = targetFormat.format(date);
            ///////////////////////////timestamp  list.get(position).getTimeStamp()
            tv_time_stamp_right.setText(time_stamp);
            tv_time_stamp_left.setText(time_stamp);
        } catch (ParseException e) {
            e.printStackTrace();

            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("h:mm a");
            Date date = null;
            try {
                date = originalFormat.parse(list.get(position).getTimeStamp());
            } catch (ParseException e1) {
                e1.printStackTrace();
                originalFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm:ss a", Locale.ENGLISH);

                try {
                    date = originalFormat.parse(list.get(position).getTimeStamp());
                } catch (ParseException e2) {
                    e2.printStackTrace();

                    originalFormat = new SimpleDateFormat("dd MMM yyyy h:mm:ss a", Locale.ENGLISH);
                    try {
                        date = originalFormat.parse(list.get(position).getTimeStamp());
                    } catch (ParseException e3) {
                        e3.printStackTrace();
                    }
                    try {
                        String time_stamp = targetFormat.format(date);
                        tv_time_stamp_right.setText(time_stamp);
                        tv_time_stamp_left.setText(time_stamp);
                    } catch (Exception e11) {
                        final String format = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);//get format from the device
                        if (TextUtils.isEmpty(format)) {
                            originalFormat = android.text.format.DateFormat.getMediumDateFormat(context);
                        } else {
                            originalFormat = new SimpleDateFormat(format);
                        }
                        try {
                            date = originalFormat.parse(list.get(position).getTimeStamp());

                        String time_stamp = targetFormat.format(date);
                        tv_time_stamp_right.setText(time_stamp);
                        tv_time_stamp_left.setText(time_stamp);
                        } catch (ParseException e3) {
                            e3.printStackTrace();
                        }
                    }
                }

                try {
                    String time_stamp = targetFormat.format(date);
                    tv_time_stamp_right.setText(time_stamp);
                    tv_time_stamp_left.setText(time_stamp);
                } catch (Exception e11) {

                    /*Aug 11, 2017 13:01:09*/


                    originalFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);
                    try {
                        date = originalFormat.parse(list.get(position).getTimeStamp());
                    } catch (ParseException e3) {
                        e3.printStackTrace();
                    }
                    try {
                        String time_stamp = targetFormat.format(date);
                        tv_time_stamp_right.setText(time_stamp);
                        tv_time_stamp_left.setText(time_stamp);
                    } catch (Exception e12) {

                    }


                }
            }


            try {
                String time_stamp = targetFormat.format(date);
                tv_time_stamp_right.setText(time_stamp);
                tv_time_stamp_left.setText(time_stamp);
            } catch (Exception e11) {

            }


        } catch (NullPointerException e) {

        }

        tv_sender_left.setText(list.get(position).getName().replace("#", "@"));
        tv_message_left.setText(list.get(position).getMessage());

        tv_sender_right.setText(list.get(position).getName().replace("#", "@"));


        tv_message_right.setText(list.get(position).getMessage());
/*        try {
            if (list.get(position).getSender_profile_picture().startsWith("http://")) {
                Picasso.with(context).load(list.get(position).getSender_profile_picture()).
                        placeholder(R.drawable.usertwo).into(iv_chat_member_left);
            } else {
                Picasso.with(context).load("http://" + list.get(position).getSender_profile_picture()).
                        placeholder(R.drawable.usertwo).into(iv_chat_member_left);
            }
        } catch (Exception e) {

        }*/



        for(int i=0;i<groupmembers.size();i++)
        {
            if((groupmembers.get(i).getLoginName().replace("#", "@")).equals(list.get(position).getName().replace("#", "@")))
            {
                if (groupmembers.get(i).getPhotoUrl().startsWith("http://")) {
                    Picasso.with(context).load(groupmembers.get(i).getPhotoUrl()).
                            placeholder(R.drawable.usertwo).into(iv_chat_member_left);
                } else {
                    Picasso.with(context).load("http://" + groupmembers.get(i).getPhotoUrl()).
                            placeholder(R.drawable.usertwo).into(iv_chat_member_left);
                }
            }
        }


        try {


            if (sharedPreferenceManager.getKEY_PROFILEPICTURE().startsWith("http://")) {
                Picasso.with(context).load(sharedPreferenceManager.getKEY_PROFILEPICTURE()).
                        placeholder(R.drawable.usertwo).into(iv_chat_member);

            } else {
                Picasso.with(context).load("http://" + sharedPreferenceManager.getKEY_PROFILEPICTURE()).
                        placeholder(R.drawable.usertwo).into(iv_chat_member);
            }

        } catch (Exception e) {

        }


        if (list.get(position).isMyMessage() == 1) {

            iv_chat_member_left.setVisibility(View.GONE);
            messageLayout_left.setVisibility(View.GONE);
            iv_chat_member.setVisibility(View.VISIBLE);
            messageLayout_right.setVisibility(View.VISIBLE);

            inner_right.setBackgroundResource(R.drawable.balloon_outgoing_normal);


            if (list.get(position).isMultimedia() == 1) {

                Log.e("Position : ", position + list.get(position).isMultimedia() + "");

                File file;

                tv_message_right.setVisibility(View.GONE);
                iv_message_right.setVisibility(View.VISIBLE);


                    URL url = null;
                    try {

                        Log.e("URL : ", list.get(position).getFile_url());
                        url = new URL(list.get(position).getFile_url());


                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();

                        Log.e("Malformed url : ", e1.toString());
                    }
                    /*file = new File(url.getFile());*/

                    Log.e("FILE URL : ", list.get(position).getFile_url());
                    Log.e("FILE NAME : ", list.get(position).getFile_name());
                if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("jpg") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("jpeg") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("png") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("gif")) {
                    /*Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());*/
                    Picasso.with(context).load(list.get(position).getFile_url()).placeholder(R.drawable.image).into(iv_message_right);
                } else if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("amr") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("midi") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("aac") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mp3") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("3gp")) {
                    iv_message_right.setImageDrawable(context.getResources().getDrawable(R.drawable.audio));
                }

                /*else if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("vob") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mp4") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mkv") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mpeg")) {
                    iv_message_right.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
                }*/


            } else {
                Log.e("Position : ", position + list.get(position).isMultimedia() + "");
                tv_message_right.setVisibility(View.VISIBLE);
                iv_message_right.setVisibility(View.GONE);

            }
        } else {
            iv_chat_member_left.setVisibility(View.VISIBLE);
            messageLayout_left.setVisibility(View.VISIBLE);
            iv_chat_member.setVisibility(View.GONE);
            messageLayout_right.setVisibility(View.GONE);
            inner_left.setBackgroundResource(R.drawable.balloon_incoming_normal);


            if (list.get(position).isMultimedia() == 1) {

                File file;


                    URL url = null;
                    try {

                        Log.e("URL : ", list.get(position).getFile_url());
                        url = new URL(list.get(position).getFile_url());


                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();

                        Log.e("Malformed url : ", e1.toString());
                    } catch (Exception e)
                    {
                    }
                    /*file = new File(url.getFile());*/


                try{
                    Log.e("FILE URL : ", list.get(position).getFile_url());
                }catch (Exception e)
                {

                }




                tv_message_left.setVisibility(View.GONE);
                iv_message_left.setVisibility(View.VISIBLE);


                if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("jpg") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("jpeg") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("png") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("gif")) {
                    /*Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                    iv_message_left.setImageBitmap(bitmap);*/
                    Picasso.with(context).load(list.get(position).getFile_url()).placeholder(R.drawable.image).into(iv_message_left);
                } else if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("amr") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("midi") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("aac") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mp3") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("3gp")) {
                    iv_message_left.setImageDrawable(context.getResources().getDrawable(R.drawable.audio));

                } else if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("vob") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mp4") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mkv") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mpeg")) {
                    iv_message_left.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
                }


            } else {

                tv_message_left.setVisibility(View.VISIBLE);
                iv_message_left.setVisibility(View.GONE);

            }


        }

        iv_message_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file;
                URL url = null;
                try {

                    Log.e("URL : ", list.get(position).getFile_url());
                    url = new URL(list.get(position).getFile_url());


                } catch (MalformedURLException e1) {
                    e1.printStackTrace();

                    Log.e("Malformed url : ", e1.toString());
                }
                /*file = new File(url.getFile());*/

                Log.e("FILE URL : ", list.get(position).getFile_url());

                if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("jpg") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("jpeg") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("png") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("gif")) {
                    if (isGroupChat) {


                        GroupChatActivity.fullscreen.setVisibility(View.VISIBLE);
                        /*GroupChatActivity.photo_view.setImageBitmap(BitmapFactory.decodeFile());*/

                        Picasso.with(context).load(list.get(position).getFile_url()).placeholder(R.drawable.image).into(GroupChatActivity.photo_view);


                    } else {
                        IndividualChatActivity.fullscreen.setVisibility(View.VISIBLE);

                        Picasso.with(context).load(list.get(position).getFile_url()).placeholder(R.drawable.image).into(GroupChatActivity.photo_view);
                    }
                } else if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("amr") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("midi") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("aac") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mp3") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("3gp")) {

                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        /*intent.setDataAndType(Uri.fromFile(file), "audio*//*");*/
                        intent.setDataAndType(Uri.parse(list.get(position).getFile_url()), "audio/*");
                        context.startActivity(intent);
                    } catch (Exception e) {

                    }


                } else if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("vob") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mp4") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mkv") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mpeg")) {
                }


            }
        });


        iv_message_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                File file;

                URL url = null;
                try {

                    Log.e("URL : ", list.get(position).getFile_url());
                    url = new URL(list.get(position).getFile_url());


                } catch (MalformedURLException e1) {
                    e1.printStackTrace();

                    Log.e("Malformed url : ", e1.toString());
                }
                /*file = new File(url.getFile());*/

                Log.e("FILE URL : ", list.get(position).getFile_url());

                if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("jpg") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("jpeg") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("png") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("gif")) {


                    if (isGroupChat) {
                        GroupChatActivity.fullscreen.setVisibility(View.VISIBLE);
                        /*GroupChatActivity.photo_view.setImageBitmap(BitmapFactory.decodeFile(list.get(position).getFile().getPath()));*/
                        Picasso.with(context).load(list.get(position).getFile_url()).placeholder(R.drawable.image).into(GroupChatActivity.photo_view);

                    } else {
                        IndividualChatActivity.fullscreen.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(list.get(position).getFile_url()).placeholder(R.drawable.image).into(IndividualChatActivity.photo_view);

                    }

                } else if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("amr") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("midi") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("aac") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mp3") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("3gp")) {

                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        /*intent.setDataAndType(Uri.fromFile(file), "audio*//*");*/
                        intent.setDataAndType(Uri.parse(list.get(position).getFile_url()), "audio/*");
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
                /*else if (FilenameUtils.getExtension(list.get(position).getFile_name()).equals("vob") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mp4") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mkv") || FilenameUtils.getExtension(list.get(position).getFile_name()).equals("mpeg")) {
                }*/


            }
        });


        return v;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            holder = new HeaderViewHolder();
            convertView = vi.inflate(R.layout.chat_time_stamp, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.tv_chat_timestamp);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        DateFormat dateFormat;

        final String format = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);//get format from the device
        if (TextUtils.isEmpty(format)) {
            dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
        } else {
            dateFormat = new SimpleDateFormat(format);
        }

        String headerText = null;

        SimpleDateFormat dt1 = new SimpleDateFormat("dd MMMM yyyy");
        try {
            headerText = "" + dt1.format(dateFormat.parse(list.get(position).getTimeStamp())).toUpperCase();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.text.setText(headerText);
/*

        //set header text as first char in name
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy");

        String headerText = null;
        try {
            SimpleDateFormat dt1 = new SimpleDateFormat("dd MMMM yyyy");
            headerText = "" + dt1.format(df.parse(list.get(position).getTimeStamp())).toUpperCase();
            holder.text.setText(headerText);
        } catch (ParseException e) {
            e.printStackTrace();
            df = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
            headerText = null;
            SimpleDateFormat dt1 = new SimpleDateFormat("dd MMMM yyyy");
            try {
                headerText = "" + dt1.format(df.parse(list.get(position).getTimeStamp())).toUpperCase();
            } catch (ParseException e1) {
                e1.printStackTrace();

                df = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                headerText = null;
                dt1 = new SimpleDateFormat("dd MMMM yyyy");
                try {
                    headerText = "" + dt1.format(df.parse(list.get(position).getTimeStamp())).toUpperCase();
                } catch (ParseException e2) {
                    e2.printStackTrace();
//                    01-Aug-2017 4:46:33 pm
                    df = new SimpleDateFormat("dd-MMM-yyyy h:mm:ss a");
                    headerText = null;
                    dt1 = new SimpleDateFormat("dd MMMM yyyy");
                    try {
                        headerText = "" + dt1.format(df.parse(list.get(position).getTimeStamp())).toUpperCase();
                    } catch (ParseException e3) {
                        e3.printStackTrace();

//                    01-Aug-2017 4:46:33
                        df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
                        headerText = null;
                        dt1 = new SimpleDateFormat("dd MMMM yyyy");
                        try {
                            headerText = "" + dt1.format(df.parse(list.get(position).getTimeStamp())).toUpperCase();
                        } catch (ParseException e4) {
                            e3.printStackTrace();

                        }

                    }

                }

            }
            holder.text.setText(headerText);


        } catch (NullPointerException e) {

        } catch (IndexOutOfBoundsException e1) {

        }*/
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        Date startDate;

        long id = 0;



        final String format = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
        if (TextUtils.isEmpty(format)) {
            df = android.text.format.DateFormat.getMediumDateFormat(context);
        } else {
            df = new SimpleDateFormat(format);
        }

        try {
            startDate = df.parse(list.get(position).getTimeStamp());
            id = startDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                startDate = df.parse(list.get(position).getTimeStamp());
                id = startDate.getTime();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }


        } catch (NullPointerException e) {

        } catch (IndexOutOfBoundsException e1) {

        }

        Log.e("HeaderID", id + "");
        return id;
    }

    class HeaderViewHolder {
        TextView text;
    }
}
