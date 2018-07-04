package com.dcdroid.chatapp.Configuration;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * Created by darshit on 24/4/17.
 */
public class Config {


    //Configure your own server

    public static final String openfire_host_server_IP = "xx.x.x.x.x.x.x.";
    public static final String openfire_host_server_key = "--------------------";
    public static final String openfire_host_server_RESOURCE = "------------";
    public static final int openfire_host_server_PORT = 5222;
    public static final String openfire_host_server_SERVICE = "//////";
    public static final String openfire_host_server_CONFERENCE_SERVICE = "conference.//////";


    public static AbstractXMPPConnection conn1;
    public static ChatManager chatManager;
    public static XMPPTCPConnectionConfiguration config;



}
