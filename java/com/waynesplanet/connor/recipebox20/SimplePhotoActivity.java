package com.waynesplanet.connor.recipebox20;

import android.Manifest;
//import android.annotation.SuppressLint;
import android.app.ActivityOptions;
//import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.transition.Explode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.content.Context;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import static android.os.Environment.DIRECTORY_PICTURES;

public class SimplePhotoActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SimplePhotoActivity";

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    final Intent home_intent
                            = new Intent(SimplePhotoActivity.this, MainActivity.class);
                    startActivity(home_intent,
                            ActivityOptions.makeSceneTransitionAnimation
                                    (SimplePhotoActivity.this).toBundle());
                    return true;
            }
            return onNavigationItemSelected(menuItem);
        }
    };

    public MaterialButton image_button;
    public AppCompatImageView image_view;
    public static File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Code for the animations
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementEnterTransition(new Explode());
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_simple_photo);

        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        bottomNavView.setOnNavigationItemSelectedListener(navListener);

        image_view = findViewById(R.id.imageview);
        image_button = findViewById(R.id.image_button);

        if (ContextCompat.checkSelfPermission
                (this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            image_button.setEnabled(false);
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }
        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture(v);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int request_code, String[] permission, int[] grant_results) {
        if (request_code == 0) {
            if (grant_results.length > 0 && grant_results[0] == PackageManager.PERMISSION_GRANTED
                    && grant_results[1] == PackageManager.PERMISSION_GRANTED) {
                image_button.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data) {
        if (request_code == 100 || result_code == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getName());
            image_view.setImageBitmap(bitmap);
        }
    }

    public void takePicture(View view) {
        final Intent picture_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //file = getOutputMediaFile();
        file = getOutputMediaFilev2();
        picture_intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(picture_intent, 100);
    }
    /*
              Here is how to get a File-object, that represents the internal-
              storage directory of your application:
                  File directory = context.getFilesDir();

              And then this would create a new file in said directory:
                  File file = new File(directory, filename);

       */
    private File  getOutputMediaFilev2() {
        //Context contextWrapper = new ContextWrapper(getApplicationContext());
       // File imageDir          = contextWrapper.getDir("RecipeImages", MODE_PRIVATE);
        Context context = this;
        File directory = context.getFilesDir();
        long milliseconds = System.currentTimeMillis();
        String imageFilename = milliseconds + "_recipe.jpg";

        //String file_name = "IMG_ " + time_stamp + ".jpg";
        //return new File(imageDir, file_name);
        File imageFile = new File(directory, imageFilename);
        return imageFile;
    }




    /*
        Below are copy-pasted functions from:
        https://developer.android.com/training/data-storage/files#WriteInternalStorage
        (use as needed!)
     */

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    public File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }
    public File getPrivateAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

}