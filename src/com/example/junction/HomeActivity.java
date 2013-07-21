package com.example.junction;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
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
		junctionDB.execSQL(subjectsTableSql);
		subjectsTableSql = "CREATE TABLE IF NOT EXISTS `subjects` (`id` VARCHAR(45) NOT NULL ,`userId` VARCHAR(45) NOT NULL ,`locationId` VARCHAR(45) NOT NULL ,`subjectIndex` INT NOT NULL ,`dateTime` DATETIME NOT NULL ,`image` BLOB NOT NULL ,PRIMARY KEY (`id`) );";
		
		junctionDB.execSQL(subjectsTableSql);
		
		String userTableSql = "CREATE TABLE IF NOT EXISTS `users` (`id` VARCHAR(45) NOT NULL ,`name` VARCHAR(45) NOT NULL ,`password` VARCHAR(45) NOT NULL ,`locationIds` VARCHAR(45) NOT NULL ,PRIMARY KEY (`id`) );";
		junctionDB.execSQL(userTableSql);
		
		String getUsers = "SELECT * FROM users;";
		junctionDB.execSQL(getUsers);
		
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
			Intent i = new Intent(this, SearchActivity.class);
			startActivity(i);
		}
		
		if(v == cameraActivityButton){
			Intent i = new Intent(this, CameraActivity.class);
			startActivity(i);
		}
		
	}
}
