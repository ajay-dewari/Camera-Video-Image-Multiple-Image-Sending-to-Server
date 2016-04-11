package com.forthcode.feedbackapp.Fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.forthcode.feedbackapp.Activities.AttachImgActivity;
import com.forthcode.feedbackapp.Activities.HomeActivity;
import com.forthcode.feedbackapp.Adapters.ListAdapter;
import com.forthcode.feedbackapp.Database.MyDb;
import com.forthcode.feedbackapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionListFragment extends Fragment {

    private static final int CAMERA_REQUEST = 100;
    private SharedPreferences mySharedpref;
    ListView questionList;
    ListAdapter adapter;
    Button btnClear;
    String catName;
    ImageView iv_camera, iv_video;
    LinearLayout attach_layout;
    File output;
    String photoPath = "";
    MyDb db;
    private Uri fileUri;
    ArrayList<String> mMedia = new ArrayList<String>();
    boolean feedBol = true;
    String[] subject_list = {"City", "Dates", "Client", "DNA Represntatives", "Venue", "Venue Liason", "Crowd Attended", "F& B", "Setup", "Emcee", "Housekeeping", "Security", "Crowd Management", "Feed", "Technicals", "Crowd Engaging Activties", "Issues Faced", "Remarks"};

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int REQUEST_EXTERNALSTORAGERESULT=201;
    private static final int REQUEST_CAMERARESULT=201;

    public QuestionListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle b = this.getArguments();
        catName = b.getString("catName");
        db = new MyDb(getActivity());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questionlist, container, false);
        mySharedpref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        attach_layout = (LinearLayout) view.findViewById(R.id.attach_layout);
        iv_camera = (ImageView) view.findViewById(R.id.iv_camera);
        iv_video=(ImageView) view.findViewById(R.id.iv_video);
        btnClear= (Button) view.findViewById(R.id.btnClear);
        questionList = (ListView) view.findViewById(R.id.questionList);
        adapter = new ListAdapter(getActivity(), getActivity().getSupportFragmentManager(), catName);
        questionList.setAdapter(adapter);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.open();
                Map<String, String> feedMap = db.getFeed(catName);
                if (feedMap != null) {
                    db.removeFeedback(catName);
                    questionList.setAdapter(adapter);
                }
                db.close();

            }
        });

        attach_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long tsLong = System.currentTimeMillis() / 1000;
                String timeStamp = tsLong.toString();

                SharedPreferences.Editor et = mySharedpref.edit();
                et.putString("category", catName);
                et.putString("timeStamp", timeStamp);
                et.commit();

                db.open();
                Map<String, String> feedMap = db.getFeed(catName);
                db.close();

                if (feedMap != null) {
                    Intent intent = new Intent(getActivity(), AttachImgActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Please fill out the some fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(getActivity().checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                        ///method to get Images
                        getImages();
                    }else{
                        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                            Toast.makeText(getActivity(),"Your Permission is needed to get access the camera and save your image",Toast.LENGTH_LONG).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, REQUEST_EXTERNALSTORAGERESULT);
                    }
                }else{
                    getImages();
                }

            }
        });

        iv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(getActivity().checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                        ///method to get Images
                        takeVideo();
                    }else{
                        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                            Toast.makeText(getActivity(),"Your Permission is needed to get access the camera",Toast.LENGTH_LONG).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, REQUEST_CAMERARESULT);
                    }
                }else{
                    takeVideo();
                }
            }
        });

        return view;
    }

    public void takeVideo(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        getParentFragment().startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_EXTERNALSTORAGERESULT){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //call the orginal code of cursor to get the images
                getImages();
            }else{
                Toast.makeText(getActivity(),"Your Permission is needed to access the camera",Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if(requestCode==REQUEST_CAMERARESULT){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //call the orginal code of cursor to get the images
                takeVideo();
            }else{
                Toast.makeText(getActivity(),"Your Permission is needed to access the camera",Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    private void getImages() {
        String fileName = System.currentTimeMillis()+".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        fileUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }


    private void galleryAddPic(String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode==CAMERA_CAPTURE_VIDEO_REQUEST_CODE){
            if (resultCode == HomeActivity.RESULT_OK) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        try {
                            String imageTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                                    Locale.getDefault()).format(new Date());
                            AssetFileDescriptor videoAsset = getActivity().getContentResolver().openAssetFileDescriptor(intent.getData(), "r");
                            FileInputStream fis = videoAsset.createInputStream();
                            File videoFile = new File(Environment.getExternalStorageDirectory(), imageTimeStamp+".mp4");
                            FileOutputStream fos = new FileOutputStream(videoFile);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                            fis.close();
                            fos.close();
                        } catch (IOException e) {
                            // TODO: handle error
                        }
                    }
                }).start();
            }
        }else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == HomeActivity.RESULT_OK) {
                /*Uri photouri=intent.getData();
                String PhotoPath=(getRealPathFromURI(photouri));
                galleryAddPic(fileUri.getPath());*/
            }
//                saveImage();
            } else if (resultCode == HomeActivity.RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getActivity(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getActivity(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

    }

    @SuppressWarnings("deprecation")
    private String getPath(Uri selectedImaeUri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = getActivity().getContentResolver().query(selectedImaeUri, projection, null, null,
                null);

        if (cursor != null)
        {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            return cursor.getString(columnIndex);
        }

        return selectedImaeUri.getPath();
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        questionList.setAdapter(adapter);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


}