package com.example.junction;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.widget.TabHost;

public class LocationActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		
		//For Tab Layout
		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		
		//Adding Tabs
		intent = new Intent().setClass(this, LocationsMain.class);
		spec = tabHost.newTabSpec("LocationMainPage").setIndicator("Main").setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, LocationsHistogram.class);
		spec = tabHost.newTabSpec("LocationHistogramPage").setIndicator("Histogram").setContent(intent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}

}
