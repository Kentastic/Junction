package com.example.junction;

import java.util.Locale;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity implements OnClickListener {
	public Button searchActivityButton, cameraActivityButton;
	public static SQLiteDatabase junctionDB;
	public static String username;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		searchActivityButton = new Button(this);
		searchActivityButton = (Button) findViewById(R.id.searchActivityButton);
		searchActivityButton.setOnClickListener(this);
		
		cameraActivityButton = new Button(this);
		cameraActivityButton = (Button) findViewById(R.id.cameraActivityButton);
		cameraActivityButton.setOnClickListener(this);
		
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
		
		String userTableSql = "CREATE TABLE IF NOT EXISTS `users` (`name` VARCHAR(45) PRIMARY KEY NOT NULL ,`password` VARCHAR(45) NOT NULL ,`locationIds` VARCHAR(45) NOT NULL );";
		junctionDB.execSQL(userTableSql);
		
		//change to Decimal(9,6)
		String locationTableSql = "CREATE TABLE IF NOT EXISTS `locations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,`title` VARCHAR(45) NOT NULL ,`locationName` VARCHAR(45) NOT NULL ,`latitude` VARCHAR(45) NOT NULL ,`longitude` VARCHAR(45) NOT NULL ,`backdrop` VARCHAR(45) NULL ,`currentSnapshot` BLOB NULL );";
		junctionDB.execSQL(locationTableSql);

		
		if (username == null) {
			DialogFragment newFragment = new LoginDialogFragment();
	        newFragment.show(getFragmentManager(),"Login");
		}
		
		Cursor userData = junctionDB.query("users", null, null, null, null, null, null);
		if (userData.getCount() == 0) {
			Log.e("test", "NONE");
		} else {
			Log.e("test", "SOME");
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
//			Intent i = new Intent(this, SearchActivity.class);
//			startActivity(i);
			
			//myLocations
			Intent i = new Intent(this, MyLocationsActivity.class);
			startActivity(i);
			
			//location activity
//			Intent i = new Intent(this, LocationsMain.class);
//			//i.putExtra("locationId", "test"); 
//			startActivity(i);
		}
		
		if(v == cameraActivityButton){
			Intent i = new Intent(this, CameraActivity.class);
			startActivity(i);
		}
		
	}
}
