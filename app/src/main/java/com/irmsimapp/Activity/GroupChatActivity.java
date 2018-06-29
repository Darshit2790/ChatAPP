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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import uk.co.senab.photoview.PhotoView;

public class GroupChatActivity extends AppCompatActivity {


    private static final String TAG = "GroupChatActivity";
    //request code when comming in imapp from gallery and camrea
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 100;
    private static final int SELECT_AUDIO = 101;
    public static FrameLayout fullscreen;
    public static PhotoView photo_view;
    public static ImageView iv_close;
    public String groupName = "";
    public String transfereename = "";
    public String myMUCfullName = groupName.toLowerCase() + "@" + Config.openfire_host_server_CONFERENCE_SERVICE;
    public MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(Config.conn1);
    public MultiUserChat mMultiUserChat = manager.getMultiUserChat(myMUCfullName);
    MediaRecorder recorder;
    File audiofile = null;
    File incommingFile;
    boolean hidden = true;
    LinearLayout mRevealView;
    String currentDateTimeString;
    boolean isMessageExist;
    //chat data in arraylist
    ArrayList<ChatModel> chatArrayList;
    //View in which chat is shown
    se.emilsjolander.stickylistheaders.StickyListHeadersListView list_chat;
    ChatDbHelper db;
    ChatAdapter chatAdapter;
    LinearLayout layout_gallery, layout_camera, layout_audio;
    ImageView iv_attachment;
    SharedPreferenceManager sharedPreferenceManager;
    ArrayList<GroupUser.Datum.UserList> groupmembers;
    BadKeyWord badKeyWords = null;
    private TextView tv_heading, tv_send;
    ProgressDialog progressDialog;
    boolean isRecording=false;
    CountDown timer;
    /*old flow*/
    /*ArrayList<GroupUser.Datum> groupmembers;*/
    private EditText edt_send;
    private ImageView iv_back, iv_right_icon, gallery, camera, audio;

    @Override
    protected void onResume() {
        super.onResume();

        chatAdapter.notifyDataSetChanged();
        list_chat.invalidateViews();
        list_chat.scrollBy(0, 0);
    }


    @Override
    protected void onPause() {
        super.onPause();

        fullscreen.setVisibility(View.GONE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();
        findViewById();
        GroupListActivity.isFirstTime=false;
        db = new ChatDbHelper(this);
        Intent intent = getIntent();
        if (intent.hasExtra("name"))

        {
            tv_heading.setText(intent.getStringExtra("name"));
            groupName = intent.getStringExtra("name");
            transfereename = intent.getStringExtra("transfereename");
        }

         /*old flow*/
            /*groupmembers=(ArrayList<GroupUser.Datum>) intent.getSerializableExtra("groupmembers");*/


        groupmembers = (ArrayList<GroupUser.Datum.UserList>) intent.getSerializableExtra("groupmembers");
        chatArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(GroupChatActivity.this, chatArrayList, true, groupmembers);
        list_chat.setAdapter(chatAdapter);

        sharedPreferenceManager = new SharedPreferenceManager(GroupChatActivity.this);

        setActions();

        //Fetch Latest Message From Group Chat from db and new messages from server

        chatArrayList = db.getRoomHistroy(groupName.toLowerCase());
        setListAdapter();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getBadkeywords();
            }
        }, 500 * 1);


/*get all messages*/
/*
        getBadkeywords();*/

    }


    //Binding views
    public void findViewById() {

        tv_heading = (TextView) findViewById(R.id.tv_heading);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_right_icon = (ImageView) findViewById(R.id.iv_right_icon);
        tv_send = (TextView) findViewById(R.id.tv_send);
        edt_send = (EditText) findViewById(R.id.edt_send);
        list_chat = (se.emilsjolander.stickylistheaders.StickyListHeadersListView) findViewById(R.id.lv_group_chat);
        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);
        iv_attachment = (ImageView) findViewById(R.id.iv_attachment);

        fullscreen = (FrameLayout) findViewById(R.id.fullscreen);
        photo_view = (PhotoView) findViewById(R.id.photo_view);
        iv_close = (ImageView) findViewById(R.id.iv_close);

        gallery = (ImageView) findViewById(R.id.gallery);
        layout_gallery = (LinearLayout) findViewById(R.id.layout_gallery);
        camera = (ImageView) findViewById(R.id.camera);
        layout_camera = (LinearLayout) findViewById(R.id.layout_camera);

        audio = (ImageView) findViewById(R.id.audio);
        layout_audio = (LinearLayout) findViewById(R.id.layout_audio);


    }

    //Setting Actions
    public void setActions() {


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


        layout_audio.setOnTouchListener(new View.OnTouchListener() {
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

                try {


                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                } catch (Exception e) {

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

   /*     final FileTransferManager file_manager = FileTransferManager.getInstanceFor(Config.conn1);

        file_manager.addFileTransferListener(new FileTransferListener() {
            public void fileTransferRequest(FileTransferRequest request) {
                // Check to see if the request should be accepted
                // Accept it
                IncomingFileTransfer transfer = request.accept();
                try {
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        //handle case of no SDCARD present
                    } else {
                        String dir = Environment.getExternalStorageDirectory()+ File.separator+"IMCHATAPP";
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

                        *//*SmackUtils.parseResource(request.getRequestor().toString().replace("@","#")).equals(sharedPreferenceManager.getKEY_LOGINNAME())*//*

                        if (request.getRequestor().toString().replace("@","#").equals(sharedPreferenceManager.getKEY_LOGINNAME())) {

                        } else {


                            Log.e("else","else");

                            Log.e("1",request.getRequestor().toString().replace("@","#").trim());
                            Log.e("2",sharedPreferenceManager.getKEY_LOGINNAME().trim());


                            ChatModel chatModel = new ChatModel();
                            chatModel.setName(request.getRequestor().toString().replace("@","#"));
                            chatModel.setMessage("");
                            chatModel.setMyMessage(0);
                            chatModel.setMultimedia(1);
                            *//*chatModel.setFile(incommingFile);*//*
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
                                }else if(FilenameUtils.getExtension(transfer.getFileName()).equals("3gp")||FilenameUtils.getExtension(transfer.getFileName()).equals("amr")||FilenameUtils.getExtension(transfer.getFileName()).equals("midi")||FilenameUtils.getExtension(transfer.getFileName()).equals("aac")||FilenameUtils.getExtension(transfer.getFileName()).equals("mp3"))
                                {
                                    MsgType="voice";
                                }else if(FilenameUtils.getExtension(transfer.getFileName()).equals("vob")||FilenameUtils.getExtension(transfer.getFileName()).equals("mp4")||FilenameUtils.getExtension(transfer.getFileName()).equals("mkv")||FilenameUtils.getExtension(transfer.getFileName()).equals("mpeg"))
                                {
                                    MsgType="vedio";
                                }

                              *//*Save multimedia message on Server**************************************************//*
                                String messageJson= "{\n" +
                                        "    \"GroupFg\": \"True\",\n" +
                                        "    \"GroupNo\": \""+groupName+"\",\n" +
                                        "    \"SendFrom\": \""+sharedPreferenceManager.getKEY_LOGINNAME()+"\",\n" +
                                        "    \"SendTo\": \"\",\n" +
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
                                TypedFile typedFile = new TypedFile("multipart/form-data", incommingFile);
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
*/


        iv_attachment.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {



                try{

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                // finding X and Y co-ordinates
                int cy = (mRevealView.getLeft() + mRevealView.getRight());
                int cx = (mRevealView.getBottom());

                // to find  radius when icon is tapped for showing layout
                int startradius = 0;
                int endradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());


                // performing circular reveal when icon will be tapped
                Animator animator = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, startradius, endradius);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(300);

                //reverse animation
                // to find radius when icon is tapped again for hiding layout


                //  starting radius will be the radius or the extent to which circular reveal animation is to be shown
                int reverse_startradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
                //endradius will be zero
                int reverse_endradius = 0;


                // performing circular reveal for reverse animation
                Animator animate = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, reverse_startradius, reverse_endradius);


                if (hidden) {

                    // to show the layout when ic
                    // is tapped
                    mRevealView.setVisibility(View.VISIBLE);
                    animator.start();
                    hidden = false;
                } else {

                    mRevealView.setVisibility(View.VISIBLE);

                    // to hide layout on animation end
                    animate.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRevealView.setVisibility(View.INVISIBLE);
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


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        //Sending Group Chat message
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utills.isConnectingToInternet(GroupChatActivity.this)) {
                    try {
                        String message = edt_send.getText().toString();


                        if (message.length() > 0)


                            if (badKeyWords != null) {
                                String filteredMessage = edt_send.getText().toString();


                                for (int i = 0; i < badKeyWords.getData().size(); i++) {

                                    String badkeyWord = badKeyWords.getData().get(i).getItemName();

                                    filteredMessage = filteredMessage.replaceAll("(?i)" + badkeyWord, "*");
                                }


                                Message msg = new Message();
                                msg.setBody(filteredMessage);
                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time => " + c.getTime());
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String chatTime = df.format(c.getTime());

                                DefaultExtensionElement extchatTime = new DefaultExtensionElement(
                                        "chatTime", "urn:xmpp:extchatTime");
                                extchatTime.setValue("chatTime", chatTime + "");

                                DefaultExtensionElement extuserProfilePicture = new DefaultExtensionElement(
                                        "userProfilePicture", "urn:xmpp:extuserProfilePicture");
                                extuserProfilePicture.setValue("userProfilePicture", sharedPreferenceManager.getKEY_PROFILEPICTURE() + "");


                                msg.addExtension(extuserProfilePicture);
                                msg.addExtension(extchatTime);

                                mMultiUserChat.sendMessage(msg);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    /*Save message on Server**************************************************/
                                String messageTimeStamp = dateFormat.format(calendar.getTime());

                                String messageJson = "{\n" +
                                        "    \"GroupFg\": \"True\",\n" +
                                        "    \"GroupNo\": \"" + groupName + "\",\n" +
                                        "    \"SendFrom\": \"" + sharedPreferenceManager.getKEY_LOGINNAME() + "\",\n" +
                                        "    \"SendTo\": \"\",\n" +
                                        "    \"SendDate\": \"" + messageTimeStamp + "\",\n" +
                                        "    \"FileSize\": \"\",\n" +
                                        "    \"FileType\": \"\",\n" +
                                        "    \"FileName\": \"\",\n" +
                                        "    \"MsgType\": \"chat\",\n" +
                                        "    \"Content\": \"" + filteredMessage + "\"\n" +
                                        "}";
                                Map<String, String> group_save_message_param = new HashMap<>();
                                group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                                group_save_message_param.put("JsonObj", messageJson);
                                group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());

                                ApiHandler.getIMApiService().saveMessageonServer(group_save_message_param, new Callback<SaveMessage>() {
                                    @Override
                                    public void success(final SaveMessage saveMessage, Response response) {
                                    /*Utills.dismissDialog();*/
                                        Log.e("Save Message  :", "Message Saved Succesfully on server with badkeywords.");
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Utills.dismissDialog();
                                        Log.e("Failure", "Fail" + error.toString());
                                        Log.e("Save Message  :", "Message Failed on server with badkeywords.");
                                    }
                                });
                                /*************************************************************************/

                            } else {

                                Message msg = new Message();
                                msg.setBody(edt_send.getText().toString());
                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time => " + c.getTime());

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String chatTime = df.format(c.getTime());

                                DefaultExtensionElement extchatTime = new DefaultExtensionElement(
                                        "chatTime", "urn:xmpp:extchatTime");
                                extchatTime.setValue("chatTime", chatTime + "");

                                DefaultExtensionElement extuserProfilePicture = new DefaultExtensionElement(
                                        "userProfilePicture", "urn:xmpp:extuserProfilePicture");
                                extuserProfilePicture.setValue("userProfilePicture", sharedPreferenceManager.getKEY_PROFILEPICTURE() + "");


                                msg.addExtension(extuserProfilePicture);
                                msg.addExtension(extchatTime);

                                mMultiUserChat.sendMessage(msg);


                            /*Save message on Server**************************************************/

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                                String messageTimeStamp = dateFormat.format(calendar.getTime());
                                String messageJson = "{\n" +
                                        "    \"GroupFg\": \"True\",\n" +
                                        "    \"GroupNo\": \"" + groupName + "\",\n" +
                                        "    \"SendFrom\": \"" + sharedPreferenceManager.getKEY_LOGINNAME() + "\",\n" +
                                        "    \"SendTo\": \"\",\n" +
                                        "    \"SendDate\": \"" + messageTimeStamp + "\",\n" +
                                        "    \"FileSize\": \"\",\n" +
                                        "    \"FileType\": \"\",\n" +
                                        "    \"FileName\": \"\",\n" +
                                        "    \"MsgType\": \"chat\",\n" +
                                        "    \"Content\": \"" + edt_send.getText().toString() + "\"\n" +
                                        "}";


                                Map<String, String> group_save_message_param = new HashMap<>();


                                group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                                group_save_message_param.put("JsonObj", messageJson);
                                group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());

                                ApiHandler.getIMApiService().saveMessageonServer(group_save_message_param, new Callback<SaveMessage>() {
                                    @Override
                                    public void success(final SaveMessage saveMessage, Response response) {
                                    /*Utills.dismissDialog();*/

                                        Log.e("Save Message  :", "Message Saved Succesfully on server without badkeywords.");


                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Utills.dismissDialog();
                                        Log.e("Failure", "Fail" + error.toString());

                                        Log.e("Save Message  :", "Message Failed on server without badkeywords.");

                                    }
                                });

                                /*************************************************************************/
                            }
                        edt_send.setText("");
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                        Toast.makeText(GroupChatActivity.this, "Not connected to Server,Try Again!!!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Utills.showAlertOkDialog(GroupChatActivity.this, "Please Check Your Network !!!");
                }

            }


        });


        chatAdapter = new ChatAdapter(GroupChatActivity.this, chatArrayList, true, groupmembers);
        list_chat.setAdapter(chatAdapter);
        list_chat.setStackFromBottom(true);

        list_chat.setOnItemClickListener(null);

        iv_right_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this, GroupProfileActivity.class);
                intent.putExtra("groupname", groupName);
                intent.putExtra("transfereename", transfereename);
                intent.putExtra("groupmembers", groupmembers);
                GroupChatActivity.this.startActivity(intent);
            }
        });

        iv_back.setVisibility(View.VISIBLE);
        iv_right_icon.setVisibility(View.VISIBLE);
        iv_right_icon.setImageResource(R.drawable.group_icon_small);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }

    //Setting up chat instances

    public void setupChat() {

        myMUCfullName = groupName.toLowerCase() + "@" + Config.openfire_host_server_CONFERENCE_SERVICE;
        manager = MultiUserChatManager.getInstanceFor(Config.conn1);
        mMultiUserChat = manager.getMultiUserChat(myMUCfullName);
        Log.e("Group : ", groupName + " has " + groupmembers.size() + "");


        try {
/*yyyy-MM-dd'T'HH:mm:ss.SSS'Z'*/
            DiscussionHistory history = new DiscussionHistory();
            //fetch since the last messafe date received from db
/*check latest date from db,if null take today's date*/
            String latest_date = db.getLatestMessageDateFromgroupChat(groupName.toLowerCase());

            Log.e("database date  : ", latest_date + " ------" + "");
            Date since_history_date = new Date();
            if (latest_date != null) {
                /*DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");*/ //date format 1
                //EXAMPLE - /*Fri Aug 11 15:12:53 GMT+05:30 2017*/


                //UTC FORMAT
                SimpleDateFormat df = new SimpleDateFormat();
                df.setTimeZone(TimeZone.getTimeZone("UTC"));

                /*EXAMPLE - Fri Aug 04 15:15:57 GMT+05:30 2017*/


                try {
                    since_history_date = df.parse(latest_date);
                } catch (ParseException e) {
                    Log.e("Exception ", e.toString());
                }


            } else {

                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_YEAR, -7);
                since_history_date = cal.getTime();

            }

            Log.e("--------", "Fetching history of group  " + groupName.toLowerCase() + " since " + since_history_date.toString());


            history.setSince(since_history_date);


            mMultiUserChat.join(sharedPreferenceManager.getKEY_LOGINNAME(), null, history, SmackConfiguration.getDefaultPacketReplyTimeout());
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.e("Exception ", e.toString());
        } catch (XMPPException.XMPPErrorException e) {
            Log.e("Exception ", e.toString());
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.e("Exception ", e.toString());
        }

        /*CAUSES 406 ERROR if joining group in loop (Solution : users need invite and then join from invite listener)*/
        for (int i = 0; i < groupmembers.size(); i++) {

            Log.e("tag", "group chating purpose1 ::" + groupmembers);
            try {
                mMultiUserChat.invite(groupmembers.get(i).getUserName() + "@" + Config.openfire_host_server_CONFERENCE_SERVICE + "/" + Config.openfire_host_server_RESOURCE, "Join the room!!!");
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

        }

        manager.addInvitationListener(new InvitationListener() {
            @Override
            public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {


                Toast.makeText(GroupChatActivity.this, "Invitation Received!!", Toast.LENGTH_SHORT).show();

                Log.e("Invite called", "'----");

                try {
                    mMultiUserChat.join(sharedPreferenceManager.getKEY_LOGINNAME());
                    Log.e("User", "join room successfully");

                } catch (XMPPException e) {
                    e.printStackTrace();
                    Log.e("User", "join room failed!");
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                }

            }
        });


        //Receiving Group Chat message from other Users
        mMultiUserChat.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(final Message message) {

                if (message.getBody() != null) {
                    if (message.getBody().length() > 0) {


                        if (badKeyWords != null) {

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

                                    final String name = SmackUtils.parseResource(message.getFrom());
                                    final String to = SmackUtils.parseName(message.getTo());
                                    DelayInformation inf = null;
                                    try {
                                        inf = (DelayInformation) message.getExtension("delay", "urn:xmpp:delay");
                                    } catch (Exception e) {

                                        Log.e("DELAY in Timestamp ", e.toString());
                                    }
// get offline message timestamp

                                    String messageTimeStamp = "";
                                    String timeinMillis = "";
                                    if (inf != null) {
                                        Date date = inf.getStamp();
                                        currentDateTimeString = DateFormat.getDateTimeInstance().format(date);
                                        messageTimeStamp = dateFormat.format(date);
                                        timeinMillis = date.getTime() + "";


                                        Log.e("if statement : ", currentDateTimeString + "   --  " + timeinMillis + "");

                                    } else {

                                        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                        messageTimeStamp = dateFormat.format(calendar.getTime());
                                        timeinMillis = System.currentTimeMillis() + "";
                                        Log.e("else statement : ", currentDateTimeString + "   --  " + timeinMillis + "");
                                    }
                                    final ChatModel chatModel = new ChatModel();
                                    chatModel.setName(name);
                                    chatModel.setConfId(groupName.toLowerCase());
                                    chatModel.setMessage_id(message.getStanzaId());
                                    chatModel.setTimeStamp(currentDateTimeString);
                                    /*chatModel.setTimeinMillis(System.currentTimeMillis()+"");*/
                                    chatModel.setTimeinMillis(timeinMillis);


                                    chatModel.setTime(messageTimeStamp + "");


                                    //Multimedia or Text Message

                                    final DefaultExtensionElement extPhotoChat = (DefaultExtensionElement) message
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

                                    if (extTypeOfChat != null) {

                                        String dir = Environment.getExternalStorageDirectory() + File.separator + "IMCHATAPP";
                                        //create folder
                                        File folder = new File(dir); //folder name
                                        folder.mkdirs();

                                        incommingFile = new File(dir, extFileName.getValue("FileName"));
                                                        /*NEEDS TO RUN IN BACKGROUND*/

                                        try {

                                            if (extTypeOfChat.getValue("typeofchat").equals("image")) {
                                                try {
                                                    chatModel.setFile_url(extPhotoChat.getValue("PhotoChat"));
                                                          /*  FileUtils.copyURLToFile(new URL(extPhotoChat.getValue("PhotoChat")), incommingFile);*/

                                                    chatModel.setImage_height(extImageHeight.getValue("imageHeight"));
                                                    chatModel.setImage_width(extImageWidth.getValue("imageWidth"));

                                                } catch (NullPointerException e) {

                                                }


                                                try {
                                                    chatModel.setMessage("[Picture]");
                                                } catch (Exception e) {

                                                }


                                            } else if (extTypeOfChat.getValue("typeofchat").equals("audio")) {
                                                try {
                                                    chatModel.setFile_url(extAudiourl.getValue("audiourl"));
                                                           /* FileUtils.copyURLToFile(new URL(extAudiourl.getValue("audiourl")), incommingFile);*/
                                                    chatModel.setImage_height("");
                                                    chatModel.setImage_width("");
                                                } catch (NullPointerException e) {

                                                }


                                                try {
                                                    chatModel.setMessage("[Audio]");
                                                } catch (Exception e) {

                                                }

                                            }



                                            chatModel.setMultimedia(1);
                                                    /*chatModel.setFile(incommingFile);*/
                                            chatModel.setFile_name(extFileName.getValue("FileName"));


                                        } catch (NullPointerException e1) {
                                            e1.printStackTrace();
                                        }


                                    } else {
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
                                    try {
                                        chatModel.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));


                                    } catch (Exception e) {

                                    }

                                    chatModel.setMessage_id(message.getStanzaId() + "");
                                    /****Save message in Database SQLITE*****/
                                    db.insertGroupChat(chatModel);
                                    /****************************************/
                                    chatArrayList.add(chatModel);
                                /*chatArrayList=db.getRoomHistroy(groupName.toLowerCase());*/
                                    Log.e("Message", message.toString());
                                    Log.e("chat size", chatArrayList.size() + "");
                                    setListAdapter();

                                }
                            });
                        } else {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    final String name = SmackUtils.parseResource(message.getFrom());
                                    final String to = SmackUtils.parseName(message.getTo());

                                    DelayInformation inf = null;
                                    try {
                                        inf = (DelayInformation) message.getExtension("delay", "urn:xmpp:delay");
                                    } catch (Exception e) {

                                        Log.e("DELAY in Timestamp ", e.toString());
                                    }
// get offline message timestamp
                                    if (inf != null) {
                                        Date date = inf.getStamp();
                                        currentDateTimeString = DateFormat.getDateTimeInstance().format(date);
                                    } else {

                                        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                    }
                                    String messageBody = message.getBody();

                                    final ChatModel chatModel = new ChatModel();
                            /*chatModel.setName(delayInformation.getFrom().replace("@","#"));*/
                                    chatModel.setName(name);

                                    chatModel.setTimeinMillis(System.currentTimeMillis() + "");
                                    chatModel.setConfId(groupName.toLowerCase());
                                    chatModel.setTimeStamp(currentDateTimeString);

                                    Calendar cal = Calendar.getInstance();
                                    Date date = cal.getTime();

                                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String date1 = format1.format(date);


                                    chatModel.setTime(date1 + "");


                                    //Multimedia or Text Message


                                    final DefaultExtensionElement extPhotoChat = (DefaultExtensionElement) message
                                            .getExtension("urn:xmpp:photochat");

                                    final DefaultExtensionElement extFileName = (DefaultExtensionElement) message
                                            .getExtension("urn:xmpp:extfilename");
                                    final DefaultExtensionElement extuserProfilePicture = (DefaultExtensionElement) message
                                            .getExtension("urn:xmpp:extuserProfilePicture");


                                    if (extPhotoChat != null) {

                                        String dir = Environment.getExternalStorageDirectory() + File.separator + "IMCHATAPP";
                                        //create folder
                                        File folder = new File(dir); //folder name
                                        folder.mkdirs();

                                        incommingFile = new File(dir, extFileName.getValue("FileName"));




                                                    /*FileUtils.copyURLToFile(new URL(extPhotoChat.getValue("PhotoChat")), incommingFile);*/
                                        chatModel.setMessage("["+extPhotoChat.getValue("typeofchat")+"]");
                                        chatModel.setMultimedia(1);
                                                    /*chatModel.setFile(incommingFile);*/
                                        chatModel.setFile_name(extFileName.getValue("FileName"));
                                        chatModel.setImage_height("");
                                        chatModel.setImage_width("");
                                        chatModel.setFile_url(extPhotoChat.getValue("PhotoChat"));


                                    } else {
                                        chatModel.setMessage(messageBody);
                                        chatModel.setMultimedia(0);
                                        /*chatModel.setFile(null);*/

                                        chatModel.setFile_name("");
                                        chatModel.setImage_height("");
                                        chatModel.setImage_width("");
                                        chatModel.setFile_url("");
                                    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                                    if (SmackUtils.parseResource(message.getFrom()).equals(sharedPreferenceManager.getKEY_LOGINNAME())) {
                                        chatModel.setMyMessage(1);
                                    } else {
                                        chatModel.setMyMessage(0);
                                    }
                                    chatModel.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));

                                    chatModel.setMessage_id(message.getStanzaId() + "");
                                    db.insertGroupChat(chatModel);
                                    chatArrayList.add(chatModel);
                                    Log.e("Message", message.toString());
                                    Log.e("chat size", chatArrayList.size() + "");
                                    setListAdapter();

                                }
                            });

                        }


                    }

                }


            }
        });


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
                    OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(loginName.replace("@","#") + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);
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




        /*OPTION 1 :- GALLERY - when user selects image from Gallery*/


        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = GroupChatActivity.this.getContentResolver().openInputStream(data.getData());

                // Create the file transfer manager
          /*      FileTransferManager manager = FileTransferManager.getInstanceFor(Config.conn1);
                // Create the outgoing file transfer
                OutgoingFileTransfer transfer = null;*/

                try {
                    final File file = File.createTempFile("IM_" + System.currentTimeMillis(), ".jpg");

                    FileUtils.copyInputStreamToFile(inputStream, file);
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



                    /*option 1- send file trough filetransfer*/


                    /*for(int i=0;i<groupmembers.size();i++)
                    {
                        transfer = manager.createOutgoingFileTransfer(groupName.toLowerCase()+ "@" + Config.openfire_host_server_SERVICE + "/" +groupmembers.get(i).getLoginName().replace("@","#"));
                        // Send the file to all members

                        //i-at-100400060@conference.inno1.dyndns.org/app.programming


                        if(file!=null)
                        transfer.sendFile(file, "ImChatAPP File Transfer");
                        Log.e("File Transfer to "+groupmembers.get(i).getLoginName().replace("@","#"),"success");
                    }*/


                    if (file != null) {


                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String messageTimeStamp = dateFormat.format(calendar.getTime());
                        String MsgType = "";

                        if (FilenameUtils.getExtension(file.getName()).equals("jpg") || FilenameUtils.getExtension(file.getName()).equals("jpeg") || FilenameUtils.getExtension(file.getName()).equals("png") || FilenameUtils.getExtension(file.getName()).equals("gif")) {
                            MsgType = "image";
                        } else if (FilenameUtils.getExtension(file.getName()).equals("3gp") || FilenameUtils.getExtension(file.getName()).equals("amr") || FilenameUtils.getExtension(file.getName()).equals("midi") || FilenameUtils.getExtension(file.getName()).equals("aac") || FilenameUtils.getExtension(file.getName()).equals("mp3")) {
                            MsgType = "voice";
                        } else if (FilenameUtils.getExtension(file.getName()).equals("vob") || FilenameUtils.getExtension(file.getName()).equals("mp4") || FilenameUtils.getExtension(file.getName()).equals("mkv") || FilenameUtils.getExtension(file.getName()).equals("mpeg")) {
                            MsgType = "vedio";
                        }




                              /*Save multimedia message on Server**************************************************/
                        String messageJson = "{\n" +
                                "    \"GroupFg\": \"True\",\n" +
                                "    \"GroupNo\": \"" + groupName + "\",\n" +
                                "    \"SendFrom\": \"" + sharedPreferenceManager.getKEY_LOGINNAME() + "\",\n" +
                                "    \"SendTo\": \"\",\n" +
                                "    \"SendDate\": \"" + messageTimeStamp + "\",\n" +
                                "    \"FileSize\": \"" + Integer.parseInt(String.valueOf(file.length() / 1024)) + "\",\n" +
                                "    \"FileType\": \"" + FilenameUtils.getExtension(file.getName()) + "\",\n" +
                                "    \"FileName\": \"" + file.getName() + "\",\n" +
                                "    \"MsgType\": \"" + MsgType + "\",\n" +
                                "    \"Content\": \"" + "" + "\"\n" +
                                "}";


                        Map<String, String> group_save_message_param = new HashMap<>();
                        group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                        group_save_message_param.put("JsonObj", messageJson);
                        group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());
                        TypedFile typedFile = new TypedFile("multipart/form-data", file);
                        ApiHandler.getIMApiService().saveMessageonServerWithData(group_save_message_param, typedFile, new Callback<SaveMessage>() {
                            @Override
                            public void success(final SaveMessage saveMessage, Response response) {
                                    /*Utills.dismissDialog();*/
                                Log.e("Save Message  :", "Message with Multimedia Saved Successfully on server");



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
                                extFileName.setValue("FileName", file.getName() + "");
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
                                extImageWidth.setValue("imageWidth", imageWidth + "");


                                DefaultExtensionElement extImageHeight = new DefaultExtensionElement(
                                        "imageHeight", "urn:xmpp:extimageHeight");
                                extImageHeight.setValue("imageHeight", imageHeight + "");


                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time => " + c.getTime());

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String chatTime = df.format(c.getTime());

                                DefaultExtensionElement extchatTime = new DefaultExtensionElement(
                                        "chatTime", "urn:xmpp:extchatTime");
                                extchatTime.setValue("chatTime", chatTime + "");

                                DefaultExtensionElement extuserProfilePicture = new DefaultExtensionElement(
                                        "userProfilePicture", "urn:xmpp:extuserProfilePicture");
                                extuserProfilePicture.setValue("userProfilePicture", sharedPreferenceManager.getKEY_PROFILEPICTURE() + "");


                                msg.addExtension(extuserProfilePicture);

                                msg.addExtension(extPhotoChat);
                                msg.addExtension(extPhotoThumb);
                                msg.addExtension(extTypeOfChat);
                                msg.addExtension(extFileName);
                                msg.addExtension(extImageWidth);
                                msg.addExtension(extImageHeight);
                                msg.addExtension(extchatTime);



                    /*add to chat List*/
                                final ChatModel chatModel = new ChatModel();
                                chatModel.setName(sharedPreferenceManager.getKEY_LOGINNAME().replace("@", "#"));
                                chatModel.setMultimedia(1);
                    /*chatModel.setFile(file);*/
                                chatModel.setMyMessage(1);


                                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                chatModel.setTimeStamp(currentDateTimeString);
                                chatModel.setTimeinMillis(System.currentTimeMillis() + "");
                                chatModel.setMessage("[" + extTypeOfChat.getValue("typeofchat") + "]");
                                chatModel.setFile_name(file.getName());
                                chatModel.setFile_url(extPhotoChat.getValue("PhotoChat"));
                                chatModel.setImage_height(imageHeight + "");
                                chatModel.setImage_width(imageWidth + "");
                                chatModel.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));

                                chatModel.setMessage_id(msg.getStanzaId() + "");

/*
                                if (db.insertGroupChat(chatModel)) {
                                    Log.e("DATABASE while sending : ", "Record Inserted");
                                } else {
                                    Log.e("DATABASE while sending : ", "Record NOT Inserted");
                                }*/
                                try {
                                    mMultiUserChat.sendMessage(msg);
                                } catch (SmackException.NotConnectedException e) {
                                    e.printStackTrace();
                                }

                                try {


                                /*dont set List from here beacuse it will duplicate message*/
                                /*chatArrayList.add(chatModel);*/
                                /*Message already come from muc received messages*/
                            /*    chatAdapter.notifyDataSetChanged();
                                list_chat.setSelection(chatArrayList.size()-1);*/
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Utills.dismissDialog();
                                Log.e("Failure", "Fail" + error.toString());

                                Log.e("Save Message  :", "Message with Multimedia Failed to save on server");

                            }
                        });


                        /*************************************************************************/


                    }


                    //////////
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("File Transfer", e.toString());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }


        //OPTION 2 : AUDIO - when user records Audio

/*
        if (requestCode == SELECT_AUDIO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = GroupChatActivity.this.getContentResolver().openInputStream(data.getData());

                // Create the file transfer manager
                FileTransferManager manager = FileTransferManager.getInstanceFor(Config.conn1);
                // Create the outgoing file transfer
                OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(sharedPreferenceManager.getKEY_LOGINNAME().replace("@","#") + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);
                // Send the file
                try {
                   final  File file = File.createTempFile("sample_"+System.currentTimeMillis(),".mp3");

                    FileUtils.copyInputStreamToFile(inputStream,file);

                    byte[] bytesofFile;
                    try {
                        bytesofFile= FileUtils.readFileToByteArray(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("eee",e.toString());
                        bytesofFile=null;
                    }
           *//*         File tempFile = File.createTempFile("sample", ".jpg", null);
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(bytesofFile);*//*


                    for(int i=0;i<groupmembers.size();i++)
                    {
                        transfer = manager.createOutgoingFileTransfer(groupName.toLowerCase()+ "@" + Config.openfire_host_server_SERVICE + "/" +groupmembers.get(i).getLoginName().replace("@","#"));
                        // Send the file to all members

                        //i-at-100400060@conference.inno1.dyndns.org/app.programming



                        if(file!=null) {

                            *//*transfer.sendFile(file, "ImChatAPP File Transfer");*//*
                        }
                        Log.e("File Transfer to "+groupmembers.get(i).getLoginName().replace("@","#"),"success");
                    }



                    if(file!=null) {

                    *//*add to chat List*//*
                        final ChatModel chatModel = new ChatModel();
                        chatModel.setName(sharedPreferenceManager.getKEY_LOGINNAME().replace("@","#"));

                        chatModel.setMultimedia(1);
                        *//*chatModel.setFile(file);*//*
                        chatModel.setMyMessage(1);
                        chatModel.setFile_name(file.getName());
                        chatModel.setImage_height("");
                        chatModel.setImage_width("");

                        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        chatModel.setTimeStamp(currentDateTimeString);
                        chatArrayList.add(chatModel);

                        try {
                            chatAdapter.notifyDataSetChanged();
                            list_chat.setSelection(chatArrayList.size() - 1);
                        } catch (Exception e) {
                        }


                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String messageTimeStamp = dateFormat.format(calendar.getTime());
                        String MsgType = "";


                        if (FilenameUtils.getExtension(transfer.getFileName()).equals("jpg") || FilenameUtils.getExtension(transfer.getFileName()).equals("jpeg") || FilenameUtils.getExtension(transfer.getFileName()).equals("png") || FilenameUtils.getExtension(transfer.getFileName()).equals("gif")) {
                            MsgType = "image";
                        } else if (FilenameUtils.getExtension(transfer.getFileName()).equals("3gp") ||FilenameUtils.getExtension(transfer.getFileName()).equals("amr") || FilenameUtils.getExtension(transfer.getFileName()).equals("midi") || FilenameUtils.getExtension(transfer.getFileName()).equals("aac") || FilenameUtils.getExtension(transfer.getFileName()).equals("mp3")) {
                            MsgType = "voice";
                        } else if (FilenameUtils.getExtension(transfer.getFileName()).equals("vob") || FilenameUtils.getExtension(transfer.getFileName()).equals("mp4") || FilenameUtils.getExtension(transfer.getFileName()).equals("mkv") || FilenameUtils.getExtension(transfer.getFileName()).equals("mpeg")) {
                            MsgType = "vedio";
                        }

                              *//*Save multimedia message on Server**************************************************//*
                        String messageJson = "{\n" +
                                "    \"GroupFg\": \"True\",\n" +
                                "    \"GroupNo\": \"" + groupName + "\",\n" +
                                "    \"SendFrom\": \"" + sharedPreferenceManager.getKEY_LOGINNAME() + "\",\n" +
                                "    \"SendTo\": \"\",\n" +
                                "    \"SendDate\": \"" + messageTimeStamp + "\",\n" +
                                "    \"FileSize\": \"" + transfer.getFileSize() + "\",\n" +
                                "    \"FileType\": \"" + FilenameUtils.getExtension(transfer.getFileName()) + "\",\n" +
                                "    \"FileName\": \"" + transfer.getFileName() + "\",\n" +
                                "    \"MsgType\": \"" + MsgType + "\",\n" +
                                "    \"Content\": \"" + "" + "\"\n" +
                                "}";


                        Map<String, String> group_save_message_param = new HashMap<>();
                        group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                        group_save_message_param.put("JsonObj", messageJson);
                        group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());
                        TypedFile typedFile = new TypedFile("multipart/form-data", file);
                        ApiHandler.getIMApiService().saveMessageonServerWithData(group_save_message_param, typedFile, new Callback<SaveMessage>() {
                            @Override
                            public void success(final SaveMessage saveMessage, Response response) {
                                    *//*Utills.dismissDialog();*//*
                                Log.e("Save Message  :", "Message with Multimedia Saved Successfully on server");



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

                                chatModel.setMessage(extTypeOfChat.getValue("typeofchat"));
                                chatModel.setFile_url(extPhotoChat.getValue("PhotoChat"));
                                chatModel.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));
                                if(db.insertGroupChat(chatModel))
                                {
                                    Log.e("DATABASE : ","Record Inserted");
                                }else
                                {
                                    Log.e("DATABASE : ","Record NOT Inserted");
                                }
                                try {
                                    mMultiUserChat.sendMessage(msg);
                                } catch (SmackException.NotConnectedException e) {
                                    e.printStackTrace();
                                }


                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Utills.dismissDialog();
                                Log.e("Failure", "Fail" + error.toString());

                                Log.e("Save Message  :", "Message with Multimedia Failed to save on server");

                            }
                        });


                        *//*************************************************************************//*

                    }


                    //////////
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

        //OPTION 3 :- CAMERA - when user Captures image from Camera

        else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            // Create the file transfer manager option 1
           /* FileTransferManager manager = FileTransferManager.getInstanceFor(Config.conn1);
            // Create the outgoing file transfer
            OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(sharedPreferenceManager.getKEY_LOGINNAME().replace("@","#") + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);*/
            //create a file to write bitmap data
            final File file = new File(GroupChatActivity.this.getCacheDir(), "IMCHATAPP_" + System.currentTimeMillis() + ".png");
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Send the file
            try {


                if (file != null) {



                    /*FILE TRANSFER*/
/*
                    for(int i=0;i<groupmembers.size();i++)
                    {
                        transfer = manager.createOutgoingFileTransfer(groupName.toLowerCase()+ "@" + Config.openfire_host_server_SERVICE + "/" +groupmembers.get(i).getLoginName().replace("@","#"));
                        // Send the file to all members

                        //i-at-100400060@conference.inno1.dyndns.org/app.programming



                        if(f!=null) {

                            transfer.sendFile(f, "ImChatAPP File Transfer");
                        }
                        Log.e("File Transfer to "+groupmembers.get(i).getLoginName().replace("@","#"),"success");
                    }*/


                    ///////////////


                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String messageTimeStamp = dateFormat.format(calendar.getTime());
                    String MsgType = "";


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


                    if (FilenameUtils.getExtension(file.getName()).equals("jpg") || FilenameUtils.getExtension(file.getName()).equals("jpeg") || FilenameUtils.getExtension(file.getName()).equals("png") || FilenameUtils.getExtension(file.getName()).equals("gif")) {
                        MsgType = "image";
                    } else if (FilenameUtils.getExtension(file.getName()).equals("3gp") || FilenameUtils.getExtension(file.getName()).equals("amr") || FilenameUtils.getExtension(file.getName()).equals("midi") || FilenameUtils.getExtension(file.getName()).equals("aac") || FilenameUtils.getExtension(file.getName()).equals("mp3")) {
                        MsgType = "voice";
                    } else if (FilenameUtils.getExtension(file.getName()).equals("vob") || FilenameUtils.getExtension(file.getName()).equals("mp4") || FilenameUtils.getExtension(file.getName()).equals("mkv") || FilenameUtils.getExtension(file.getName()).equals("mpeg")) {
                        MsgType = "vedio";
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

                    String messageJson = "{\n" +
                            "    \"GroupFg\": \"True\",\n" +
                            "    \"GroupNo\": \"" + groupName + "\",\n" +
                            "    \"SendFrom\": \"" + sharedPreferenceManager.getKEY_LOGINNAME() + "\",\n" +
                            "    \"SendTo\": \"\",\n" +
                            "    \"SendDate\": \"" + messageTimeStamp + "\",\n" +
                            "    \"FileSize\": \"" + Integer.parseInt(String.valueOf(file.length() / 1024)) + "\",\n" +
                            "    \"FileType\": \"" + FilenameUtils.getExtension(file.getName()) + "\",\n" +
                            "    \"FileName\": \"" + file.getName() + "\",\n" +
                            "    \"MsgType\": \"" + MsgType + "\",\n" +
                            "    \"Content\": \"" + "" + "\"\n" +
                            "}";


                    Map<String, String> group_save_message_param = new HashMap<>();
                    group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                    group_save_message_param.put("JsonObj", messageJson);
                    group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());
                    TypedFile typedFile = new TypedFile("multipart/form-data", file);
                    ApiHandler.getIMApiService().saveMessageonServerWithData(group_save_message_param, typedFile, new Callback<SaveMessage>() {
                        @Override
                        public void success(final SaveMessage saveMessage, Response response) {
                                    /*Utills.dismissDialog();*/
                            Log.e("Save Message  :", "Message with Multimedia Saved Successfully on server");

                             /*Below is line of code SENDING FILE THOUGH SIMPLE TEXT CHAT METHOD - method 2*/
                            /*ADD xml stanza*/
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
                                extFileName.setValue("FileName", file.getName() + "");
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
                                extImageWidth.setValue("imageWidth", imageWidth + "");


                                DefaultExtensionElement extImageHeight = new DefaultExtensionElement(
                                        "imageHeight", "urn:xmpp:extimageHeight");
                                extImageHeight.setValue("imageHeight", imageHeight + "");

                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time => " + c.getTime());

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String chatTime = df.format(c.getTime());

                                DefaultExtensionElement extchatTime = new DefaultExtensionElement(
                                        "chatTime", "urn:xmpp:extchatTime");
                                extchatTime.setValue("chatTime", chatTime + "");

                                DefaultExtensionElement extuserProfilePicture = new DefaultExtensionElement(
                                        "userProfilePicture", "urn:xmpp:extuserProfilePicture");
                                extuserProfilePicture.setValue("userProfilePicture", sharedPreferenceManager.getKEY_PROFILEPICTURE() + "");


                                msg.addExtension(extuserProfilePicture);
                                msg.addExtension(extchatTime);
                                msg.addExtension(extPhotoChat);
                                msg.addExtension(extPhotoThumb);
                                msg.addExtension(extTypeOfChat);
                                msg.addExtension(extFileName);
                                msg.addExtension(extImageWidth);
                                msg.addExtension(extImageHeight);
                                mMultiUserChat.sendMessage(msg);




                    /*Add to chat List*/
                                final ChatModel chatModel = new ChatModel();
                                chatModel.setName(sharedPreferenceManager.getKEY_LOGINNAME());
                                chatModel.setMultimedia(1);
                    /*chatModel.setFile(file);*/
                                chatModel.setMyMessage(1);
                                chatModel.setFile_name(file.getName());
                                chatModel.setTimeinMillis(System.currentTimeMillis() + "");


                                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                chatModel.setTimeStamp(currentDateTimeString);


                                chatModel.setMessage("[" + extTypeOfChat.getValue("typeofchat") + "]");
                                chatModel.setFile_url(extPhotoChat.getValue("PhotoChat"));
                                chatModel.setImage_height(imageHeight + "");
                                chatModel.setImage_width(imageWidth + "");
                                chatModel.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));

                                chatModel.setMessage_id(msg.getStanzaId() + "");

/*
                                if (db.insertGroupChat(chatModel)) {
                                    Log.e("DATABASE while sending: ", "Record Inserted");
                                } else {
                                    Log.e("DATABASE while sending : ", "Record NOT Inserted");
                                }*/
                                try {
                                     /*dont set List from here beacuse it will duplicate message*/
                                    /*chatArrayList.add(chatModel);*/
                                /*Message already come from muc received messages*/
                            /*    chatAdapter.notifyDataSetChanged();
                                list_chat.setSelection(chatArrayList.size()-1);*/
                                } catch (Exception e) {
                                }

                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Utills.dismissDialog();
                            Log.e("Failure", "Fail" + error.toString());

                            Toast.makeText(GroupChatActivity.this, "Error sending file,please try again later !", Toast.LENGTH_SHORT).show();
                            Log.e("Save Message  :", "Message with Multimedia Failed to save on server");

                        }
                    });
                    ///////////////
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

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

    private synchronized void setListAdapter() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                new sortList().execute();
            }
        });
    }


/*    public class DescendingCustomComparator implements Comparator<ChatModel> {
        @Override
        public int compare(ChatModel o1, ChatModel o2) {

            return Long.parseLong(o1.getTimeinMillis()) > Long.parseLong(o2.getTimeinMillis()) ? -1 : (Long.parseLong(o1.getTimeinMillis()) < Long.parseLong(o2.getTimeinMillis()) ) ? 1 : 0;
        }
    }*/

    public void getBadkeywords() {

        Map<String, String> group_bad_keyword_param = new HashMap<>();


        group_bad_keyword_param.put("UserType", sharedPreferenceManager.getKeyUsertype());

        ApiHandler.getIMApiService().checkBadKeyWords(group_bad_keyword_param, new Callback<BadKeyWord>() {
            @Override
            public void success(final BadKeyWord badKeyWord, Response response) {
                                    /*Utills.dismissDialog();*/
                badKeyWords = badKeyWord;
                setupChat();
            }

            @Override
            public void failure(RetrofitError error) {
                Utills.dismissDialog();
                Log.e("Failure", "Fail" + error.toString());
                badKeyWords = null;
                setupChat();
            }
        });


    }

    private void startrecord() {
        // TODO Auto-generated method stub
        isRecording=true;
        vibrate();
        Toast.makeText(GroupChatActivity.this, "Recording Started...", Toast.LENGTH_SHORT).show();
        File dir = Environment.getExternalStorageDirectory();
        try {
            audiofile = File.createTempFile("IMCHAT_RECORDING", ".3gp", dir);
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
        Toast.makeText(GroupChatActivity.this, "Recording Stopped...", Toast.LENGTH_SHORT).show();
        //stopping recorder
        try {
            recorder.stop();
            recorder.release();
            //after stopping the recorder, create the sound file and add it to media library.
            addRecordingToMediaLibraryandsendchat();
        } catch (RuntimeException e) {

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
        values.put(MediaStore.Audio.Media.TITLE, audiofile.getName());
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
            InputStream inputStream = GroupChatActivity.this.getContentResolver().openInputStream(Uri.fromFile(audiofile));


            /*OPTION 1*/
            /*
            // Create the file transfer manager
            FileTransferManager manager = FileTransferManager.getInstanceFor(Config.conn1);
            // Create the outgoing file transfer
            OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(sharedPreferenceManager.getKEY_LOGINNAME().replace("@","#") + "@" + Config.openfire_host_server_SERVICE + "/" + Config.openfire_host_server_RESOURCE);*/
            // Send the file
            try {
        /*        File file = File.createTempFile("sample",".mp3");

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


                /*for(int i=0;i<groupmembers.size();i++)
                {
                    transfer = manager.createOutgoingFileTransfer(groupName.toLowerCase()+ "@" + Config.openfire_host_server_SERVICE + "/" +groupmembers.get(i).getLoginName().replace("@","#"));
                    // Send the file to all members

                    //i-at-100400060@conference.inno1.dyndns.org/app.programming



                    if(audiofile!=null) {

                        transfer.sendFile(audiofile, "ImChatAPP File Transfer");
                    }
                    Log.e("File Transfer to "+groupmembers.get(i).getLoginName().replace("@","#"),"success");
                }*/


                if (audiofile != null) {


                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String messageTimeStamp = dateFormat.format(calendar.getTime());
                    String MsgType = "";


                 /*   if (FilenameUtils.getExtension(transfer.getFileName()).equals("jpg") || FilenameUtils.getExtension(transfer.getFileName()).equals("jpeg") || FilenameUtils.getExtension(transfer.getFileName()).equals("png") || FilenameUtils.getExtension(transfer.getFileName()).equals("gif")) {
                        MsgType = "image";
                    } else if (FilenameUtils.getExtension(transfer.getFileName()).equals("amr") || FilenameUtils.getExtension(transfer.getFileName()).equals("midi") || FilenameUtils.getExtension(transfer.getFileName()).equals("aac") || FilenameUtils.getExtension(transfer.getFileName()).equals("mp3")) {
                        MsgType = "voice";
                    } else if (FilenameUtils.getExtension(transfer.getFileName()).equals("vob") || FilenameUtils.getExtension(transfer.getFileName()).equals("mp4") || FilenameUtils.getExtension(transfer.getFileName()).equals("mkv") || FilenameUtils.getExtension(transfer.getFileName()).equals("mpeg")) {
                        MsgType = "vedio";
                    }*/
                    if (FilenameUtils.getExtension(audiofile.getName()).equals("jpg") || FilenameUtils.getExtension(audiofile.getName()).equals("jpeg") || FilenameUtils.getExtension(audiofile.getName()).equals("png") || FilenameUtils.getExtension(audiofile.getName()).equals("gif")) {
                        MsgType = "image";
                    } else if (FilenameUtils.getExtension(audiofile.getName()).equals("amr") || FilenameUtils.getExtension(audiofile.getName()).equals("midi") || FilenameUtils.getExtension(audiofile.getName()).equals("aac") || FilenameUtils.getExtension(audiofile.getName()).equals("mp3")) {
                        MsgType = "voice";
                    } else if (FilenameUtils.getExtension(audiofile.getName()).equals("vob") || FilenameUtils.getExtension(audiofile.getName()).equals("mp4") || FilenameUtils.getExtension(audiofile.getName()).equals("mkv") || FilenameUtils.getExtension(audiofile.getName()).equals("mpeg")) {
                        MsgType = "vedio";
                    }




                              /*Save multimedia message on Server**************************************************/
                    String messageJson = "{\n" +
                            "    \"GroupFg\": \"True\",\n" +
                            "    \"GroupNo\": \"" + groupName + "\",\n" +
                            "    \"SendFrom\": \"" + sharedPreferenceManager.getKEY_LOGINNAME() + "\",\n" +
                            "    \"SendTo\": \"\",\n" +
                            "    \"SendDate\": \"" + messageTimeStamp + "\",\n" +
                            "    \"FileSize\": \"" + Integer.parseInt(String.valueOf(audiofile.length() / 1024)) + "\",\n" +
                            "    \"FileType\": \"" + FilenameUtils.getExtension(audiofile.getName()) + "\",\n" +
                            "    \"FileName\": \"" + audiofile.getName() + "\",\n" +
                            "    \"MsgType\": \"" + MsgType + "\",\n" +
                            "    \"Content\": \"" + "" + "\"\n" +
                            "}";


                    Map<String, String> group_save_message_param = new HashMap<>();
                    group_save_message_param.put("UserType", sharedPreferenceManager.getKeyUsertype());
                    group_save_message_param.put("JsonObj", messageJson);
                    group_save_message_param.put("UserName", sharedPreferenceManager.getKeyUsername());
                    TypedFile typedFile = new TypedFile("multipart/form-data", audiofile);
                    ApiHandler.getIMApiService().saveMessageonServerWithData(group_save_message_param, typedFile, new Callback<SaveMessage>() {
                        @Override
                        public void success(final SaveMessage saveMessage, Response response) {
                                    /*Utills.dismissDialog();*/
                            Log.e("Save Message  :", "Message with Multimedia Saved Successfully on server");
                                    /*Utills.dismissDialog();*/
                        /*Utills.dismissDialog();*/
                            Log.e("Save Message  :", "Message with Multimedia Saved Successfully on server");
                            /*Below is line of code SENDING FILE THOUGH SIMPLE TEXT CHAT METHOD - method 2*/
                            /*ADD xml stanza*/

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
                                extFileName.setValue("FileName", audiofile.getName() + "");
                                DefaultExtensionElement extTypeOfChat = new DefaultExtensionElement(
                                        "typeofchat", "urn:xmpp:exttypeofchat");
                                extTypeOfChat.setValue("typeofchat", "audio");

                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time => " + c.getTime());

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String chatTime = df.format(c.getTime());

                                DefaultExtensionElement extchatTime = new DefaultExtensionElement(
                                        "chatTime", "urn:xmpp:extchatTime");
                                extchatTime.setValue("chatTime", chatTime + "");

                                DefaultExtensionElement extuserProfilePicture = new DefaultExtensionElement(
                                        "userProfilePicture", "urn:xmpp:extuserProfilePicture");
                                extuserProfilePicture.setValue("userProfilePicture", sharedPreferenceManager.getKEY_PROFILEPICTURE() + "");


                                msg.addExtension(extuserProfilePicture);
                                msg.addExtension(extchatTime);
                                msg.addExtension(extAudiourl);
                                msg.addExtension(extPhotoThumb);
                                msg.addExtension(extTypeOfChat);
                                msg.addExtension(extFileName);

                                  /*add to chat List*/
                                final ChatModel chatModel = new ChatModel();
                                chatModel.setName(sharedPreferenceManager.getKEY_LOGINNAME().replace("@", "#"));
                                chatModel.setMessage("[Audio]");
                                chatModel.setMultimedia(1);
                    /*chatModel.setFile(audiofile);*/
                                chatModel.setMyMessage(1);
                                chatModel.setFile_name(audiofile.getName());
                                chatModel.setImage_height("");
                                chatModel.setImage_width("");

                                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                chatModel.setTimeStamp(currentDateTimeString);

                                chatModel.setFile_url(extAudiourl.getValue("audiourl"));
                                chatModel.setSender_profile_picture(extuserProfilePicture.getValue("userProfilePicture"));
                                chatModel.setTimeinMillis(System.currentTimeMillis() + "");
                                chatModel.setMessage_id(msg.getStanzaId() + "");
                                mMultiUserChat.sendMessage(msg);
                            /*    if (db.insertGroupChat(chatModel)) {
                                    Log.e("DATABASE while sending: ", "Record Inserted");
                                } else {
                                    Log.e("DATABASE while sending: ", "Record NOT Inserted");
                                }
*/

                                try {
                            /*dont set List from here beacuse it will duplicate message*/
                                    /*chatArrayList.add(chatModel);*/
                                /*Message already come from muc received messages*/
                            /*    chatAdapter.notifyDataSetChanged();
                                list_chat.setSelection(chatArrayList.size()-1);*/
                                } catch (Exception e) {
                                }
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Utills.dismissDialog();
                            Log.e("Failure", "Fail" + error.toString());

                            Log.e("Save Message  :", "Message with Multimedia Failed to save on server");

                        }
                    });


                    /*************************************************************************/

                }


                //////////
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("File Transfer", e.toString());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
/*CLOSE KEYBOARD AND LAYOUT*/

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        mRevealView.setVisibility(View.GONE);
        hidden = true;


/***/
    }

    public class sortList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

                /*solving on ordering and duplication*/
            Set<ChatModel> foo = new HashSet<>(chatArrayList);
            chatArrayList.clear();
            chatArrayList.addAll(foo);


            Collections.sort(chatArrayList, new Comparator<ChatModel>() {
                public int compare(ChatModel m1, ChatModel m2) {


                    Log.e("MILLIS 1 : ", m1.getTimeinMillis());
                    Log.e("MILLIS 2 : ", m2.getTimeinMillis());
                    if (Long.parseLong(m1.getTimeinMillis()) == Long.parseLong(m2.getTimeinMillis())) {
                        return 0;
                    } else if (Long.parseLong(m1.getTimeinMillis()) > Long.parseLong(m2.getTimeinMillis())) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });


            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            chatAdapter = new ChatAdapter(GroupChatActivity.this, chatArrayList, true, groupmembers);
            list_chat.setAdapter(chatAdapter);
        }
    }


    //countdown class
    public class CountDown extends CountDownTimer {

        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

            progressDialog= new ProgressDialog(GroupChatActivity.this);
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


