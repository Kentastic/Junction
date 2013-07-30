package com.example.junction;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class MyLocationGroups extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_location_groups);
		
		LinearLayout myLocationsLinearLayout = (LinearLayout)findViewById(R.id.myLocationsLinearLayout);
		
		Cursor locationData = HomeActivity.junctionDB.query("locations", null, null , null, null, null, null);
		if (locationData.getCount() != 0) {
			int titleColumn = locationData.getColumnIndex("title");
			locationData.moveToFirst();
			while (locationData.isAfterLast() == false) 
			{
				Button locationButton = new Button(this);
				locationButton.setText(locationData.getString(titleColumn));
				myLocationsLinearLayout.addView(locationButton);
				
				locationButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent i = new Intent(getApplicationContext(), LocationActivity.class);
						Button b = (Button) v;
						
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
				locationData.moveToNext();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_location_groups, menu);
		return true;
	}
}
