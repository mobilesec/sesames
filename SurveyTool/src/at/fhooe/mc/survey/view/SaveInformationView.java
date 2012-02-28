package at.fhooe.mc.survey.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import at.fhooe.mc.R;
import at.fhooe.mc.notification.Toasts;

/**
 * The saveinformation view class
 * 
 * @author bernhard_stemmer
 *
 */
public class SaveInformationView extends Activity implements OnClickListener {
	/** the log tag **/
	private static final String LOG_TAG = "SaveInformationView";

	/** the filename key **/
	public static final String RESULT_BUNDLE_FILENAME_KEY = "at.fhooe.mc.survey.fileName";
	/** the directory key **/
	public static final String RESULT_BUNDLE_DIRECTORY_KEY = "at.fhoe.mc.survey.directory";

	/** the request save **/
	private static final int REQUEST_SAVE = 3;

	/** the file name text field **/
	private EditText m_fileNameField;
	/** the export button **/
	private Button m_exportButton;
	/** the choose directory button **/
	private Button m_chooseDirButton;
	/** the path text view **/
	private TextView m_pathTextView;
	/** the path text view **/
	private String m_choosenDirectory;

	/**
	 * On create method
	 * 
	 * @_savedInstance the saved instance
	 */
	public void onCreate(Bundle _savedInstance) {
		super.onCreate(_savedInstance);
		setContentView(R.layout.save_arff_file_view);
		m_fileNameField = (EditText) findViewById(R.id.save_arff_file_view_fileNameTextField);
		m_pathTextView = (TextView) findViewById(R.id.save_arff_file_view_PathLabel);
		
		m_exportButton = (Button) findViewById(R.id.save_arff_file_view_saveButton);
		m_exportButton.setEnabled(true);
		m_exportButton.setOnClickListener(this);

		m_chooseDirButton = (Button) findViewById(R.id.save_arff_file_view_chooseDirButton);
		m_chooseDirButton.setEnabled(true);
		m_chooseDirButton.setOnClickListener(this);
		



	}

	/**
	 * the on click method
	 * 
	 * @param _v the view
	 */
	public void onClick(View _v) {

		switch (_v.getId()) {
		case R.id.save_arff_file_view_saveButton:
			addAndExit();
			break;
		case R.id.save_arff_file_view_chooseDirButton:
			showFileDialog();
			break;
		}
	}

	/**the save dialog **/
	public void showFileDialog() {
		Log.e(LOG_TAG, "in select path");
		Intent i = new Intent();
		i.setClass(getApplicationContext(), FileDialogView.class);
		startActivityForResult(i, REQUEST_SAVE);
	}

	/**
	 * The onActivityResult method
	 * 
	 * @param requestCode the request code
	 * @param resultCode the result code
	 * @param data the data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null == data) {
			Log.e(LOG_TAG, "data was null");
			return;
		}
		switch (requestCode) {
		case REQUEST_SAVE:

			m_choosenDirectory = data
					.getStringExtra(SaveInformationView.RESULT_BUNDLE_DIRECTORY_KEY);
			m_pathTextView.setText(m_choosenDirectory);
			
			break;			
		}
	}

	/**
	 * Called to store data and exit activity
	 */
	public void addAndExit() {

		Log.e(LOG_TAG, "in addAndExit");

		// check if text fields are not empty

		if (m_fileNameField.getText().toString().compareTo("") == 0) {
			Toasts.fileNameEmpty(getApplicationContext());
		} else {

			Log.e(LOG_TAG, "text fields ok");
			Intent data = new Intent();

			data.putExtra(RESULT_BUNDLE_FILENAME_KEY, m_fileNameField.getText()
					.toString());

			data.putExtra(RESULT_BUNDLE_DIRECTORY_KEY, m_choosenDirectory);
			setResult(RESULT_OK, data);
			finish();
		}
	}

}
