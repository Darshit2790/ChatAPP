package com.dcdroid.chatapp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.dcdroid.chatapp.Adapter.UserListAdapter;
import com.dcdroid.chatapp.Configuration.Config;
import com.dcdroid.chatapp.R;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RosterList extends AppCompatActivity {

    Roster roster;

    ListView UserList;

    ArrayList<RosterEntry> rosterLists= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roster_list);
        UserList = (ListView) findViewById(R.id.list_users);

        try {
            getBuddies();
        } catch (SmackException.NotLoggedInException e) {

            Log.d("BUDDIES EXCEPTION",e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();Log.d("BUDDIES EXCEPTION",e.toString());

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("BUDDIES EXCEPTION",e.toString());
        }



        UserListAdapter userListAdapter = new UserListAdapter(RosterList.this,rosterLists);
        UserList.setAdapter(userListAdapter);

    }


    public List<RosterEntry> getBuddies() throws SmackException.NotLoggedInException, InterruptedException, SmackException.NotConnectedException {


        roster = Roster.getInstanceFor(Config.conn1);
        if (!roster.isLoaded())
        {
            roster.reloadAndWait();
            Log.e("Roster :","Reload and wait");
        }

        Collection<RosterEntry> entries = roster.getEntries();


        Log.e("Size of Roster :",entries.size()+"");

        for (RosterEntry entry : entries) {
            rosterLists.add(entry);
            Log.d("Buddies","Here: " + entry.toString());
            Log.d("Buddies","User: " + entry.getUser());//get userinfo
            Log.d("Buddies","User Name:"+entry.getName());//get username
            Log.d("Buddies","User Status: "+entry.getStatus());//get status of user
        }
        return rosterLists;
    }
}

