/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
	private WifiAccess mWifiAccess;
	private Context mContext;
	
	public WifiBroadcastReceiver(Context _c, WifiAccess _wa)
	{
//		super();
//		setOrderedHint(true);
		mContext = _c;
		mWifiAccess = _wa;
		register();
	}
	
	/**
	 * registers this WifiBroadcastReceiver at the WifiManager to receive Broadcasts
	 * @param _c the context to register
	 */
	private void register()
	{
		mContext.registerReceiver(this, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}
	
	public void unregister()
	{
		try
		{
			mContext.unregisterReceiver(this);
		}
		catch(Exception _e)
		{
		}
	}
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
	

	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
//		Log.e("WifiBroadcastReceiver", "received wifi data");
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
				// TODO Auto-generated method stub
		if(null==mWifiAccess)
		{
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
