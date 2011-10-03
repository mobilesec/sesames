/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.wifi;

import java.util.ArrayList;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * this class combines the information provided by WifiManager and
 * WifiBroadcastReceiver
 * @author Peter Riedl
 *
 */
public class WifiAccess
implements Runnable
{
	private static final String TAG = "WifiAccess";
	
	/**
	 * the WifiManager to retrieve information from
	 */
	private static WifiManager mManager;
	
	/**
	 * the actual instance used by the singleton
	 */
	private static WifiAccess mInstance;
	
	private long mScanTimeout = 1000;
	
	private boolean mThreadRunning = true;
	
	private Context mContext;
	
	private ArrayList<IWifiScanReceiver> mReceiver = new ArrayList<IWifiScanReceiver>();
	
	private WifiBroadcastReceiver mWifiReceiver;
	
	private static ArrayList<ScanResult> mCachedResults = new ArrayList<ScanResult>();
	
	/**
	 * creates a new WifiAccess with passed context
	 * @param _c the context for the WifiAccess
	 */
	private  WifiAccess(Context _c, boolean _startAutoScan)
	{
		mContext = _c;
		mManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);

//		mRecv = new WifiBroadcastReceiver(mContext, this);
		mWifiReceiver = new WifiBroadcastReceiver(mContext, this);
		WifiInfo info = mManager.getConnectionInfo();
		
		Log.e(TAG, "Wifi Status:"+info.toString());
		if(_startAutoScan)
		{
			startContinuousScanning();
		}
	}
	
	public void cleanUp()
	{
		mThreadRunning = false;
		mWifiReceiver.unregister();
	}
	
	/**
	 * returns the instance of WifiAccess
	 * @param _c the context to be set for the WifiAccess
	 * @return the instance of the WifiAccess
	 */
	public static WifiAccess getInstance(Context _c, boolean _startAutoScan)
	{
		if(null==mInstance)
		{
			mInstance = new WifiAccess(_c, _startAutoScan);
		}
		return mInstance;
	}
	
	/**
	 * adds a new receiver to the WifiBroadcastReceiver
	 * @param _recv the IWifiScanReceive to be added
	 */
	public void addWifiScanReceiver(IWifiScanReceiver _recv)
	{
		mReceiver.add(_recv);
	}
	
//	public String getConfiguredNetworks()
//	{
//		// List available networks
//		List<WifiConfiguration> configs = mManager.getConfiguredNetworks();
//		Log.e(TAG, "available nets:");
//		StringBuffer sb = new StringBuffer();
//		for (WifiConfiguration config : configs) {
//			sb.append(config.toString());
//		}
//		return sb.toString();
//	}
	
	public void notifyScanResultAvailable()
	{
		synchronized(mCachedResults)
		{
			Log.e("WifiAccess","printing received results");
			mCachedResults = getWifiScanResults();
			for(ScanResult sr:mCachedResults)
			{
				Log.e("WifiAcces", sr.toString());
			}
		}
		for(IWifiScanReceiver recv:mReceiver)
		{
			recv.setWifiScanResults(mCachedResults);
		}
	}
	
	public synchronized ArrayList<ScanResult> getCachedResults()
	{
		return mCachedResults;
	}
	
	/**
	 * triggers the WifiManager to start a scan
	 */
	public void startSingleWifiScan()
	{
		Log.e(TAG, "initiating scan");
		mManager.startScan();
	}
	
	public void startContinuousScanning()
	{
		mThreadRunning = true;
//		try
//		{
//			mRecv.register(mContext);
//		}
//		catch(Exception e)
//		{
//			
//		}
		new Thread(this).start();
	}
	
	/**
	 * retrieves the current WifiScanResults from the WifiManager
	 * @return the current WifiScanResults
	 */
	public ArrayList<ScanResult> getWifiScanResults()
	{
		return (ArrayList<ScanResult>) mManager.getScanResults();
	}

	@Override
	public void run() {
		while(mThreadRunning)
		{
			startSingleWifiScan();
			try {
				Thread.sleep(mScanTimeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
