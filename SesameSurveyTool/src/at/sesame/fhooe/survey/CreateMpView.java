package at.sesame.fhooe.survey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateMpView 
extends Activity 
{
	private static final String TAG = "CreateMpView";
	public static final String RESULT_BUNDLE_NAME_KEY = "at.sesame.fhooe.survey.name";
	public static final String RESULT_BUNDLE_ROOM_KEY = "at.sesame.fhooe.survey.room"; 
	private EditText mNameField;
	private EditText mRoomField;
	
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.create_mp_view);
		mNameField = (EditText)findViewById(R.id.createMpView_nameField);
		mRoomField = (EditText)findViewById(R.id.createMpView_roomField);
		
		Button addButt = (Button)findViewById(R.id.createMpView_addButton);
		addButt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addAndExit();
				
			}
		});
	}
	
	public void addAndExit()
	{
		
		Log.e(TAG, "addAndExit");
		Intent data = new Intent();
		data.putExtra(RESULT_BUNDLE_NAME_KEY, mNameField.getText().toString());
		data.putExtra(RESULT_BUNDLE_ROOM_KEY, mRoomField.getText().toString());
		
		setResult(RESULT_OK, data);
		finish();
	}

}
