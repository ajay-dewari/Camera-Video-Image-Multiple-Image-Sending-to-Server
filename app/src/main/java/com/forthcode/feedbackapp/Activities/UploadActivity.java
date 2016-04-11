package com.forthcode.feedbackapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.forthcode.feedbackapp.R;

import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity {

    public int count;
    public final int REQUEST_EXTERNALSTORAGERESULT=201;
    public Bitmap[] thumbnails;
    public boolean[] thumbnailsselection;
    public String[] arrPath;
    public ImageAdapter imageAdapter;
    public static final int PICK_FROM_CAMERA = 1;
    ArrayList<String> IPath = new ArrayList<String>();
    public static Uri uri;
    Cursor imagecursor;
    GridView imagegrid;
    ProgressBar progressBar;
    final String[] columns = { MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID };
//    final String orderBy = MediaStore.Images.Media.DEFAULT_SORT_ORDER;
    final String orderBy =MediaStore.Images.Media.DATE_ADDED + " DESC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        new MyAsyncTask().execute();
       /* new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                imagecursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                        null, orderBy);

                int image_column_index = imagecursor
                        .getColumnIndex(MediaStore.Images.Media._ID);
                UploadActivity.this.count = imagecursor.getCount();
                UploadActivity.this.thumbnails = new Bitmap[UploadActivity.this.count];
                UploadActivity.this.arrPath = new String[UploadActivity.this.count];
                UploadActivity.this.thumbnailsselection = new boolean[UploadActivity.this.count];
                for (int i = 0; i < UploadActivity.this.count; i++) {
                    imagecursor.moveToPosition(i);
                    int id = imagecursor.getInt(image_column_index);
                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);
                    thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
                            getApplicationContext().getContentResolver(), id,
                            MediaStore.Images.Thumbnails.MICRO_KIND, null);
                    arrPath[i] = imagecursor.getString(dataColumnIndex);

                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
                        imageAdapter = new ImageAdapter();
                        imagegrid.setAdapter(imageAdapter);
                        imagecursor.close();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
*/

        final Button uploadBtn = (Button) findViewById(R.id.uploadDONE);
        uploadBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectImages = arrPath[i];
                        IPath.add(selectImages);
                    }
                }

                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please select at least one image",
                            Toast.LENGTH_LONG).show();
                }else if(cnt==1){
                    Toast.makeText(getApplicationContext(),
                            "You've selected Total " + cnt + " image.",
                            Toast.LENGTH_LONG).show();
                    Log.d("SelectedImages", selectImages);

            Intent intentMessage = new Intent(UploadActivity.this,
                            AttachImgActivity.class);
                    intentMessage.putStringArrayListExtra("IMAGE", IPath);
                    startActivity(intentMessage);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "You've selected Total " + cnt + " images.",
                            Toast.LENGTH_LONG).show();
                    Log.d("SelectedImages", selectImages);

                    // Intent intentMessage = new Intent();
                    // intentMessage.putExtra("IMAGE", IPath);
                    // setResult(Activity.RESULT_OK, intentMessage);
                    // finish();

                    Intent intentMessage = new Intent(UploadActivity.this,
                            AttachImgActivity.class);
                    intentMessage.putStringArrayListExtra("IMAGE", IPath);
                    startActivity(intentMessage);
                    finish();
                }
            }
        });
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.galleryitem, null);
                holder.imageview = (ImageView) convertView
                        .findViewById(R.id.thumbImage);
                holder.checkbox = (CheckBox) convertView
                        .findViewById(R.id.itemCheckBox);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            holder.checkbox.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });

            // holder.imageview.setOnClickListener(new OnClickListener() {
            //
            // public void onClick(View v) {
            // // TODO Auto-generated method stub
            // int id = v.getId();
            // Intent intent = new Intent();
            // intent.setAction(Intent.ACTION_VIEW);
            // intent.setDataAndType(Uri.parse("file://" + arrPath[id]),
            // "image/*");
            // startActivity(intent);
            // }
            // });

            holder.imageview.setImageBitmap(thumbnails[position]);
            holder.checkbox.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub

            imagecursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                    null, orderBy);

            int image_column_index = imagecursor
                    .getColumnIndex(MediaStore.Images.Media._ID);
            UploadActivity.this.count = imagecursor.getCount();
            UploadActivity.this.thumbnails = new Bitmap[UploadActivity.this.count];
            UploadActivity.this.arrPath = new String[UploadActivity.this.count];
            UploadActivity.this.thumbnailsselection = new boolean[UploadActivity.this.count];
            for (int i = 0; i < UploadActivity.this.count; i++) {
                imagecursor.moveToPosition(i);
                int id = imagecursor.getInt(image_column_index);
                int dataColumnIndex = imagecursor
                        .getColumnIndex(MediaStore.Images.Media.DATA);
                thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
                        getApplicationContext().getContentResolver(), id,
                        MediaStore.Images.Thumbnails.MICRO_KIND, null);
                arrPath[i] = imagecursor.getString(dataColumnIndex);
                publishProgress(i);
            }

            return null;
        }

        protected void onPostExecute(Void result){
//            Toast.makeText(UploadActivity.this,""+result, Toast.LENGTH_LONG).show();
            imagecursor.close();
            progressBar.setVisibility(View.GONE);
        }

        protected void onProgressUpdate(Integer... progress){
            imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
            imageAdapter = new ImageAdapter();
            imagegrid.setAdapter(imageAdapter);

        }
    }


    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(UploadActivity.this, AttachImgActivity.class);
        UploadActivity.this.finish();
        startActivity(i);
        super.onBackPressed();
    }
}
