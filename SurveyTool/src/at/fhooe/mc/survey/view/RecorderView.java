package at.fhooe.mc.survey.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import at.fhooe.mc.R;
import at.fhooe.mc.consts.ConstParameters;
import at.fhooe.mc.extern.fingerprintInformation.AccessPoint;
import at.fhooe.mc.extern.fingerprintInformation.FPIParser;
import at.fhooe.mc.extern.fingerprintInformation.FingerPrint;
import at.fhooe.mc.extern.fingerprintInformation.FingerPrintItem;
import at.fhooe.mc.extern.fingerprintInformation.FingerPrintItem.Type;
import at.fhooe.mc.extern.fingerprintInformation.MeasurementPoint;
import at.fhooe.mc.extern.fingerprintInformation.Room;
import at.fhooe.mc.extern.util.ARFFGenerator;
import at.fhooe.mc.extern.util.ARFFGenerator.ARFFType;
import at.fhooe.mc.graphical.GraphicalMpSelector;
import at.fhooe.mc.notification.Toasts;
import at.fhooe.mc.survey.recorder.WifiRecorder;

/**
 * Class Recorder View
 * 
 * Represents the Class for the recorder view
 * 
 * @author bernhard_stemmer
 * 
 */
public class RecorderView extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	/** LOG_TAG for easier Log-Management **/
	private static final String LOG_TAG = "RecorderView";
	
	/** bundle key for all mp's in rooms **/
	public static final String RESULT_BUNDLE_MP_IN_ROOM_KEY = "at.fhooe.mc.survey.mpInRooms";

	/** bundle key for room number **/
	public static final String RESULT_BUNDLE_ROOM_NUMBER_KEY = "at.fhooe.mc.survey.roomNumber";

	/** Dialog ID for MP Selection **/
	private static final int MP_SELECTION_DIALOG = 0;

	/** Dialog ID for Measurement in Progress **/
	private static final int MEASUREMENT_IN_PROGRESS_DIALOG = 1;

//	/** Dialog ID for room selection **/
//	private static final int SELCECT_ROOM_DIALOG = 2;

	/** Current MP Name Key **/
	private static final String CURRENT_MP_NAME_KEY = "curMPName";

	/** Constant for non BSSID receive **/
	private static final int NO_BSSID_RSSI = -100;
	
	/** Constant for timeout for autosave **/
	private static final int AUTO_SAVE_TIME_OUT = 60000;

	/** Code for save information **/
	private static final int SAVE_INFORMATION_REQUEST_CODE = 0;

	public static final int MP_SELECTION_REQUEST_CODE = 1;

	/** Code for settings page **/
	private static final int SETTINGS_CODE = 1;

//	/** Code for visual mode page **/
//	private static final int VISUAL_CODE = 2;

	/** the WifiRecorder to record, store and export the fingerprints **/
	private WifiRecorder m_Recorder;

	/** the button to open the mp selection dialog **/
	private Button m_select_MP;

	/** The text view to show the actual mp **/
	private TextView m_actMPLabel;

	/** The button to start the measurement when actual mp is reached **/
	private Button m_startOnMPReached;

	/** Check Box for auto mode **/
	private CheckBox m_autoModeCheckBox;

	/** Array with all MeasurementPoints extracted from the xml-file **/
	private ArrayList<MeasurementPoint> m_allMPs = new ArrayList<MeasurementPoint>();

	/** Array with all Rooms extracted from the xml-file **/
	private ArrayList<Room> m_allRooms = new ArrayList<Room>();

	/** Array with all AccessPoints extracted from the xml file. **/
	private ArrayList<AccessPoint> m_allAPs = new ArrayList<AccessPoint>();

//	/** Array with all names of MeasurementPoints **/
//	private ArrayList<String> m_allMPNames;

//	/** Array with all names of Rooms **/
//	private ArrayList<String> m_allRoomNames;

//	/** Array List with ArrayList of all Rooms and their MPs in it **/
//	private ArrayList<ArrayList<MeasurementPoint>> m_MPinRooms = new ArrayList<ArrayList<MeasurementPoint>>();

//	/** The name of the actual MeasurementPoint **/
//	private String m_actMPName = null;

	/** The button to open the visual mode **/
	private Button m_openVisualMode;

	/** Flag for Activity is active **/
	private boolean m_isRunning = false;

	private Timer m_saveTimer;
	
	private MeasurementPoint mCurMp = null;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param _savesInstanceStat
	 *            the saves State
	 */
	@Override
	public void onCreate(Bundle _savedInstanceState) {

		Log.e(LOG_TAG, "in onCreate!");
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.recorder_view);

//		if (_savedInstanceState != null) {
//			m_actMPName = _savedInstanceState.getString(CURRENT_MP_NAME_KEY);
//		} else {
//			Log.e(LOG_TAG, "saved instance was null");
//		}

		m_select_MP = (Button) findViewById(R.id.chooseMPButton);
		m_select_MP.setEnabled(true);
		m_select_MP.setOnClickListener(this);

		m_startOnMPReached = (Button) findViewById(R.id.startAtMPButton);
		m_startOnMPReached.setEnabled(true);
		m_startOnMPReached.setOnClickListener(this);

		m_openVisualMode = (Button) findViewById(R.id.recorder_view_visualButton);
		m_openVisualMode.setEnabled(true);
		m_openVisualMode.setOnClickListener(this);
//		m_openVisualMode.setEnabled(false);

		m_actMPLabel = (TextView) findViewById(R.id.recorder_view_actMPLabel);

		m_autoModeCheckBox = (CheckBox) findViewById(R.id.recorder_view_autoCheckBox);
		m_autoModeCheckBox.setChecked(false);
		m_autoModeCheckBox.setOnCheckedChangeListener(this);

		m_Recorder = new WifiRecorder(this);

		// check WLAN at start of WIFI Recorder
		checkWLANActive();

		m_isRunning = true;

	}

	/**
	 * Method check if WLAN is active
	 */
	private void checkWLANActive() {

		if (!isConnected(getApplicationContext())) {
			Log.e(LOG_TAG, "WLAN is inactive!!");
			Toasts.wifiNotEnabled(getApplicationContext());

		} else {
			Log.e(LOG_TAG, "WLAN is active!!");
		}

	}

	/**
	 * Returns whether WLAN is active or not
	 * 
	 * @param context
	 *            the application context
	 * @return true is wifi is active, otherwise false
	 */
	private static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
			networkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		}
		// boolean res = networkInfo != null;
		if (networkInfo == null) {
			return false;
		}
		return networkInfo.isAvailable();

	}

	/**
	 * initiates parsing of the xml file storing FingerprintItems
	 */
	private void loadFPI() {

		FPIParser parser = new FPIParser(getResources().getXml(R.xml.fpi));
		parser.parse();
		ArrayList<Room> parsedRooms = parser.getRooms();

		Log.e(LOG_TAG, "number of rooms read = " + parsedRooms.size());

		for (Room r : parsedRooms) {
			if (!m_allRooms.contains(r)) {
				m_allRooms.add(r);
			}
		}

		ArrayList<FingerPrintItem> items = parser.getFingerPrintItems();
		Log.e(LOG_TAG, "number of fpi read = " + parsedRooms.size());
		for (FingerPrintItem fpi : items) {
			if (fpi.getType().equals(Type.MEASUREMENT_POINT)) {
				if (getMPbyName(fpi.getName()) != null) {
					Log.e(LOG_TAG, "list already contains \"" + fpi.getName()
							+ "\"");
				} else {
					m_allMPs.add((MeasurementPoint) fpi);
				}

			} else if (fpi.getType().equals(Type.ACCESS_POINT)) {
				m_allAPs.add((AccessPoint) fpi);
			}
		}

		// FPI loaded successfully
		Toasts.fPIloadSuccess(getApplicationContext());

		// generate ArrayList with rooms and mp's in it
//		for (int i = 0; i < m_allRooms.size(); i++) {
//
//			m_MPinRooms.add(new ArrayList<MeasurementPoint>());
//			for (int j = 0; j < items.size(); j++) {
//				// check if room name of fpi is equal to actual room name
//				if (items.get(j).getRoom()
//						.compareTo(m_allRooms.get(i).getName()) == 0) {
//					m_MPinRooms.get(i).add((MeasurementPoint) items.get(j));
//				}
//			}
//		}

	}

//	/**
//	 * Creates the Locations with all MP names
//	 */
//	private void createLocations() {
//
//		m_allMPNames = new ArrayList<String>();
//		for (FingerPrintItem mp : m_allMPs) {
//			m_allMPNames.add(mp.getName());
//		}
//	}

//	/**
//	 * Creates the Rooms with all room names
//	 */
//	private void createRooms() {
//
//		m_allRoomNames = new ArrayList<String>();
//		for (int i = 0; i < m_allRooms.size(); i++) {
//			m_allRoomNames.add(m_allRooms.get(i).getName());
//		}
//	}

	/**
	 * Called when App is resumed
	 */
	public void onResume() {
		super.onResume();
//		Log.e(LOG_TAG, "onResume():" + m_actMPName);
		m_isRunning = true;
		if (ConstParameters.autoSaveActive) {
			startAutoSave();
		}
	}

	/**
	 * Called when App is paused
	 */
	public void onPause() {
		super.onPause();
		Log.e(LOG_TAG, "in onPause");

		m_isRunning = false;
		stopAutoSave();

	}

	/**
	 * Starts the autosave
	 */
	private void startAutoSave() {

		if (ConstParameters.autoSaveActive) {
			stopAutoSave();
			m_saveTimer = new Timer();
			m_saveTimer.schedule(new AutoSaveTask(), 0, AUTO_SAVE_TIME_OUT);
			Log.e(LOG_TAG, "Start Auto Save");
		}
	}

	/**
	 * Stops the autosave
	 */
	private void stopAutoSave() {

		if (null != m_saveTimer) {
			m_saveTimer.cancel();
			m_saveTimer.purge();
			Log.e(LOG_TAG, "Stop Auto Save");
		}
	}

	/**
	 * returns the first MeasurementPoint which's name equals the passed name
	 * 
	 * @param _name
	 *            the name to search for
	 * @return the MeasurementPoint with specified name, null if the
	 *         MeasurementPoint was not found
	 */
	private MeasurementPoint getMPbyName(String _name) {
		for (MeasurementPoint mp : m_allMPs) {
			if (mp.getName().equals(_name)) {
				return mp;
			}
		}

		return null;
	}

	/**
	 * Called to create a Dialog
	 * 
	 * @param _id
	 *            Id of the dialog
	 */
	@Override
	public Dialog onCreateDialog(int _id) {
		Log.e(LOG_TAG, "onCreateDialog");
		Dialog d = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (_id) {
		case MP_SELECTION_DIALOG:
			if (m_allMPs.isEmpty()) {
				Toasts.emptyMpList(getApplicationContext());
				return d;
			}
//			createLocations();

			builder.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					removeDialog(MP_SELECTION_DIALOG);
				}
			});
			builder.setTitle(R.string.RecorderView_Dialog_choose_mp);
//			String[] locations = new String[m_allMPs.size()];
//			m_allMPNames.toArray(locations);
//			Log.e(LOG_TAG, Arrays.toString(locations));
			ArrayList<String> mpNames = new ArrayList<String>(m_allAPs.size());
			for(MeasurementPoint mp:m_allMPs)
			{
				mpNames.add(mp.getName());
			}
			builder.setItems((String[]) mpNames.toArray(new String[mpNames.size()]), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					setCurrentMP(m_allMPs.get(item));
					removeDialog(MP_SELECTION_DIALOG);
				}
			});
			d = builder.create();
			d.show();
			break;

//		case SELCECT_ROOM_DIALOG:
//			if (m_allMPs.isEmpty()) {
//				Toasts.emptyMpList(getApplicationContext());
//				return d;
//			}
//			createRooms();
//			builder.setOnCancelListener(new OnCancelListener() {
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					removeDialog(MP_SELECTION_DIALOG);
//				}
//			});
//			builder.setTitle(R.string.Recorder_View_choose_Room);
//			String[] rooms = new String[m_allRoomNames.size()];
//			m_allRoomNames.toArray(rooms);
//			Log.e(LOG_TAG, Arrays.toString(rooms));
//			builder.setItems(rooms, new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int item) {
//
//					Log.e(LOG_TAG, "chosen room: " + m_allRoomNames.get(item));
//
//					startViusalModeWithRoom(item);
//
//					removeDialog(MP_SELECTION_DIALOG);
//				}
//			});
//			d = builder.create();
//			d.show();
//			break;

		case MEASUREMENT_IN_PROGRESS_DIALOG:
			ProgressDialog pd = new ProgressDialog(this);
			String message = getString(R.string.RecorderView_act_mp)
					+ mCurMp.getName();
			Log.e(LOG_TAG, message);
			pd.setMessage(message);
			pd.setCancelable(false);
			pd.show();
			d = pd;
			break;

		}
		return d;
	}

	/**
	 * Current MP is set
	 * 
	 * @param _mp
	 *            the mp to set
	 */
	protected void setCurrentMP(MeasurementPoint _mp) {
//		m_actMPName = _mp.getName();
		mCurMp = _mp;
		m_Recorder.setCurrentMP(_mp);
		m_actMPLabel.setText(mCurMp.getName());
	}

//	/**
//	 * Starts the visual mode with the chosen room
//	 * 
//	 * @param _roomNumber
//	 *            the number of chosen room
//	 */
//	private void startViusalModeWithRoom(int _roomNumber) {
//
//		// store all mps in room for the visual mode view
//		Bundle bundle = new Bundle();
//		bundle.putSerializable(RESULT_BUNDLE_MP_IN_ROOM_KEY, m_MPinRooms);
//		bundle.putInt(RESULT_BUNDLE_ROOM_NUMBER_KEY, _roomNumber);
//
//		Intent i = new Intent();
//		i.putExtras(bundle);
//		i.setClass(getApplicationContext(), VisualModeView.class);
//
//		// deactivate the automatic mode in visual mode
//		m_autoModeCheckBox.setChecked(false);
//
//		startActivityForResult(i, VISUAL_CODE);
//	}

	/**
	 * Starts the measurement with actual mp
	 */
	protected boolean startMeasureWithMP() {

		// act MP name valid
		if (mCurMp != null) {
			// check wifi again before measurement
			if (!isConnected(getApplicationContext())) {
				// problem occurred
				Toasts.wifiNotEnabled(getApplicationContext());
				return false;
			} else {
				// measuring begins
				showDialog(MEASUREMENT_IN_PROGRESS_DIALOG);
				
				m_Recorder.m_MPfinished = false;
				//start recording when button was pressed
				m_Recorder.startRecording();
				
				Log.e(LOG_TAG,"mpFinished: false");
				return true;
			}
		} else {
			// act name not valid
			Toasts.mPnotVaild(getApplicationContext());
			return false;
		}

	}

	@Override
	/**
	 * Called when Button was clicked
	 * 
	 * @param _v the view
	 */
	public void onClick(View _v) {

		switch (_v.getId()) {
		case R.id.startAtMPButton:

			if(startMeasureWithMP())
			{
				m_startOnMPReached.setEnabled(false);
			}

			break;
		case R.id.chooseMPButton:
			showDialog(MP_SELECTION_DIALOG);
			
			break;

		case R.id.recorder_view_visualButton:

//			if (m_allMPs.isEmpty()) {
//				Toasts.emptyMpList(getApplicationContext());
//			} else {
//				Log.e(LOG_TAG, "open Visual Mode");
//
//				showDialog(SELCECT_ROOM_DIALOG);
//
//			}
			Intent i = new Intent(getApplicationContext(), GraphicalMpSelector.class);
			for(MeasurementPoint mp:m_allMPs)
			{
				Log.e("CHECKCHECK", mp.getName()+": "+mp.getFingerPrints().size());
			}
//			ArrayList<MeasurementPoint> list = new ArrayList<MeasurementPoint>();
//			for(int j = 0;j<10;j++)
//			{
//				MeasurementPoint mp = new MeasurementPoint("MP "+j, "egal", 10, 10);
//				for(int k = 0;k<10;k++)
//				{
//					mp.addFingerPrint(new FingerPrint("asdf", 100, 0));					
//				}
//				list.add(mp);
//			}
			i.putExtra("asdf", m_allMPs);
			startActivityForResult(i, MP_SELECTION_REQUEST_CODE);
			break;
		}

	}

	/**
	 * Method Autosaves the data everey two minutes
	 */
	private void autoSave() {

		if (null == m_allMPs || m_allMPs.isEmpty()) {
			return;
		}
		// Log.e(LOG_TAG, "Timer for autoSave!");
		// get device info for relation
		String relation = "\n Device-infos:";
		relation += "\nOS Version: " + System.getProperty("os.version") + "("
				+ android.os.Build.VERSION.INCREMENTAL + ")";
		relation += "\nOS API Level: " + android.os.Build.VERSION.SDK;
		relation += "\nDevice: " + android.os.Build.DEVICE;
		relation += "\nModel: " + android.os.Build.MODEL;

		String fileName = "autosave";
		String path = "../sdcard/WifiSurveyTool/";

		Log.e(LOG_TAG, "autosave to: " + path + "/" + fileName);

		ARFFGenerator.writeSurveyResultsToArff(m_allMPs, path + fileName
				+ "_room", relation, ARFFType.ROOM, NO_BSSID_RSSI);
		ARFFGenerator.writeSurveyResultsToArff(m_allMPs, path + fileName
				+ "_mp", relation, ARFFType.MP, NO_BSSID_RSSI);

	}

	/**
	 * Menu is create
	 * 
	 * @param the
	 *            menu
	 */
	public boolean onCreateOptionsMenu(Menu _menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.recorder_menu, _menu);

		return true;
	}

	/**
	 * Is called when item in menu is selected
	 * 
	 * @param the
	 *            selected item
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.write_arff:
			i = new Intent();
			i.setClass(getApplicationContext(), SaveInformationView.class);
			startActivityForResult(i, SAVE_INFORMATION_REQUEST_CODE);
			return true;

		case R.id.load_fpi:
			loadFPI();
			return true;

		case R.id.settings:
			i = new Intent();
			i.setClass(getApplicationContext(), SettingsView.class);
			startActivityForResult(i, SETTINGS_CODE);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called for notify of current MP is finished
	 */
	public void notifyCurrentMPFinished() {
		Log.e(LOG_TAG, "notified about mp finish");
		removeDialog(MEASUREMENT_IN_PROGRESS_DIALOG);

		m_Recorder.m_MPfinished = true;
		Log.e(LOG_TAG,"mpFinished: true");

		MeasurementPoint mp = getNextMP();

		if (m_autoModeCheckBox.isChecked()) {
			if (null == mp) {

				Log.e(LOG_TAG, "finished!");
				return;
			}
			setCurrentMP(mp);
		}

		m_startOnMPReached.setEnabled(true);

	}

	/**
	 * Notifies number of received measurements
	 * 
	 * @param _scanNr
	 */
	public void notifyScanFinished(int _scanNr) {
		if (_scanNr % 2 == 0) {
			Toast.makeText(getApplicationContext(),
					mCurMp.getName() + ":" + _scanNr + " Messungen empfangen",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * returns the next MeasurementPoint after the passed MeasurementPoint.
	 * 
	 * @param _mp
	 *            the MeasurementPoint to get the next MeasurementPoint from
	 * @return the next MeasurementPoint, null if no MeasurementPoint was found
	 */
	private MeasurementPoint getNextMP() {
//		MeasurementPoint mp = getMPbyName(m_actMPName);
		for (int i = 0; i < m_allMPs.size(); i++) {
			MeasurementPoint toCheck = m_allMPs.get(i);
			if (toCheck.equals(mCurMp)) {
				try {
					return m_allMPs.get(i + 1);
				} catch (IndexOutOfBoundsException _aioobe) {
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * Called when checkbox changes
	 */
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		switch (arg0.getId()) {
		case R.id.recorder_view_autoCheckBox:
			if (m_allMPs.isEmpty()) {
				Toasts.emptyMpList(getApplicationContext());
				m_autoModeCheckBox.setChecked(false);
				return;
			}
			boolean state = arg0.isChecked();
			if (null == mCurMp) {
				Log.e(LOG_TAG, "curMP was null");
				setCurrentMP(m_allMPs.get(0));
			}
			m_select_MP.setEnabled(!state);
			break;
		}
	}

	@Override
	/**
	 * Called when Activity is finished with result
	 * 
	 * @param requestCode the request Code of the activity
	 * @param resultCode the result Code of the activity
	 * @param data the Intent data
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null == data) {
			Log.e(LOG_TAG, "data was null");
			return;
		}
		switch (requestCode) {

		case SAVE_INFORMATION_REQUEST_CODE:

			// get device info for relation
			String relation = "\n Device-infos:";
			relation += "\nOS Version: " + System.getProperty("os.version")
					+ "(" + android.os.Build.VERSION.INCREMENTAL + ")";
			relation += "\nOS API Level: " + android.os.Build.VERSION.SDK;
			relation += "\nDevice: " + android.os.Build.DEVICE;
			relation += "\nModel: " + android.os.Build.MODEL;

			String fileName = data
					.getStringExtra(SaveInformationView.RESULT_BUNDLE_FILENAME_KEY);
			String path = "../"
					+ data.getStringExtra(SaveInformationView.RESULT_BUNDLE_DIRECTORY_KEY)
					+ "/";

			Log.e(LOG_TAG, "export to: " + path + "/" + fileName);

			boolean res1 = ARFFGenerator.writeSurveyResultsToArff(m_allMPs,
					path + fileName + "_room", relation, ARFFType.ROOM,
					NO_BSSID_RSSI);
			boolean res2 = ARFFGenerator.writeSurveyResultsToArff(m_allMPs,
					path + fileName + "_mp", relation, ARFFType.MP,
					NO_BSSID_RSSI);
			if (res1) {
				Toasts.exportArffSuccessfull(getApplicationContext());
				Log.e(LOG_TAG, "save arff file OK!");
			} else {
				Toasts.exportArffFailed(getApplicationContext());
				Log.e(LOG_TAG, "save arff file NOK!");
			}
			if (res2) {
				Toasts.exportMPSuccessfull(getApplicationContext());
				Log.e(LOG_TAG, "save mp file OK!");
			} else {
				Toasts.exportMPFailed(getApplicationContext());
				Log.e(LOG_TAG, "save mp file NOK!");
			}

			break;
		case MP_SELECTION_REQUEST_CODE:
//			setCurrentMP((MeasurementPoint)data.getSerializableExtra(GraphicalMpSelector.SELECTED_MP_KEY));
			String selectedMpName = data.getStringExtra(GraphicalMpSelector.SELECTED_MP_KEY);
			setCurrentMP(getMPbyName(selectedMpName));
			break;

		}

	}

	/**
	 * Method is called when key is pressed
	 * 
	 * @param keyCode
	 *            Code of the Key
	 * @param event
	 *            the event of key down
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// if back button is pressed, only put task to background
		// to get sure that no data get lost
		if ((keyCode == KeyEvent.KEYCODE_BACK) && m_isRunning) {
			Log.e(LOG_TAG, "Back Button pressed!");
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Class AutoSaveTask - Extends TimerTask Task periodically save
	 * automatically
	 * 
	 * @author bernhard_stemmer
	 * 
	 */
	private class AutoSaveTask extends TimerTask {

		@Override
		public void run() {
			autoSave();

		}
	}

	
}
