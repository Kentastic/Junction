package com.example.junction;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchActivityByName extends Activity implements OnClickListener, LocationListener, TextWatcher, OnEditorActionListener {	
	LocationManager LocManager;
	Location userLocation;
	EditText userEntry;
	String userInput, userGeocode, newUserInput;
	Geocoder myGeocoder;
	TextView coordinates;
	Button goButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_activity_by_name);
		
		LocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		userEntry = (EditText) findViewById(R.id.userEntry);
		coordinates = (TextView) findViewById(R.id.coordinates);
		goButton = new Button(this);
		goButton = (Button) findViewById(R.id.goButton);
		goButton.setOnClickListener(this);
		
		Criteria myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		myCriteria.setPowerRequirement(Criteria.POWER_LOW);
		
		String bestProvider = LocManager.getBestProvider(myCriteria, true);
		userLocation = LocManager.getLastKnownLocation(bestProvider);
		
		myGeocoder = new Geocoder (this, Locale.CANADA);
		//userInput = userEntry.toString();
		//userInput = "";
		coordinates.setText("Address: \n");
		//getPlace(userInput);
		
		userEntry.addTextChangedListener(this);
	}
		
	private void getPlace(String string) {
		Address userAddress = new Address(Locale.CANADA);
		
		try {
			List<Address> userList = myGeocoder.getFromLocationName(string, 1);
			if (userList!= null){
				userAddress = userList.get(0);
				StringBuilder userPlace = new StringBuilder("Address: \n");
				
				for (int i = 0; i < userAddress.getMaxAddressLineIndex(); i++) {
					userPlace.append(userAddress.getAddressLine(i) + "\n");
				}
				coordinates.setText(userPlace.toString());
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
	public void onLocationChanged(Location location) {
		userLocation = location;
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterTextChanged(Editable editText) {
		//if (editText == userEntry.getText()){
			userInput = userEntry.getText().toString();
			Log.i("TEXT CHANGED", "text changed");
			//getPlace(userInput);
		//}
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
				//getPlace(userInput);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v == goButton){
			//if(userInput != ""){
				//userInput = userEntry.toString();
				getPlace(userInput);
			//}
		}
	}
}
