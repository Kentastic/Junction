package com.example.junction;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

@SuppressLint("NewApi")
public class LocationsMain extends Activity {
	static public int locationId = -1;
	Button takePhotoButton, starButton;
	Boolean newLocation = false;
	
	EditText titleEditText;
	TextView titleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations_main);
		
		titleEditText = (EditText)findViewById(R.id.LocationTitleEditText1);
		titleTextView = (TextView)findViewById(R.id.locationTitleTextView);
		
		takePhotoButton = (Button) findViewById(R.id.takePhotoButton1);
		takePhotoButton.setOnClickListener(takePhotoButtonListener);
		
		starButton = (Button) findViewById(R.id.starButton);
		starButton.setOnClickListener(takePhotoButtonListener);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			locationId = extras.getInt("locationId");
			Log.e("main", Integer.toString(locationId));
		}
		
		ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.locationTextViewSwitcher1);
		
		if (locationId == -1) {
			switcher.showNext();
			newLocation = true;
		} else {
			String whereClause = "id = ?";
			String[] whereArgs = new String[] { Integer.toString(locationId) };
			
			Cursor locationData = HomeActivity.junctionDB.query("locations", null, whereClause , whereArgs, null, null, null);
			
			if (locationData.getCount() != 0) {
				int titleColumn = locationData.getColumnIndex("title");
				locationData.moveToFirst();
				titleTextView.setText(locationData.getString(titleColumn));
			}
		}
		
	}
	
	View.OnClickListener takePhotoButtonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == takePhotoButton) {
				if (newLocation) {
					ContentValues cv = new ContentValues();
					String title = titleEditText.getText().toString();
					if (title.isEmpty()) {
						cv.put("title", "");
					} else {
						cv.put("title", title);
					}
		        	cv.put("locationName", "");
		        	cv.put("latitude", "");
		        	cv.put("longitude", "");
		            HomeActivity.junctionDB.insert("locations", null, cv);
		            
		            Cursor locationData = HomeActivity.junctionDB.query("locations", null, null , null, null, null, null);
		            locationId = locationData.getCount();
				}
				
				Intent i = new Intent(LocationsMain.this, CameraActivity.class);
				i.putExtra("locationId", locationId); 
				startActivity(i);
			}
			
			if (v == starButton) {
				String whereClause = "name = ?";
				String[] whereArgs = new String[] { HomeActivity.username };
				
				Cursor userData = HomeActivity.junctionDB.query("users", null, whereClause , whereArgs, null, null, null);
				if (userData.getCount() != 0) {
					userData.moveToFirst();
					int starIdsColumn = userData.getColumnIndex("starIds");
					String starIds = userData.getString(starIdsColumn);
					
					
					if (starIds.length() == 0) {
						starIds = Integer.toString(locationId);
					} else {
						starIds += "," + Integer.toString(locationId);
					}
		            
		            ContentValues cv = new ContentValues();
		        	cv.put("starIds", starIds);
		            HomeActivity.junctionDB.update("users", cv, whereClause, whereArgs);
				} else {
					Toast.makeText(getApplicationContext(), "You need to login before starring a location", Toast.LENGTH_LONG).show();
				}
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locations_main, menu);
		return true;
	}

}
