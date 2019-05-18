package com.waynesplanet.connor.recipebox20;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.BitmapFactory.Options;
//import android.graphics.Canvas;
//import android.graphics.Paint;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;

public class ViewImageActivity extends AppCompatActivity {

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
            case R.id.nav_home:
                final Intent home_intent = new Intent(ViewImageActivity.this, MainActivity.class);
                startActivity(home_intent,
                        ActivityOptions.makeSceneTransitionAnimation(ViewImageActivity.this).toBundle());
                return true;
            case R.id.nav_modify_recipe_name:
                updateandsave();
                Toast.makeText(ViewImageActivity.this, "Check ya fields!", Toast.LENGTH_SHORT).show();
                return true;
            }
            return onNavigationItemSelected(menuItem);
        }
    };

    public void updateandsave() {
        Intent intent = getIntent();
        int position = Objects.requireNonNull(intent.getExtras()).getInt("position");
        String[] filepaths = intent.getStringArrayExtra("filepath");
        String[] filenames = intent.getStringArrayExtra("filename");
        String cname = filenames[position];
        String cpath = filepaths[position];
        Context context = this;
        File textdir = context.getFilesDir();
        String suffix = ".jpg";
        final int textstr = cname.lastIndexOf(suffix);
        String textfile_name = cname.substring(0, textstr);
        textfile_name = textfile_name.concat(".txt");

        TextInputEditText text = findViewById(R.id.imagetext_tiet);
        TextInputEditText label = findViewById(R.id.label_tiet);
        TextInputEditText notes = findViewById(R.id.notes_tiet);
        TextInputEditText instructions = findViewById(R.id.instructions_tiet);
        TextInputEditText ingredients = findViewById(R.id.ingredients_tiet);
        TextInputEditText category = findViewById(R.id.category_tiet);
        String updatedText = Objects.requireNonNull(text.getText()).toString();
        String updatedLabel = Objects.requireNonNull(label.getText()).toString();
        String updatedNotes = Objects.requireNonNull(notes.getText()).toString();
        String updatedInstructions = Objects.requireNonNull(instructions.getText()).toString();
        String updatedIngredients = Objects.requireNonNull(ingredients.getText()).toString();
        String updatedCategory = Objects.requireNonNull(category.getText()).toString();

        //String[] search_strings = { "Ingredients: ", "Recipe Label: ", "Instructions: ", "Category: ", "Notes: " };

        ArrayList<String> updated_fields = new ArrayList<String>();
        updated_fields.add(updatedIngredients);
        updated_fields.add(updatedLabel);
        updated_fields.add(updatedInstructions);
        updated_fields.add(updatedCategory);
        updated_fields.add(updatedNotes);

        File tfile = new File(textdir, textfile_name);

        try {
            FileWriter writer = new FileWriter(tfile);
            for (int i = 0; i < updated_fields.size(); i++) {
                writer.append(updated_fields.get(i));
                writer.append(System.getProperty("line.separator"));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setuptextfile() {
        Intent intent = getIntent();
        int position = Objects.requireNonNull(intent.getExtras()).getInt("position");
        String[] filepaths = intent.getStringArrayExtra("filepath");
        String[] filenames = intent.getStringArrayExtra("filename");
        String cname = filenames[position];
        String cpath = filepaths[position];
        Context context = this;
        File textdir = context.getFilesDir();
        String suffix = ".jpg";
        final int textstr = cname.lastIndexOf(suffix);
        String textfile_name = cname.substring(0, textstr);
        textfile_name = textfile_name.concat(".txt");

        File tfile = new File(textdir, textfile_name);
        TextInputEditText text = findViewById(R.id.imagetext_tiet);
        TextInputEditText label = findViewById(R.id.label_tiet);
        TextInputEditText notes = findViewById(R.id.notes_tiet);
        TextInputEditText instructions = findViewById(R.id.instructions_tiet);
        TextInputEditText ingredients = findViewById(R.id.ingredients_tiet);
        TextInputEditText category = findViewById(R.id.category_tiet);
        // String[] search_strings = { "Ingredients: ", "Recipe Label: ", "Instructions: ", "Category: ", "Notes: " };

        //StringBuilder sb = new StringBuilder();
        ArrayList<String> recipe_fields = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(tfile))) {
            String line;
            while ((line = br.readLine()) != null) {
                //sb.append(line);
                //sb.append(System.getProperty("line.separator"));
                recipe_fields.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //sb.append(System.getProperty("line.separator"));
        try {
            ingredients.setText(recipe_fields.get(0));
            label.setText(recipe_fields.get(1));
            instructions.setText(recipe_fields.get(2));
            category.setText(recipe_fields.get(3));
            notes.setText(recipe_fields.get(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBitmap(String imageFilePath, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(imageFilePath);
    }
    

    private void initforeal() {
        Intent intent = getIntent();
        int position = Objects.requireNonNull(intent.getExtras()).getInt("position");
        String[] filepaths = intent.getStringArrayExtra("filepath");
        String[] filenames = intent.getStringArrayExtra("filename");
        ImageView imageView = findViewById(R.id.full_image_view);
        TextInputEditText text = findViewById(R.id.imagetext_tiet);
        text.setText(filenames[position]);
        loadBitmap(filepaths[position], imageView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Code for the animations
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSharedElementEnterTransition(new Explode());
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_glide_view_image);

        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        bottomNavView.setOnNavigationItemSelectedListener(navListener);

        initforeal();
        setuptextfile();
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