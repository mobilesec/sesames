/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.survey;

import java.util.ArrayList;
import java.util.Arrays;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import at.sesame.fhooe.lib.fingerprintInformation.AccessPoint;
import at.sesame.fhooe.lib.fingerprintInformation.FPIParser;
import at.sesame.fhooe.lib.fingerprintInformation.FingerPrintItem;
import at.sesame.fhooe.lib.fingerprintInformation.RawMeasurementPoint;
import at.sesame.fhooe.lib.fingerprintInformation.FingerPrintItem.Type;
import at.sesame.fhooe.lib.fingerprintInformation.MeasurementPoint;
import at.sesame.fhooe.lib.fingerprintInformation.Room;
import at.sesame.fhooe.lib.util.ARFFGenerator;
import at.sesame.fhooe.lib.util.ARFFGenerator.ARFFType;
import at.sesame.fhooe.survey.R;
import at.sesame.fhooe.survey.wifi.recorder.WifiRecorder;


/**
 * this class implements the GUI for measurement of fingerprints
 * @author Peter Riedl
 *
 */
public class WifiRecorderView
extends Activity
implements OnClickListener, OnCheckedChangeListener
{
	
	private static final String TAG = "WifiRecorderView";
	/**
	 * id for the location selection dialog
	 */
	private static final int LOCATION_SELECTION_DIALOG = 0;
	
	private static final int MEASUREMENT_IN_PROGRESS_DIALOG = 1;
	
	private static final String CURRENT_MP_NAME_KEY = "curMPName";
	
	private static final int NO_BSSID_RSSI = -100;
	
	private static final int CREATE_MP_REQUEST_CODE = 0;
	private static final int CREATE_ROOM_REQUEST_CODE = 1;
	private static final int SAVE_INFORMATION_REQUEST_CODE = 2;
	
	/**
	 * the WifiRecorder to record, store and export the fingerprints
	 */
	private WifiRecorder mRecorder;
	
	/**
	 * the button to open the location selection dialog
	 */
	private Button mChangeLoc;
	
	private TextView mCurrentMPLabel;
	
	private CheckBox mAutoCheckbox;
	
	/**
	 * pressed when the user has reached the current MeasurementPoint
	 */
	private Button mLocReached;
	
	/**
	 * a list of all MeasurementPoints extracted from the xml file
	 */
	private ArrayList<MeasurementPoint> mMPs = new ArrayList<MeasurementPoint>();
	
	private ArrayList<Room>mRooms = new ArrayList<Room>();
	
	/**
	 * a list of all AccessPoints extracted from the xml file.
	 */
	private ArrayList<AccessPoint> mAPs = new ArrayList<AccessPoint>();
	
	/**
	 * a list of all names of MeasurementPoints
	 */
	private ArrayList<String> mLocations;
	
	/**
	 * the name of the current MeasurementPoint
	 */
	private String mCurMpName = null;
	
	private SharedPreferences mPrefs;

	
	@Override
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.wifi_recorder_view);
		mPrefs = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor ed = mPrefs.edit();
        ed.putString(CURRENT_MP_NAME_KEY, "");
        ed.commit();
		Log.e(TAG, "onCreate");
		if(null!=_savedInstance)
		{
			mCurMpName = _savedInstance.getString(CURRENT_MP_NAME_KEY);
		}
		else
		{
			Log.e(TAG, "saved instance was null");
		}
		
		mCurrentMPLabel = (TextView)findViewById(R.id.currentMPLabel);
		
		mAutoCheckbox = (CheckBox)findViewById(R.id.WifiRecorderAutoModeCB);
		mAutoCheckbox.setChecked(false);
		mAutoCheckbox.setOnCheckedChangeListener(this);
		
		mChangeLoc = (Button)findViewById(R.id.setWifiRecordingLocation);
		mChangeLoc.setEnabled(true);
		mChangeLoc.setOnClickListener(this);
		
		mLocReached = (Button)findViewById(R.id.MPReachedButton);
		mLocReached.setOnClickListener(this);
		
		mRecorder = new WifiRecorder(this);
	}
	


	/**
	 * initiates parsing of the xml file storing FingerprintItems
	 */
	private void loadFPI() 
	{
		FPIParser parser = new FPIParser(getResources().getXml(R.xml.fpi));
		parser.parse();
		ArrayList<Room> parsedRooms = parser.getRooms();
		
		for(Room r:parsedRooms)
		{
			if(!mRooms.contains(r))
			{
				mRooms.add(r);
			}
		}
		
		ArrayList<FingerPrintItem> items = parser.getFingerPrintItems();
		for(FingerPrintItem fpi:items)
		{
			if(fpi.getType().equals(Type.MEASUREMENT_POINT))
			{
				if(getMPbyName(fpi.getName())!=null)
				{
					Log.e(TAG,"list already contains \""+fpi.getName()+"\"");
				}
				else
				{
					mMPs.add((MeasurementPoint)fpi);
				}
				
			}
			else if(fpi.getType().equals(Type.ACCESS_POINT))
			{
				mAPs.add((AccessPoint)fpi);
			}
		}
	}
	
	private void createLocations() 
	{
//		mLocations = new String[mMPs.size()];
//		for(int i = 0;i<mLocations.length;i++)
//		{
//			mLocations[i] = mMPs.get(i).getName();
//		}
		mLocations = new ArrayList<String>();
		for(FingerPrintItem mp:mMPs)
		{
			mLocations.add(mp.getName());
		}
	}
	
	public void onResume()
	{
		super.onResume();
		mCurMpName = mPrefs.getString(CURRENT_MP_NAME_KEY, "");
		Log.e(TAG,"onResume():"+mCurMpName);
	}
	
	public void onPause()
	{
		super.onPause();
		Log.e(TAG, "onPause");
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putString(CURRENT_MP_NAME_KEY, mCurMpName);
        ed.commit();

	}
	
	/**
	 * returns the first MeasurementPoint which's name equals the passed name
	 * @param _name the name to search for
	 * @return the MeasurementPoint with specified name, null if the 
	 * MeasurementPoint was not found
	 */
	private MeasurementPoint getMPbyName(String _name)
	{
		for(MeasurementPoint mp:mMPs)
		{
			if(mp.getName().equals(_name))
			{
				return mp;
			}
		}
		
		return null;
	}
	
	@Override
	public Dialog onCreateDialog(int _id)
	{
		Log.e(TAG, "onCreateDialog");
		Dialog d = null;
		switch(_id)
		{
		case LOCATION_SELECTION_DIALOG:
			if(mMPs.isEmpty())
			{
				toastEmptyMpList();
				return d;
			}
			createLocations();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setOnCancelListener(new OnCancelListener() 
			{	
				@Override
				public void onCancel(DialogInterface dialog) 
				{
					removeDialog(LOCATION_SELECTION_DIALOG);
				}
			});
			builder.setTitle(R.string.WifiRecorderView_chooseLocationDialogTitle);
			String[] locations = new String[mMPs.size()];
			mLocations.toArray(locations);
			Log.e(TAG,Arrays.toString(locations));
			builder.setItems(locations, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) 
			    {
			    	setCurrentMP(getMPbyName(mLocations.get(item)));
			    	removeDialog(LOCATION_SELECTION_DIALOG);
			    }
			});
			d = builder.create();
			d.show();
			break;
		case MEASUREMENT_IN_PROGRESS_DIALOG:
			ProgressDialog pd = new ProgressDialog(this);
			String message = getString(R.string.WifiRecorder_progressDialogPrefix)+mCurMpName;
			Log.e(TAG, message);
			pd.setMessage(message);
			pd.show();
			d=pd;
			break;

		}
		return d;
	}
	
	
	private void toastEmptyMpList() 
	{
		Toast.makeText(getApplicationContext(), "no Measurementpoints yet", Toast.LENGTH_SHORT).show();
	}
	
	private void toastWifiNotEnabled()
	{
		Toast.makeText(getApplicationContext(), "wifi is not enabled, please enable it to use this tool.", Toast.LENGTH_SHORT).show();
	}



	private void setCurrentMP(MeasurementPoint _mp)
	{
		mCurMpName = _mp.getName();
		mCurrentMPLabel.setText(getString(R.string.wifi_recorder_view_currentMPLabelPrefix)+_mp.getName());
		mRecorder.setCurrentMP(_mp);
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
		case R.id.MPReachedButton:
			showDialog(MEASUREMENT_IN_PROGRESS_DIALOG);
			if(!mRecorder.startRecording())
			{
				toastWifiNotEnabled();
			}
			break;
		case R.id.setWifiRecordingLocation:
			showDialog(LOCATION_SELECTION_DIALOG);
			break;
		}	
	}
	@Override
    public boolean onCreateOptionsMenu(Menu _menu)
    {
    	MenuInflater mi = getMenuInflater();
    	mi.inflate(R.menu.wifi_recorder_menu, _menu);

    	return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent i;
        switch (item.getItemId()) 
        {
        case R.id.wifiMenuExport:
        	i = new Intent();
        	i.setClass(getApplicationContext(), SaveInformationView.class);
        	
        	startActivityForResult(i, SAVE_INFORMATION_REQUEST_CODE);
            return true; 
        case R.id.menu_add_raw:
        	Log.e(TAG,"adding raw mp");
        	i = new Intent();
        	i.setClass(getApplicationContext(), CreateMpView.class);
        	i.putStringArrayListExtra("roomList", getRoomNameList());
        	startActivityForResult(i, CREATE_MP_REQUEST_CODE);
        	return true;
        case R.id.menu_load_fpi:
        	loadFPI();
        	return true;
        case R.id.menu_add_room:
        	Log.e(TAG,"adding room");
        	i = new Intent();
        	i.setClass(getApplicationContext(), CreateRoomView.class);
        	startActivityForResult(i, CREATE_ROOM_REQUEST_CODE);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	public void notifyCurrentMPFinished()
	{
		Log.e(TAG, "notified about mp finish");
		removeDialog(MEASUREMENT_IN_PROGRESS_DIALOG);
		
		MeasurementPoint mp = getNextMP();
		
		if(mAutoCheckbox.isChecked())
		{
			if(null==mp)
			{
				Toast.makeText(getApplicationContext(), "all measurements for "+mCurMpName+" finished",Toast.LENGTH_SHORT).show();
				return;
			}
			setCurrentMP(mp);
		}
		
	}
	
	public void notifyScanFinished(int _scanNr)
	{
		if(_scanNr%2==0)
		{
			Toast.makeText(getApplicationContext(), mCurMpName+":"+_scanNr+" scans received", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * returns the next MeasurementPoint after the passed
	 * MeasurementPoint.
	 * @param _mp the MeasurementPoint to get the next
	 * MeasurementPoint from
	 * @return the next MeasurementPoint, null if no 
	 * MeasurementPoint was found
	 */
	private MeasurementPoint getNextMP()
	{
		MeasurementPoint mp = getMPbyName(mCurMpName);
		for(int i = 0;i<mMPs.size();i++)
		{
			MeasurementPoint toCheck = mMPs.get(i);
			if(toCheck.equals(mp))
			{
				try
				{
					return mMPs.get(i+1);
				}
				catch(IndexOutOfBoundsException _aioobe)
				{
					return null;
				}
			}
		}
		return null;
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
	{
		switch(arg0.getId())
		{
		case R.id.WifiRecorderAutoModeCB:
			if(mMPs.isEmpty())
			{
				toastEmptyMpList();
				return;
			}
			boolean state = arg0.isChecked();
			if(null==mCurMpName||mCurMpName.isEmpty())
			{
				Log.e(TAG,"curMP was null");
				setCurrentMP(mMPs.get(0));
			}
			mChangeLoc.setEnabled(!state);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent data) 
	{
		if(null==data)
		{
			Log.e(TAG, "data was null");
			return;
		}
		switch(requestCode)
		{
		case CREATE_MP_REQUEST_CODE:

			String name = data.getStringExtra(CreateMpView.RESULT_BUNDLE_NAME_KEY);
			String roomNameForMp = data.getStringExtra(CreateMpView.RESULT_BUNDLE_ROOM_KEY);
			
			RawMeasurementPoint rawMp = new RawMeasurementPoint(name, roomNameForMp);
			if(null==getMPbyName(name))
			{
				mMPs.add(rawMp);
			}
			else
			{
				Toast.makeText(getApplicationContext(), "\""+name+"\" already exists, MP not added", Toast.LENGTH_SHORT).show();
			}
			
			break;
		case SAVE_INFORMATION_REQUEST_CODE:

			String relation = data.getStringExtra(SaveInformationView.RESULT_BUNDLE_RELATION_KEY);
			String fileName = data.getStringExtra(SaveInformationView.RESULT_BUNDLE_FILENAME_KEY);
			boolean res = ARFFGenerator.writeSurveyResultsToArff(mMPs, fileName, relation, ARFFType.ROOM, NO_BSSID_RSSI);
			if(res)
			{
				Toast.makeText(getApplicationContext(), "Export successful", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Export failed", Toast.LENGTH_SHORT).show();
			}
			break;
		case CREATE_ROOM_REQUEST_CODE:
			String roomName = data.getStringExtra(CreateRoomView.RESULT_BUNDLE_ROOM_NAME_KEY);
			Room r = new Room(roomName);
			mRooms.add(r);
			Log.e(TAG, "processing result from room creation:"+roomName);
			break;
		}
	}
	
	/**
	 * creates a list of all currently stored room names
	 * @return a list of all room names
	 */
	private ArrayList<String> getRoomNameList()
	{
		ArrayList<String> res = new ArrayList<String>();
		for(Room r:mRooms)
		{
			res.add(r.getName());
		}
		return res;
	}
	
	/**
	 * searches the list of rooms for a room with the passed name and returns it
	 * @param _name the name of the room to look for
	 * @return the room with the specified name, or null if the room was not found
	 */
	private Room getRoomByName(String _name)
	{
		for(Room r:mRooms)
		{
			if(r.getName().equals(_name))
			{
				return r;
			}
		}
		return null;
	}
}
