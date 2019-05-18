package com.waynesplanet.connor.recipebox20;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
//import android.os.Environment;
import android.transition.Explode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
//import java.util.ArrayList;
//import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//import static android.os.Environment.DIRECTORY_PICTURES;

public class MyRecipesActivity extends AppCompatActivity {

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
            case R.id.nav_home:
                final Intent home_intent = new Intent(MyRecipesActivity.this, MainActivity.class);
                startActivity(home_intent,
                        ActivityOptions.makeSceneTransitionAnimation(MyRecipesActivity.this).toBundle());
                return true;
            case R.id.nav_recipe_images:
                Toast.makeText(MyRecipesActivity.this, "Coming soon....", Toast.LENGTH_SHORT).show();
                return true;
            }
            return onNavigationItemSelected(menuItem);
        }
    };

    private void displayRecipeImages() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File imagesDir = contextWrapper.getDir("RecipeImages", Context.MODE_PRIVATE);
        Context context = this;
        File publicImages = context.getFilesDir();
        // final ArrayList<String> allJpegNames = new ArrayList<String>();
        // final ArrayList<String> allJpegFiles = new ArrayList<String>();

        if (publicImages != null && imagesDir != null) {
            // Get all filenames and directory-paths for public-images:
            // File[] jpegPublicImageFiles = publicImages.listFiles();
            // final String[] publicFilePathStrings = new
            // String[jpegPublicImageFiles.length];
            // final String[] publicFileNameStrings = new
            // String[jpegPublicImageFiles.length];

            // Get all filenames and directory-paths for private-images:
            File[] jpegImageFiles = imagesDir.listFiles();
            final String[] filePathStrings = new String[jpegImageFiles.length];
            final String[] fileNameStrings = new String[jpegImageFiles.length];

            // String[] allPaths = new String[publicFilePathStrings.length +
            // filePathStrings.length];
            // String[] allNames = new String[publicFileNameStrings.length +
            // fileNameStrings.length];

            // Loop through all potential image-files in RecipeImages
            for (int i = 0; i < jpegImageFiles.length; ++i) {
                filePathStrings[i] = jpegImageFiles[i].getAbsolutePath();
                fileNameStrings[i] = jpegImageFiles[i].getName();
                // allJpegNames.add(jpegImageFiles[i].getName());
                // allJpegFiles.add(jpegImageFiles[i].getAbsolutePath());
            }
            // for (int i = 0; i < jpegPublicImageFiles.length; ++i) {
            // allJpegNames.add(jpegPublicImageFiles[i].getName());
            // allJpegFiles.add(jpegPublicImageFiles[i].getAbsolutePath());
            // }
            // The objects and data structures which will be used down in "onCreate"
            GridView grid = findViewById(R.id.recipe_images_grid_view);
            // Pass String arrays to LazyAdapter Class
            LazyAdapter adapter = new LazyAdapter(MyRecipesActivity.this, filePathStrings, fileNameStrings);
            // LazyAdapter adapter = new LazyAdapter(MyRecipesActivity.this, allJpegFiles,
            // allJpegNames);
            // Set the LazyAdapter to the GridView
            grid.setAdapter(adapter);
            // Finally, Capture gridview item click,
            // and startup ViewImageActivity upon said click
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Intent internal_intent = new Intent(MyRecipesActivity.this, ViewImageActivity.class);
                    // Pass String arrays filePathStrings
                    internal_intent.putExtra("filepath", filePathStrings);
                    // internal_intent.putExtra("filepath", allJpegFiles);
                    // Pass String arrays fileNameStrings
                    internal_intent.putExtra("filename", fileNameStrings);
                    // internal_intent.putExtra("filename", allJpegNames);
                    // Pass click position
                    internal_intent.putExtra("position", position);
                    startActivity(internal_intent,
                            ActivityOptions.makeSceneTransitionAnimation(MyRecipesActivity.this).toBundle());
                }
            });
        } else {
            Toast.makeText(MyRecipesActivity.this, "imagesDir was null.... :(", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Code for the animations
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementEnterTransition(new Explode());
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_my_recipes);

        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        bottomNavView.setOnNavigationItemSelectedListener(navListener);

        displayRecipeImages();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(getClass().getSimpleName(), "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(getClass().getSimpleName(), "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(getClass().getSimpleName(), "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(getClass().getSimpleName(), "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(getClass().getSimpleName(), "onRestart");
    }
}