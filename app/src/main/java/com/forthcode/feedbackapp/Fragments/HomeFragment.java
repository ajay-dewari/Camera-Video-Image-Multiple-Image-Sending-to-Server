package com.forthcode.feedbackapp.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forthcode.feedbackapp.Adapters.PagerAdapter;
import com.forthcode.feedbackapp.R;
import com.forthcode.feedbackapp.Utils.SlidingTabLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    SlidingTabLayout mSlidingTabLayout;
    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;
    SharedPreferences mySharedpref;
    String[] catName={"Corporates", "Concerts","Private Affair", "Road Show"};
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        mySharedpref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String category = mySharedpref.getString("category", "0");
        int tabPosition=0;
        for(int i=0; i<catName.length;i++){
            if(category.equals(catName[i])){
                tabPosition= i;
            }
        }

        mPagerAdapter=new PagerAdapter(getChildFragmentManager());
        mViewPager= (ViewPager) view.findViewById(R.id.vpPager);
        mSlidingTabLayout= (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mViewPager.setAdapter(mPagerAdapter);
        mSlidingTabLayout.setCustomTabView(R.layout.pager_item, R.id.textView1);
        mSlidingTabLayout.setSelectedIndicatorColors(R.color.tabIndicator);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {//change the color of the tab indcator

            @Override
            public int getIndicatorColor(int position) {
                // TODO Auto-generated method stub
                return getResources().getColor(R.color.tabIndicator);
            }
        });

        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(tabPosition);
return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }    super.onActivityResult(requestCode, resultCode, data);
    }

}
