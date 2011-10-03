/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.wifi;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * receives Broadcasts from the WifiManager and publishes them
 * @author Peter Riedl
 *
 */
public class WifiBroadcastReceiver 
extends BroadcastReceiver 
{
//	/**
//	 * the WifiAccess to inform about new ScanResults
//	 */
//	private WifiAccess mWifiAccess;
//	
	/**
	 * a list of all WifiScanResultReceivers to be notified
	 * about ScanResults
	 */
	private ArrayList<IWifiScanReceiver> mRecvs;
	
	
	
	private static WifiAccess mWifiAccess;
	
//	public WifiBroadcastReceiver(Context _c, WifiAccess _wa)
//	{
//		super();
//		setOrderedHint(true);
//		mWifiAccess = _wa;
//		register(_c);
//	}
	
//	/**
//	 * registers this WifiBroadcastReceiver at the WifiManager to receive Broadcasts
//	 * @param _c the context to register
//	 */
//	public void register(Context _c)
//	{
//		//_c.registerReceiver(this, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//	}
//	/**
//	 * adds a new receiver to the WifiBroadcastReceiver
//	 * @param _recv the IWifiScanReceive to be added
//	 */
//	public void addIWifiScanReceiver(IWifiScanReceiver _recv)
//	{
//		if(null==mRecvs)
//		{
//			mRecvs = new ArrayList<IWifiScanReceiver>();
//		}
//		mRecvs.add(_recv);
//	}
	
	public static void setWifiAccess(WifiAccess _access)
	{
		mWifiAccess = _access;
	}
	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		Log.e("WifiBroadcastReceiver", "received wifi data");
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
				// TODO Auto-generated method stub
		if(null==mWifiAccess)
		{
			Log.e("WifiBroadcastReceiver","wifiAccess was null");
			return;
		}
				mWifiAccess.notifyScanResultAvailable();
//			}
//		}).start();
		

//		Log.e("WifiBroadCastReceiver", "result received");
//		ArrayList<ScanResult> res = mWifiAccess.getWifiScanResults();
//		for(IWifiScanReceiver recv:mRecvs)
//		{
//			recv.setWifiScanResults(res);
//		}
	}

}
