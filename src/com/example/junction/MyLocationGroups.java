package com.example.junction;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MyLocationGroups extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_location_groups);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_location_groups, menu);
		return true;
	}

}
