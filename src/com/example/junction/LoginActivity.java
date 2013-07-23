package com.example.junction;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	Boolean isRegistering;
	
	TextView loginTextView;
	Button loginButton;
	
	EditText usernameEditText, passwordEditText;
	
	String username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		loginTextView = (TextView) findViewById(R.id.loginTextView1);
		loginButton = (Button) findViewById(R.id.loginButton1);
		loginButton.setOnClickListener(registerButtonListener);
		
		usernameEditText = (EditText)findViewById(R.id.usernameEditText1);
		passwordEditText = (EditText)findViewById(R.id.passwordEditText2);
		
		Bundle extras = getIntent().getExtras();
		isRegistering = extras.getBoolean("registering");
		
		
		
		if (isRegistering) {
			loginTextView.setText("Choose a username and password to register your account");
			loginButton.setText("Register");
		} else {
			loginTextView.setText("Login with your username and password");
			loginButton.setText("Login");
		}
	}
	
	View.OnClickListener registerButtonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			username = usernameEditText.getText().toString();
			password = passwordEditText.getText().toString();
			
			if (isRegistering) {
				
				String whereClause = "name = ?";
				String[] whereArgs = new String[] { username };
				   
				
				Cursor userData = HomeActivity.junctionDB.query("users", null, whereClause , whereArgs, null, null, null);
				if (userData.getCount() == 0) {
					Log.e("register", "NONE");
					
//					ContentValues cv = new ContentValues();
//		        	cv.put("name", username);
//		        	cv.put("password", password);
//		        	cv.put("locationIds", "");
//		            HomeActivity.junctionDB.insert("users", null, cv);
					new InsertUser().execute("");
		            
		            HomeActivity.username = username;
		            Toast.makeText(getApplicationContext(), "Account Created now logged in as " + username, Toast.LENGTH_LONG).show();
		            
		            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
					startActivity(i);
				} else {
					Log.e("register", "taken");
					Toast.makeText(getApplicationContext(), "Username has already been taken. Please try another one", Toast.LENGTH_LONG).show();
				}
				
				
	            
			} else {
				
				String whereClause = "name = ? AND password = ?";
				String[] whereArgs = new String[] { username, password };
			
				Cursor userData = HomeActivity.junctionDB.query("users", null, whereClause , whereArgs, null, null, null);
				if (userData.getCount() == 0) {
					Log.e("test", "NONE");
					Toast.makeText(getApplicationContext(), "Incorrect username or password. Try Again", Toast.LENGTH_LONG).show();
				} else {
					Log.e("test", "SOME");
					HomeActivity.username = username;
		            Toast.makeText(getApplicationContext(), "Now logged in as " + username, Toast.LENGTH_LONG).show();
		            
		            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
					startActivity(i);
				}
				
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	
	private class InsertUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
        	ContentValues cv = new ContentValues();
        	cv.put("name", username);
        	cv.put("password", password);
        	cv.put("locationIds", "");
            HomeActivity.junctionDB.insert("users", null, cv);
            return null;
        }        

        @Override
        protected void onPostExecute(String result) {    
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
