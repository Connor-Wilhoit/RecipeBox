package com.waynesplanet.connor.recipebox20;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {


    private final BottomNavigationView.OnNavigationItemSelectedListener navListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch(menuItem.getItemId())
            {
                case R.id.nav_home:
                    Toast.makeText(MainActivity.this,
							"We're already home silly! ;)", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.nav_add_new_recipe:
                    final Intent intent = new Intent(MainActivity.this,
							AddNewRecipeActivity.class);
                    startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation(
                                    MainActivity.this).toBundle());
                    return true;
            }
            return onNavigationItemSelected(menuItem);
        }
    };

	private void myDietImageButtonClicked() {
		final Intent diet_intent = new Intent(MainActivity.this, DietLifestyleActivity.class);
		startActivity(diet_intent,
                ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
	}
	private View.OnClickListener dietOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) { myDietImageButtonClicked(); }
	};
    private void myAddNewRecipeImageButtonClicked() {
        final Intent addrec_intent = new Intent(MainActivity.this, AddNewRecipeActivity.class);
        startActivity(addrec_intent,
                ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
    }
    private View.OnClickListener addrecOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) { myAddNewRecipeImageButtonClicked(); }
    };

    private void myRecipesImageButtonClicked() {
        final Intent recipes_intent = new Intent(MainActivity.this, MyRecipesActivity.class);
        startActivity(recipes_intent,
                ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
    }
    private View.OnClickListener myRecipesOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) { myRecipesImageButtonClicked(); }
    };

    private void mySimplePhotoButtonClicked() {
        final Intent simple_intent = new Intent(MainActivity.this, SimplePhotoActivity.class);
        startActivity(simple_intent,
                ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
    }
    private View.OnClickListener mySimpleListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) { mySimplePhotoButtonClicked(); }
    };

	private void myGroceryListImageButtonClicked() {
		final Intent grocery_intent = new Intent(MainActivity.this, GroceryListActivity.class);
		startActivity(grocery_intent,
                ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
	}
	private View.OnClickListener myGroceryListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) { myGroceryListImageButtonClicked(); }
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i(getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);
        // Code for the animations
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        bottomNavView.setOnNavigationItemSelectedListener(navListener);

        AppCompatImageButton addrec = findViewById(R.id.new_recipe_acib);
        addrec.setOnClickListener(addrecOnClickListener);

        AppCompatImageButton recipes = findViewById(R.id.my_recipes_acib);
        recipes.setOnClickListener(myRecipesOnClickListener);

        AppCompatImageButton simples = findViewById(R.id.simple_photo_acib);
        simples.setOnClickListener(mySimpleListener);

		AppCompatImageButton diets	= findViewById(R.id.diet_lifestyle_acib);
		diets.setOnClickListener(dietOnClickListener);

		AppCompatImageButton groceries	= findViewById(R.id.grocery_list_acib);
		groceries.setOnClickListener(myGroceryListener);
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