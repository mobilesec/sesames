/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.wifi.recorder;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;
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
import at.sesame.fhooe.R;
import at.sesame.fhooe.wifi.recorder.model.AccessPoint;
import at.sesame.fhooe.wifi.recorder.model.FingerPrintItem;
import at.sesame.fhooe.wifi.recorder.model.MeasurementPoint;
import at.sesame.fhooe.wifi.recorder.model.FingerPrintItem.Type;

/**
 * this class implements the GUI for measurement of fingerprints
 * @author Peter Riedl
 *
 */
public class WifiRecorderView
extends Activity
implements OnClickListener, OnCheckedChangeListener
{
	/**
	 * id for the location selection dialog
	 */
	private static final int LOCATION_SELECTION_DIALOG = 0;
	
	
	
	/**
	 * the WifiRecorder to record, store and export the fingerprints
	 */
	private WifiRecorder mRecorder;
	
	/**
	 * the button to start recording
	 */
	private Button mStart;
	
	/**
	 * the button to stop recording
	 */
	private Button mStop;
	
	/**
	 * the button to open the location selection dialog
	 */
	private Button mChangeLoc;
	
	/**
	 * pressed when the user has reached the current MeasurementPoint
	 */
	private Button mLocReached;
	
	/**
	 * a list of all MeasurementPoints extracted from the xml file
	 */
	private ArrayList<MeasurementPoint> mMPs = new ArrayList<MeasurementPoint>();
	
	/**
	 * a list of all AccessPoints extracted from the xml file.
	 */
	private ArrayList<AccessPoint> mAPs = new ArrayList<AccessPoint>();
	
	/**
	 * a list of all names of MeasurementPoints
	 */
	private String[] mLocations;
	
	/**
	 * the name of the current MeasurementPoint
	 */
	private String mCurMpName;
	
	/**
	 * the progressdialog to show during measurement
	 */
	private ProgressDialog mPD;
	
	@Override
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		
		
		setContentView(R.layout.wifi_recorder_view);
		
		CheckBox autoBox = (CheckBox)findViewById(R.id.WifiRecorderAutoModeCB);
		autoBox.setChecked(false);
		autoBox.setOnCheckedChangeListener(this);
		
		mStart = (Button)findViewById(R.id.startWifiRecording);
		mStart.setEnabled(true);
		mStart.setOnClickListener(this);
		
		mStop = (Button)findViewById(R.id.stopWifiRecording);
		mStop.setEnabled(false);
		mStop.setOnClickListener(this);
		
		mChangeLoc = (Button)findViewById(R.id.setWifiRecordingLocation);
		mChangeLoc.setEnabled(true);
		mChangeLoc.setOnClickListener(this);
		
		mLocReached = (Button)findViewById(R.id.MPReachedButton);
		mLocReached.setOnClickListener(this);
		
		getMpAndAp();
		createLocations();
		
		mRecorder = new WifiRecorder(getApplicationContext(),mMPs, mLocReached, this);
		
	}
	

	


	/**
	 * initiates parsing of the xml file storing FingerprintItems
	 */
	private void getMpAndAp() 
	{
		try {
			ArrayList<FingerPrintItem> items = new FingerPrintItemParser().parseFingerprintItems(getResources().getXml(R.xml.fpi));
			for(FingerPrintItem fpi:items)
			{
				if(fpi.getType().equals(Type.MEASUREMENT_POINT))
				{
					mMPs.add((MeasurementPoint)fpi);
				}
				else if(fpi.getType().equals(Type.ACCESS_POINT))
				{
					mAPs.add((AccessPoint)fpi);
				}
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createLocations() 
	{
		mLocations = new String[mMPs.size()];
		for(int i = 0;i<mLocations.length;i++)
		{
			mLocations[i] = mMPs.get(i).getName();
		}
	}
	
	/**
	 * shows a dialog while recording fingerprints at a
	 * given MeasurementPoint
	 * @param _curMpName the name of the current MeasurementPoint
	 */
	public void showProgressDialog(String _curMpName)
	{
		if(null==_curMpName)
		{
			return;
		}
		mCurMpName = _curMpName;
		String msg = getString(R.string.WifiRecorder_progressDialogPrefix)+mCurMpName;
		Log.e("WifiRecorderView", "msg:"+msg);
		mPD = ProgressDialog.show(this, "", msg, true);
	}
	
	/**
	 * dismisses the progress dialog while recording fingerprints
	 */
	public void dismissProgressDialog()
	{
		mPD.dismiss();
	}
	
	@Override
	public Dialog onCreateDialog(int _id)
	{
		Dialog d = null;
		switch(_id)
		{
		case LOCATION_SELECTION_DIALOG:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.WifiRecorderView_chooseLocationDialogTitle);
			builder.setItems(mLocations, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) 
			    {
			        mRecorder.setCurrentMP(mLocations[item]);
			    }
			});
			d = builder.create();
			d.show();
			break;

		}
		return d;
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
		case R.id.startWifiRecording:
			mRecorder.startRecording();
			//mStop.setEnabled(true);
			//mStart.setEnabled(false);
			break;
		case R.id.stopWifiRecording:
			mRecorder.stopRecording();
			//mStart.setEnabled(true);
			//mStop.setEnabled(false);
			break;
		case R.id.MPReachedButton:
			mRecorder.nextMP();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) 
        {
        case R.id.wifiMenuExport:
        	mRecorder.export();
            return true; 	
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
	{
		switch(arg0.getId())
		{
		case R.id.WifiRecorderAutoModeCB:
			boolean state = arg0.isChecked();
			mStart.setEnabled(!state);
			mStop.setEnabled(!state);
			mChangeLoc.setEnabled(!state);
			mLocReached.setEnabled(state);
			break;
		}
		
	}
}
