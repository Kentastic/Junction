package com.example.junction;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.widget.TabHost;

public class MyLocationsActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_locations);
		
		//For Tab Layout
		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
				
		//Adding Tabs
		intent = new Intent().setClass(this, MyLocationGroups.class);
		spec = tabHost.newTabSpec("myLocationGroups").setIndicator("My Location Groups").setContent(intent);
		tabHost.addTab(spec);
				
		intent = new Intent().setClass(this, MyLocationsStarred.class);
		spec = tabHost.newTabSpec("myStarredLocations").setIndicator("My Starred Locations").setContent(intent);
		tabHost.addTab(spec);
				
		tabHost.setCurrentTab(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_locations, menu);
		return true;
	}

}
