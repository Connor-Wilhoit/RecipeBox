package com.waynesplanet.connor.recipebox20;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.content.Intent;
import android.transition.Explode;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class GroceryListActivity extends AppCompatActivity {


	private final BottomNavigationView.OnNavigationItemSelectedListener navListener
        = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            switch(menuItem.getItemId())
            {
                case R.id.nav_home:
                    final Intent home_intent
                        = new Intent(GroceryListActivity.this, MainActivity.class);
                    startActivity(home_intent,
                            ActivityOptions.makeSceneTransitionAnimation
                                    (GroceryListActivity.this).toBundle());
                    return true;
            }
            return onNavigationItemSelected(menuItem);
        }};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // Code for the animations
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementEnterTransition(new Explode());
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

		setContentView(R.layout.activity_grocery_list);

        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

		BottomNavigationView bottomNavView	= findViewById(R.id.bottom_nav_view);
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
