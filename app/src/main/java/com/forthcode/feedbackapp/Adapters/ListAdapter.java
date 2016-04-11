package com.forthcode.feedbackapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.forthcode.feedbackapp.Database.MyDb;
import com.forthcode.feedbackapp.Fragments.CommentFragment;
import com.forthcode.feedbackapp.R;

import java.util.Map;

/**
 * Created by Ajay on 28-03-2016.
 */
public class ListAdapter extends BaseAdapter{


    String[] subject_list={"City","Dates","Client", "DNA Representatives", "Venue", "Venue Liaison", "Crowd Attended", "F&B", "Setup", "Emcee", "Housekeeping", "Security", "Crowd Management", "Feed", "Technicals", "Crowd Engaging Activties", "Issues Faced", "Remarks"};
    Context context;
    FragmentManager fm;
    String catName,subject, subjectDb;
    MyDb db;
    public ListAdapter(Context context, FragmentManager fm, String catName){
        this.context=context;
        this.fm=fm;
        this.catName=catName;
        db=new MyDb(context);
    }

    @Override
    public int getCount() {
        return subject_list.length;
    }

    @Override
    public Object getItem(int position) {
        return subject_list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.custom_subject_vew, parent, false);
        } else {
            v = convertView;
        }
        TextView tv_subject = (TextView) v.findViewById(R.id.tv_subject);
        ImageButton ib_editbtn= (ImageButton) v.findViewById(R.id.ib_editbtn);
        final TextView tv_comment = (TextView) v.findViewById(R.id.tv_comment);
        subject=subject_list[position];

        if(subject.equals("City")){
            subjectDb="city";
        }else if(subject.equals("Dates")){
            subjectDb="dates";
        }else if(subject.equals("Client")){
            subjectDb="client";
        }else if(subject.equals("DNA Representatives")){
            subjectDb="dnaRepresentative";
        }else if(subject.equals("Venue")){
            subjectDb="venue";
        }else if(subject.equals("Venue Liaison")){
            subjectDb="venueLiason";
        }else if(subject.equals("Crowd Attended")){
            subjectDb="crowdAttended";
        }else if(subject.equals("F&B")){
            subjectDb="FnB";
        }else if(subject.equals("Setup")){
            subjectDb="setup";
        }else if(subject.equals("Emcee")){
            subjectDb="emcee";
        }else if(subject.equals("Housekeeping")){
            subjectDb="housekeeping";
        }else if(subject.equals("Security")){
            subjectDb="security";
        }else if(subject.equals("Crowd Management")){
            subjectDb="crowdManagement";
        }else if(subject.equals("Feed")){
            subjectDb="feed";
        }else if(subject.equals("Technicals")){
            subjectDb="technicals";
        }else if(subject.equals("Crowd Engaging Activties")){
            subjectDb="crowdEngagingActivties";
        }else if(subject.equals("Issues Faced")){
            subjectDb="issueFaced";
        }else if(subject.equals("Remarks")){
            subjectDb="remarks";
        }
        tv_subject.setText(subject_list[position]);
        db.open();
        Map<String, String> feedMap=db.getFeed(catName);

        if(feedMap!=null){
            String cmt=feedMap.get(subjectDb);
            if(cmt.length()>=10) {
                cmt = cmt.substring(0, 9);
                tv_comment.setText(cmt+"...");
            }else{
            tv_comment.setText(cmt);}
        }

        tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyDataSetChanged();
                FragmentTransaction ft=fm.beginTransaction();
                Fragment f=new CommentFragment();

                Bundle bundle = new Bundle();
                bundle.putString("subject", ""+subject_list[position]);
                bundle.putString("comment", tv_comment.getText().toString());
                bundle.putString("catName", catName);
                f.setArguments(bundle);
                ft.add(R.id.container, f);
                ft.addToBackStack(null);
                ft.commit();

            }
        });

        ib_editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyDataSetChanged();
                FragmentTransaction ft=fm.beginTransaction();
                Fragment f=new CommentFragment();

                Bundle bundle = new Bundle();
                bundle.putString("subject", ""+subject_list[position]);
                bundle.putString("comment", tv_comment.getText().toString());
                bundle.putString("catName", catName);
                f.setArguments(bundle);
                ft.add(R.id.container, f);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return v;

    }
}
