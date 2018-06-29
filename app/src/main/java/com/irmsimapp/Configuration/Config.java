package com.irmsimapp.Configuration;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * Created by darshit on 24/4/17.
 */
public class Config {

    /*public static final String openfire_host_server_IP = "192.168.1.147";*/
    /*public static final String openfire_host_server_IP = "120.72.93.181";


    public static final String openfire_host_server_key = "Pvx8xT4qB8xEBDay";
    public static final String openfire_host_server_RESOURCE = "i-RMSIMApp";
    public static final int openfire_host_server_PORT = 5222;

    public static final String openfire_host_server_SERVICE = "darshitchatserver";
    public static final String openfire_host_server_CONFERENCE_SERVICE = "conference.darshitchatserver";*/


    public static AbstractXMPPConnection conn1;
    public static ChatManager chatManager;
    public static XMPPTCPConnectionConfiguration config;


    /*CLIENT's openfire*/
    public static final String openfire_host_server_key = "irms21897016";
    public static final String openfire_host_server_RESOURCE = "i-RMSIMApp";
    public static final String openfire_host_server_IP = "58.250.169.98";
    public static final int openfire_host_server_PORT = 5232;
    public static final String openfire_host_server_SERVICE = "inno1.dyndns.org";
    public static final int openfire_host_server_CHAT_PORT = 5222;
    public static final String openfire_host_server_CONFERENCE_SERVICE = "conference.inno1.dyndns.org";
}
