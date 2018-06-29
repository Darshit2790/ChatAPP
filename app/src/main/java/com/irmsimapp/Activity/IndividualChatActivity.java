package com.irmsimapp.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.irmsimapp.Adapter.ChatAdapter;
import com.irmsimapp.ApiClient.ApiHandler;
import com.irmsimapp.Configuration.Config;
import com.irmsimapp.Database.ChatDbHelper;
import com.irmsimapp.Model.BadKeyWord.BadKeyWord;
import com.irmsimapp.Model.ChatModel;
import com.irmsimapp.Model.GroupUsers.GroupUser;
import com.irmsimapp.Model.SaveMessage.SaveMessage;
import com.irmsimapp.R;
import com.irmsimapp.Uitils.SharedPreferenceManager;
import com.irmsimapp.Uitils.SmackUtils;
import com.irmsimapp.Uitils.Utills;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;

import org.jivesoftware.smackx.filetransfer.FileTransferManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import uk.co.senab.photoview.PhotoView;

public class IndividualChatActivity extends AppCompatActivity {




    MediaRecorder recorder;
    File audiofile = null;


    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 100;
    private static final int SELECT_AUDIO = 101;
    private static final int ACTIVITY_RECORD_SOUND = 102;
    private static final String TAG = "IndividualChatActivity";
    ArrayList<GroupUser.Datum.UserList> groupmembers;
    private TextView tv_heading;
    private ImageView iv_back, iv_right_icon,gallery,camera,audio;
    boolean hidden=true;
    LinearLayout mRevealView;
    ProgressDialog progressDialog;
    EditText edt_message;
    TextView send;
    ChatDbHelper db;
    CountDown timer;
    Boolean  isSpeakButtonLongPressed=false;


    File incommingFile;


    public static FrameLayout fullscreen;
    public static PhotoView photo_view;
    public static ImageView iv_close;
    ChatAdapter chatAdapter;
    ArrayList<ChatModel> chatArrayList;
    se.emilsjolander.stickylistheaders.StickyListHeadersListView list_chat;
    String currentDateTimeString;
    SharedPreferenceManager sharedPreferenceManager;
    ImageView iv_attachment;
    String loginName,userName,userType;
    LinearLayout layout_gallery,layout_camera,layout_audio;
    BadKeyWord badKeyWords=null;

    boolean isRecording=false;


    @Override
    protected void onPause() {
        super.onPause();


        fullscreen.setVisibility(View.GONE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();




        sharedPreferenceManager = new SharedPreferenceManager(IndividualChatActivity.this);
        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.GONE);
        tv_heading = (TextView) findViewById(R.id.tv_heading);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_right_icon = (ImageView) findViewById(R.id.iv_right_icon);
        iv_attachment = (ImageView) findViewById(R.id.iv_attachment);


        fullscreen=(FrameLayout) findViewById(R.id.fullscreen);
        photo_view=(PhotoView) findViewById(R.id.photo_view);
        iv_close=(ImageView) findViewById(R.id.iv_close);


        gallery = (ImageView) findViewById(R.id.gallery);
        layout_gallery = (LinearLayout) findViewById(R.id.layout_gallery);
        camera = (ImageView) findViewById(R.id.camera);
        layout_camera = (LinearLayout) findViewById(R.id.layout_camera);

        audio = (ImageView) findViewById(R.id.audio);
        layout_audio = (LinearLayout) findViewById(R.id.layout_audio);


        Intent intent = getIntent();
        if (intent.hasExtra("full_name")) {
            tv_heading.setText(intent.getStringExtra("full_name"));
        }else if(intent.hasExtra("name")){
            tv_heading.setText(intent.getStringExtra("name"));
        }

        if (intent.hasExtra("LoginName")) {

            loginName = intent.getStringExtra("LoginName");
        }


        if (intent.hasExtra("name")) {

            userName = intent.getStringExtra("name");
        }
        if (intent.hasExtra("user_type")) {

            userType = intent.getStringExtra("user_type");
        }
        groupmembers=(ArrayList<GroupUser.Datum.UserList>) intent.getSerializableExtra("groupmembers");

        iv_back.setVisibility(View.VISIBLE);
        iv_right_icon.setVisibility(View.VISIBLE);
        iv_right_icon.setImageResource(R.drawable.user_grey);

        iv_right_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndividualChatActivity.this,UserProfileActivity.class);
                intent.putExtra("isFromChat",true);
                intent.putExtra("userName",userName);
                intent.putExtra("userType",userType);
                IndividualChatActivity.this.startActivity(intent);
            }
        });

        chatArrayList = new ArrayList<>();
        /*name=intent.getStringExtra("name");*/


        layout_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openImageChooser();
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullscreen.setVisibility(View.GONE);

            }
        });


        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullscreen.setVisibility(View.GONE);

            }
        });
        photo_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullscreen.setVisibility(View.GONE);

            }
        });


       /* layout_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
*//*                openAudioChooser();*//*



            }
        });*/

      /*  audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                *//*choose existing Audio Tracks*//*
                *//*openAudioChooser();*//*
            }
        });*/

/*
        audio.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                isSpeakButtonLongPressed = true;
                return true;
            }
        });*/


   /*     layout_audio.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                isSpeakButtonLongPressed = true;
                return true;
            }
        });

*/
        /*audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent, ACTIVITY_RECORD_SOUND);
            }
        });


        layout_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent, ACTIVITY_RECORD_SOUND);
            }
        });*/



        layout_audio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {



                /*RECORDING FROM APP ITSELF*/
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                        startrecord();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                        || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {

                    if(isRecording)
                    {
                        stoprecord();
                    }

                }

                view.onTouchEvent(motionEvent);
                return true;
            }
        });




   audio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                        startrecord();

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                        || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {

                    if(isRecording)
                    {
                        stoprecord();
                    }

                }

                view.onTouchEvent(motionEvent);
                return true;
            }
        });




        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRevealView.setVisibility(View.GONE);
                hidden = true;



                try{

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }catch (Exception e)
                {

                }
            }
        });
        layout_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRevealView.setVisibility(View.GONE);
                hidden = true;

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        list_chat = (se.emilsjolander.stickylistheaders.StickyListHeadersListView) findViewById(R.id.lv_individual_chat);
        list_chat.setOnItemClickListener(null);

        getBadkeywords();




        // Create the file transfer manager - incomming file transfer

/*

try
{
    final FileTransferManager manager = FileTransferManager.getInstanceFor(Config.conn1);
// Create the listener

        manager.addFileTransferListener(new FileTransferListener() {
            public void fileTransferRequest(FileTransferRequest request) {
                // Check to see if the request should be accepted
                // Accept it
                IncomingFileTransfer transfer = request.accept();

                try {
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        //handle case of no SDCARD present
                    } else {
                        String dir = Environment.getExternalStorageDirectory()+File.separator+"IMCHATAPP";
                        //create folder
                        File folder = new File(dir); //folder name
                        folder.mkdirs();

                        //create file
                        File incommingFile = new File(dir, request.getFileName());

                        ////
                        transfer.recieveFile(incommingFile);

                        ////
                        Log.e("Incomming File : ",incommingFile.toString());
                        Log.e("Incomming File : ",incommingFile.getTotalSpace()+"");


                        ChatModel chatModel = new ChatModel();
                        chatModel.setName(request.getRequestor().toString().replace("@","#").toLowerCase());
                        chatModel.setMessage("");
                        chatModel.setMultimedia(true);
                        chatModel.setFile(incommingFile);
                        if (SmackUtils.parseResource(request.getRequestor().toString().replace("@","#").toLowerCase()).equals(sharedPreferenceManager.getKEY_LOGINNAME().toLowerCase())) {
                            chatModel.setMyMessage(1);
                        } else {
                            chatModel.setMyMessage(0);
                        }
                        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        chatModel.setTimeStamp(currentDateTimeString);
                        chatArrayList.add(chatModel);

                        try {
                            chatAdapter.notifyDataSetChanged();
                            list_chat.setSelection(chatArrayList.size()-1);

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String messageTimeStamp = dateFormat.format(calendar.getTime());
                            String MsgType="";


                            if(FilenameUtils.getExtension(transfer.getFileName()).equals("jpg")||FilenameUtils.getExtension(transfer.getFileName()).equals("jpeg")||FilenameUtils.getExtension(transfer.getFileName()).equals("png")||FilenameUtils.getExtension(transfer.getFileName()).equals("gif"))
                            {
                                MsgType="image";
                            }else if(FilenameUtils.getExtension(transfer.getFileName()).equals("amr")||FilenameUtils.getExtension(transfer.getFileName()).equals("midi")||FilenameUtils.getExtension(transfer.getFileName()).equals("aac")||FilenameUtils.getExtension(transfer.getFileName()).equals("mp3"))
                            {
                                MsgType="voice";
                            }else if(FilenameUtils.getExtension(transfer.getFileName()).equals("vob")||FilenameUtils.getExtension(transfer.getFileName()).equals("mp4")||FilenameUtils.getExtension(transfer.getFileName()).equals("mkv")||FilenameUtils.getExtension(transfer.getFileName()).equals("mpeg"))
                            {
                                MsgType="vedio";
                            }

                              *//*Save multimedia message on Server**************************************************//*
                            String messageJson= "{\n" +
                                    "    \"GroupFg\": \"False\",\n" +
                                    "    \"GroupNo\": \""+""    +"\",\n" +
                                    "    \"SendFrom\": \""+sharedPreferenceManager.getKEY_LOGINNAME()+"\",\n" +
                                    "    \"SendTo\": \""+transfer.getPeer().toString().replace("@","#").toLowerCase()+"\",\n" +
                                    "    \"SendDate\": \""+messageTimeStamp+"\",\n" +
                                    "    \"FileSize\": \""+transfer.getFileSize()+"\",\n" +
                                    "    \"FileType\": \""+ FilenameUtils.getExtension(transfer.getFileName())+"\",\n" +
                                    "    \"FileName\": \""+transfer.getFileName()+"\",\n" +
                                    "    \"MsgType\": \""+MsgType+"\",\n" +
                                    "    \"Content\": \""+""+"\"\n" +
                                    "}";
                            Map<String, String> group_save_message_param = new HashMap<>();
                            group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                            group_save_message_param.put("JsonObj", messageJson);
                            group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());
                            TypedFile typedFile = new TypedFile("multipart/form-data",incommingFile);
                            ApiHandler.getIMApiService().saveMessageonServerWithData(group_save_message_param, typedFile,new Callback<SaveMessage>() {
                                @Override
                                public void success(final SaveMessage saveMessage, Response response) {
                                    *//*Utills.dismissDialog();*//*
                                    Log.e("Save Message  :","Message with Multimedia Saved Successfully on server");

                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Utills.dismissDialog();
                                    Log.e("Failure", "Fail" + error.toString());

                                    Log.e("Save Message  :","Message with Multimedia Failed to save on server");

                                }
                            });
                            *//*************************************************************************//*

                        }catch (Exception e)
                        {
                        }

                    }

                } catch (SmackException e) {
                    e.printStackTrace();
                    Log.e("Incomming File : ",e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Incomming File : ",e.toString());
                }


          *//*      if(shouldAccept(request)) {
                    // Accept it
                    IncomingFileTransfer transfer = request.accept();
                    try {
                        transfer.recieveFile(new File("shakespeare_complete_works.txt"));
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Reject it
                    try {
                        request.reject();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }*//*
            }
        });

}catch (Exception e)
{

}*/



        //Receiving 1-1 Chat message


        if(Config.chatManager==null)
        {
            Config.chatManager = ChatManager.getInstanceFor(Config.conn1);
        }


        if(Config.chatManager!=null)
        {
            Config.chatManager.addChatListener(
                    new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally) {
                            chat.addMessageListener(new ChatMessageListener() {
                                @Override
                                public void processMessage(final Chat chat, final Message message) {
                                    System.out.println("Received message: "
                                            + (message != null ? message.getBody() : "NULL"));


                                    Log.e("Message Received : ",message.getBody());

                                    Log.e("Message ID : ",message.getStanzaId());


                                    if(badKeyWords!=null)
                                    {

                                        //check wether message is chat or multimedia
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                String filteredMessage = message.getBody();
                                                    for (int i = 0; i < badKeyWords.getData().size(); i++) {

                                                        String badkeyWord = badKeyWords.getData().get(i).getItemName();

                                                        filteredMessage = filteredMessage.replaceAll("(?i)"+badkeyWord, "*");
                                                    }

                                                   final  ChatModel chatModel = new ChatModel();
                                                    chatModel.setConfId(loginName.replace("@","#").toLowerCase());
                                                    chatModel.setName(chat.getParticipant().split("@")[0]);


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


                                                final DefaultExtensionElement extuserProfilePicture = (DefaultExtensionElement) message
                                                        .getExtension("urn:xmpp:extuserProfilePicture");
                                                if(extTypeOfChat!=null)
                                                {

                                                    String dir = Environment.getExternalStorageDirectory()+File.separator+"IMCHATAPP";
                                                    //create folder
                                                    File folder = new File(dir); //folder name
                                                    folder.mkdirs();

                                                    incommingFile = new File(dir, extFileName.getValue("FileName"));



                                                        /*NEEDS TO RUN IN BACKGROUND*/

                                                        Thread thread = new Thread() {
                                                            @Override
                                                            public void run() {
                                                                try {


                                                                    if(extTypeOfChat.getValue("typeofchat").equals("image"))
                                                                    {   chatModel.setFile_url(extPhotoChat.getValue("PhotoChat"));
                                                                        FileUtils.copyURLToFile(new URL(extPhotoChat.getValue("PhotoChat")), incommingFile);
                                                                        chatModel.setImage_height(extImageHeight.getValue("imageHeight"));
                                                                        chatModel.setImage_width(extImageWidth.getValue("imageWidth"));
                                                                        chatModel.setMessage("[Picture]");
                                                                    }else if(extTypeOfChat.getValue("typeofchat").equals("[Audio]"))
                                                                    {
                                                                        chatModel.setFile_url(extAudiourl.getValue("audiourl"));
                                                                        FileUtils.copyURLToFile(new URL(extAudiourl.getValue("audiourl")), incommingFile);
                                                                        chatModel.setImage_height("");
                                                                        chatModel.setImage_width("");
                                                                        chatModel.setMessage("[Audio]");
                                                                    }

                                                                    chatModel.setMultimedia(1);
                                                                    /*chatModel.setFile(incommingFile);*/
                                                                    chatModel.setFile_name(extFileName.getValue("FileName"));

                                                                    Log.e("1-setting file name",extFileName.getValue("FileName"));


                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        };

                                                        thread.start();

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
                                                }
                                                if (SmackUtils.parseResource(message.getFrom()).equals(sharedPreferenceManager.getKEY_LOGINNAME())) {
                                                        chatModel.setMyMessage(1);
                                                    } else {
                                                        chatModel.setMyMessage(0);
                                                    }
                                                    currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                                    chatModel.setTimeStamp(currentDateTimeString);

                                                    Calendar calendar = Calendar.getInstance();
                                                    calendar.setTimeInMillis(System.currentTimeMillis());
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    chatModel.setTimeinMillis(System.currentTimeMillis()+"");
                                                    String messageTimeStamp = dateFormat.format(calendar.getTime());



                                                chatModel.setTime(messageTimeStamp + "");
                                                    chatModel.setTime(messageTimeStamp + "");
                                                    chatModel.setMessage_id(message.getStanzaId()+"");


                                                try{
                                                    chatModel.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));
                                                }catch(Exception e)
                                                {

                                                }

                                                    /****Save message in Database SQLITE*****/


                                                Log.e("RECEIVER : ",loginName.replace("@","#").toLowerCase());
                                                Log.e("SENDER : ",chat.getParticipant().split("@")[0]);


                                                    if(db.insertSingleChat(chatModel))
                                                    {
                                                        Log.e("DATABASE : ","Record Inserted");
                                                    }else
                                                    {
                                                        Log.e("DATABASE : ","Record NOT Inserted");
                                                    }

                                                    /****************************************/
                                                /*chatArrayList.add(chatModel);
                                                chatAdapter.notifyDataSetChanged();
                                                list_chat.setSelection(chatArrayList.size()-1);*/


                                                if(extPhotoChat!=null)
                                                {

                                                    /*wait for file to get download before setting*/



                                                    final Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            //Do something after 5000ms
                                                            chatArrayList.add(chatModel);
                                                            list_chat.setAdapter(chatAdapter);
                                                            list_chat.setSelection(chatArrayList.size()-1);
                                                        }
                                                    }, 5000);


                                                }else
                                                {
                                                    chatArrayList.add(chatModel);
                                                    list_chat.setAdapter(chatAdapter);
                                                    list_chat.setSelection(chatArrayList.size()-1);
                                                }

                                                }



                                        });
                                    }else
                                    {
                                        runOnUiThread(new Runnable() {


                                            @Override
                                            public void run() {





                                                   final ChatModel chatModel = new ChatModel();
                                                    chatModel.setConfId(loginName.replace("@","#").toLowerCase());
                                                    chatModel.setName(chat.getParticipant().split("@")[0]);


                                                //Multimedia or Text Message


                                                final DefaultExtensionElement extPhotoChat = (DefaultExtensionElement) message
                                                        .getExtension("urn:xmpp:photochat");

                                                final DefaultExtensionElement extFileName = (DefaultExtensionElement) message
                                                        .getExtension("urn:xmpp:extfilename");

                                                final DefaultExtensionElement extuserProfilePicture = (DefaultExtensionElement) message
                                                        .getExtension("urn:xmpp:extuserProfilePicture");

                                                if(extPhotoChat!=null)
                                                {

                                                    String dir = Environment.getExternalStorageDirectory()+File.separator+"IMCHATAPP";
                                                    //create folder
                                                    File folder = new File(dir); //folder name
                                                    folder.mkdirs();

                                                    incommingFile = new File(dir, extFileName.getValue("FileName"));



                                                        /*NEEDS TO RUN IN BACKGROUND*/

                                                    Thread thread = new Thread() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                FileUtils.copyURLToFile(new URL(extPhotoChat.getValue("PhotoChat")), incommingFile);

                                                                chatModel.setMessage("["+extPhotoChat.getValue("typeofchat")+"]");
                                                                chatModel.setMultimedia(1);
                                                                /*chatModel.setFile(incommingFile);*/
                                                                chatModel.setFile_name(extFileName.getValue("FileName"));
                                                                chatModel.setImage_height("");
                                                                chatModel.setImage_width("");
                                                                chatModel.setFile_url(extPhotoChat.getValue("PhotoChat"));

                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    };

                                                    thread.start();



                                                }
                                                else
                                                {
                                                    chatModel.setMessage(message.getBody());
                                                    chatModel.setMultimedia(0);
                                                    /*chatModel.setFile(null);*/
                                                    chatModel.setFile_name("");
                                                    chatModel.setImage_height("");
                                                    chatModel.setImage_width("");
                                                    chatModel.setFile_url("");
                                                }




                                                if (SmackUtils.parseResource(message.getFrom()).equals(sharedPreferenceManager.getKEY_LOGINNAME())) {
                                                        chatModel.setMyMessage(1);
                                                    } else {
                                                        chatModel.setMyMessage(0);
                                                    }
                                                    currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                                    chatModel.setTimeStamp(currentDateTimeString);
                                                try{
                                                    chatModel.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));

                                                }catch (Exception e)
                                                {

                                                }


                                                Log.e("RECEIVER : ",loginName.replace("@","#").toLowerCase());
                                                Log.e("SENDER : ",chat.getParticipant().split("@")[0]);

                                                chatModel.setMessage_id(message.getStanzaId()+"");
                                                    /****Save message in Database SQLITE*****/
                                                    if(db.insertSingleChat(chatModel))
                                                    {
                                                        Log.e("DATABASE : ","Record Inserted");
                                                    }else
                                                    {
                                                        Log.e("DATABASE : ","Record NOT Inserted");
                                                    }

                                                    /****************************************/
                                             /*   chatArrayList.add(chatModel);
                                                chatAdapter.notifyDataSetChanged();
                                                list_chat.setSelection(chatArrayList.size()-1);*/

                                                    chatArrayList.add(chatModel);
                                                    list_chat.setAdapter(chatAdapter);
                                                    list_chat.setSelection(chatArrayList.size()-1);




                                            }

                                        });
                                    }

                                }
                            });
                            Log.w("app", chat.toString());
                        }
                    });
        }else
        {
            Log.e("Else Part ","Connection Null");
        }


        edt_message = (EditText) findViewById(R.id.edt_message);
        send = (TextView) findViewById(R.id.tv_send);

        chatAdapter = new ChatAdapter(IndividualChatActivity.this, chatArrayList,false,groupmembers);
        list_chat.setAdapter(chatAdapter);
        list_chat.setStackFromBottom(true);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


        iv_attachment.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {




                try{

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // only for Lolipop and newer versions


                    // finding X and Y co-ordinates
                    int cy = (mRevealView.getLeft() + mRevealView.getRight());
                    int cx = (mRevealView.getBottom());

                    // to find  radius when icon is tapped for showing layout
                    int startradius=0;
                    int endradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());


                    // performing circular reveal when icon will be tapped
                    Animator animator = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, startradius, endradius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(300);

                    //reverse animation
                    // to find radius when icon is tapped again for hiding layout


                    //  starting radius will be the radius or the extent to which circular reveal animation is to be shown
                    int reverse_startradius = Math.max(mRevealView.getWidth(),mRevealView.getHeight());
                    //endradius will be zero
                    int reverse_endradius=0;


                    // performing circular reveal for reverse animation
                    Animator animate = ViewAnimationUtils.createCircularReveal(mRevealView,cx,cy,reverse_startradius,reverse_endradius);


                    if(hidden) {

                        // to show the layout when icon is tapped
                        mRevealView.setVisibility(View.VISIBLE);
                        animator.start();
                        hidden = false;
                    }
                    else {

                        mRevealView.setVisibility(View.VISIBLE);

                        // to hide layout on animation end
                        animate.addListener(new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mRevealView.setVisibility(View.GONE);
                                hidden = true;

                            }
                        });
                        animate.start();

                    }
                }else
                {
                    if(hidden) {

                        mRevealView.setVisibility(View.VISIBLE);

                        hidden = false;
                    }
                    else {

                        mRevealView.setVisibility(View.GONE);
                        hidden = true;

                    }
                }

                }catch (Exception e)
                {
                    if(hidden) {

                        mRevealView.setVisibility(View.VISIBLE);

                        hidden = false;
                    }
                    else {

                        mRevealView.setVisibility(View.GONE);
                        hidden = true;

                    }
                }

            }
        });

        //Sending 1-1 Text  Chat message

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utills.isConnectingToInternet(IndividualChatActivity.this)) {
                if(edt_message.getText().toString().length()>0)
                {
                send.setEnabled(false);


                    if(badKeyWords!=null)
                    {
                        runOnUiThread(new Runnable() {


                            @Override
                            public void run() {

                                String filteredMessage = edt_message.getText().toString();


                                for (int i = 0; i < badKeyWords.getData().size(); i++) {

                                    String badkeyWord = badKeyWords.getData().get(i).getItemName();

                                    filteredMessage = filteredMessage.replaceAll("(?i)"+badkeyWord, "*");
                                }
                                Chat chat1 = ChatManager.getInstanceFor(Config.conn1).createChat(loginName.replace("@","#").toLowerCase() + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);

                                try {
                                    Message msg = new Message();
                                    ChatModel chat = new ChatModel();
                                    if(filteredMessage.length()>0)
                                    {
                                        msg.setBody(filteredMessage);
                                        chat.setMessage(filteredMessage);
                                    }else
                                    {
                                        msg.setBody(edt_message.getText().toString());
                                        chat.setMessage(edt_message.getText().toString());
                                    }


                                    Calendar c = Calendar.getInstance();
                                    System.out.println("Current time => "+c.getTime());

                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String chatTime = df.format(c.getTime());

                                    DefaultExtensionElement extchatTime = new DefaultExtensionElement(
                                            "chatTime", "urn:xmpp:extchatTime");
                                    extchatTime.setValue("chatTime",chatTime+"");
                                    msg.addExtension(extchatTime);


                                    DefaultExtensionElement extuserProfilePicture = new DefaultExtensionElement(
                                            "userProfilePicture", "urn:xmpp:extuserProfilePicture");
                                    extuserProfilePicture.setValue("userProfilePicture",sharedPreferenceManager.getKEY_PROFILEPICTURE()+"");


                                    msg.addExtension(extuserProfilePicture);
                                    //Send Text Message

                                    chat1.sendMessage(msg);
                                    chat.setConfId(loginName.replace("@","#").toLowerCase());
                                    chat.setName(sharedPreferenceManager.getKEY_LOGINNAME());
                                    chat.setMyMessage(1);
                                    currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                    chat.setTimeStamp(currentDateTimeString);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(System.currentTimeMillis());
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    chat.setTimeinMillis(System.currentTimeMillis()+"");
                                    String messageTimeStamp = dateFormat.format(calendar.getTime());
                                    chat.setTime(messageTimeStamp + "");
                                    chat.setFile_name("");
                                    chat.setImage_height("");
                                    chat.setImage_width("");
                                    chat.setFile_url("");
                                    chat.setMessage_id(msg.getStanzaId()+"");
                                    chatArrayList.add(chat);
                                    chatAdapter.notifyDataSetChanged();
                                    list_chat.setSelection(chatArrayList.size()-1);


                                    try
                                    {
                                        chat.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));
                                    }catch (Exception e)
                                    {

                                    }


                                    Log.e("RECEIVER : ",loginName.replace("@","#").toLowerCase());
                                    Log.e("SENDER : ",sharedPreferenceManager.getKEY_LOGINNAME());
                                    /****Save message in Database SQLITE*****/

                                    if(db.insertSingleChat(chat))
                                    {
                                        Log.e("DATABASE : ","Record Inserted");
                                    }else
                                    {
                                        Log.e("DATABASE : ","Record NOT Inserted");
                                    }

                                    /****************************************/


                                    /*Save message on Server**************************************************/


                                    String messageJson= "{\n" +
                                            "    \"GroupFg\": \"False\",\n" +
                                            "    \"GroupNo\": \""+""+"\",\n" +
                                            "    \"SendFrom\": \""+sharedPreferenceManager.getKEY_LOGINNAME()+"\",\n" +
                                            "    \"SendTo\": \""+loginName+"\",\n" +
                                            "    \"SendDate\": \""+currentDateTimeString+"\",\n" +
                                            "    \"FileSize\": \"\",\n" +
                                            "    \"FileType\": \"\",\n" +
                                            "    \"FileName\": \"\",\n" +
                                            "    \"MsgType\": \"chat\",\n" +
                                            "    \"Content\": \""+filteredMessage+"\"\n" +
                                            "}";




                                    Map<String, String> group_save_message_param = new HashMap<>();


                                    group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                                    group_save_message_param.put("JsonObj", messageJson);
                                    group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());

                                    ApiHandler.getIMApiService().saveMessageonServer(group_save_message_param, new Callback<SaveMessage>() {
                                        @Override
                                        public void success(final SaveMessage saveMessage, Response response) {
                                    /*Utills.dismissDialog();*/
                                            Log.e("Save Message  :","Message Saved Succesfully on server with badkeywords.");
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Utills.dismissDialog();
                                            Log.e("Failure", "Fail" + error.toString());
                                            Log.e("Save Message  :","Message Failed on server with badkeywords.");
                                        }
                                    });
                                    /*************************************************************************/

                                } catch (final SmackException.NotConnectedException e) {
                                    e.printStackTrace();

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            /*Toast.makeText(IndividualChatActivity.this, e.toString(), Toast.LENGTH_SHORT).show();*/
                                            Toast.makeText(IndividualChatActivity.this, "Your account has been logged on to another device.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                        edt_message.setText("");
                    }else
                    {

                        runOnUiThread(new Runnable() {


                            @Override
                            public void run() {

                                Chat chat1 = ChatManager.getInstanceFor(Config.conn1).createChat(loginName.replace("@","#").toLowerCase() + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);

                                try {


                                    Message msg = new Message();
                                    ChatModel chat = new ChatModel();

                                    msg.setBody(edt_message.getText().toString());
                                    chat.setMessage(edt_message.getText().toString());
                                    Calendar c = Calendar.getInstance();
                                    System.out.println("Current time => "+c.getTime());

                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String chatTime = df.format(c.getTime());

                                    DefaultExtensionElement extchatTime = new DefaultExtensionElement(
                                            "chatTime", "urn:xmpp:extchatTime");
                                    extchatTime.setValue("chatTime",chatTime+"");
                                    msg.addExtension(extchatTime);
                                    DefaultExtensionElement extuserProfilePicture = new DefaultExtensionElement(
                                            "userProfilePicture", "urn:xmpp:extuserProfilePicture");
                                    extuserProfilePicture.setValue("userProfilePicture",sharedPreferenceManager.getKEY_PROFILEPICTURE()+"");


                                    msg.addExtension(extuserProfilePicture);


                                    //Send Text Message
                                    chat1.sendMessage(msg);
                                    chat.setConfId(loginName.replace("@","#").toLowerCase());
                                    chat.setName(sharedPreferenceManager.getKEY_LOGINNAME());
                                    chat.setMyMessage(1);
                                    currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                    chat.setTimeStamp(currentDateTimeString);
                                    chatArrayList.add(chat);
                                    chatAdapter.notifyDataSetChanged();
                                    list_chat.setSelection(chatArrayList.size()-1);

                                } catch (final SmackException.NotConnectedException e) {
                                    e.printStackTrace();

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(IndividualChatActivity.this, "Your account has been logged on to another device.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });


                        edt_message.setText("");
                    }

                    send.setEnabled(true);
                }else {

                }

            }else
            {
                Utills.showAlertOkDialog(IndividualChatActivity.this,"Please Check Your Network !!!");
            }

            }
        });
        chatArrayList.clear();
             /*Fetch all chat messages from db*/
        //Fetch Latest Message From Individual Chat
        String receiverName=loginName.replace("@","#").toLowerCase(),sendername=sharedPreferenceManager.getKEY_LOGINNAME().toLowerCase();
        db = new ChatDbHelper(this);
        chatArrayList=db.getIndividualChatMessages(sendername,receiverName);


        Log.e("Fetching History : ","Sender : "+sendername+" Receiver : "+receiverName);


        /*get Files from db*/
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                chatAdapter = new ChatAdapter(IndividualChatActivity.this, chatArrayList,false,groupmembers);
                list_chat.setAdapter(chatAdapter);
                list_chat.setStackFromBottom(true);
            }
        }, 1000 * 0);



        CountDownTimer countDownTime = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                chatAdapter.notifyDataSetChanged();
            }
        };
        countDownTime.start();
        /////////////////////////////////////////////////////

    }



    /* Choose an image from Gallery */
    void openAudioChooser() {
        /*Intent intent = new Intent();
        intent.setType("audio*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);*/


        /*Samsung s7 edge*/
//        try{

        //          Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        //        intent.putExtra("CONTENT_TYPE", "*/*");
        //      intent.addCategory(Intent.CATEGORY_DEFAULT);
        //    startActivityForResult(intent, SELECT_PICTURE);
        //}catch (Exception e)
        //{

//        }


        mRevealView.setVisibility(View.GONE);
        hidden = true;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, SELECT_AUDIO);



/*        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file*//*");
        startActivityForResult(intent, SELECT_PICTURE);*/

        /*startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);*/
    }



    /* Choose an image from Gallery */
    void openImageChooser() {
        /*Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);*/


        /*Samsung s7 edge*/
//        try{

  //          Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
    //        intent.putExtra("CONTENT_TYPE", "*/*");
      //      intent.addCategory(Intent.CATEGORY_DEFAULT);
        //    startActivityForResult(intent, SELECT_PICTURE);
        //}catch (Exception e)
        //{

//        }


        mRevealView.setVisibility(View.GONE);
        hidden = true;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE);



/*        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file*//*");
        startActivityForResult(intent, SELECT_PICTURE);*/

        /*startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);*/
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {




        /*SAMSUNG GALAXY S7 EDGE*/
     /*   if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    *//*String path = getPathFromURI(selectedImageUri);*//*

                    // Set the image in ImageView
                    *//*imgView.setImageURI(selectedImageUri);*//*

                    // Create the file transfer manager
                    FileTransferManager manager = FileTransferManager.getInstanceFor(Config.conn1);
                    // Create the outgoing file transfer
                    OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(loginName.replace("@","#").toLowerCase() + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);
                    // Send the file
                    try {
                            File file = new File(selectedImageUri.getPath());
                        *//*byte[] bytesofFile;
                        try {
                            bytesofFile=org.apache.commons.io.FileUtils.readFileToByteArray(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("eeeeeeeeeeeeeeeeeeeeeeeeeeee",e.toString());
                            bytesofFile=null;
                        }
                        *//**//*File tempFile = File.createTempFile(file.getName(), file.getName().split(".")[1], null);*//**//*

                        File tempFile = File.createTempFile("sample", ".jpg", null);
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        fos.write(bytesofFile);*//*
                        transfer.sendFile(file, "ImChatAPP File Transfer");
                        Log.e("Single chat File Transfer","success");
                    }
                    catch (SmackException e) {
                        e.printStackTrace();
                        Log.e("Single chat File Transfer Exception ",e.toString());
                    }

                    catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Single chat File Transfer Exception ",e.toString());
                    }
                }
            }
        }*/




        /*OPTION 1 :- GALLERY*/


        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = IndividualChatActivity.this.getContentResolver().openInputStream(data.getData());

                // Create the file transfer manager
                FileTransferManager manager = FileTransferManager.getInstanceFor(Config.conn1);
                // Create the outgoing file transfer

                // Send the file
                try {
                    final  File file = File.createTempFile("IM_groupchat_"+System.currentTimeMillis(),".jpg");

                    FileUtils.copyInputStreamToFile(inputStream,file);
                /*
                    byte[] bytesofFile;
                    try {
                        bytesofFile=org.apache.commons.io.FileUtils.readFileToByteArray(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("eeeeeeeeeeeeeeeeeeeeeeeeeeee",e.toString());
                        bytesofFile=null;
                    }
                    File tempFile = File.createTempFile("sample", ".jpg", null);
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(bytesofFile);*/

                    /*OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(loginName.replace("@","#").toLowerCase() + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);*/

                    /*BELOW LINE OF CODE IS TO SEND FILE VIA FILETRANSFER -option -1*/


                    /*transfer.sendFile(file, "ImChatAPP File Transfer");*/  /*gallery*/

                    Log.e("File Transfer","success");

                    /*add to chat List*/
                    final  ChatModel chatModel = new ChatModel();
                    chatModel.setConfId(loginName.replace("@","#").toLowerCase());
                    chatModel.setName(sharedPreferenceManager.getKEY_LOGINNAME());

                    chatModel.setMultimedia(1);
                    chatModel.setMessage("[Picture]");
                    /*chatModel.setFile(file);*/
                    chatModel.setMyMessage(1);
                    chatModel.setFile_name(file.getName());
                    Log.e("2-setting file name",file.getName());



                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String messageTimeStamp = dateFormat.format(calendar.getTime());
                    String MsgType="";


                 /*   if(FilenameUtils.getExtension(transfer.getFileName()).equals("jpg")||FilenameUtils.getExtension(transfer.getFileName()).equals("jpeg")||FilenameUtils.getExtension(transfer.getFileName()).equals("png")||FilenameUtils.getExtension(transfer.getFileName()).equals("gif"))
                    {
                        MsgType="image";
                    }else if(FilenameUtils.getExtension(transfer.getFileName()).equals("amr")||FilenameUtils.getExtension(transfer.getFileName()).equals("midi")||FilenameUtils.getExtension(transfer.getFileName()).equals("aac")||FilenameUtils.getExtension(transfer.getFileName()).equals("mp3"))
                    {
                        MsgType="voice";
                    }else if(FilenameUtils.getExtension(transfer.getFileName()).equals("vob")||FilenameUtils.getExtension(transfer.getFileName()).equals("mp4")||FilenameUtils.getExtension(transfer.getFileName()).equals("mkv")||FilenameUtils.getExtension(transfer.getFileName()).equals("mpeg"))
                    {
                        MsgType="vedio";
                    }*/



                    if(FilenameUtils.getExtension(file.getName()).equals("jpg")||FilenameUtils.getExtension(file.getName()).equals("jpeg")||FilenameUtils.getExtension(file.getName()).equals("png")||FilenameUtils.getExtension(file.getName()).equals("gif"))
                    {
                        MsgType="image";
                    }else if(FilenameUtils.getExtension(file.getName()).equals("3gp")||FilenameUtils.getExtension(file.getName()).equals("amr")||FilenameUtils.getExtension(file.getName()).equals("midi")||FilenameUtils.getExtension(file.getName()).equals("aac")||FilenameUtils.getExtension(file.getName()).equals("mp3"))
                    {
                        MsgType="voice";
                    }else if(FilenameUtils.getExtension(file.getName()).equals("vob")||FilenameUtils.getExtension(file.getName()).equals("mp4")||FilenameUtils.getExtension(file.getName()).equals("mkv")||FilenameUtils.getExtension(file.getName()).equals("mpeg"))
                    {
                        MsgType="vedio";
                    }



                              /*Save multimedia message on Server**************************************************/
           /*         String messageJson= "{\n" +
                            "    \"GroupFg\": \"False\",\n" +
                            "    \"GroupNo\": \""+""    +"\",\n" +
                            "    \"SendFrom\": \""+sharedPreferenceManager.getKEY_LOGINNAME()+"\",\n" +
                            "    \"SendTo\": \""+transfer.getPeer().toString().replace("@","#").toLowerCase()+"\",\n" +
                            "    \"SendDate\": \""+messageTimeStamp+"\",\n" +
                            "    \"FileSize\": \""+transfer.getFileSize()+"\",\n" +
                            "    \"FileType\": \""+ FilenameUtils.getExtension(transfer.getFileName())+"\",\n" +
                            "    \"FileName\": \""+transfer.getFileName()+"\",\n" +
                            "    \"MsgType\": \""+MsgType+"\",\n" +
                            "    \"Content\": \""+""+"\"\n" +
                            "}";
*/

                    String messageJson= "{\n" +
                            "    \"GroupFg\": \"False\",\n" +
                            "    \"GroupNo\": \""+""    +"\",\n" +
                            "    \"SendFrom\": \""+sharedPreferenceManager.getKEY_LOGINNAME()+"\",\n" +
                            "    \"SendTo\": \""+loginName.replace("@","#").toLowerCase()+"\",\n" +
                            "    \"SendDate\": \""+messageTimeStamp+"\",\n" +
                            "    \"FileSize\": \""+Integer.parseInt(String.valueOf(file.length()/1024))+"\",\n" +
                            "    \"FileType\": \""+ FilenameUtils.getExtension(file.getName())+"\",\n" +
                            "    \"FileName\": \""+file.getName()+"\",\n" +
                            "    \"MsgType\": \""+MsgType+"\",\n" +
                            "    \"Content\": \""+""+"\"\n" +
                            "}";





                    Map<String, String> group_save_message_param = new HashMap<>();
                    group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                    group_save_message_param.put("JsonObj", messageJson);
                    group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());
                    TypedFile typedFile = new TypedFile("multipart/form-data", file);
                    ApiHandler.getIMApiService().saveMessageonServerWithData(group_save_message_param,typedFile ,new Callback<SaveMessage>() {
                        @Override
                        public void success(final SaveMessage saveMessage, Response response) {
                                    /*Utills.dismissDialog();*/
                            Log.e("Save Message  :","Message with Multimedia Saved Successfully on server");


                                        /*Below is line of code SENDING FILE THOUGH SIMPLE TEXT CHAT METHOD - method 2*/


                            /*ADD xml stanza*/
                            Chat chat1 = ChatManager.getInstanceFor(Config.conn1).createChat(loginName.replace("@","#").toLowerCase() + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);
                            try {
                                /*Send Image from gallery in message*/
                                Message msg = new Message();


                                DefaultExtensionElement extPhotoChat = new DefaultExtensionElement(
                                        "PhotoChat", "urn:xmpp:photochat");
                                String fileUrl = saveMessage.getData().get(0).getFileUrl();
                                extPhotoChat.setValue("PhotoChat", fileUrl);
                                msg.setBody("[Picture]");
                                DefaultExtensionElement extPhotoThumb = new DefaultExtensionElement(
                                        "PhotoThumb", "urn:xmpp:photothumb");
                                extPhotoThumb.setValue("PhotoThumb", fileUrl);
                                DefaultExtensionElement extFileName = new DefaultExtensionElement(
                                        "FileName", "urn:xmpp:extfilename");
                                extFileName.setValue("FileName",file.getName()+"");
                                DefaultExtensionElement extTypeOfChat = new DefaultExtensionElement(
                                        "typeofchat", "urn:xmpp:exttypeofchat");
                                extTypeOfChat.setValue("typeofchat", "image");



                            /*DIMENSIONS*/

                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                                int imageHeight = options.outHeight;
                                int imageWidth = options.outWidth;


                                DefaultExtensionElement extImageWidth = new DefaultExtensionElement(
                                        "imageWidth", "urn:xmpp:extimageWidth");
                                extImageWidth.setValue("imageWidth",imageWidth+"" );



                                DefaultExtensionElement extImageHeight = new DefaultExtensionElement(
                                        "imageHeight", "urn:xmpp:extimageHeight");
                                extImageHeight.setValue("imageHeight",imageHeight+"");
                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time => "+c.getTime());

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String chatTime = df.format(c.getTime());

                                DefaultExtensionElement extchatTime = new DefaultExtensionElement(
                                        "chatTime", "urn:xmpp:extchatTime");
                                extchatTime.setValue("chatTime",chatTime+"");

                                DefaultExtensionElement extuserProfilePicture = new DefaultExtensionElement(
                                        "userProfilePicture", "urn:xmpp:extuserProfilePicture");
                                extuserProfilePicture.setValue("userProfilePicture",sharedPreferenceManager.getKEY_PROFILEPICTURE()+"");


                                msg.addExtension(extuserProfilePicture);
                                msg.addExtension(extchatTime);
                                msg.addExtension(extPhotoChat);
                                msg.addExtension(extPhotoThumb);
                                msg.addExtension(extTypeOfChat);
                                msg.addExtension(extFileName);
                                msg.addExtension(extImageWidth);
                                msg.addExtension(extImageHeight);
                                chat1.sendMessage(msg);
                                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                chatModel.setTimeStamp(currentDateTimeString);
                                chatModel.setMessage("[Picture]");
                                chatModel.setFile_url(extPhotoChat.getValue("PhotoChat"));
                                chatModel.setImage_height(imageHeight+"");
                                chatModel.setImage_width(imageWidth+"");
                                chatModel.setTimeinMillis(System.currentTimeMillis()+"");
                                chatArrayList.add(chatModel);

                                try {
                                    chatAdapter.notifyDataSetChanged();
                                    list_chat.setSelection(chatArrayList.size()-1);
                                }catch (Exception e)
                                {
                                }

                                try
                                {
                                    chatModel.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));
                                }catch (Exception e)
                                {

                                }



                                Log.e("RECEIVER : ",loginName.replace("@","#").toLowerCase());
                                Log.e("SENDER : ",sharedPreferenceManager.getKEY_LOGINNAME());

                                chatModel.setMessage_id(msg.getStanzaId()+"");
                                if(db.insertSingleChat(chatModel))
                                {
                                    Log.e("DATABASE : ","Record Inserted");
                                }else
                                {
                                    Log.e("DATABASE : ","Record NOT Inserted");
                                }


                            } catch (SmackException.NotConnectedException e) {
                                Toast.makeText(IndividualChatActivity.this, "Your account has been logged on to another device.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Utills.dismissDialog();
                            Log.e("Failure", "Fail" + error.toString());

                            Toast.makeText(IndividualChatActivity.this,"Error sending file,please try again later !",Toast.LENGTH_SHORT).show();
                            Log.e("Save Message  :","Message with Multimedia Failed to save on server");

                        }
                    });





                    /*************************************************************************/




                    //////////
                }
               /* catch (SmackException e) {
                    e.printStackTrace();
                    Log.e("File Transfer",e.toString());
                }*/

                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("File Transfer",e.toString());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }


        //OPTION 2 : getting audio from the recorder
        else if (requestCode == ACTIVITY_RECORD_SOUND && resultCode == Activity.RESULT_OK) {

            if (data == null) {
                //Display an error
                return;
            }


            File dir = Environment.getExternalStorageDirectory();
            try {
                audiofile = File.createTempFile("IMCHAT_RECORDING_", ".3gp", dir);

                InputStream inputStream = IndividualChatActivity.this.getContentResolver().openInputStream(data.getData());
                FileUtils.copyInputStreamToFile(inputStream,audiofile);
            } catch (IOException e) {
                Log.e(TAG, "external storage access error");
                return;
            }
            addRecordingToMediaLibraryandsendchat();
        }

        //OPTION 2 : AUDIO choosing from sdcard

/*
        if (requestCode == SELECT_AUDIO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = IndividualChatActivity.this.getContentResolver().openInputStream(data.getData());

                // Create the file transfer manager
                FileTransferManager manager = FileTransferManager.getInstanceFor(Config.conn1);
                // Create the outgoing file transfer
                OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(loginName.replace("@","#").toLowerCase() + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);
                // Send the file
                try {
                    File file = File.createTempFile("sample",".mp3");

                    FileUtils.copyInputStreamToFile(inputStream,file);
                *//*
                    byte[] bytesofFile;
                    try {
                        bytesofFile=org.apache.commons.io.FileUtils.readFileToByteArray(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("eeeeeeeeeeeeeeeeeeeeeeeeeeee",e.toString());
                        bytesofFile=null;
                    }
                    File tempFile = File.createTempFile("sample", ".jpg", null);
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(bytesofFile);*//*
                    transfer.sendFile(file, "ImChatAPP File Transfer");
                    Log.e("File Transfer","success");

                    *//*add to chat List*//*
                    ChatModel chatModel = new ChatModel();
                    chatModel.setConfId(loginName.replace("@","#").toLowerCase());
                    chatModel.setName(sharedPreferenceManager.getKEY_LOGINNAME());
                    chatModel.setMessage("");
                    chatModel.setMultimedia(true);
                    chatModel.setFile(file);
                    chatModel.setMyMessage(1);

                    currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    chatModel.setTimeStamp(currentDateTimeString);
                    chatArrayList.add(chatModel);

                    try {
                        chatAdapter.notifyDataSetChanged();
                        list_chat.setSelection(chatArrayList.size()-1);
                    }catch (Exception e)
                    {
                    }


                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String messageTimeStamp = dateFormat.format(calendar.getTime());
                    String MsgType="";


                    if(FilenameUtils.getExtension(transfer.getFileName()).equals("jpg")||FilenameUtils.getExtension(transfer.getFileName()).equals("jpeg")||FilenameUtils.getExtension(transfer.getFileName()).equals("png")||FilenameUtils.getExtension(transfer.getFileName()).equals("gif"))
                    {
                        MsgType="image";
                    }else if(FilenameUtils.getExtension(transfer.getFileName()).equals("amr")||FilenameUtils.getExtension(transfer.getFileName()).equals("midi")||FilenameUtils.getExtension(transfer.getFileName()).equals("aac")||FilenameUtils.getExtension(transfer.getFileName()).equals("mp3"))
                    {
                        MsgType="voice";
                    }else if(FilenameUtils.getExtension(transfer.getFileName()).equals("vob")||FilenameUtils.getExtension(transfer.getFileName()).equals("mp4")||FilenameUtils.getExtension(transfer.getFileName()).equals("mkv")||FilenameUtils.getExtension(transfer.getFileName()).equals("mpeg"))
                    {
                        MsgType="vedio";
                    }

                              *//*Save multimedia message on Server**************************************************//*
                    String messageJson= "{\n" +
                            "    \"GroupFg\": \"False\",\n" +
                            "    \"GroupNo\": \""+""    +"\",\n" +
                            "    \"SendFrom\": \""+sharedPreferenceManager.getKEY_LOGINNAME()+"\",\n" +
                            "    \"SendTo\": \""+transfer.getPeer().toString().replace("@","#").toLowerCase()+"\",\n" +
                            "    \"SendDate\": \""+messageTimeStamp+"\",\n" +
                            "    \"FileSize\": \""+transfer.getFileSize()+"\",\n" +
                            "    \"FileType\": \""+ FilenameUtils.getExtension(transfer.getFileName())+"\",\n" +
                            "    \"FileName\": \""+transfer.getFileName()+"\",\n" +
                            "    \"MsgType\": \""+MsgType+"\",\n" +
                            "    \"Content\": \""+""+"\"\n" +
                            "}";




                    Map<String, String> group_save_message_param = new HashMap<>();
                    group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                    group_save_message_param.put("JsonObj", messageJson);
                    group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());
                    TypedFile typedFile = new TypedFile("multipart/form-data", file);
                    ApiHandler.getIMApiService().saveMessageonServerWithData(group_save_message_param,typedFile ,new Callback<SaveMessage>() {
                        @Override
                        public void success(final SaveMessage saveMessage, Response response) {
                                    *//*Utills.dismissDialog();*//*
                            Log.e("Save Message  :","Message with Multimedia Saved Successfully on server");

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Utills.dismissDialog();
                            Log.e("Failure", "Fail" + error.toString());

                            Log.e("Save Message  :","Message with Multimedia Failed to save on server");

                        }
                    });





                    *//*************************************************************************//*




                    //////////
                }
                catch (SmackException e) {
                    e.printStackTrace();
                    Log.e("File Transfer",e.toString());
                }

                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("File Transfer",e.toString());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }*/

        //OPTION 3 :- CAMERA

        else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            // Create the file transfer manager
         /*   FileTransferManager manager = FileTransferManager.getInstanceFor(Config.conn1);
            // Create the outgoing file transfer
            OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(loginName.replace("@","#").toLowerCase() + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);*/
                        //create a file to write bitmap data
            final File file = new File(IndividualChatActivity.this.getCacheDir(),"IMCHATAPP_"+ System.currentTimeMillis()+".png");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

//Convert bitmap to byte array
            Bitmap bitmap = photo;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(bitmapdata);

            fos.flush();
            fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            // Send the file
            try {


                /*option 1 sendfile audio*/
                /*transfer.sendFile(f, "ImChatAPP File Transfer");*/ /*audio*/


                    /*add to chat List*/


                final ChatModel chatModel = new ChatModel();
                chatModel.setConfId(loginName.replace("@","#").toLowerCase());
                chatModel.setName(sharedPreferenceManager.getKEY_LOGINNAME());

                chatModel.setMultimedia(1);
                /*chatModel.setFile(file);*/

                chatModel.setMyMessage(1);

                ///////////////


                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String messageTimeStamp = dateFormat.format(calendar.getTime());
                String MsgType="";


                 /*   if(FilenameUtils.getExtension(transfer.getFileName()).equals("jpg")||FilenameUtils.getExtension(transfer.getFileName()).equals("jpeg")||FilenameUtils.getExtension(transfer.getFileName()).equals("png")||FilenameUtils.getExtension(transfer.getFileName()).equals("gif"))
                    {
                        MsgType="image";
                    }else if(FilenameUtils.getExtension(transfer.getFileName()).equals("amr")||FilenameUtils.getExtension(transfer.getFileName()).equals("midi")||FilenameUtils.getExtension(transfer.getFileName()).equals("aac")||FilenameUtils.getExtension(transfer.getFileName()).equals("mp3"))
                    {
                        MsgType="voice";
                    }else if(FilenameUtils.getExtension(transfer.getFileName()).equals("vob")||FilenameUtils.getExtension(transfer.getFileName()).equals("mp4")||FilenameUtils.getExtension(transfer.getFileName()).equals("mkv")||FilenameUtils.getExtension(transfer.getFileName()).equals("mpeg"))
                    {
                        MsgType="vedio";
                    }*/



                if(FilenameUtils.getExtension(file.getName()).equals("jpg")||FilenameUtils.getExtension(file.getName()).equals("jpeg")||FilenameUtils.getExtension(file.getName()).equals("png")||FilenameUtils.getExtension(file.getName()).equals("gif"))
                {
                    MsgType="image";
                }else if(FilenameUtils.getExtension(file.getName()).equals("3gp")||FilenameUtils.getExtension(file.getName()).equals("amr")||FilenameUtils.getExtension(file.getName()).equals("midi")||FilenameUtils.getExtension(file.getName()).equals("aac")||FilenameUtils.getExtension(file.getName()).equals("mp3"))
                {
                    MsgType="voice";
                }else if(FilenameUtils.getExtension(file.getName()).equals("vob")||FilenameUtils.getExtension(file.getName()).equals("mp4")||FilenameUtils.getExtension(file.getName()).equals("mkv")||FilenameUtils.getExtension(file.getName()).equals("mpeg"))
                {
                    MsgType="vedio";
                }



                              /*Save multimedia message on Server**************************************************/
           /*         String messageJson= "{\n" +
                            "    \"GroupFg\": \"False\",\n" +
                            "    \"GroupNo\": \""+""    +"\",\n" +
                            "    \"SendFrom\": \""+sharedPreferenceManager.getKEY_LOGINNAME()+"\",\n" +
                            "    \"SendTo\": \""+transfer.getPeer().toString().replace("@","#").toLowerCase()+"\",\n" +
                            "    \"SendDate\": \""+messageTimeStamp+"\",\n" +
                            "    \"FileSize\": \""+transfer.getFileSize()+"\",\n" +
                            "    \"FileType\": \""+ FilenameUtils.getExtension(transfer.getFileName())+"\",\n" +
                            "    \"FileName\": \""+transfer.getFileName()+"\",\n" +
                            "    \"MsgType\": \""+MsgType+"\",\n" +
                            "    \"Content\": \""+""+"\"\n" +
                            "}";
*/

                String messageJson= "{\n" +
                        "    \"GroupFg\": \"False\",\n" +
                        "    \"GroupNo\": \""+""    +"\",\n" +
                        "    \"SendFrom\": \""+sharedPreferenceManager.getKEY_LOGINNAME()+"\",\n" +
                        "    \"SendTo\": \""+loginName.replace("@","#").toLowerCase()+"\",\n" +
                        "    \"SendDate\": \""+messageTimeStamp+"\",\n" +
                        "    \"FileSize\": \""+Integer.parseInt(String.valueOf(file.length()/1024))+"\",\n" +
                        "    \"FileType\": \""+ FilenameUtils.getExtension(file.getName())+"\",\n" +
                        "    \"FileName\": \""+file.getName()+"\",\n" +
                        "    \"MsgType\": \""+MsgType+"\",\n" +
                        "    \"Content\": \""+""+"\"\n" +
                        "}";

                Map<String, String> group_save_message_param = new HashMap<>();
                group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                group_save_message_param.put("JsonObj", messageJson);
                group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());
                TypedFile typedFile = new TypedFile("multipart/form-data", file);
                ApiHandler.getIMApiService().saveMessageonServerWithData(group_save_message_param,typedFile ,new Callback<SaveMessage>() {
                    @Override
                    public void success(final SaveMessage saveMessage, Response response) {
                                    /*Utills.dismissDialog();*/
                        Log.e("Save Message  :","Message with Multimedia Saved Successfully on server");


                                        /*Below is line of code SENDING FILE THOUGH SIMPLE TEXT CHAT METHOD - method 2*/


                            /*ADD xml stanza*/
                        Chat chat1 = ChatManager.getInstanceFor(Config.conn1).createChat(loginName.replace("@","#").toLowerCase() + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);
                        try {
                                /*Send Image from gallery in message*/
                            Message msg = new Message();


                            DefaultExtensionElement extPhotoChat = new DefaultExtensionElement(
                                    "PhotoChat", "urn:xmpp:photochat");
                            String fileUrl = saveMessage.getData().get(0).getFileUrl();
                            extPhotoChat.setValue("PhotoChat", fileUrl);
                            msg.setBody("[Picture]");
                            DefaultExtensionElement extPhotoThumb = new DefaultExtensionElement(
                                    "PhotoThumb", "urn:xmpp:photothumb");
                            extPhotoThumb.setValue("PhotoThumb", fileUrl);
                            DefaultExtensionElement extFileName = new DefaultExtensionElement(
                                    "FileName", "urn:xmpp:extfilename");
                            extFileName.setValue("FileName",file.getName()+"");
                            DefaultExtensionElement extTypeOfChat = new DefaultExtensionElement(
                                    "typeofchat", "urn:xmpp:exttypeofchat");
                            extTypeOfChat.setValue("typeofchat", "image");




                            /*DIMENSIONS*/

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                            int imageHeight = options.outHeight;
                            int imageWidth = options.outWidth;


                            DefaultExtensionElement extImageWidth = new DefaultExtensionElement(
                                    "imageWidth", "urn:xmpp:extimageWidth");
                            extImageWidth.setValue("imageWidth",imageWidth+"" );
                            DefaultExtensionElement extImageHeight = new DefaultExtensionElement(
                                    "imageHeight", "urn:xmpp:extimageHeight");
                            extImageHeight.setValue("imageHeight",imageHeight+"");
                            Calendar c = Calendar.getInstance();
                            System.out.println("Current time => "+c.getTime());

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String chatTime = df.format(c.getTime());

                            DefaultExtensionElement extchatTime = new DefaultExtensionElement(
                                    "chatTime", "urn:xmpp:extchatTime");
                            extchatTime.setValue("chatTime",chatTime+"");


                            DefaultExtensionElement extuserProfilePicture = new DefaultExtensionElement(
                                    "userProfilePicture", "urn:xmpp:extuserProfilePicture");
                            extuserProfilePicture.setValue("userProfilePicture",sharedPreferenceManager.getKEY_PROFILEPICTURE()+"");


                            msg.addExtension(extuserProfilePicture);
                            msg.addExtension(extchatTime);
                            msg.addExtension(extPhotoChat);
                            msg.addExtension(extPhotoThumb);
                            msg.addExtension(extTypeOfChat);
                            msg.addExtension(extFileName);
                            msg.addExtension(extImageWidth);
                            msg.addExtension(extImageHeight);
                            chat1.sendMessage(msg);

                            chatModel.setMessage("[Picture]");
                            chatModel.setMultimedia(1);
                            chatModel.setFile_name(extFileName.getValue("FileName"));
                            chatModel.setImage_height(imageHeight+"");
                            chatModel.setImage_width(imageWidth+"");
                            chatModel.setFile_url(extPhotoChat.getValue("PhotoChat"));
                            currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                            chatModel.setTimeStamp(currentDateTimeString);
                            chatModel.setTimeinMillis(System.currentTimeMillis()+"");
                            chatArrayList.add(chatModel);

                            try {
                                chatAdapter.notifyDataSetChanged();
                                list_chat.setSelection(chatArrayList.size()-1);
                            }catch (Exception e)
                            {
                            }

                            try
                            {
                                chatModel.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));
                            }catch (Exception e)
                            {

                            }



                            Log.e("RECEIVER : ",loginName.replace("@","#").toLowerCase());
                            Log.e("SENDER : ",sharedPreferenceManager.getKEY_LOGINNAME());

                            chatModel.setMessage_id(msg.getStanzaId()+"");
                            if(db.insertSingleChat(chatModel))
                            {
                                Log.e("DATABASE : ","Record Inserted");
                            }else
                            {
                                Log.e("DATABASE : ","Record NOT Inserted");
                            }


                        } catch (SmackException.NotConnectedException e) {
                            Toast.makeText(IndividualChatActivity.this, "Your account has been logged on to another device.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Utills.dismissDialog();
                        Log.e("Failure", "Fail" + error.toString());

                        Toast.makeText(IndividualChatActivity.this,"Error sending file,please try again later !",Toast.LENGTH_SHORT).show();
                        Log.e("Save Message  :","Message with Multimedia Failed to save on server");

                    }
                });
                ///////////////


            }
            catch (Exception e) {
                e.printStackTrace();
            }
            /*catch (SmackException e) {
                e.printStackTrace();
            }*/

        }
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
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

                Config.chatManager = ChatManager.getInstanceFor(Config.conn1);
            }

            @Override
            public void failure(RetrofitError error) {
                Utills.dismissDialog();
                Log.e("Failure", "Fail" + error.toString());
                badKeyWords=null;
                Config.chatManager = ChatManager.getInstanceFor(Config.conn1);
            }
        });


    }
    private void startrecord() {
        // TODO Auto-generated method stub
        isRecording=true;


/*recording from the app itself*/
        vibrate();
        Toast.makeText(IndividualChatActivity.this,"Recording Started...",Toast.LENGTH_SHORT).show();
        File dir = Environment.getExternalStorageDirectory();
        try {
            audiofile = File.createTempFile("IMCHAT_RECORDING_", ".3gp", dir);
        } catch (IOException e) {
            Log.e(TAG, "external storage access error");
            return;
        }
        try {
        //Creating MediaRecorder and specifying audio source, output format, encoder & output format
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }



        //start button click
        timer = new CountDown(60000, 1000);
        timer.start();



    }

    private void stoprecord() {
        // TODO Auto-generated method stub

        isRecording=false;
        vibrate();
        //stop button click
        timer.cancel();
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        Toast.makeText(IndividualChatActivity.this,"Recording Stopped...",Toast.LENGTH_SHORT).show();
        //stopping recorder

        try{
            recorder.stop();
            recorder.release();
            //after stopping the recorder, create the sound file and add it to media library.
            addRecordingToMediaLibraryandsendchat();
        }catch (RuntimeException e)
        {

        }

    }



    private void vibrate() {
        // TODO Auto-generated method stub
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void addRecordingToMediaLibraryandsendchat() {
        //creating content values of size 4
        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());

        //creating content resolver and storing it in the external content uri
        ContentResolver contentResolver = getContentResolver();
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        //sending broadcast message to scan the media file so that it can be available
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
        /*Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();*/

        try {
            InputStream inputStream = IndividualChatActivity.this.getContentResolver().openInputStream(Uri.fromFile(audiofile));


            /*option 1*/
         /*   // Create the file transfer manager
            FileTransferManager manager = FileTransferManager.getInstanceFor(Config.conn1);
            // Create the outgoing file transfer
            OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(loginName.replace("@","#").toLowerCase() + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);*/
            // Send the file
            try {
             /*   File file = File.createTempFile("sample",".mp3");

                FileUtils.copyInputStreamToFile(inputStream,file);*/


                /*
                    byte[] bytesofFile;
                    try {
                        bytesofFile=org.apache.commons.io.FileUtils.readFileToByteArray(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("eeeeeeeeeeeeeeeeeeeeeeeeeeee",e.toString());
                        bytesofFile=null;
                    }
                    File tempFile = File.createTempFile("sample", ".jpg", null);
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(bytesofFile);*/
//                transfer.sendFile(audiofile, "ImChatAPP File Transfer");
                Log.e("File Transfer","success");

                    /*add to chat List*/
                final ChatModel chatModel = new ChatModel();
                chatModel.setConfId(loginName.replace("@","#").toLowerCase());
                chatModel.setName(sharedPreferenceManager.getKEY_LOGINNAME());

                chatModel.setMultimedia(1);
                /*chatModel.setFile(audiofile);*/
                chatModel.setMyMessage(1);


                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String messageTimeStamp = dateFormat.format(calendar.getTime());
                String MsgType="";

                if(FilenameUtils.getExtension(audiofile.getName()).equals("jpg")||FilenameUtils.getExtension(audiofile.getName()).equals("jpeg")||FilenameUtils.getExtension(audiofile.getName()).equals("png")||FilenameUtils.getExtension(audiofile.getName()).equals("gif"))
                {
                    MsgType="image";
                }else if(FilenameUtils.getExtension(audiofile.getName()).equals("3gp")||FilenameUtils.getExtension(audiofile.getName()).equals("amr")||FilenameUtils.getExtension(audiofile.getName()).equals("midi")||FilenameUtils.getExtension(audiofile.getName()).equals("aac")||FilenameUtils.getExtension(audiofile.getName()).equals("mp3"))
                {
                    MsgType="voice";
                }
                /*else if(FilenameUtils.getExtension(audiofile.getName()).equals("vob")||FilenameUtils.getExtension(audiofile.getName()).equals("mp4")||FilenameUtils.getExtension(audiofile.getName()).equals("mkv")||FilenameUtils.getExtension(audiofile.getName()).equals("mpeg"))
                {
                    MsgType="vedio";
                }*/

                              /*Save multimedia message on Server**************************************************/
                String messageJson= "{\n" +
                        "    \"GroupFg\": \"False\",\n" +
                        "    \"GroupNo\": \""+""    +"\",\n" +
                        "    \"SendFrom\": \""+sharedPreferenceManager.getKEY_LOGINNAME()+"\",\n" +
                        "    \"SendTo\": \""+loginName.replace("@","#").toLowerCase()+"\",\n" +
                        "    \"SendDate\": \""+messageTimeStamp+"\",\n" +
                        "    \"FileSize\": \""+Integer.parseInt(String.valueOf(audiofile.length()/1024))+"\",\n" +
                        "    \"FileType\": \""+ FilenameUtils.getExtension(audiofile.getName())+"\",\n" +
                        "    \"FileName\": \""+audiofile.getName()+"\",\n" +
                        "    \"MsgType\": \""+MsgType+"\",\n" +
                        "    \"Content\": \""+""+"\"\n" +
                        "}";
                Map<String, String> group_save_message_param = new HashMap<>();
                group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                group_save_message_param.put("JsonObj", messageJson);
                group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());
                TypedFile typedFile = new TypedFile("multipart/form-data", audiofile);
                ApiHandler.getIMApiService().saveMessageonServerWithData(group_save_message_param,typedFile ,new Callback<SaveMessage>() {
                    @Override
                    public void success(final SaveMessage saveMessage, Response response) {
                                    /*Utills.dismissDialog();*/
                        /*Utills.dismissDialog();*/
                        Log.e("Save Message  :","Message with Multimedia Saved Successfully on server");
                            /*Below is line of code SENDING FILE THOUGH SIMPLE TEXT CHAT METHOD - method 2*/
                            /*ADD xml stanza*/
                        Chat chat1 = ChatManager.getInstanceFor(Config.conn1).createChat(loginName.replace("@","#").toLowerCase() + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);
                        try {
                                /*Send Image from gallery in message*/
                            Message msg = new Message();


                            DefaultExtensionElement extAudiourl = new DefaultExtensionElement(
                                    "audiourl", "urn:xmpp:audiourl");
                            String fileUrl = saveMessage.getData().get(0).getFileUrl();
                            extAudiourl.setValue("audiourl", fileUrl);
                            msg.setBody("[Audio]");
                            DefaultExtensionElement extPhotoThumb = new DefaultExtensionElement(
                                    "PhotoThumb", "urn:xmpp:photothumb");
                            extPhotoThumb.setValue("PhotoThumb", fileUrl);
                            DefaultExtensionElement extFileName = new DefaultExtensionElement(
                                    "FileName", "urn:xmpp:extfilename");
                            extFileName.setValue("FileName",audiofile.getName()+"");
                            DefaultExtensionElement extTypeOfChat = new DefaultExtensionElement(
                                    "typeofchat", "urn:xmpp:exttypeofchat");
                            extTypeOfChat.setValue("typeofchat", "audio");
                            Calendar c = Calendar.getInstance();
                            System.out.println("Current time => "+c.getTime());

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String chatTime = df.format(c.getTime());

                            DefaultExtensionElement extchatTime = new DefaultExtensionElement(
                                    "chatTime", "urn:xmpp:extchatTime");
                            extchatTime.setValue("chatTime",chatTime+"");

                            DefaultExtensionElement extuserProfilePicture = new DefaultExtensionElement(
                                    "userProfilePicture", "urn:xmpp:extuserProfilePicture");
                            extuserProfilePicture.setValue("userProfilePicture",sharedPreferenceManager.getKEY_PROFILEPICTURE()+"");


                            msg.addExtension(extuserProfilePicture);
                            msg.addExtension(extchatTime);
                            msg.addExtension(extAudiourl);
                            msg.addExtension(extPhotoThumb);
                            msg.addExtension(extTypeOfChat);
                            msg.addExtension(extFileName);
                            chat1.sendMessage(msg);

                            chatModel.setMessage("[Audio]");
                            chatModel.setMultimedia(1);
                            /*chatModel.setFile(audiofile);*/
                            chatModel.setFile_name(extFileName.getValue("FileName"));
                            chatModel.setImage_height("");
                            chatModel.setImage_width("");
                            chatModel.setFile_url(extAudiourl.getValue("audiourl"));
                            currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                            chatModel.setTimeStamp(currentDateTimeString);
                            chatModel.setTimeinMillis(System.currentTimeMillis()+"");
                            chatArrayList.add(chatModel);

                            try {
                                chatAdapter.notifyDataSetChanged();
                                list_chat.setSelection(chatArrayList.size()-1);
                            }catch (Exception e)
                            {
                            }

                            try
                            {
                                chatModel.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));
                            }catch (Exception e)
                            {

                            }


                            Log.e("RECEIVER : ",loginName.replace("@","#").toLowerCase());
                            Log.e("SENDER : ",sharedPreferenceManager.getKEY_LOGINNAME());

                            chatModel.setMessage_id(msg.getStanzaId()+"");
                            if(db.insertSingleChat(chatModel))
                            {
                                Log.e("DATABASE : ","Record Inserted");
                            }else
                            {
                                Log.e("DATABASE : ","Record NOT Inserted");
                            }


                        } catch (SmackException.NotConnectedException e) {
                            Toast.makeText(IndividualChatActivity.this, "Your account has been logged on to another device.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Utills.dismissDialog();
                        Log.e("Failure", "Fail" + error.toString());

                        Log.e("Save Message  :","Message with Multimedia Failed to save on server");

                    }
                });





                /*************************************************************************/




                //////////
            }


            catch (Exception e) {
                e.printStackTrace();
                Log.e("File Transfer",e.toString());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
/*CLOSE KEYBOARD AND LAYOUT*/

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        mRevealView.setVisibility(View.GONE);
        hidden = true;


/***/
    }

   /* public class FetchMessages extends AsyncTask<Void,Void,Void>
    {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog= new ProgressDialog(IndividualChatActivity.this);
            progressDialog.setMessage("Loading Messages..");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

             *//*Fetch all chat messages from db*//*

            //Fetch Latest Message From Individual Chat
            String receiverName=loginName.replace("@","#").toLowerCase(),sendername=sharedPreferenceManager.getKEY_LOGINNAME();
            chatArrayList=db.getIndividualChatMessages(sendername,receiverName);



            for(int i=0;i<chatArrayList.size();i++)
            {
                if(chatArrayList.get(i).isMultimedia()==1)
                {

                    final int position = i;


                    String fileUrl = chatArrayList.get(position).getMessage();

                    String dir = Environment.getExternalStorageDirectory()+File.separator+"IMCHATAPP";
                    //create folder
                    File folder = new File(dir); //folder name
                    folder.mkdirs();

                    incommingFile = new File(dir,fileUrl);


                            try {
                                FileUtils.copyURLToFile(new URL(fileUrl), incommingFile);
                                chatArrayList.get(position).setFile(incommingFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                }else
                {
                    chatArrayList.get(i).setFile(null);
                }
            }



            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(progressDialog.isShowing())
                progressDialog.dismiss();

            chatAdapter = new ChatAdapter(IndividualChatActivity.this, chatArrayList,false);
            list_chat.setAdapter(chatAdapter);
            list_chat.setStackFromBottom(true);

*//*
        Log.e("Fetching Receiver db query fired : ",receiverName);
        Log.e("Fetching Sender db query fired : ",sendername);
        Log.e("Individual chat history db query fired : ",chatArrayList.size()+" messages found");*//*



        }
    }
*/



    //countdown class
    public class CountDown extends CountDownTimer {

        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

            progressDialog= new ProgressDialog(IndividualChatActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Recording Audio");
            progressDialog.setMessage("Time Left : ");
            progressDialog.setInverseBackgroundForced(true);
            progressDialog.show();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long ms = millisUntilFinished;
/*
            String text = String.format("%02d\' %02d\"",
                    TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                    TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
*/
            String text = String.format("%02d",
                    TimeUnit.MILLISECONDS.toSeconds(ms));

            progressDialog.setMessage(text);
        }

        @Override
        public void onFinish() {
            Log.e("Timer  : ","Finish");


            if(isRecording)
            {
                stoprecord();
            }

        }



    }


}
