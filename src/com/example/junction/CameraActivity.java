package com.example.junction;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

@SuppressLint("NewApi")
public class CameraActivity extends Activity implements LocationListener, OnClickListener {
	
	private int locationId;
	private Camera mCamera;
    private CameraPreview mCameraPreview;
    private ImageView image;
    private SeekBar seek;
    private Parameters parameters;
    private Display display;
    private Button captureButton;
    
    private LocationManager locManager;
    private Location userLocation;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		image = (ImageView) findViewById(R.id.HistogramImageView);
		captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(this);
		seek = (SeekBar) findViewById(R.id.HistogramSeekBar1);
		seek.setOnSeekBarChangeListener(seekListener);
        
        mCamera = getCameraInstance();
        parameters = mCamera.getParameters();
        display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		
		surfaceChanged(width, height);
		
        mCameraPreview = new CameraPreview(this, mCamera);
        preview.addView(mCameraPreview);
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			locationId = extras.getInt("locationId");
			
			//setting photo
			String whereClause = "dateTime in (SELECT min(dateTime)FROM subjects WHERE locationId = ?)";
			String[] whereArgs = new String[] { Integer.toString(locationId) };
			Cursor subjectData = HomeActivity.junctionDB.query("subjects", null, whereClause , whereArgs, null, null, null);
			if (subjectData.getCount() != 0) {
				int subjectImageColumn = subjectData.getColumnIndex("image");
				subjectData.moveToFirst();
				
				byte[] img = subjectData.getBlob(subjectImageColumn);
				Bitmap bmp = BitmapFactory.decodeByteArray(img,0,img.length);
				
				Matrix m = new Matrix();
				m.setScale(-1, 1);

				bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
				
				image.setImageBitmap(bmp);
				image.setAlpha(127);
				image.invalidate();
			}
		}
		
		///GPS stuff
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Criteria myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
		locManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, this);
		String bestProvider = locManager.getBestProvider(myCriteria, true);
		userLocation = locManager.getLastKnownLocation(bestProvider);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}
	
	OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener() {
		
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
			image.setAlpha(seek.getProgress());
			image.invalidate();
		}
	};

	public void onClick(View v) {
		if (v == captureButton) {
			mCamera.takePicture(null, null, mPicture);
		}
        
    }
	
	private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(0);
        } catch (Exception e) {
        	
        }
        return camera;
    }

    PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        	String whereClause = "locationId = ?";
			String[] whereArgs = new String[] { Integer.toString(locationId) };
			String orderClause = "subjectIndex DESC";
			
			int currentSubjectIndex = 0;
			Cursor subjectData = HomeActivity.junctionDB.query("subjects", null, whereClause , whereArgs, null, null, orderClause);
			if (subjectData.getCount() != 0) {
				int subjectIndexColumn = subjectData.getColumnIndex("subjectIndex");
				subjectData.moveToFirst();
				currentSubjectIndex = subjectData.getInt(subjectIndexColumn);
				currentSubjectIndex++;
			}
        	
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        	Date date = new Date();
			
        	ContentValues cv = new ContentValues();
        	cv.put("username", HomeActivity.username);
        	cv.put("locationId", locationId);
        	cv.put("subjectIndex", currentSubjectIndex);
        	cv.put("dateTime", dateFormat.format(date));
            cv.put("image", data);
            HomeActivity.junctionDB.insert("subjects", null, cv);
            
            whereClause = "name = ?";
			whereArgs = new String[] { HomeActivity.username };
			
			Cursor userData = HomeActivity.junctionDB.query("users", null, whereClause , whereArgs, null, null, null);
			if (userData.getCount() != 0) {
				userData.moveToFirst();
				int locationIdsColumn = userData.getColumnIndex("locationIds");
				String locationIds = userData.getString(locationIdsColumn);
				
				Boolean inArray = false;
				if (!locationIds.isEmpty()) {
					String[] ids = locationIds.split(",");
					
					if (ids.length > 0) {
						for (int i = 0; i < ids.length; i++) {
							if (Integer.parseInt(ids[i]) == locationId) {
								inArray = true;
							}
						}
					} 
				}
				
				if (!inArray) {
					if (locationIds.length() == 0) {
						locationIds = Integer.toString(locationId);
					} else {
						locationIds += "," + Integer.toString(locationId);
					}
				}
				
	            
	            cv = new ContentValues();
	        	cv.put("locationIds", locationIds);
	            HomeActivity.junctionDB.update("users", cv, whereClause, whereArgs);
			} else {
				Toast.makeText(getApplicationContext(), "You need to login before adding to a location", Toast.LENGTH_LONG).show();
			}
			
			
			whereClause = "id = ?";
			whereArgs = new String[] { Integer.toString(locationId) };
			Cursor locationData = HomeActivity.junctionDB.query("locations", null, whereClause , whereArgs, null, null, null);
			
			if (locationData.getCount() != 0) {
				locationData.moveToFirst();
				int latColumn = locationData.getColumnIndex("latitude");
				if (locationData.getString(latColumn).isEmpty() && userLocation != null) {
					cv = new ContentValues();
		        	cv.put("latitude", Double.toString(userLocation.getLatitude()));
		        	cv.put("longitude",  Double.toString(userLocation.getLongitude()));
		            HomeActivity.junctionDB.update("locations", cv, whereClause, whereArgs);
				}
			}
        	
			Intent i = new Intent(getApplicationContext(), LocationActivity.class);
			i.putExtra("locationId", locationId); 
     	    startActivity(i);
        }
    };
    
    public void surfaceChanged(int width, int height) {            
        if (display.getRotation() == Surface.ROTATION_0) {
//            parameters.setPreviewSize(height, width);    
        	Log.i("test", "1");
            mCamera.setDisplayOrientation(90);
            parameters.setRotation(270);
//        	parameters.set("orientation", "portrait");
        }

        if (display.getRotation() == Surface.ROTATION_90) {
        	Log.i("test", "2");
        }

        if (display.getRotation() == Surface.ROTATION_180) {
        	Log.i("test", "3");
//            parameters.setPreviewSize(height, width);  
        	parameters.setRotation(270);
        }

        if (display.getRotation() == Surface.ROTATION_270) {
        	Log.i("test", "4");
//            parameters.setPreviewSize(width, height);
            mCamera.setDisplayOrientation(180);
            parameters.setRotation(180);
        }
        
        mCamera.setParameters(parameters);                     
    }

	@Override
	public void onLocationChanged(Location location) {
		userLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
