package com.forthcode.feedbackapp.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forthcode.feedbackapp.Activities.HomeActivity;
import com.forthcode.feedbackapp.Database.MyDb;
import com.forthcode.feedbackapp.R;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment {

    MyDb db;
    String subject, comment,subjectDb, catName;
    TextView tv_subject;
    EditText et_comment;
    Button btn_done;
    private SharedPreferences mySharedpref;
    LinearLayout commentLayout;

    public CommentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_comment, container, false);
        mySharedpref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        db=new MyDb(getActivity());
        et_comment= (EditText) view.findViewById(R.id.et_comment);
        tv_subject=(TextView) view.findViewById(R.id.tv_subject);
        commentLayout=(LinearLayout)view.findViewById(R.id.commentLayout);
        btn_done=(Button)view.findViewById(R.id.btn_done);
        et_comment.requestFocus();

        Bundle b=this.getArguments();
        subject=b.getString("subject","");
        catName=b.getString("catName","");
        SharedPreferences.Editor et = mySharedpref.edit();
        et.putString("category", catName);
        et.commit();

        tv_subject.setText(""+subject);


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

        db.open();
        comment=db.getComment(catName,subjectDb);
        db.close();
        if(comment!=""&& comment!=null){
            if(comment.equals(" ")){
                et_comment.setText("");
            }else {
                et_comment.setText("" + comment);
            }
        }

        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comment=et_comment.getText().toString();
                if(comment.length()==0){
                    comment=" ";
                }
                    Map<String, String> feedMap;
                    db.open();
                    feedMap=db.getFeed(catName);
                    if(feedMap==null){
                        feedMap=new HashMap<String, String>();
                        feedMap.put("category",catName);
                        feedMap.put(subjectDb,comment);
                        db.addFeed(feedMap);
                    }else{
                        db.updateFeedback(catName, comment, subjectDb);
                    }
                    db.close();

                Intent in=new Intent(getActivity(), HomeActivity.class);
                startActivity(in);
                getActivity().finish();

            }
        });

    return view;
    }

    protected boolean bringKeyboard(EditText view) {
        if (view == null) {
            return false;
        }
        try {
            // Depending if edittext has some pre-filled values you can decide whether to bring up soft keyboard or not
            String value = view.getText().toString();
            if (value == null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return true;
            }
        } catch (Exception e) {
//            Log.e(TAG, "decideFocus. Exception", e);
        }
        return false;
    }

}
