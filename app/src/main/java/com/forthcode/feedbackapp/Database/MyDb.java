package com.forthcode.feedbackapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Ajay on 29-03-2016.
 */
public class MyDb {

    SQLiteDatabase db;
    DbHelper dbHelper;

public MyDb(Context context){
    dbHelper=new DbHelper(context,"Feedback.db",null,1);
}

    public void open(){
        db=dbHelper.getWritableDatabase();
    }

    public void close(){}

    public void updateFeedback(String catName, String comment, String subject) {

        ContentValues cv = new ContentValues();
        cv.put(subject, comment);
        db.update("feedback", cv, "category=?",new String[] {catName});

    }

    public Map<String, String> getFeed(String catName) {

        Cursor c = db.query("feedback", null, "category=?",
                new String[] { catName }, null, null, null);

        if(c!=null && c.getCount()>0){
            Map<String, String> map = new HashMap<String, String>();
            try {
                c.moveToPosition(0);
                for (int i = 0; i < c.getColumnCount(); i++) {
                    map.put(c.getColumnName(i), c.getString(i));
                }
                return map;
            } finally {
                c.close();
            }
        }else{
            return null;
        }
    }

    public void addFeed(Map<String, String> feedMap) {

        try {
            int i = 0;
            db.beginTransaction();

//            db.delete("favorites", null, null);

                ContentValues favoritesValues = new ContentValues();
                Map<String, String> itemsMap = feedMap;
                Iterator<String> iterator = itemsMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    favoritesValues.put(key, itemsMap.get(key));
                }
                try {
                    db.insert("feedback", null, favoritesValues);
                } catch (Exception ex) {
//                    Log.v("Insert into database exception caught",
//                            ex.getMessage());
                }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    public void addUsers(ArrayList<Map<String, String>> userList) {
        try {
            int i = 0;
            db.beginTransaction();
            db.delete("user", null, null);
            while (i < userList.size()) {
                ContentValues itemValues = new ContentValues();
                Map<String, String> itemsMap = userList.get(i);
                Iterator<String> iterator = itemsMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    itemValues.put(key, itemsMap.get(key));
                }
                try {
                    db.insert("user", null, itemValues);
                } catch (Exception ex) {
//                    Log.v("Insert into database exception caught",
//                            ex.getMessage());
                }
                i++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    public String getPassword(String userName){
        String pwd;
        Cursor c=null;
        c = db.query("user", new String[]{"password"},"uName=?", new String[]{userName}, null, null, null, null);
        if(c!=null && c.getCount()>0){
                c.moveToPosition(0);
                pwd=c.getString(c.getColumnIndex("password"));
                c.moveToNext();
                c.close();
            return pwd;
        }else{
            return null;
        }

    }

    public String getComment(String catName, String subject){
        String comment;
        Cursor c=null;
        c = db.query("feedback", new String[]{subject},"category=?", new String[]{catName}, null, null, null, null);
        if(c!=null && c.getCount()>0){
            c.moveToPosition(0);
            comment=c.getString(c.getColumnIndex(subject));
            c.moveToNext();
            c.close();
            return comment;
        }else{
            return null;
        }

    }

    public void removeFeedback(String category) {

        db.delete("feedback", "category=?", new String[] { category });


    }

    public void deleteUser() {

        db.delete("feedback", null, null);


    }

}
