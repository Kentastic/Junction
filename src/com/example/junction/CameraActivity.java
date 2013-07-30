package com.example.junction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CameraActivity extends Activity implements LocationListener, OnClickListener {
	
	private int locationId;
	private Camera mCamera;
    private CameraPreview mCameraPreview;
    private ImageView image;
    private SeekBar seek;
    Parameters parameters;
    Display display;
    Button captureButton;
    
    LocationManager locManager;
	Location userLocation;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		captureButton = (Button) findViewById(R.id.button_capture);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		image = (ImageView) findViewById(R.id.HistogramImageView);
		seek = (SeekBar) findViewById(R.id.HistogramSeekBar1);
		
//		image.setAlpha(127);
//        image.invalidate();
        
        mCamera = getCameraInstance();
        parameters = mCamera.getParameters();
        display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();  
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		
		
		
		surfaceChanged(width, height);
		
        mCameraPreview = new CameraPreview(this, mCamera);
        //FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);

        //Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(this);
        
        seek.setOnSeekBarChangeListener(seekListener);
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			locationId = extras.getInt("locationId");
			
			//setting photo
			String whereClause = "dateTime in (SELECT min(dateTime)FROM subjects WHERE locationId = ?)";
			String[] whereArgs = new String[] { Integer.toString(locationId) };
			
			byte[] img = null;
			
			Cursor subjectData = HomeActivity.junctionDB.query("subjects", null, whereClause , whereArgs, null, null, null);
			if (subjectData.getCount() != 0) {
				int subjectImageColumn = subjectData.getColumnIndex("image");
				subjectData.moveToFirst();
				img = subjectData.getBlob(subjectImageColumn);
				
				Bitmap bmp = BitmapFactory.decodeByteArray(img,0,img.length);
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
	    
	    //locManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, getMainLooper());
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
			locManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, this);
		}
        
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}
	
	private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        	
        	Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);
//        	ImageView image=new ImageView(this);
//        	image.setImageBitmap(bmp);
//        	
//            image.invalidate();
        	
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        	Date date = new Date();
        	
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
				
				String[] ids = locationIds.split(",");
				Boolean inArray = false;
				
				for (int i = 0; i < ids.length; i++) {
					if (Integer.parseInt(ids[i]) == locationId) {
						inArray = true;
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
				//Log.i("test", Boolean.toString(userLocation != null));
				if (locationData.getString(latColumn).isEmpty() && userLocation != null) {
					cv = new ContentValues();
		        	cv.put("latitude", Double.toString(userLocation.getLatitude()));
		        	cv.put("longitude",  Double.toString(userLocation.getLongitude()));
		        	Toast.makeText(getApplicationContext(), Double.toString(userLocation.getLongitude()), Toast.LENGTH_SHORT).show();
		            HomeActivity.junctionDB.update("locations", cv, whereClause, whereArgs);
				}
			}
        	
			
			Intent i = new Intent(getApplicationContext(), LocationsMain.class);
			i.putExtra("locationId", locationId); 
     	    startActivity(i);
        }
    };

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Junction");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Junction", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }
    
    public void surfaceChanged(int width, int height)
    {            
    	
        if(display.getRotation() == Surface.ROTATION_0)
        {
            parameters.setPreviewSize(height, width);                           
            mCamera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
            parameters.setPreviewSize(width, height);                           
        }

        if(display.getRotation() == Surface.ROTATION_180)
        {
            parameters.setPreviewSize(height, width);               
        }

        if(display.getRotation() == Surface.ROTATION_270)
        {
            parameters.setPreviewSize(width, height);
            mCamera.setDisplayOrientation(180);
        }

        mCamera.setParameters(parameters);                     
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Toast.makeText(this, Double.toString(location.getLatitude()), Toast.LENGTH_SHORT).show();
		Toast.makeText(this, Double.toString(location.getLongitude()), Toast.LENGTH_SHORT).show();
		userLocation = location;
		locManager.removeUpdates(this);
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
