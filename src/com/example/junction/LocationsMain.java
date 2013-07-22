package com.example.junction;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewSwitcher;

public class LocationsMain extends Activity {
	int locationId = -1;
	Button takePhotoButton;
	Boolean newLocation = false;
	
	EditText titleEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations_main);
		
		titleEditText = (EditText)findViewById(R.id.LocationTitleEditText1);
		
		takePhotoButton = (Button) findViewById(R.id.takePhotoButton1);
		takePhotoButton.setOnClickListener(takePhotoButtonListener);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			locationId = extras.getInt("locationId");
		}
		
		ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.locationTextViewSwitcher1);
		
		if (locationId == -1) {
			switcher.showNext();
			newLocation = true;
		} else {
			
		}
		
	}
	
	View.OnClickListener takePhotoButtonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			if (newLocation) {
				ContentValues cv = new ContentValues();
	        	cv.put("title", titleEditText.getText().toString());
	        	cv.put("locationName", "test");
	        	cv.put("latitude", "test");
	        	cv.put("longitude", "test");
	            HomeActivity.junctionDB.insert("locations", null, cv);
	            
	            Cursor locationData = HomeActivity.junctionDB.query("locations", null, null , null, null, null, null);
	            locationId = locationData.getCount();
			}
			
			Intent i = new Intent(LocationsMain.this, CameraActivity.class);
			i.putExtra("locationId", locationId); 
			startActivity(i);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locations_main, menu);
		return true;
	}

}
