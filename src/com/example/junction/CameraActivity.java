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
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CameraActivity extends Activity {
	
	private int locationId;
	private Camera mCamera;
    private CameraPreview mCameraPreview;
    private ImageView image;
    private SeekBar seek;
    Parameters parameters;
    Display display;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		Button captureButton = (Button) findViewById(R.id.button_capture);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		image = (ImageView) findViewById(R.id.HistogramImageView);
		seek = (SeekBar) findViewById(R.id.HistogramSeekBar1);
		
		image.setAlpha(127);
        image.invalidate();
        
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
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });
        
        seek.setOnSeekBarChangeListener(seekListener);
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			locationId = extras.getInt("locationId");
		}
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
        	image.setImageBitmap(bmp);
        	
            image.invalidate();
        	
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        	Date date = new Date();
        	
        	String whereClause = "locationId = ?";
			String[] whereArgs = new String[] { Integer.toString(locationId) };
			String orderClause = "subjectIndex DESC";
			
			int currentSubjectIndex = 0;
			
			Cursor subjectData = HomeActivity.junctionDB.query("subjects", null, whereClause , whereArgs, null, null, orderClause);
			Log.e("test", "first");
			if (subjectData.getCount() != 0) {
				int subjectIndexColumn = subjectData.getColumnIndex("subjectIndex");
				subjectData.moveToFirst();
				currentSubjectIndex = subjectData.getInt(subjectIndexColumn);
				
//				subjectData.moveToFirst();
//				while (subjectData.isAfterLast() == false) 
//				{
//					int rowSubjectIndex = subjectData.getInt(subjectIndexColumn);
//				    if (rowSubjectIndex > currentSubjectIndex) {
//				    	currentSubjectIndex = rowSubjectIndex;
//				    }
//				}
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
			Log.e("test", "second");
			if (userData.getCount() != 0) {
				Log.e("test", "third");
				userData.moveToFirst();
				int locationIdsColumn = userData.getColumnIndex("locationIds");
				String locationIds = userData.getString(locationIdsColumn);
				
				
				if (locationIds.length() == 0) {
					locationIds = Integer.toString(locationId);
				} else {
					locationIds += "," + Integer.toString(locationId);
				}
	            
	            cv = new ContentValues();
	        	cv.put("locationIds", locationIds);
	            HomeActivity.junctionDB.update("users", cv, whereClause, whereArgs);
			} else {
				Toast.makeText(getApplicationContext(), "You need to login before adding to a location", Toast.LENGTH_LONG).show();
			}
        	
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
}
