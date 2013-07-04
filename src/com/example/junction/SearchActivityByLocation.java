package com.example.junction;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.MapView;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class SearchActivityByLocation extends Activity implements LocationListener {
	
	LocationManager LocManager;
	TextView userLocationTextView, addressTextView;	 
 	String geocode;
 	Geocoder myGeocoder;
 	MapView userMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_activity_by_location);
		
		LocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		userLocationTextView = (TextView) findViewById(R.id.coordinates);
		
		Criteria myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		myCriteria.setPowerRequirement(Criteria.POWER_LOW);
		
		String bestProvider = LocManager.getBestProvider(myCriteria, true);
		Location userLocation = LocManager.getLastKnownLocation(bestProvider);
		userLocationTextView.setText("Latitude: " + userLocation.getLatitude() + "\nLongitude: " + userLocation.getLongitude());
		
		LocManager.requestLocationUpdates(bestProvider, 300000, 20.0f, this);
		
		
		myGeocoder = new Geocoder(this, Locale.CANADA);
		addressTextView = (TextView) findViewById(R.id.address);
		
		getAddress(userLocation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_activity_by_location, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		Location userLocation = location;
		userLocationTextView.setText("Latitude: " + userLocation.getLatitude() + "\nLongitude: " + userLocation.getLongitude());
		
	}

	private void getAddress(Location location) {
		Address myAddress = new Address(Locale.CANADA);
		
		try {
			List<Address> addresses = myGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			if (addresses != null)
			{
				myAddress = addresses.get(0);
				geocode = myAddress.getAddressLine(0);
				addressTextView.setText(geocode);						
				}
		}
		
		catch (IOException e) {
			addressTextView.setText(e.getMessage()); 
		} 
	}

	@Override
	public void onProviderDisabled(String provider) {
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

}
