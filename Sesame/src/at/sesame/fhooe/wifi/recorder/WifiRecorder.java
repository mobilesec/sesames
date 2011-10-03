/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.wifi.recorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import at.sesame.fhooe.R;
import at.sesame.fhooe.lib.wifi.IWifiScanReceiver;
import at.sesame.fhooe.lib.wifi.WifiAccess;
import at.sesame.fhooe.wifi.recorder.model.FingerPrint;
import at.sesame.fhooe.wifi.recorder.model.MeasurementPoint;

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
	 * the prefix for exported CSV files
	 */
	private static final String LOG_FILENAME_BASE = "fp";
	
	/**
	 * the filetype of exported CSV files
	 */
	private static final String LOG_FILE_TYPE = ".csv";
	
	/**
	 * the WifiAccess to register this class as WifiScanReceiver
	 */
	private WifiAccess mWifiAccess;
	
	/**
	 * the timeout between two wifi scans
	 */
	private static final int SCAN_TIMEOUT = 1000;
	
	/**
	 * boolean flag to start/stop the scanning thread
	 */
	private boolean mRunning = true;
	
	
	/**
	 * list of all available measurement points
	 */
	private ArrayList<MeasurementPoint> mMPs;
	
	/**
	 * the measurement point at which the fingerprints are recorded currently
	 */
	private MeasurementPoint mCurrentMP;
	
	/**
	 * a counter for the number of received scan results
	 */
	private int mScanCnt = 0;
	
	/**
	 * the context for the WifiRecorder
	 */
	private Context mContext;
	
	/**
	 * the button clicked when the specified MeasurementPoint is reached
	 */
	private Button mMpReachedButt;
	
	/**
	 * the prefix for the text to set the button
	 */
	private String mBaseButtonText;
	
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
	public WifiRecorder(Context _c, ArrayList<MeasurementPoint> _MPs, Button _mpReachedButt, WifiRecorderView _owner)
	{
		mContext = _c;
		mMPs = _MPs;
		mOwner = _owner;
		mMpReachedButt = _mpReachedButt;
		mBaseButtonText = (String) mMpReachedButt.getText();
		mMpReachedButt.setText(mBaseButtonText+mMPs.get(0).getName());
		mCurrentMP = mMPs.get(0);
		mWifiAccess = WifiAccess.getInstance(mContext, false);
		mWifiAccess.addWifiScanReceiver(this);
	}
	
	/**
	 * starts a new recording thread
	 */
	public void startRecording()
	{	
		String name = mCurrentMP.getName();
		Log.e(TAG, "current MP name:"+name);
		mOwner.showProgressDialog(name);
		mRunning = true;
		new Thread(this).start();
	}
	
	/**
	 * stops the recording thread
	 */
	public void stopRecording()
	{
		mScanCnt = 0;
		mRunning = false;
		mOwner.dismissProgressDialog();
	}
	
	
	/**
	 * gets the next MeasurementPoint in the list and starts
	 * recording for the current MeasurementPoint. The text of
	 * the "MP reached" button is also set here
	 */
	public void nextMP()
	{
		if(null==mCurrentMP)
		{
			mCurrentMP = mMPs.get(0);
		}
		MeasurementPoint next = getMpAfter(mCurrentMP);
		String text = "--------";
		if(null!=next)
		{
			text = mBaseButtonText+next.getName();
		}
		mMpReachedButt.setText(text);
		mMpReachedButt.setEnabled(false);
		startRecording();
	}
	
	/**
	 * when all measurements for all MeasurementPoints have
	 * finished the recording is stopped and the user is
	 * informed
	 */
	private void notifyFinished()
	{
		stopRecording();
		Toast.makeText(mContext, R.string.WifiRecorder_finishedToastMsg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * everytime the measurements for one MeasurementPoint are finished this method is called
	 * and retrieves the next MeasurementPoint. If the next point is null, notifyFinished() is
	 * called. If the next MeasurementPoint is not null, the "MP reached" button is enabled, 
	 * the next MeasurementPoint is set and recording is stopped.
	 */
	@SuppressWarnings("unused")
	private void currentMPFinished()
	{
		mScanCnt = 0;
		MeasurementPoint next = getMpAfter(mCurrentMP);
		if(null==next)
		{
			notifyFinished();
			return;
		}
		mMpReachedButt.setEnabled(true);
		setCurrentMP(next);
		stopRecording();
	}
	
	/**
	 * called by WifiBroadcastReceiver when scan results arrive
	 */
	@Override
	public void setWifiScanResults(ArrayList<ScanResult> _results) 
	{
		if(!isMpValid())
		{
			Toast.makeText(mContext, R.string.WifiRecorder_invalidLocationToastMsg, Toast.LENGTH_SHORT).show();
			return;
		}
		//synchronized (mCurrentMP) 
		{
			mScanCnt++;
			Toast.makeText(mContext, "Measurement #"+mScanCnt, Toast.LENGTH_SHORT).show();
			for(ScanResult sr:_results)
			{
				mCurrentMP.addFingerPrint(new FingerPrint(sr));
			}
			if(mScanCnt == mNoMeasurementsPerMP)
			{
				//this.currentMPFinished();
				stopRecording();
				
			}
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
	public void setCurrentMP(String _name)
	{
		mCurrentMP = getMPbyName(_name);
	}
	
	/**
	 * sets the current MeasurementPoint
	 * @param _name the MeasurementPoint to be set
	 */
	private void setCurrentMP(MeasurementPoint _mp)
	{
		mCurrentMP = _mp;
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
	
	/**
	 * returns the next MeasurementPoint after the passed
	 * MeasurementPoint.
	 * @param _mp the MeasurementPoint to get the next
	 * MeasurementPoint from
	 * @return the next MeasurementPoint, null if no 
	 * MeasurementPoint was found
	 */
	private MeasurementPoint getMpAfter(MeasurementPoint _mp)
	{
		Log.e(TAG, "passed MP:"+_mp.getName());
		for(int i = 0;i<mMPs.size();i++)
		{
			MeasurementPoint toCheck = mMPs.get(i);
			Log.e(TAG, "MP to check:"+toCheck.getName());
			if(toCheck.equals(_mp))
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
	public void run() 
	{
		while(mRunning)
		{
			Log.e(TAG, "scan started");
			mWifiAccess.startSingleWifiScan();
			try {
				Thread.sleep(SCAN_TIMEOUT);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * exports all MeasurementPoints with their Fingerprints
	 * to an CSV file
	 */
	public void export()
	{
		File dataDir = Environment.getExternalStorageDirectory();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		String destName = 	LOG_FILENAME_BASE+"_"+
							c.get(Calendar.DATE)+"_"+
							c.get(Calendar.MONTH)+"_"+
							c.get(Calendar.YEAR)+"_"+
							c.get(Calendar.HOUR_OF_DAY)+"_"+
							c.get(Calendar.MINUTE)+"_"+
							c.get(Calendar.SECOND)+
							LOG_FILE_TYPE;
		File dest = new File(dataDir.getAbsolutePath()+"/"+destName);
		
		try 
		{
			FileOutputStream fos = new FileOutputStream(dest);//mContext.openFileOutput(LOG_FILENAME, Context.MODE_WORLD_WRITEABLE);
			Log.e(TAG, "writing export file to:"+Environment.getDataDirectory().getAbsolutePath()+LOG_FILENAME_BASE);
			PrintWriter pw = new PrintWriter(fos);
			pw.write(getCSVHeader());
			for(MeasurementPoint mp:mMPs)
			{
				pw.write(mp.toCSVString());
			}
			pw.flush();
			fos.flush();
			pw.close();
			fos.close();
			Toast.makeText(mContext, mContext.getString(R.string.WifiRecorder_exportSuccessfulToastMsg) + " ("+dest.getAbsolutePath()+")", Toast.LENGTH_SHORT).show();
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * creates a String containing headers for all columns
	 * in the csv file
	 * @return String with header names
	 */
	private String getCSVHeader()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("Name of MP");
		sb.append(MeasurementPoint.CSV_SEPERATOR);
		sb.append("Room of MP");
		sb.append(MeasurementPoint.CSV_SEPERATOR);
		sb.append("X of MP");
		sb.append(MeasurementPoint.CSV_SEPERATOR);
		sb.append("Y of MP");
		sb.append(MeasurementPoint.CSV_SEPERATOR);
		sb.append("Timestamp of FP");
		sb.append(MeasurementPoint.CSV_SEPERATOR);
		sb.append("BSSID of FP");
		sb.append(MeasurementPoint.CSV_SEPERATOR);
		sb.append("Level of FP");
		sb.append("\r\n");
		
		return sb.toString();
	}
}
