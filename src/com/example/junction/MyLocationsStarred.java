package com.example.junction;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MyLocationsStarred extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_locations_starred);
		
		String[] ids = null;
		
		LinearLayout myStarredLinearLayout = (LinearLayout)findViewById(R.id.myStarredLinearLayout);

		String whereClause = "name = ?";
		String[] whereArgs = new String[] { HomeActivity.username };
		
		Cursor userData = HomeActivity.junctionDB.query("users", null, whereClause , whereArgs, null, null, null);
		if (userData.getCount() != 0) {
			userData.moveToFirst();
			int starIdsColumn = userData.getColumnIndex("starIds");
			String starIds = userData.getString(starIdsColumn);
			
			ids = starIds.split(",");
		} else {
			Toast.makeText(getApplicationContext(), "You need to login to see your starred locations", Toast.LENGTH_LONG).show();
		}
		
		if (ids.length > 0) {
			
			whereClause = "id = ?";
			whereArgs = ids;
			
			Cursor locationData = HomeActivity.junctionDB.query("locations", null, whereClause , whereArgs, null, null, null);
			
			if (locationData.getCount() != 0) {
				int titleColumn = locationData.getColumnIndex("title");
				
				locationData.moveToFirst();
				while (locationData.isAfterLast() == false) 
				{
					Button locationButton = new Button(this);
					locationButton.setText(locationData.getString(titleColumn));
					myStarredLinearLayout.addView(locationButton);
					
					locationButton.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent i = new Intent(getApplicationContext(), LocationActivity.class);
							
							Button b = (Button)v;
							
							String whereClause = "title = ?";
							String[] whereArgs = new String[] { b.getText().toString() };
							
							Cursor locationData = HomeActivity.junctionDB.query("locations", null, whereClause , whereArgs, null, null, null);
							if (locationData.getCount() != 0) {
								int idColumn = locationData.getColumnIndex("id");
								locationData.moveToFirst();
								i.putExtra("locationId", locationData.getInt(idColumn)); 
								Log.e("put", Integer.toString(locationData.getInt(idColumn)));
							}
							startActivity(i);
						}
					});
					locationData.moveToNext();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_locations_starred, menu);
		return true;
	}

}
