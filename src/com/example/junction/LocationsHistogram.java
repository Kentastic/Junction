package com.example.junction;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class LocationsHistogram extends Activity {

	private int locationId;
	ImageView histogramImage;
	SeekBar histogramSeekBar;
	TextView histogramTitleTextView;
	ArrayList<Bitmap> images = new ArrayList<Bitmap>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations_histogram);
		histogramImage = (ImageView) findViewById(R.id.HistogramImageView);
		histogramSeekBar = (SeekBar) findViewById(R.id.HistogramSeekBar1);
		histogramTitleTextView = (TextView) findViewById(R.id.histogramTitleTextView);

		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			locationId = extras.getInt("locationId");
			Log.e("main", Integer.toString(locationId));
		}
		
		if (locationId == -1) {
		} else {
			String whereClause = "id = ?";
			String[] whereArgs = new String[] { Integer.toString(locationId) };
			
			Cursor locationData = HomeActivity.junctionDB.query("locations", null, whereClause , whereArgs, null, null, null);
			
			if (locationData.getCount() != 0) {
				int titleColumn = locationData.getColumnIndex("title");
				locationData.moveToFirst();
				histogramTitleTextView.setText(locationData.getString(titleColumn));
			}
		}
		
		String whereClause = "locationId = ?";
		String[] whereArgs = new String[] { Integer.toString(locationId) };
		String orderClause = "subjectIndex ASC";
		Cursor subjectData = HomeActivity.junctionDB.query("subjects", null, whereClause , whereArgs, null, null, orderClause);
		int imageNum = subjectData.getCount();
		if (imageNum != 0) {
			subjectData.moveToFirst();
			histogramSeekBar.setMax(imageNum-1);
			histogramSeekBar.setProgress(imageNum-1);
			int imageColumn = subjectData.getColumnIndex("image");
			while (subjectData.isAfterLast() == false) 
			{
				
				byte[] data = subjectData.getBlob(imageColumn);
				
				Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);
				images.add(bmp);
				subjectData.moveToNext();
			}
			histogramImage.setImageBitmap(images.get(images.size()-1));
			histogramImage.invalidate();
		}
		
		histogramSeekBar.setOnSeekBarChangeListener(histogramSeekListener);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locations_histogram, menu);
		return true;
	}
	
	OnSeekBarChangeListener histogramSeekListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			histogramImage.setImageBitmap(images.get(histogramSeekBar.getProgress()));
			histogramImage.invalidate();
		}
	};

}
