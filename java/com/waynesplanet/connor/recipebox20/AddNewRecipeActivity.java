package com.waynesplanet.connor.recipebox20;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.MenuItem;
//import android.view.View;
import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;

public class AddNewRecipeActivity extends AppCompatActivity {

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
            case R.id.nav_home:
                final Intent home_intent = new Intent(AddNewRecipeActivity.this, MainActivity.class);
                startActivity(home_intent,
                        ActivityOptions.makeSceneTransitionAnimation(AddNewRecipeActivity.this).toBundle());
                return true;
            case R.id.add_recipe_menu_photo:
                final Intent add_recipe_intent = new Intent(AddNewRecipeActivity.this, TakePhotoActivity.class);
                startActivity(add_recipe_intent,
                        ActivityOptions.makeSceneTransitionAnimation(AddNewRecipeActivity.this).toBundle());
                return true;
            case R.id.nav_save_recipe:
                saveRecipe();
                return true;
            }
            return onNavigationItemSelected(menuItem);
        }
    };

    //String category;

    public void saveRecipe() {
        // Connect the EditText & TextView objects to their UI objects:
        TextInputEditText recipeLabelEditText = findViewById(R.id.recipe_label_tiet);
        TextInputEditText ingredientsEditText = findViewById(R.id.ingredients_tiet);
        TextInputEditText instructionsEditText = findViewById(R.id.instructions_tiet);
        TextInputEditText categoryEditText = findViewById(R.id.category_tiet);
        TextInputEditText notesEditText = findViewById(R.id.notes_tiet);

        // Now get the user-input text from all of the EditText objects, store the data
        // into the normal String objects:
        String recipe = Objects.requireNonNull(recipeLabelEditText.getText()).toString();
        String ingredients = Objects.requireNonNull(ingredientsEditText.getText()).toString();
        String instructions = Objects.requireNonNull(instructionsEditText.getText()).toString();
        String category = Objects.requireNonNull(categoryEditText.getText()).toString();
        String notes = Objects.requireNonNull(notesEditText.getText()).toString();

        // These next String objects are for formatting of the data when writing to the
        // file:
        // (put these into a little array once this works)
        String ingredientsLine = "Ingredients: ";
        String recipeLine = "Recipe Label: ";
        String instructionsLine = "Instructions: ";
        String categoryLine = "Category: ";
        String notesLine = "Notes: ";
        String newline = "\n"; // use this after each of the 'fields' is written to the file

        String filename = recipe + ".txt";
        Context context = this;
        File directory = context.getFilesDir();
        File file = new File(directory, filename);
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            /*
             * I want each field of the recipe description to be separated by a newline, so
             * I may need to append a newline to each String object...For now, I'm going to
             * just sequentially call the .write() method for each of the fields strings, &
             * see what that ends up looking like:
             *
             */
            // Field #1:
            outputStream.write(ingredientsLine.getBytes());
            outputStream.write(ingredients.getBytes());
            outputStream.write(newline.getBytes());

            // Field #2:
            outputStream.write(recipeLine.getBytes());
            outputStream.write(recipe.getBytes());
            outputStream.write(newline.getBytes());

            // Field #3:
            outputStream.write(instructionsLine.getBytes());
            outputStream.write(instructions.getBytes());
            outputStream.write(newline.getBytes());

            // Field #4:
            outputStream.write(categoryLine.getBytes());
            outputStream.write(category.getBytes());
            outputStream.write(newline.getBytes());

            // Field #5:
            outputStream.write(notesLine.getBytes());
            outputStream.write(notes.getBytes());
            outputStream.write(newline.getBytes());

            outputStream.close();
            Toast.makeText(AddNewRecipeActivity.this, "File Saved: " + file, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(AddNewRecipeActivity.this, "File Error!: " + file + " Failed to save", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /*
    private Spinner spinner;

    public void addItemsToSpinner() {
        spinner = findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.recipe_category_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void addListenerToSpinner() {
        spinner = findViewById(R.id.category_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // String spinnerItemSelected = parent.getItemAtPosition(pos).toString();
                category = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                category = "Other";
            }
        });
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Code for the animations
        getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_add_new_recipe);

        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        bottomNavView.setOnNavigationItemSelectedListener(navListener);

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