package com.irmsimapp.Activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.irmsimapp.ApiClient.ApiHandler;
import com.irmsimapp.Configuration.Config;
import com.irmsimapp.Encryption.EncryptionMethods;
import com.irmsimapp.Model.Login.LoginAPI;
import com.irmsimapp.R;
import com.irmsimapp.Uitils.Constants;
import com.irmsimapp.Uitils.SharedPreferenceManager;
import com.irmsimapp.Uitils.Utills;
import com.testfairy.TestFairy;

import io.fabric.sdk.android.Fabric;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity {

    private Button signup;
    private TextView signin, tv_heading,incorrect_password;
    private ImageView iv_back, iv_right_icon;
    public String userType;
    SharedPreferenceManager sharedPreferenceManager;
    private EditText edt_loginname, edt_password;
    private RelativeLayout main_layout;

    private String UserProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        if(Config.conn1!=null)
        {
            Config.conn1.disconnect();
        }
        findViewById();
        setActions();

        /*AUTOLOGIN SECOND TIME*/
        /*if(sharedPreferenceManager.getKEY_LOGINNAME().length()>0)
        {
            main_layout.setVisibility(View.GONE);
            loginToServer(sharedPreferenceManager.getKEY_LOGINNAME(),sharedPreferenceManager.getKeyPassword());
        }else
        {
            main_layout.setVisibility(View.VISIBLE);
        }*/
        TestFairy.begin(this, LoginActivity.this.getResources().getString(R.string.testfairy_app_token));
    }

    public void findViewById() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();

        /*mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.action_bar, null);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);*/
        main_layout=(RelativeLayout) findViewById(R.id.main_layout);

        tv_heading = (TextView) findViewById(R.id.tv_heading);
        incorrect_password = (TextView) findViewById(R.id.incorrect_password);
        signin = (TextView) findViewById(R.id.signin);
        signup = (Button) findViewById(R.id.signup);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_loginname = (EditText) findViewById(R.id.edt_loginname);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_right_icon = (ImageView) findViewById(R.id.iv_right_icon);
        tv_heading.setText("SIGN IN");
        iv_back.setVisibility(View.GONE);
        iv_right_icon.setVisibility(View.GONE);
        sharedPreferenceManager = new SharedPreferenceManager(LoginActivity.this);

    }
    public void setActions() {

        //Register
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        //Login
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*LoginAPI for Server*/

            if(edt_loginname.getText().toString().length()>0&&edt_password.getText().toString().length()>0)
            loginToServer(edt_loginname.getText().toString(),edt_password.getText().toString());
            }
        });
    }


    public HashMap<String, String> loginAPIParameters(String loginname,String password) {

    /*    For client, you can use below user login:
        godmark.chan@innoways.com / inno0000
*/

        /*For consultant, you can use below user login:
app.programming / apptest987
Gray.Huang / ATOM0000*/
        String encryptedPassword = encryptPassword(password);
        HashMap<String, String> LoginParam = new HashMap<>();
        LoginParam.put("AppsName", "IM");
 /*       LoginParam.put("LoginName", "cherry.zhang11@innoways.com");
        LoginParam.put("Password", "3Ush6e9x4SkRg6RrkTrm8g==,dd4b21e9ef71e1291183a46b913ae6f2,a2a02e84adb605676878845f2d9f2858");*/
        LoginParam.put("LoginName", loginname);
        LoginParam.put("Password", encryptedPassword);
        return LoginParam;
    }
    private void loginToServer(final String loginname,final String password) {

        if (Utills.isConnectingToInternet(LoginActivity.this)) {
            /*Utills.showDialog(LoginActivity.this, "Please Wait...");*/
            Utills.showDialog(LoginActivity.this, "Loading...");
            Log.e("Api call", "Login");

            ApiHandler.getCommonApiService().LogintoServer(loginAPIParameters(loginname,password), new Callback<LoginAPI>() {
                @Override
                public void success(final LoginAPI GetResponse, Response response) {
                    Utills.dismissDialog();
                    if (GetResponse.getStatus().equals("1")) {
                        userType = GetResponse.getData().get(0).getUserType();

                        UserProfilePicture=GetResponse.getData().get(0).getPhotoUrl();


                        sharedPreferenceManager.setKEY_PROFILEPICTURE(UserProfilePicture);

                        checkUserOnOpenfire(loginname,GetResponse.getData().get(0).getUserName(), password,GetResponse.getData().get(0).getFullName());
                        incorrect_password.setVisibility(View.GONE);
                    } else {
                        incorrect_password.setVisibility(View.VISIBLE);

                    }
                }
                @Override
                public void failure(RetrofitError error) {
                    Utills.dismissDialog();
                    Log.e("Failure", "Fail" + error.toString());
                }
            });
        } else {
            Log.e("Failure", "No connection");
            Toast.makeText(LoginActivity.this, "No Internet Connection!!!", Toast.LENGTH_LONG).show();

        }
    }
    private class MyOpenfireLoginTask extends AsyncTask<String, String, String> {

        String username, password,loginname,fullname;

        public MyOpenfireLoginTask(final String loginName,final String username, final String password, final String fullname) {

            if(loginName.contains("@"))
            {
               /* this.loginname=loginName.split("@")[0].toLowerCase();*/

                //Replace @ with #
                this.loginname=loginName.replace("@","#").toLowerCase();

            }else
            {
                this.loginname = loginName.toLowerCase();
            }
            this.password = password;
            this.username=username;
            this.fullname=fullname;
        }

        @Override
        protected String doInBackground(String... params) {
            // Create a connection to the jabber.org server.

            Log.e("Login using ",loginname+" , "+password);

            Config.config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(loginname, password)
                    .setHost(Config.openfire_host_server_IP)
                    .setResource(Config.openfire_host_server_RESOURCE)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setServiceName(Config.openfire_host_server_SERVICE)
                    .setPort(Config.openfire_host_server_CHAT_PORT)
                    .setDebuggerEnabled(true) // to view what's happening in detail
                    .build();

            Config.conn1 = new XMPPTCPConnection(Config.config);
            Config.conn1.setPacketReplyTimeout(5000);
            try {
                Config.conn1.connect();
                if (Config.conn1.isConnected()) {
                    Log.w("app", "conn done");
                }
                Config.conn1.login();

                if (Config.conn1.isAuthenticated()) {
                    Log.w("app", "Auth done");

                    /*Intent intent = new Intent(MainActivity.this, RosterList.class);
                    MainActivity.this.startActivity(intent);*/
                }else {
                    Log.e("User Not Authenticated","Needs to Update Password");

                }

            } catch (Exception e) {
                Log.w("app", e.toString());
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {


            if (Config.conn1.isAuthenticated()) {

                Log.w("app", "Auth done");

                sharedPreferenceManager.setKEY_LOGINNAME(loginname);
                sharedPreferenceManager.setKeyUsername(username);
                sharedPreferenceManager.setKeyPassword(password);
                sharedPreferenceManager.setKeyUsertype(userType);
                sharedPreferenceManager.setKEY_PROFILEPICTURE(UserProfilePicture);

                Intent intent = new Intent(LoginActivity.this, GroupListActivity.class);
                LoginActivity.this.startActivity(intent);
                finish();

            } else {
                /*Toast.makeText(LoginActivity.this, "User/Password not match on Openfire Server,Try Again!!!!", Toast.LENGTH_SHORT).show();*/

                Toast.makeText(LoginActivity.this, "Updating User on Openfire", Toast.LENGTH_SHORT).show();

                /*http://192.168.1.147:9090/plugins/userService/userservice?type=update&secret=Pvx8xT4qB8xEBDay&username=app.programming&password=dummy&name=newnbhjblkjnlkj&email=email@email.com*/
                updateUseronOpenfire(loginname,username,password,fullname);
            }
        }
    }


    public void updateUseronOpenfire(final String loginName,final String userName,final String password,final String fullname) {

        if (Utills.isConnectingToInternet(LoginActivity.this)) {
            /*Utills.showDialog(LoginActivity.this, "Updating User on Openfire...");*/
            Utills.showDialog(LoginActivity.this, "Loading...");

            String _loginname=loginName;

            String email="";

            if(_loginname.contains("@"))
            {
                email=_loginname;
              /*  _loginname=_loginname.split("@")[0];*/


                //Replace @ with #
                _loginname=_loginname.replace("@","#");

            }

            Map<String,String> update_user_param = new HashMap<>();

            update_user_param.put("type","update");
            update_user_param.put("secret",Config.openfire_host_server_key);
            update_user_param.put("username",_loginname);
            update_user_param.put("password",password);
            if(email.length()>0)
            update_user_param.put("email",email);


            ApiHandler.getOpenfireApiUserService().updateUseronOpenfireServer(update_user_param, new Callback<String>() {
                @Override
                public void success(final String str, Response response) {
                    Utills.dismissDialog();

                    if (response.getStatus() == 200) {

                        MyOpenfireLoginTask task = new MyOpenfireLoginTask(loginName,userName, password,fullname);
                        task.execute("");

                    }else
                    {
                        Toast.makeText(LoginActivity.this,"Failed to update user On Openfire!!!",Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void failure(RetrofitError error) {
                    Utills.dismissDialog();
                    Log.e("Failure", "Fail in update user openfire API " + error.toString());

                }
            });
        } else {
            Log.e("Failure", "No connection");
        }
    }



    public void checkUserOnOpenfire(final String loginName,final String userName, final String password,final String fullname) {

        if (Utills.isConnectingToInternet(LoginActivity.this)) {
            /*Utills.showDialog(LoginActivity.this, "Checking User on Openfire...");*/
            Utills.showDialog(LoginActivity.this, "Loading...");

            String _loginname=loginName;

            if(_loginname.contains("@"))
            {

                /*_loginname=_loginname.split("@")[0];*/


                //Replace @ with #
                _loginname=_loginname.replace("@","#");


            }

            ApiHandler.getOpenfireApiService().checkUserOnOpenfire("application/json", Config.openfire_host_server_key, _loginname.toLowerCase(), new Callback<JsonObject>() {
                @Override
                public void success(final JsonObject response_json, Response response) {
                    Utills.dismissDialog();

                    if (response.getStatus() == 200) {

                        MyOpenfireLoginTask task = new MyOpenfireLoginTask(loginName,userName, password,fullname);
                        task.execute("");

                    } else {
                        addUserInOprnFire(loginName.toLowerCase(),userName, password,fullname);
//                        Toast.makeText(LoginActivity.this, userName + " user is not found on Openfire Server!!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void failure(RetrofitError error) {
                    Utills.dismissDialog();
                    Log.e("Failure", "Fail in openfire API " + error.toString());
                    addUserInOprnFire(loginName.toLowerCase(),userName, password,fullname);
//                    Toast.makeText(LoginActivity.this, userName + " user is not found on Openfire Server!!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("Failure", "No connection");
        }
    }

    public String encryptPassword(String passwordString) {

    /*Encryption Logic : password = IrregularMd5(passwordString),NormalMd5(passwordString),EvenOddReversal(NormalMd5(passwordString+"IRMS"))*/


        String IrregularMd5Encrypted = "", NormalMd5Encrypted = "", EvenOddReversalEncrypted = "";


        try {
            IrregularMd5Encrypted = EncryptionMethods.IrregularMD5(passwordString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NormalMd5Encrypted = EncryptionMethods.NormalMD5(passwordString);


        EvenOddReversalEncrypted = EncryptionMethods.EvenOddRevarsal(passwordString, "IRMS");


        String encryptedPassword = IrregularMd5Encrypted.trim() + "," + NormalMd5Encrypted.trim() + "," + EvenOddReversalEncrypted.trim();


        Log.e("Encrypted Password for" + passwordString, encryptedPassword);


        return encryptedPassword;
    }

    private void addUserInOprnFire(final String loginName,final String username, final String password,final String fullname) {

        final ProgressDialog dialog;
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiHandler.getOpenfireApiService().createUserOnOpenfire("application/json", Config.openfire_host_server_key, createUserJsonMAp(loginName, password,fullname), new Callback<String>() {
            @Override
            public void success(String s, Response response) {

                try {

                    dialog.dismiss();

                    if (response.getStatus() == 201) {
                        MyOpenfireLoginTask task = new MyOpenfireLoginTask(loginName,username, password,fullname);
                        task.execute("");
                    } else {

                    }


                } catch (Exception e) {
                    dialog.dismiss();
                    Log.e("--createuseropenfire---", e.toString());
                }


            }

            @Override
            public void failure(RetrofitError error) {

                dialog.dismiss();
                Log.e("--createuseropenfire--", error.toString());
            }
        });

    }

    public JsonObject createUserJsonMAp(String loginname, String password,String fullname) {

        JsonObject gsonObject = new JsonObject();

        try {
            JSONObject jsonObj_createuser = new JSONObject();
            if(loginname.contains("@"))
            {
                jsonObj_createuser.put("email", loginname);
                /*jsonObj_createuser.put("username", loginname.split("@")[0]);*/
                jsonObj_createuser.put("username", loginname.replace("@","#"));
                jsonObj_createuser.put("password", password);
                jsonObj_createuser.put("name", fullname);
            }else
            {
                jsonObj_createuser.put("username", loginname);
                jsonObj_createuser.put("password", password);
                jsonObj_createuser.put("name", fullname);
            }



            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(jsonObj_createuser.toString());

        } catch (JSONException e) {

        }

        return gsonObject;
    }
}
