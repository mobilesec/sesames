package at.sesame.fhooe.survey;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class CreateMpView 
extends Activity 
{
	private static final String TAG = "CreateMpView";
	public static final String RESULT_BUNDLE_NAME_KEY = "at.sesame.fhooe.survey.name";
	public static final String RESULT_BUNDLE_ROOM_KEY = "at.sesame.fhooe.survey.room"; 
	private EditText mNameField;
	private Spinner mRoomSelection;
	private ArrayAdapter<String> mSpinnerAdapter;
	
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.create_mp_view);

		
		mNameField = (EditText)findViewById(R.id.createMpView_nameField);
		mRoomSelection= (Spinner)findViewById(R.id.createMpView_roomSelection);
		
		Bundle b = getIntent().getExtras();
		if(null==b)
		{
			Log.e(TAG, "bundle was null");
		}

		 mSpinnerAdapter= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, b.getStringArrayList("roomList"));
		 mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mRoomSelection.setAdapter(mSpinnerAdapter);
		
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
		data.putExtra(RESULT_BUNDLE_ROOM_KEY, (String)mRoomSelection.getSelectedItem());
		//data.putExtra(RESULT_BUNDLE_ROOM_KEY, mRoomField.getText().toString());
		
		setResult(RESULT_OK, data);
		finish();
	}

}
