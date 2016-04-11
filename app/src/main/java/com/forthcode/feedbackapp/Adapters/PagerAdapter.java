package com.forthcode.feedbackapp.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.forthcode.feedbackapp.Fragments.QuestionListFragment;

/**
 * Created by Ajay on 27-03-2016.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    String[] catName={"Corporates", "Concerts","Private Affair", "Road Show"};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment= new QuestionListFragment();
        Bundle args = new Bundle();
        args.putString("catName", catName[position]);
        fragment.setArguments(args);
    return fragment;
    }

    @Override
    public int getCount() {
        return catName.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return catName[position];
    }
}
