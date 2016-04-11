package com.forthcode.feedbackapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ajay on 29-03-2016.
 */
public class DbHelper extends SQLiteOpenHelper{
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("Create table feedback(_id integer primary key,"+"category text NOT NULL DEFAULT '',city text NOT NULL DEFAULT '', dates text NOT NULL DEFAULT '', client text NOT NULL DEFAULT '', dnaRepresentative text NOT NULL DEFAULT '', venue text NOT NULL DEFAULT '', venueLiason text NOT NULL DEFAULT '', crowdAttended text NOT NULL DEFAULT '', FnB text NOT NULL DEFAULT '', setup text NOT NULL DEFAULT '', emcee text NOT NULL DEFAULT '', housekeeping text NOT NULL DEFAULT '', security text NOT NULL DEFAULT '', crowdManagement text NOT NULL DEFAULT '', feed text NOT NULL DEFAULT '', technicals text NOT NULL DEFAULT '', crowdEngagingActivties text NOT NULL DEFAULT '', issueFaced text NOT NULL DEFAULT '', remarks text NOT NULL DEFAULT '')");
        db.execSQL("Create table user(_id integer primary key,"+"uName text, password text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
