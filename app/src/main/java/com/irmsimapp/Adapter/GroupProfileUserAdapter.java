package com.irmsimapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irmsimapp.Activity.IndividualChatActivity;
import com.irmsimapp.Model.GroupUsers.GroupUser;
import com.irmsimapp.Model.GroupUsersList.GroupUsersList;
import com.irmsimapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darshit on 24/4/17.
 */
public class GroupProfileUserAdapter extends BaseAdapter {

    List<GroupUsersList.Datum> list;
    Context context;

    LinearLayout layout_group;
    ImageView iv_group_member;
    TextView tv_group_member_name;

    /*old flow*/
    /*ArrayList<GroupUser.Datum> groupmembers;*/


    ArrayList<GroupUser.Datum.UserList> groupmembers;



/*

    public GroupProfileUserAdapter(Context context, List<GroupUsersList.Datum> list,ArrayList<GroupUser.Datum.UserList> groupmembers) {

        this.list = list;
        this.context = context;
        this.groupmembers = groupmembers;

        this.groupMemberList= new ArrayList<>();
        for(int i=0;i<list.size();i++)
        {
            for(int j=0;j<groupmembers.size();j++)
            {
                    if(groupmembers.get(j).getUserName().equals(list.get(i).getUserName()))
                    {
                        groupMemberList.add(list.get(i));


                    }
            }
        }
    }
*/


    public GroupProfileUserAdapter(Context context,ArrayList<GroupUser.Datum.UserList> groupmembers) {
        this.context = context;
        this.groupmembers = groupmembers;
    }




    @Override
    public int getCount() {

        return groupmembers.size();
    }


    @Override
    public Object getItem(int position) {

//        return null;
return  groupmembers.get(position) ;
    }


    @Override
    public long getItemId(int position) {

        return 0;
//        return  list.get(position) ;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.group_users, null);

            layout_group = (LinearLayout) v.findViewById(R.id.layout_group);
            iv_group_member = (ImageView) v.findViewById(R.id.iv_group_member);
            tv_group_member_name = (TextView) v.findViewById(R.id.tv_group_member_name);


            String fullname;

            if(groupmembers.get(position).getFullName().length()>8)
            {
                fullname=groupmembers.get(position).getFullName().substring(0,6)+"..";
            }else
            {
                fullname=groupmembers.get(position).getFullName();
            }

            tv_group_member_name.setText(fullname);


            if (!groupmembers.get(position).getPhotoUrl().trim().equalsIgnoreCase("")) {
                if (groupmembers.get(position).getPhotoUrl().startsWith("http://")) {
                    Picasso.with(v.getContext()).load(groupmembers.get(position).getPhotoUrl()).
                            placeholder(v.getResources().getDrawable(R.drawable.usertwo)).into(iv_group_member);
                } else {

                    String picUrl = "";
                    picUrl = "http://" + groupmembers.get(position).getPhotoUrl();
                    Picasso.with(v.getContext()).load(picUrl).
                            placeholder(v.getResources().getDrawable(R.drawable.usertwo)).into(iv_group_member);

                }

            }

            layout_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, IndividualChatActivity.class);
                    intent.putExtra("name", groupmembers.get(position).getUserName());
                    intent.putExtra("LoginName", groupmembers.get(position).getLoginName());
                    intent.putExtra("full_name", groupmembers.get(position).getFullName());
                    intent.putExtra("user_type", groupmembers.get(position).getUserType());
                    intent.putExtra("groupmembers",groupmembers);
                    context.startActivity(intent);

                }
            });

        }

        return v;
    }
}
