package com.example.junction;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.view.View;
import android.view.View.OnClickListener;

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
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchActivityByLocation extends FragmentActivity implements LocationListener {
	
	LocationManager LocManager;
	Location userLocation;
	TextView userLocationTextView, addressTextView;	 
 	String geocode;
 	Geocoder myGeocoder;
 	MapFragment mapFragment;
 	GoogleMap myMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_activity_by_location);
		
		LocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		userLocationTextView = (TextView) findViewById(R.id.coordinates);
		
		Criteria myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
		myCriteria.setPowerRequirement(Criteria.POWER_LOW);
		
		String bestProvider = LocManager.getBestProvider(myCriteria, true);
		userLocation = LocManager.getLastKnownLocation(bestProvider);
		userLocationTextView.setText("Latitude: " + userLocation.getLatitude() + "\nLongitude: " + userLocation.getLongitude());
		
		LocManager.requestLocationUpdates(bestProvider, 500, 20.0f, this);
	
		myGeocoder = new Geocoder(this, Locale.CANADA);
		addressTextView = (TextView) findViewById(R.id.address);
		
		getAddress(userLocation);

		//Map
		
		SupportMapFragment fragment = new SupportMapFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.mapFragmentActivity, fragment).commit();
        fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		myMap = fragment.getMap();
		
		myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 13.0f));
		myMap.setMyLocationEnabled(true);
		myMap.setIndoorEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_activity_by_location, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		userLocation = location;
		userLocationTextView.setText("Latitude: " + userLocation.getLatitude() + "\nLongitude: " + userLocation.getLongitude());
		myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 13.0f));
	}

	private void getAddress(Location location) {
		Address myAddress = new Address(Locale.CANADA);
		
		try {
			List<Address> addresses = myGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
			if (addresses != null)
			{
				myAddress = addresses.get(0);
				geocode = myAddress.getAddressLine(0);
				
				Address myAddress1 = addresses.get(1);
				String geocode1 = myAddress.getAddressLine(0);

				addressTextView.setText(geocode + "\n" + geocode1);						
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

//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//		// TODO Auto-generated method stub
//		
//	}

	

}
