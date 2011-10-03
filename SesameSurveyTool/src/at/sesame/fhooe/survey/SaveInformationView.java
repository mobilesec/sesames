package at.sesame.fhooe.survey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SaveInformationView 
extends Activity 
{
	private static final String TAG = "SaveInformationView";
	public static final String RESULT_BUNDLE_RELATION_KEY = "at.sesame.fhooe.survey.relation";
	public static final String RESULT_BUNDLE_FILENAME_KEY = "at.sesame.fhooe.survey.fileName";
	private EditText mRelationField;
	private EditText mFileNameField;
	
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.save_information_view);
		mRelationField = (EditText)findViewById(R.id.saveInformationView_relationField);
		mFileNameField = (EditText)findViewById(R.id.saveInformationpView_fileNameField);
		
		Button exportButt = (Button)findViewById(R.id.saveInformationView_exportButton);
		exportButt.setOnClickListener(new View.OnClickListener() {
			
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
		data.putExtra(RESULT_BUNDLE_RELATION_KEY, mRelationField.getText().toString());
		data.putExtra(RESULT_BUNDLE_FILENAME_KEY, mFileNameField.getText().toString());
		
		setResult(RESULT_OK, data);
		finish();
	}

}
