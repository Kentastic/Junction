package com.example.junction;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity implements OnClickListener {
	Button searchActivityButton, cameraActivityButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		searchActivityButton = new Button(this);
		searchActivityButton = (Button) findViewById(R.id.searchActivityButton);
		searchActivityButton.setOnClickListener(this);
		
		cameraActivityButton = new Button(this);
		cameraActivityButton = (Button) findViewById(R.id.cameraActivityButton);
		cameraActivityButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == searchActivityButton){
			Intent i = new Intent(this, SearchActivity.class);
			startActivity(i);
		}
		
		if(v == cameraActivityButton){
			Intent i = new Intent(this, CameraActivity.class);
			startActivity(i);
		}
		
	}
}
