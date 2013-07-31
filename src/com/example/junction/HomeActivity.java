package com.example.junction;

import java.util.Locale;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("NewApi")
public class HomeActivity extends Activity implements OnClickListener {
	public Button searchActivityButton, cameraActivityButton, newLocationActivityButton, loginButton, registerButton;
	public static SQLiteDatabase junctionDB;
	public static String username;
	public static SharedPreferences sharedPrefs;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		RelativeLayout homeLayout = (RelativeLayout) findViewById(R.id.homelayout);
		ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		searchActivityButton = (Button) findViewById(R.id.searchActivityButton);
		searchActivityButton.setOnClickListener(this);
		cameraActivityButton = (Button) findViewById(R.id.cameraActivityButton);
		cameraActivityButton.setOnClickListener(this);
		newLocationActivityButton = (Button) findViewById(R.id.newLocationActivityButton);
		newLocationActivityButton.setOnClickListener(this);
		loginButton = (Button) findViewById(R.id.homeLoginButton);
		loginButton.setOnClickListener(this);
		registerButton = (Button) findViewById(R.id.homeRegisterButton);
		
		junctionDB = openOrCreateDatabase("junction", SQLiteDatabase.CREATE_IF_NECESSARY, null);
		junctionDB.setLocale(Locale.getDefault());
		
		String subjectsTableSql = "DROP TABLE IF EXISTS 'subjects'";
//		junctionDB.execSQL(subjectsTableSql);
//		subjectsTableSql = "DROP TABLE IF EXISTS 'users'";
//		junctionDB.execSQL(subjectsTableSql);
//		subjectsTableSql = "DROP TABLE IF EXISTS 'locations'";
//		junctionDB.execSQL(subjectsTableSql);
		
		subjectsTableSql = "CREATE TABLE IF NOT EXISTS `subjects` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,`username` VARCHAR(45) NOT NULL ,`locationId` VARCHAR(45) NOT NULL ,`subjectIndex` INT NOT NULL ,`dateTime` DATETIME NOT NULL ,`image` BLOB NOT NULL );";
		junctionDB.execSQL(subjectsTableSql);
		
		String userTableSql = "CREATE TABLE IF NOT EXISTS `users` (`name` VARCHAR(45) PRIMARY KEY NOT NULL ,`password` VARCHAR(45) NOT NULL ,`locationIds` VARCHAR(45) NOT NULL, `starIds` VARCHAR(45) NOT NULL );";
		junctionDB.execSQL(userTableSql);

		String locationTableSql = "CREATE TABLE IF NOT EXISTS `locations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,`title` VARCHAR(45) NOT NULL ,`locationName` VARCHAR(45) NOT NULL ,`latitude` VARCHAR(45) NOT NULL ,`longitude` VARCHAR(45) NOT NULL ,`backdrop` VARCHAR(45) NULL ,`currentSnapshot` BLOB NULL );";
		junctionDB.execSQL(locationTableSql);

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		username = sharedPrefs.getString("username", "");
		
		if (username.isEmpty()) {
	        cameraActivityButton.setVisibility(View.INVISIBLE);
	        newLocationActivityButton.setVisibility(View.INVISIBLE);
	        
	        ((LinearLayout)cameraActivityButton.getParent()).removeView(cameraActivityButton);
	        ((LinearLayout)newLocationActivityButton.getParent()).removeView(newLocationActivityButton);
	        registerButton.setOnClickListener(this);
	        
		} else {
			registerButton.setVisibility(View.INVISIBLE);
			Toast.makeText(getApplicationContext(), "Logged in as " + username, Toast.LENGTH_LONG).show();
			loginButton.setText("Logout");
			
			//setting photo
			String whereClause = "dateTime in (SELECT max(dateTime)FROM subjects)";

			Cursor subjectData = HomeActivity.junctionDB.query("subjects", null, whereClause , null, null, null, null);
			if (subjectData.getCount() != 0) {
				int subjectImageColumn = subjectData.getColumnIndex("image");
				subjectData.moveToFirst();
				byte[] img = subjectData.getBlob(subjectImageColumn);
				
				
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				int height = displaymetrics.heightPixels;
				int width = displaymetrics.widthPixels;
				
				Bitmap bmp = BitmapFactory.decodeByteArray(img,0,img.length);
				Double ratio = (double)bmp.getWidth() / bmp.getHeight();
				bmp = Bitmap.createScaledBitmap(bmp, (int)((height/2)*ratio), height/2, false);
	        	imageView.setImageBitmap(bmp);
	        	imageView.invalidate();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == searchActivityButton){
			Intent i = new Intent(this, SearchActivity.class);
			startActivity(i);
		}
		
		if(v == cameraActivityButton){
			Intent i = new Intent(this, MyLocationsActivity.class);
			startActivity(i);
		}
		
		if(v == newLocationActivityButton){
			Intent i = new Intent(this, LocationsMain.class);
			startActivity(i);
		}
		
		if(v == loginButton){
			if (username.isEmpty()) {
				Intent i = new Intent(this, LoginActivity.class);
	     	    i.putExtra("registering", false); 
	     	    startActivity(i);
			} else {
				username = "";
				Editor editor = sharedPrefs.edit();
	            editor.putString("username", username);
	            editor.commit();
				
				Intent i = new Intent(this, HomeActivity.class);
	     	    startActivity(i);
			}
		}
		
		if(v == registerButton){
			Intent i = new Intent(this, LoginActivity.class);
     	    i.putExtra("registering", true); 
     	    startActivity(i);
		}
	}
}
