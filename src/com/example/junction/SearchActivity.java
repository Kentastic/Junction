package com.example.junction;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.widget.TabHost;

public class SearchActivity extends TabActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		//For Tab Layout
		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		
		//Adding Tabs
		intent = new Intent().setClass(this, SearchActivityByLocation.class);
		spec = tabHost.newTabSpec("searchByLocation").setIndicator("Search Nearby").setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, SearchActivityByName.class);
		spec = tabHost.newTabSpec("searchByName").setIndicator("Search by Name").setContent(intent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}


}
