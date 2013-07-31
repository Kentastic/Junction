package com.example.junction;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

@SuppressLint("NewApi")
public class SearchActivityByName extends Activity implements OnClickListener, TextWatcher, OnEditorActionListener {	
	private EditText userEntry;
	private String userInput;
	private Geocoder myGeocoder;
	private TextView coordinates;
	private Button goButton;
	private LinearLayout nameSearchLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_activity_by_name);
		
		userEntry = (EditText) findViewById(R.id.userEntry);
		userEntry.clearFocus();
		coordinates = (TextView) findViewById(R.id.nameSearchDetails);
		goButton = new Button(this);
		goButton = (Button) findViewById(R.id.goButton);
		goButton.setOnClickListener(this);
		
		nameSearchLinearLayout = (LinearLayout) findViewById(R.id.nameSearchLinearLayout);

		myGeocoder = new Geocoder (this, Locale.CANADA);
		coordinates.setText("Address: \n");
		userEntry.addTextChangedListener(this);
	}
		
	private void getPlace(String string) {
		Address userAddress = new Address(Locale.CANADA);
		
		try {
			List<Address> userList = myGeocoder.getFromLocationName(string, 1);
			if (userList!= null && !userList.isEmpty()){
				userAddress = userList.get(0);
				StringBuilder userPlace = new StringBuilder("Address: \n");
				
				for (int i = 0; i < userAddress.getMaxAddressLineIndex(); i++) {
					userPlace.append(userAddress.getAddressLine(i) + "\n");
				}
				coordinates.setText(userPlace.toString());
				
				Location placeLocation = new Location("name");
				placeLocation.setLatitude(userAddress.getLatitude());
				placeLocation.setLongitude(userAddress.getLongitude());
				
				
				//get locations
				Cursor locationData = HomeActivity.junctionDB.query("locations", null, null , null, null, null, null);

				if (locationData.getCount() != 0) {
					int latColumn = locationData.getColumnIndex("latitude");
					int longColumn = locationData.getColumnIndex("longitude");
					int titleColumn = locationData.getColumnIndex("title");
					
					locationData.moveToFirst();
					while (locationData.isAfterLast() == false) 
					{
						Location dbLocation = new Location("database");
						if (!locationData.getString(latColumn).isEmpty()) {
							dbLocation.setLatitude(Double.parseDouble(locationData.getString(latColumn)));
							dbLocation.setLongitude(Double.parseDouble(locationData.getString(longColumn)));
							
							Log.i("test", Float.toString(placeLocation.distanceTo(dbLocation)));
							if (placeLocation.distanceTo(dbLocation) <= 5000) {
								Button locationButton = new Button(this);
								locationButton.setText(locationData.getString(titleColumn));
								nameSearchLinearLayout.addView(locationButton);
								
								locationButton.setOnClickListener(new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										Intent i = new Intent(getApplicationContext(), LocationActivity.class);
										
										Button b = (Button)v;
										
										String whereClause = "title = ?";
										String[] whereArgs = new String[] { b.getText().toString() };
										
										Cursor locationData = HomeActivity.junctionDB.query("locations", null, whereClause , whereArgs, null, null, null);
										if (locationData.getCount() != 0) {
											int idColumn = locationData.getColumnIndex("id");
											locationData.moveToFirst();
											i.putExtra("locationId", locationData.getInt(idColumn)); 
											Log.e("put", Integer.toString(locationData.getInt(idColumn)));
										}
										startActivity(i);
									}
								});
							}
						}
						
						locationData.moveToNext();
					}
				}
				
				
			}
			
			else{
				coordinates.setText(R.string.noAddress);
			}
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_activity_by_name, menu);
		return true;
	}
	
	@Override
	public void afterTextChanged(Editable editText) {
		userInput = userEntry.getText().toString();
		Log.i("TEXT CHANGED", "text changed");
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1,
			int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before,
			int count) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v == userEntry){
			if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v == goButton){
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(userEntry.getWindowToken(), 0);
			getPlace(userInput);
		}
	}
}
