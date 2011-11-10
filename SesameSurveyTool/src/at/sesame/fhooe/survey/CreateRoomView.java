package at.sesame.fhooe.survey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateRoomView 
extends Activity
{
	private static final String TAG = "CreateRoomView";
	public static final String RESULT_BUNDLE_ROOM_NAME_KEY = "at.sesame.fhooe.survey.roomName";
	private EditText mRoomNameField;
	
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.create_room_view);
		mRoomNameField = (EditText)findViewById(R.id.createRoomView_roomField);
		
		Button addButt = (Button)findViewById(R.id.createRoomView_addButton);
		addButt.setOnClickListener(new View.OnClickListener() 
		{	
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
		data.putExtra(RESULT_BUNDLE_ROOM_NAME_KEY, mRoomNameField.getText().toString());
		
		setResult(RESULT_OK, data);
		finish();
	}

}
