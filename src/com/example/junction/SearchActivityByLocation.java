package com.example.junction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
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
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchActivityByLocation extends FragmentActivity implements LocationListener {
	
	private LocationManager LocManager;
	private Location userLocation;
	private MapFragment mapFragment;
	private GoogleMap myMap;
	private SupportMapFragment frag;
	private LinearLayout locationSearchLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_activity_by_location);
		locationSearchLinearLayout = (LinearLayout) findViewById(R.id.locationSearchLinearLayout);

		LocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
		myCriteria.setPowerRequirement(Criteria.POWER_LOW);
		String bestProvider = LocManager.getBestProvider(myCriteria, true);
		userLocation = LocManager.getLastKnownLocation(bestProvider);
		
		LocManager.requestLocationUpdates(bestProvider, 500, 20.0f, this);

		//Map
		frag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		myMap = frag.getMap();
		myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 13.0f));
		myMap.setMyLocationEnabled(true);
		myMap.setIndoorEnabled(true);
		myMap.addMarker(new MarkerOptions().position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude())).title("Current Location").snippet("Currently here"));
		
		if (userLocation != null) {
			getPlace();
		}
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
		//userLocationTextView.setText("Latitude: " + userLocation.getLatitude() + "\nLongitude: " + userLocation.getLongitude());
		myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 13.0f));
	}
	
	private void getPlace() {
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

					if (userLocation.distanceTo(dbLocation) <= 5000) {
						Button locationButton = new Button(this);
						locationButton.setText(locationData.getString(titleColumn));
						locationSearchLinearLayout.addView(locationButton);
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
								}
								startActivity(i);
							}
						});
						myMap.addMarker(new MarkerOptions().position(new LatLng(dbLocation.getLatitude(), dbLocation.getLongitude())).title(locationData.getString(titleColumn)).snippet("Location"));
					}
				}
				locationData.moveToNext();
			}
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
