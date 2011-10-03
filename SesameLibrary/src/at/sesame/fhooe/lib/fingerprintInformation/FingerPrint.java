/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.fingerprintInformation;
import java.util.Date;

import android.net.wifi.ScanResult;

/**
 * this class represents a wifi fingerprint consisting
 * of all information android provides for a ScanResult
 * @author Peter Riedl
 *
 */
public class FingerPrint 
{
	/**
	 * the BSSID associated with the FingerPrint
	 */
	private String mBssid;
	
	/**
	 * the signal level
	 */
	private int mLevel;
	
	/**
	 * the current time in ms
	 */
	private long mTimeStamp;
	
	/**
	 * the id of the scan this fingerprint is associated with
	 */
	private int mScanID = -1;
	
	/**
	 * creates a new Fingerprint
	 * @param _sr the ScanResult containing needed information
	 */
	public FingerPrint(ScanResult _sr, int _scanID)
	{
		this(_sr.BSSID, _sr.level, _scanID);
	}
	
	/**
	 * creates a new FingerPrint
	 * @param _bssid the BSSID of the FingerPrint
	 * @param _level the signal level of the FingerPrint
	 */
	public FingerPrint(String _bssid, int _level, int _scanID) 
	{
		mTimeStamp = System.currentTimeMillis();
		this.mBssid = _bssid;
		this.mLevel = _level;
		this.mScanID = _scanID;
//		Log.e(TAG, mCal.getTime().toLocaleString());
	}

	@Override
	public String toString() 
	{
		Date d = new Date(mTimeStamp);
		int millis = (int) (mTimeStamp-(mTimeStamp%1000));
		return "\n-----\nFingerPrint [scanID="+mScanID+", mBssid=" + mBssid + ", mLevel=" + mLevel
				+ ", mCal=" + d.toLocaleString() + "("+millis+")]";
	}
	
	/**
	 * creates an CSV representation of the FingerPrint
	 * @return an CSV representation of the FingerPrint
	 */
	public String toCSVString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(mTimeStamp);
		sb.append(MeasurementPoint.CSV_SEPERATOR);
		sb.append(mBssid);
		sb.append(MeasurementPoint.CSV_SEPERATOR);
		sb.append(mLevel);
		sb.append("\r\n");
		return sb.toString();
		
	}
	
	
	public String getBSSID()
	{
		return mBssid;
	}
	
	public int getLevel()
	{
		return mLevel;
	}
	
	public int getScanID()
	{
		return mScanID;
	}

}
