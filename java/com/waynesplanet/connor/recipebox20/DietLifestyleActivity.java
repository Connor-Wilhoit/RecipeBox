package com.waynesplanet.connor.recipebox20;

//import android.content.BroadcastReceiver;
//import android.content.IntentFilter;
import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
//import android.net.Network;
import android.transition.Explode;
import android.net.NetworkInfo;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ImageView;

import android.view.Window;
import android.view.View;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.util.Log;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;


import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;
//import java.io.BufferedReader;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.IOException;
//import java.util.Collections;
import java.util.Random;
//import java.util.stream.IntStream;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.widget.ImageViewCompat;
import androidx.appcompat.widget.Toolbar;
//import androidx.annotation.NonNull;


public class DietLifestyleActivity extends AppCompatActivity {

	public void toast(String text) { Toast.makeText(this, text, Toast.LENGTH_SHORT).show(); }
	public void toasty(String text) { Toast.makeText(this, text, Toast.LENGTH_LONG).show(); }

	private ProgressDialog progressDialog;
	//private ProgressDialog progressDialog2;
	private Bitmap bitmap = null;
	private Bitmap bitmap2 = null;
	private MaterialButton image_button;
	private MaterialButton image_button2;
	
	private int STATE = 0;

	public NetworkInfo[] netInfo() {
		ConnectivityManager connMgr = (ConnectivityManager)
		this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = connMgr.getAllNetworkInfo();
		return info;
	}

	public boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager)
		this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	private final BottomNavigationView.OnNavigationItemSelectedListener navListener
	= new BottomNavigationView.OnNavigationItemSelectedListener() {
	@Override public boolean onNavigationItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.nav_home:
				final Intent home_intent
				= new Intent(DietLifestyleActivity.this, MainActivity.class);
				startActivity
				(home_intent, ActivityOptions.makeSceneTransitionAnimation
				(DietLifestyleActivity.this).toBundle());
				return true;
		}
		return onNavigationItemSelected(menuItem);
	}};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
		getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getWindow().setSharedElementEnterTransition(new Explode());
		getWindow().setSharedElementExitTransition(new Explode());
		getWindow().setEnterTransition(new Explode());
		getWindow().setExitTransition(new Explode());

		setContentView(R.layout.activity_diet_lifestyle);

		final BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
		bottomNavView.setOnNavigationItemSelectedListener(navListener);

		final Toolbar toolbar = findViewById(R.id.action_bar);
		setSupportActionBar(toolbar);

		final TextInputEditText mDataText = findViewById(R.id.data_text);
		final TextInputEditText mRawData  = findViewById(R.id.raw_data);

		image_button = findViewById(R.id.imagebutton);
		image_button2 = findViewById(R.id.imagebutton2);

		setupImages(image_button, image_button2);


}	/*	end of onCreate()	*/

public void setupImages(MaterialButton image_button, MaterialButton image_button2) {
	final String waynesplanet_i	= "http://waynesplanet.com/images/";
	final String waynesplanet   = "http://waynesplanet.com/";
	final String[] images = {
			"nasa_blue.jpg",    "nasa_red.jpg",    "nasa_cool.jpg",
			"nasa_pillars.jpg", "the_matrixl.jpg", "white_rabbit_2.jpeg",
			"white_rabbit_terminal.jpeg",          "white_rabbit.jpeg"
	};
	final int min = 0;
	final int max = 8;
	final long seed =  System.currentTimeMillis();
	final int random = new Random(seed).nextInt((max - min) + 1) + min;
	final int randon = new Random(seed+1).nextInt((max-min) + 1) + min;
	final String test  = waynesplanet_i + images[randon];
	final String test2 = waynesplanet_i + images[random];

	// [1] Setup listener for the first MaterialButton:
	image_button.setOnClickListener(new View.OnClickListener() {
	@Override
	public void onClick(View v) {
		downloadImage(test);
	}});
	
	// [2] Setup listener for the second MaterialButton:
	image_button2.setOnClickListener(new View.OnClickListener() {
	@Override
	public void onClick(View v) {
		downloadImage(test2);
	}});
}

public void downloadImage(String urlStr) {
	progressDialog 	 = ProgressDialog.show(this, "", "Downloading Image From: " + urlStr);
	final String url = urlStr;
	new Thread() {
		InputStream in = null;
		public void run() {
			Message msg = Message.obtain();
			msg.what = 1;
			try {
				in       = openHttpConnection(url);
				bitmap   = BitmapFactory.decodeStream(in);
				Bundle b = new Bundle();
				b.putParcelable("bitmap", bitmap);
				msg.setData(b);
				in.close();
			} catch (IOException e) { e.printStackTrace(); }
			messageHandler.sendMessage(msg);
		}}.start();
}

@SuppressLint("HandlerLeak")
public Handler messageHandler = new Handler() {
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		if (STATE == 0) {
			ImageView img = findViewById(R.id.white_rabbit);
			img.setImageBitmap((Bitmap) (msg.getData().getParcelable("bitmap")));
			progressDialog.dismiss();
			STATE += 1;
		}
		else {
			ImageView img = findViewById(R.id.the_matrix);
			img.setImageBitmap((Bitmap) (msg.getData().getParcelable("bitmap")));
			progressDialog.dismiss();
			STATE -= 1;
		}
	}
};

public InputStream openHttpConnection(String urlStr) {
	InputStream in = null;
	int resCode = -1;
	try {
		URL url = new URL(urlStr);
		URLConnection urlConn = url.openConnection();
		if (!(urlConn instanceof HttpURLConnection)) { throw new IOException("URL is not an Http URL"); }
		HttpURLConnection httpConn = (HttpURLConnection) urlConn;
		httpConn.setAllowUserInteraction(false);
		httpConn.setInstanceFollowRedirects(true);
		httpConn.setRequestMethod("GET");
		httpConn.connect();
		resCode = httpConn.getResponseCode();
		if (resCode == HttpURLConnection.HTTP_OK) { in = httpConn.getInputStream(); }
	} catch (MalformedURLException e) { e.printStackTrace(); }
	catch (IOException e) { e.printStackTrace(); }
	return in;
	}
}