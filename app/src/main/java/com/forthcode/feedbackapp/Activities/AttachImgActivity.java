package com.forthcode.feedbackapp.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.forthcode.feedbackapp.Database.MyDb;
import com.forthcode.feedbackapp.R;
import com.forthcode.feedbackapp.Utils.AndroidMultiPartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class AttachImgActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 10;

    private Button upload, pick, picbtnVideo, btngoback;
    LinearLayout videoAttechedLayout;
    Boolean resend = false;
    private ProgressDialog dialog;
    MultipartEntity entity;
    GridView gv;
    int count = 0;
    public ArrayList<String> imgUrlArray = new ArrayList<String>();
    Bundle b;
    private ProgressBar progressBar;
    public String videoPath = null;
    private TextView txtPercentage;
    private SharedPreferences mySharedpref;
    String userName, category;
    long totalSize = 0;
    String timeStamp;
    TextView noImage, tv_category;
    MyDb db;
    private final int REQUEST_EXTERNALSTORAGERESULT = 101;
    private final int REQUEST_GET_IMAGES = 100;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_img);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        db = new MyDb(this);
        mySharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        userName = mySharedpref.getString("uName", "0");
        category = mySharedpref.getString("category", "0");
        timeStamp = mySharedpref.getString("timeStamp", "0");
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        tv_category = (TextView) findViewById(R.id.tv_category);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noImage = (TextView) findViewById(R.id.noImage);
//        feedbackStatus = (TextView) findViewById(R.id.feedbackStatus);
        upload = (Button) findViewById(R.id.btnUpload);
        btngoback = (Button) findViewById(R.id.btngoback);
        pick = (Button) findViewById(R.id.btnPicture);
        picbtnVideo = (Button) findViewById(R.id.btnVideo);
        videoAttechedLayout = (LinearLayout) findViewById(R.id.videoAttechedLayout);
//        videoView=(VideoView)findViewById(R.id.videoView);
        gv = (GridView) findViewById(R.id.gridview);
        gv.setAdapter(new ImageAdapter(this));
        tv_category.setText(category);
        b = getIntent().getExtras();
        if (b != null) {

            ArrayList<String> ImgData = b.getStringArrayList("IMAGE");
            for (int i = 0; i < ImgData.size(); i++) {
                noImage.setVisibility(View.GONE);
                imgUrlArray.add(ImgData.get(i).toString());
            }
        } else {
            noImage.setText("No Image Selected");
            noImage.setVisibility(View.VISIBLE);
        }

        upload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (resend == true) {

                    if (imgUrlArray.size() == 0) {
                        new UploadFileToServer().execute();
                    } else {
                        new ImageUploadTask()
                                .execute(count + "", "" + timeStamp + "" + userName + "" + count + ".jpg");
                    }
                } else {

                    if (imgUrlArray.size() == 0 && videoPath == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AttachImgActivity.this);
                        builder.setTitle("Video and Images are not selected");
                        builder.setMessage("Would you like to continue without attachments?");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO
                                new UploadFileToServer().execute();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else if (videoPath != null && imgUrlArray.size() > 0) {

                        new ImageUploadTask()
                                .execute(count + "", "" + timeStamp + "" + userName + "" + count + ".jpg");

                    } else if (videoPath == null && imgUrlArray.size() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AttachImgActivity.this);
                        builder.setTitle("Video not selected");
                        builder.setMessage("Would you like to continue the feedback without video?");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO
//                            new UploadFileToServer().execute();
                                new ImageUploadTask()
                                        .execute(count + "", "" + timeStamp + "" + userName + "" + count + ".jpg");
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else if (imgUrlArray.size() == 0 && videoPath != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AttachImgActivity.this);
                        builder.setTitle("Images are not selected");
                        builder.setMessage("Would you like to continue the feedback without images?");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO
                                new UploadFileToServer().execute();
//                            new ImageUploadTask()
//                                    .execute(count + "", ""+timeStamp+""+userName+""+count + ".jpg");
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }

        });

        btngoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pick.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        ///method to get Images
                        picImages();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Toast.makeText(AttachImgActivity.this, "Your Permission is needed to get the images", Toast.LENGTH_LONG).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GET_IMAGES);
                    }
                } else {
                    picImages();
                }
            }
        });

        picbtnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        ///method to get Images
                        pickVideo();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Toast.makeText(AttachImgActivity.this, "Your Permission is needed to get the video from gallery", Toast.LENGTH_LONG).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNALSTORAGERESULT);
                    }
                } else {
                    pickVideo();
                }

            }
        });

    }

    public void picImages() {
        Intent picIntent = new Intent(AttachImgActivity.this, UploadActivity.class);
        startActivity(picIntent);
        finish();
    }

    public void pickVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_EXTERNALSTORAGERESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //call the orginal code of cursor to get the images
                pickVideo();
            } else {
                Toast.makeText(AttachImgActivity.this, "You denyed the permission to get video. Please allow the app to access your gallery", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode == REQUEST_GET_IMAGES) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //call the orginal code of cursor to get the images
                picImages();
            } else {
                Toast.makeText(AttachImgActivity.this, "You denyed the permission to get Images. Please allow the app to access your gallery", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    class ImageUploadTask extends AsyncTask<String, Void, String> {

        String sResponse = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(AttachImgActivity.this, "Sending image " + (1 + count),
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String url = "" + getString(R.string.videoUploadURL);
                int i = Integer.parseInt(params[0]);
                Bitmap bitmap = decodeFile(imgUrlArray.get(i));
                HttpClient httpClient = new DefaultHttpClient();
                org.apache.http.protocol.HttpContext localContext = new org.apache.http.protocol.BasicHttpContext();
                org.apache.http.client.methods.HttpPost httpPost = new org.apache.http.client.methods.HttpPost(url);
                entity = new MultipartEntity();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] data = bos.toByteArray();
                entity.addPart("timeStamp", new StringBody(timeStamp));
                entity.addPart("userid", new StringBody(userName));
                entity.addPart("category", new StringBody(category));
                ByteArrayBody fileBody = new ByteArrayBody(data,
                        "image/jpeg", params[1]);
                String imageName = fileBody.getFilename();
                entity.addPart("image", fileBody);
                entity.addPart("imageName", new StringBody("" + timeStamp + "" + userName + "" + count + ".jpg"));

                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost,
                        localContext);
                sResponse = org.apache.http.util.EntityUtils.getContentCharSet(response.getEntity());

                System.out.println("sResponse : " + sResponse);
            } catch (Exception e) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Log.e(e.getClass().getName(), e.getMessage(), e);

            }
            return sResponse;
        }

        @Override
        protected void onPostExecute(String sResponse) {
            String msg = "Network Errir, pleasc check your Internet Connection";
            String title = "Error";
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (sResponse != null) {
                    Toast.makeText(getApplicationContext(),
                            "image"+(count+1) + " Photo uploaded successfully",
                            Toast.LENGTH_SHORT).show();
                    count++;
                    resend=false;
                    if (count < imgUrlArray.size()) {
                        new ImageUploadTask().execute(count + "", "" + timeStamp + "" + userName + "" + count + ".jpg");
                    }
                    if (count == imgUrlArray.size()) {
                        new UploadFileToServer().execute();
                    }
                } else {
                    msg = "Connection time out, please check your internet connection";
                    title = "Network Connection Error :";
                    upload.setText("Resend");
                    resend = true;
                    showAlert(msg, title);
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }

    public Bitmap decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
        return bitmap;
    }

    private class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imgUrlArray.size();
        }

        public Object getItem(int position) {
            return null;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, initialize some
                // attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85,
                        Gravity.CENTER));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(1, 1, 1, 1);

            } else {
                imageView = (ImageView) convertView;
            }

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            imageView.setImageBitmap(BitmapFactory.decodeFile(imgUrlArray.get(position), options));
            return imageView;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        AttachImgActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Uri selectedVideoUri = data.getData();
                videoPath = getPath(AttachImgActivity.this, selectedVideoUri);
                if (videoPath != null) {
                    videoAttechedLayout.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            org.apache.http.client.methods.HttpPost httppost = new org.apache.http.client.methods.HttpPost("" + getString(R.string.baseURL));

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                /*File imgsourceFile;
                for(int k=0;k<imgUrlArray.size();k++){
                    imgsourceFile = new File(imgUrlArray.get(k));
                    entity.addPart("image"+k, new FileBody(imgsourceFile));
                }*/

                if (videoPath != null) {
                    File sourceFile = new File(videoPath);
                    // Adding file data to http body
                    FileBody videoFile = new FileBody(sourceFile);
                    entity.addPart("video", videoFile);
                    entity.addPart("videoName", new StringBody(videoFile.getFilename()));

                }
                db.open();
                Map<String, String> feedMap = db.getFeed(category);
                db.close();


                Iterator<String> iterator = feedMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    entity.addPart(key,
                            new StringBody(feedMap.get(key)));
                }


                // Extra parameters if you want to pass to server
                entity.addPart("timeStamp", new StringBody(timeStamp));
                entity.addPart("userid", new StringBody(userName));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = org.apache.http.util.EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
//            Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog

            String msg = "Network error, please check your Internet Connection ";
            String title = "Network Connection Error";
            try {
                JSONObject   json = new JSONObject(result);
                if (Integer.parseInt(json.getString("success")) == 1) {
                    msg = "Your feedback was successfully sent";
                    title = "Feedback Sent";
//                    feedbackStatus.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.GONE);
                    btngoback.setVisibility(View.VISIBLE);
                    videoAttechedLayout.setVisibility(View.GONE);
                    gv.setVisibility(View.GONE);
                    noImage.setText("Feedback Sent");
                    resend=false;
                } else {
                    msg = "Connection time out, please check your internet connection";
                    title = "Network Connection Error :";
                    upload.setText("Resend");
                    resend = true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            showAlert(msg, title);

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
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
                final String[] selectionArgs = new String[]{
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
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
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

/*    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", videoPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        videoPath = savedInstanceState.getParcelable("file_uri");
    }*/
}