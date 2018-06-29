package com.irmsimapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.irmsimapp.Model.ChatModel;

import java.util.ArrayList;


public class ChatDbHelper extends SQLiteOpenHelper {


    //field for single chat table
    private static final String TABLE_CHAT_SINGLE = "tbl_single_chat";
    private static final String CID = "_id";
    private static final String CHAT_TYPE_ID = "chat_id";
    private static final String chat_chat_conv_id = "chat_chat_conv_id";
    private static final String SENDTYPE = "send_type";
    private static final String SENDER_NAME = "sender_name";
    private static final String RECEIVER_NAME = "receiver_name";
    private static final String SENDER_ID = "sender_id";
    private static final String RECEIVER_ID = "receiver_id";
    private static final String SENDER_PROFILE_PICTURE = "sender_profile_img";
    private static final String CHAT_READ_UNREAD = "receiver_profile_img";
    private static final String TIME = "send_time";
    private static final String TIMEINMILLIS = "send_time_millis";
    private static final String MESSAGE = "msg";
    private static final String ISMYMESSAGE = "ismymessage";
    private static final String ISMULTIMEDIA = "ismultimedia";
    private static final String FILENAME = "file_name";
    private static final String CHATMEDIAURL = "chat_mediaURL";
    private static final String IMAGEWIDTH = "imageWidth";
    private static final String IMAGEHEIGHT = "imageHeight";
    private static final String MESSAGEID = "message_id";
    private static final String CREATE_CHAT_SINGLE = "CREATE TABLE " + TABLE_CHAT_SINGLE + "(" + CID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            RECEIVER_NAME + " TEXT, " + SENDER_NAME + " TEXT, " + TIME + " TEXT, " + TIMEINMILLIS + " TEXT, " + MESSAGE + " TEXT, " + ISMYMESSAGE + " INTEGER , " + ISMULTIMEDIA + " INTEGER , " + FILENAME + " TEXT ," + CHATMEDIAURL + " TEXT ," + SENDER_PROFILE_PICTURE + " TEXT ," + IMAGEWIDTH + " TEXT, " + IMAGEHEIGHT + " TEXT, " + MESSAGEID + " TEXT NOT NULL UNIQUE  " + " ) ";
    //Field for group chat table
    private static final String TABLE_CHAT_GROUP = "tbl_group_chat";
    private static final String CONFID = "conf_id";
    private static final String CREATE_CHAT_GROUP = "CREATE TABLE " + TABLE_CHAT_GROUP + "(" + CID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONFID + " TEXT, " + SENDER_NAME + " TEXT, " + TIME + " TEXT, " + TIMEINMILLIS + " TEXT, " + MESSAGE + " TEXT, " + ISMYMESSAGE + " INTEGER , " + ISMULTIMEDIA + " INTEGER , " + FILENAME + " TEXT ," + CHATMEDIAURL + " TEXT ," + SENDER_PROFILE_PICTURE + " TEXT ," + IMAGEWIDTH + " TEXT, " + IMAGEHEIGHT + " TEXT, " + MESSAGEID + " TEXT NOT NULL UNIQUE  " + " ) ";
    static String DATABASE_NAME = "IMAPPCHAT";


    public ChatDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CHAT_SINGLE);
        db.execSQL(CREATE_CHAT_GROUP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public boolean insertSingleChat(ChatModel chat) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RECEIVER_NAME, chat.getConfId());
        values.put(SENDER_NAME, chat.getName());
        values.put(TIME, chat.getTimeStamp());
        values.put(TIMEINMILLIS, chat.getTimeinMillis());
        values.put(MESSAGE, chat.getMessage());
        values.put(ISMYMESSAGE, chat.isMyMessage());
        values.put(ISMULTIMEDIA, chat.isMultimedia());
        values.put(FILENAME, chat.getFile_name());
        values.put(IMAGEHEIGHT, chat.getImage_height());
        values.put(IMAGEWIDTH, chat.getImage_width());
        values.put(CHATMEDIAURL, chat.getFile_url());
        values.put(SENDER_PROFILE_PICTURE, chat.getSender_profile_picture());
        values.put(MESSAGEID, chat.getMessage_id());
        int i = (int) db.insert(TABLE_CHAT_SINGLE, null, values);

        return i > 0;
    }


    public boolean insertGroupChat(ChatModel chat) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CONFID, chat.getConfId());
        values.put(SENDER_NAME, chat.getName());
        values.put(TIME, chat.getTimeStamp());
        values.put(TIMEINMILLIS, chat.getTimeinMillis());
        values.put(MESSAGE, chat.getMessage());
        values.put(ISMYMESSAGE, chat.isMyMessage());
        values.put(ISMULTIMEDIA, chat.isMultimedia());
        values.put(FILENAME, chat.getFile_name());
        values.put(IMAGEHEIGHT, chat.getImage_height());
        values.put(IMAGEWIDTH, chat.getImage_width());
        values.put(CHATMEDIAURL, chat.getFile_url());
        values.put(SENDER_PROFILE_PICTURE, chat.getSender_profile_picture());
        values.put(MESSAGEID, chat.getMessage_id());
        int i = 0;
        try {
            i = (int) db.insert(TABLE_CHAT_GROUP, null, values);
            return i > 0;
        } catch (Exception e) {
            return i > 0;
        }


    }


    public ArrayList<ChatModel> getRoomHistroy(String roomName) {
        ArrayList<ChatModel> list = new ArrayList<ChatModel>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "select * from " + TABLE_CHAT_GROUP + " where " + CONFID + "='" + roomName + "'" + " ORDER BY " + TIMEINMILLIS + " ASC ";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            for (int i = 0; i < c.getCount(); i++) {
                ChatModel ch = new ChatModel(c.getString(c.getColumnIndex(CONFID)), c.getString(c.getColumnIndex(SENDER_NAME)), c.getString(c.getColumnIndex(TIME)), c.getString(c.getColumnIndex(MESSAGE)), c.getInt(c.getColumnIndex(ISMYMESSAGE)), c.getInt(c.getColumnIndex(ISMULTIMEDIA)), c.getString(c.getColumnIndex(FILENAME)), c.getString(c.getColumnIndex(CHATMEDIAURL)), c.getString(c.getColumnIndex(SENDER_PROFILE_PICTURE)), c.getString(c.getColumnIndex(TIMEINMILLIS)));
                list.add(ch);
                c.moveToNext();
            }
        }
        c.close();
        return list;
    }

    public ArrayList<ChatModel> getIndividualChatMessages(String senderName, String receiverName) {
        ArrayList<ChatModel> list = new ArrayList<ChatModel>();
        int size = 0;
        SQLiteDatabase db = getWritableDatabase();
        /*String query = "select * from " + TABLE_CHAT_SINGLE +" where "+" ( "+RECEIVER_NAME+" ='"+receiverName+ "'" +" or "+RECEIVER_NAME+" ='"+senderName+ "'" +" ) "+" and "+" ( "+SENDER_NAME+" ='"+senderName+"'"+" or "+ SENDER_NAME+" ='"+receiverName+"'"+" ) ";*/
        String query = "select * from " + TABLE_CHAT_SINGLE + " where" + " ( " + RECEIVER_NAME + " ='" + receiverName + "'" + " AND " + SENDER_NAME + " ='" + senderName + "')" + " OR " + " ( " + RECEIVER_NAME + " ='" + senderName + "'" + " AND " + SENDER_NAME + " ='" + receiverName + "'" + " ) " + " ORDER BY " + TIMEINMILLIS + " ASC ";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            for (int i = 0; i < c.getCount(); i++) {
                ChatModel ch = new ChatModel(c.getString(c.getColumnIndex(RECEIVER_NAME)), c.getString(c.getColumnIndex(SENDER_NAME)), c.getString(c.getColumnIndex(TIME)), c.getString(c.getColumnIndex(MESSAGE)), c.getInt(c.getColumnIndex(ISMYMESSAGE)), c.getInt(c.getColumnIndex(ISMULTIMEDIA)), c.getString(c.getColumnIndex(FILENAME)), c.getString(c.getColumnIndex(CHATMEDIAURL)), c.getString(c.getColumnIndex(SENDER_PROFILE_PICTURE)), c.getString(c.getColumnIndex(TIMEINMILLIS)));
                list.add(ch);
                c.moveToNext();
                size++;
            }
        }
        c.close();

        Log.e("FETCHING MESSAGES : ", query);
        Log.e("MESSAGES size : ", size + "");
        return list;
    }

    /*select chat_created_date from hpl_chat_datas where chat_roodJID = '%@'  order by chat_created_date desc limit 1*/


    /*2017-06-30 15:27:39*/
    public String getLatestMessageDateFromgroupChat(String groupName) {

        String date;
        SQLiteDatabase db = getWritableDatabase();
        String query = "select " + TIME + " , MAX(send_time_millis) " + " from " + TABLE_CHAT_GROUP + " where " + CONFID + " ='" + groupName + "'" + " limit 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        date = c.getString(c.getColumnIndex(TIME));
        return date;
    }


    public ChatModel getLatestIndividualChatMessage(String senderName, String receiverName) {
        ChatModel record = null;
        SQLiteDatabase db = getWritableDatabase();
        /*String query = "select "+RECEIVER_NAME+" , "+SENDER_NAME+" , " +TIME+" , "+MESSAGE+" , "+ISMYMESSAGE+" , "+ISMULTIMEDIA+" , "+ FILENAME+" , "+CHATMEDIAURL+" , "+SENDER_PROFILE_PICTURE+" , MAX(send_time_millis) " +" from " + TABLE_CHAT_SINGLE +" where "+" ( "+RECEIVER_NAME+" ='"+receiverName+ "'" +" or "+RECEIVER_NAME+" ='"+senderName+ "'" +" ) "+" and "+" ( "+SENDER_NAME+" ='"+senderName+"'"+" or "+ SENDER_NAME+" ='"+receiverName+"'"+" ) ";*/
        String query = "select " + RECEIVER_NAME + " , " + SENDER_NAME + " , " + TIME + " , " + MESSAGE + " , " + ISMYMESSAGE + " , " + ISMULTIMEDIA + " , " + FILENAME + " , " + CHATMEDIAURL + " , " + SENDER_PROFILE_PICTURE + " , " + TIMEINMILLIS + " , MAX(send_time_millis) " + " from " + TABLE_CHAT_SINGLE + " where" + " ( " + RECEIVER_NAME + " ='" + receiverName + "'" + " AND " + SENDER_NAME + " ='" + senderName + "')" + " OR " + " ( " + RECEIVER_NAME + " ='" + senderName + "'" + " AND " + SENDER_NAME + " ='" + receiverName + "')";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }

        record = new ChatModel(c.getString(c.getColumnIndex(RECEIVER_NAME)), c.getString(c.getColumnIndex(SENDER_NAME)), c.getString(c.getColumnIndex(TIME)), c.getString(c.getColumnIndex(MESSAGE)), c.getInt(c.getColumnIndex(ISMYMESSAGE)), c.getInt(c.getColumnIndex(ISMULTIMEDIA)), c.getString(c.getColumnIndex(FILENAME)), c.getString(c.getColumnIndex(CHATMEDIAURL)), c.getString(c.getColumnIndex(SENDER_PROFILE_PICTURE)), c.getString(c.getColumnIndex(TIMEINMILLIS)));
        c.close();
        return record;
    }


    public ChatModel getLatestMessageRoomWise(String roomName) {
        ChatModel record = null;
        SQLiteDatabase db = getWritableDatabase();
        String query = "select " + CONFID + " , " + SENDER_NAME + " , " + TIME + " , " + MESSAGE + " , " + ISMYMESSAGE + " , " + ISMULTIMEDIA + " , " + FILENAME + " , " + CHATMEDIAURL + " , " + SENDER_PROFILE_PICTURE + " , " + TIMEINMILLIS + " , MAX(send_time_millis) " + " from " + TABLE_CHAT_GROUP + " where " + CONFID + "='" + roomName + "'";
       /* select conf_id,name,send_time,msg,MAX(send_time_millis) AS max_milli from tbl_chat where conf_id="i-at-100400060";*/
        Log.e("Latest Message Query : ", query.toString());
        Cursor c = db.rawQuery(query, null);

        if (c != null) {
            c.moveToFirst();
        }

        record = new ChatModel(c.getString(c.getColumnIndex(CONFID)), c.getString(c.getColumnIndex(SENDER_NAME)), c.getString(c.getColumnIndex(TIME)), c.getString(c.getColumnIndex(MESSAGE)), c.getInt(c.getColumnIndex(ISMYMESSAGE)), c.getInt(c.getColumnIndex(ISMULTIMEDIA)), c.getString(c.getColumnIndex(FILENAME)), c.getString(c.getColumnIndex(CHATMEDIAURL)), c.getString(c.getColumnIndex(SENDER_PROFILE_PICTURE)), c.getString(c.getColumnIndex(TIMEINMILLIS)));

        c.close();
        return record;
    }

    public void removeAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CHAT_GROUP, null, null);
        db.delete(TABLE_CHAT_SINGLE, null, null);
    }

}

