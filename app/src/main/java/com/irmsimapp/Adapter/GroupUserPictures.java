package com.irmsimapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.irmsimapp.Model.GroupUsers.GroupUser;
import com.irmsimapp.Model.GroupUsersList.GroupUsersList;
import com.irmsimapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darshit on 5/7/17.
 */
public class GroupUserPictures extends  BaseAdapter{


    Context context;
    private ArrayList<GroupUser.Datum> arrayChilds;
    List<GroupUsersList.Datum> pictures;

    ArrayList<String> images;


    public GroupUserPictures(Context context, ArrayList<GroupUser.Datum> arrayChilds,List<GroupUsersList.Datum> pictures) {

        this.context = context;
        this.pictures = pictures;
        this.arrayChilds = arrayChilds;

        this.images=new ArrayList<>();
        for(int i=0;i<pictures.size();i++)
        {
            for(int j=0;j<arrayChilds.size();j++)
            {

                /*old flow*/
/*
                if(pictures.get(i).getFullName().equals(arrayChilds.get(j).getFullName()))
                {
                    images.add(pictures.get(i).getPhotoUrl());
                }*/
            }


        }

    }

    @Override
    public int getCount() {
        return arrayChilds.size();
    }

    @Override
    public Object getItem(int position) {

        return  arrayChilds.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.matrix, null);

            ImageView iv_member_pic = (ImageView) v.findViewById(R.id.iv_member_pic);


                    if (!arrayChilds.get(position).getPhotoUrl().trim().equalsIgnoreCase("")) {
                        if (arrayChilds.get(position).getPhotoUrl().startsWith("http://")) {
                            Picasso.with(context).load(arrayChilds.get(position).getPhotoUrl()).into(iv_member_pic);
                        } else {

                            String picUrl = "";
                            picUrl = "http://" + arrayChilds.get(position).getPhotoUrl();
                            Picasso.with(context).load(picUrl).into(iv_member_pic);
                        }
                        final float scale = context.getResources().getDisplayMetrics().density;
                        int pixels;

                        if(arrayChilds.size()%2==0)
                        {
                            pixels = (int) (26 * scale + 0.5f);
                            iv_member_pic.getLayoutParams().width = pixels;

                            if(arrayChilds.size()==2)
                            {
                                pixels = (int) (53 * scale + 0.5f);
                                iv_member_pic.getLayoutParams().height =pixels;

                            }else
                            {
                                if(arrayChilds.size()>4)
                                {
                                    pixels = (int) (17 * scale + 0.5f);
                                    iv_member_pic.getLayoutParams().height = pixels;
                                }else
                                {            pixels = (int) (26 * scale + 0.5f);

                                    iv_member_pic.getLayoutParams().height = pixels;
                                }

                            }

                            iv_member_pic.requestLayout();
                        }else
                        {
                            pixels = (int) (26 * scale + 0.5f);
                            iv_member_pic.getLayoutParams().height = pixels;
                            iv_member_pic.getLayoutParams().width = pixels;
                            iv_member_pic.requestLayout();

                            if(arrayChilds.size()==3)
                            {
                                pixels = (int) (53 * scale + 0.5f);
                                iv_member_pic.getLayoutParams().height =pixels;

                            }else
                            {
                                if(arrayChilds.size()>6)
                                {
                                    pixels = (int) (17 * scale + 0.5f);
                                    iv_member_pic.getLayoutParams().height = pixels;
                                }else
                                {         pixels = (int) (26 * scale + 0.5f);

                                    iv_member_pic.getLayoutParams().height = pixels;
                                }

                            }

                            iv_member_pic.requestLayout();
                        }


            }

        }

        return v;
    }
}
