package at.fhooe.mc.survey.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import at.fhooe.mc.R;
import at.fhooe.mc.consts.ConstParameters;
import at.fhooe.mc.notification.Toasts;

/**
 * Class represents the Setting View
 * 
 * @author bernhard_stemmer
 * 
 */
public class SettingsView extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	/** The Log Tag **/
	private static final String LOG_TAG = "SettingsView";
	/** the Key for Bundle result for measure counter **/
	public static final String RESULT_BUNDLE_MEASURE_COUNTER_KEY = "at.fhooe.mc.survey.measure";
	/** the Key for Bundle result for mp in row **/
	public static final String RESULT_BUNDLE_MEASURE_MPINROW_KEY = "at.fhooe.mc.survey.mpInRow";
	/** the measure count Edit Text Field **/
	private EditText m_measureCount;
	/** the save Button **/
	private Button m_saveButton;

	/** the auto save check box **/
	private CheckBox m_autoSave_CheckBox;

	/**
	 * Called when Activity is created
	 * 
	 * @param _savedInstance
	 *            the saved instance
	 */
	public void onCreate(Bundle _savedInstance) {
		super.onCreate(_savedInstance);
		setContentView(R.layout.settings_view);

		m_measureCount = (EditText) findViewById(R.id.setting_view_MesaureCountField);
		m_saveButton = (Button) findViewById(R.id.settings_view_saveButton);
		m_saveButton.setEnabled(true);
		m_saveButton.setOnClickListener(this);

		m_autoSave_CheckBox = (CheckBox) findViewById(R.id.settings_view_autoSaveCheckBox);
		m_autoSave_CheckBox.setChecked(ConstParameters.autoSaveActive);
		m_autoSave_CheckBox.setOnCheckedChangeListener(this);

		m_measureCount.setText("" + ConstParameters.countOfMeasures);
	}

	/**
	 * Called when button is clicked
	 * 
	 * @param _v
	 *            the view
	 */
	@Override
	public void onClick(View _v) {

		switch (_v.getId()) {
		case R.id.settings_view_saveButton:
			addAndExit();
			break;
		}
	}

	/**
	 * Called to finish and exit the Activity with data
	 */
	private void addAndExit() {

		try {
			Log.e(LOG_TAG, "mesasure count field ok");
			ConstParameters.countOfMeasures = Integer.parseInt(m_measureCount
					.getText().toString());
			finish();
		} catch (Exception e) {

			Toasts.noInterger(getApplicationContext());
		}

	}

	/**
	 * Called when check box for auto save changes
	 * 
	 * @param arg0
	 *            the checkbox object
	 * @param isChecked
	 *            boolean if checkbox is checked
	 */
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		switch (arg0.getId()) {
		case R.id.settings_view_autoSaveCheckBox:

			if (m_autoSave_CheckBox.isChecked()) {
				ConstParameters.autoSaveActive = true;
			} else {
				ConstParameters.autoSaveActive = false;
			}

			break;
		}

	}

}
