package com.example.junction;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LocationsHistogram extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations_histogram);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locations_histogram, menu);
		return true;
	}

}
