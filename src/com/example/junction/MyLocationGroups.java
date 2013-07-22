package com.example.junction;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
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
			int subjectIndexColumn = locationData.getColumnIndex("title");
			locationData.moveToFirst();
			//locationData = locationData.getInt(subjectIndexColumn);
			
//			subjectData.moveToFirst();
			while (locationData.isAfterLast() == false) 
			{
//				Log.e("test", "first");
				Button locationButton = new Button(this);
				locationButton.setText(locationData.getString(subjectIndexColumn));
//				Log.e("test", "second");
//				LayoutParams lp = locationButton.getLayoutParams();
//				lp.width = LayoutParams.MATCH_PARENT;
//				locationButton.setLayoutParams(lp);
				myLocationsLinearLayout.addView(locationButton);
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
