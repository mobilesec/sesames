/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.survey.wifi.recorder;


import java.util.ArrayList;

import android.net.wifi.ScanResult;
import android.util.Log;
import android.widget.Toast;
import at.sesame.fhooe.lib.fingerprintInformation.FingerPrint;
import at.sesame.fhooe.lib.fingerprintInformation.MeasurementPoint;
import at.sesame.fhooe.lib.wifi.IWifiScanReceiver;
import at.sesame.fhooe.lib.wifi.WifiAccess;
import at.sesame.fhooe.survey.WifiRecorderView;

/**
 * this class records WifiScanResults, extracts Fingerprints from them and
 * associates those Fingerprints with the according MeasurementPoints
 * @author Peter Riedl
 *
 */
public class WifiRecorder
implements IWifiScanReceiver, Runnable
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "WifiRecorder";
	
	/**
	 * the WifiAccess to register this class as WifiScanReceiver
	 */
	private WifiAccess mWifiAccess;
	
	/**
	 * the measurement point at which the fingerprints are recorded currently
	 */
	private MeasurementPoint mCurrentMP;
	
	/**
	 * a counter for the number of received scan results
	 */
	private int mScanCnt = 0;
	
	/**
	 * number of measurements per MeasurementPoint
	 */
	private int mNoMeasurementsPerMP = 10;
	
	/**
	 * only used to show progress dialog
	 */
	private WifiRecorderView mOwner;
	
	/**
	 * creates a new WifiRecorder 
	 * @param _c the context of the WifiRecorder
	 * @param _MPs a list of all available measurement points
	 */
	public WifiRecorder(WifiRecorderView _owner)
	{
		mOwner = _owner;
		mWifiAccess = WifiAccess.getInstance(_owner.getApplicationContext(), false);
		mWifiAccess.addWifiScanReceiver(this);
	}
	
	/**
	 * starts a new recording thread
	 */
	public void startRecording()
	{	
		if(null==mCurrentMP)
		{
			Toast.makeText(mOwner, "invalid MP", Toast.LENGTH_SHORT).show();
			return;
		}
		String name = mCurrentMP.getName();
		Log.e(TAG, "current MP name:"+name);
		new Thread(this).start();
	}
	
	/**
	 * called by WifiBroadcastReceiver when scan results arrive
	 */
	@Override
	public synchronized void setWifiScanResults(ArrayList<ScanResult> _results) 
	{

		if(!isMpValid())
		{
			return;
		}

		mScanCnt++;
		Log.e(TAG,"SCAN COUNT="+mScanCnt);
		mOwner.notifyScanFinished(mScanCnt);
		for(ScanResult sr:_results)
		{
			mCurrentMP.addFingerPrint(new FingerPrint(sr, mScanCnt));
		}
		
		
		if(mScanCnt == mNoMeasurementsPerMP)
		{
			mScanCnt = 0;
			mOwner.notifyCurrentMPFinished();
		}
		else
		{
			startRecording();
		}
	}
	
	/**
	 * returns whether the current MeasurementPoint is valid or not
	 * @return true if the current MeasurementPoint is valid, false otherwise
	 */
	private boolean isMpValid()
	{
		return null!=mCurrentMP;
	}
	
	/**
	 * sets the current MeasurementPoint
	 * @param _name the name of the MeasurementPoint to be set
	 */
	public void setCurrentMP(MeasurementPoint _mp)
	{
		mCurrentMP = _mp;
	}
	
	@Override
	public void run() 
	{
		Log.e(TAG, "scan started");
		mWifiAccess.startSingleWifiScan();
	}
}
