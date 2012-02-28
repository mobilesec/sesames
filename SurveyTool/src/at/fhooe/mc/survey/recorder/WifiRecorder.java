/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.fhooe.mc.survey.recorder;

import java.util.ArrayList;

import android.net.wifi.ScanResult;
import android.util.Log;
import at.fhooe.mc.consts.ConstParameters;
import at.fhooe.mc.extern.fingerprintInformation.FingerPrint;
import at.fhooe.mc.extern.fingerprintInformation.MeasurementPoint;
import at.fhooe.mc.extern.wifi.IWifiScanReceiver;
import at.fhooe.mc.extern.wifi.WifiAccess;
import at.fhooe.mc.survey.view.RecorderView;


/**
 * this class records WifiScanResults, extracts Fingerprints from them and
 * associates those Fingerprints with the according MeasurementPoints
 * 
 * 
 */
public class WifiRecorder implements IWifiScanReceiver// , Runnable
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
	private int mNoMeasurementsPerMP;

	/**
	 * only used to show progress dialog
	 */
	private RecorderView mOwner;
	
	
	public boolean m_MPfinished;

	/**
	 * creates a new WifiRecorder
	 * 
	 * @param _c
	 *            the context of the WifiRecorder
	 * @param _MPs
	 *            a list of all available measurement points
	 */
	public WifiRecorder(RecorderView _owner) {
				
		mOwner = _owner;
		mWifiAccess = WifiAccess.getInstance(_owner.getApplicationContext(),
				false);
		mWifiAccess.addWifiScanReceiver(this);
	}

	/**
	 * starts a new recording thread
	 */
	public boolean startRecording() {

		//set number of counts from setting page here
		mNoMeasurementsPerMP = ConstParameters.countOfMeasures;
		
		String name = mCurrentMP.getName();
		Log.e(TAG, "current MP name:" + name);
		// new Thread(this).start();
		return mWifiAccess.startSingleWifiScan();
	}

	/**
	 * called by WifiBroadcastReceiver when scan results arrive
	 */
	@Override
	public synchronized void setWifiScanResults(ArrayList<ScanResult> _results) {
		Log.e(TAG, "-------------------------setWifiScan");
		Log.e(TAG, "---- Scan Result arrived ----");
		
		if(m_MPfinished){
			Log.e(TAG, "in MPFinished = true");
			return;
		}
//		
		if (!isMpValid()) {
			return;
		}

		mScanCnt++;
		Log.e(TAG, "SCAN COUNT=" + mScanCnt);
		mOwner.notifyScanFinished(mScanCnt);
		for (ScanResult sr : _results) {
			mCurrentMP.addFingerPrint(new FingerPrint(sr, mScanCnt));
		}

		if (mScanCnt == mNoMeasurementsPerMP) {
			mScanCnt = 0;
			mOwner.notifyCurrentMPFinished();
		} else {
			startRecording();
		}
	}

	/**
	 * returns whether the current MeasurementPoint is valid or not
	 * 
	 * @return true if the current MeasurementPoint is valid, false otherwise
	 */
	private boolean isMpValid() {
		return null != mCurrentMP;
	}

	/**
	 * sets the current MeasurementPoint
	 * 
	 * @param _name
	 *            the name of the MeasurementPoint to be set
	 */
	public void setCurrentMP(MeasurementPoint _mp) {
		mCurrentMP = _mp;
	}

}
