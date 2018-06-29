package com.irmsimapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.irmsimapp.Configuration.Config;
import com.irmsimapp.R;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;

/**
 * Created by darshit on 24/4/17.
 */
public class UserListAdapter extends BaseAdapter {



    ArrayList<RosterEntry> list;

    Context context;

    public UserListAdapter(Context context, ArrayList<RosterEntry> list){

        this.list=list;
        this.context=context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.user_layout, null);
            Roster roster = Roster.getInstanceFor(Config.conn1);
            Presence presence;
            presence = roster.getPresence(list.get(position).getUser());
            TextView tv_username = (TextView) v.findViewById(R.id.tv_username);
            TextView tv_status = (TextView) v.findViewById(R.id.tv_message);


            tv_username.setText(list.get(position).getName());
            tv_status.setText(presence.getStatus());

        }

        return v;
    }
}
