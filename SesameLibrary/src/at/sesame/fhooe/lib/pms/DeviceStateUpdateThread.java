/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 11/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms;

import java.util.ArrayList;

import android.util.Log;
import at.sesame.fhooe.lib.pms.model.ControllableDevice;
import at.sesame.fhooe.lib.pms.model.ExtendedPMSStatus;

public class DeviceStateUpdateThread 
extends Thread 
{
	private static final String TAG = "DeviceStateUpdateThread";

	private ArrayList<ControllableDevice> mDevs;
	private ArrayList<String> mMacs;
	private boolean mUpdating = true;
	private IPMSUpdateListener mUpdateListener;

	private long mUpdatePeriod = 10000;
	
	private String mUser;
	private String mPass;

	public DeviceStateUpdateThread(IPMSUpdateListener _updateListener, ArrayList<ControllableDevice> _devs, String _user, String _pass)
	{
		mUser = _user;
		mPass = _pass;
		mUpdateListener = _updateListener;
		mDevs = _devs;
		
		mMacs = new ArrayList<String>();
		for(ControllableDevice cd:mDevs)
		{
			mMacs.add(cd.getMac());
		}
	}

	@Override
	public void run() 
	{
		while(mUpdating)
		{
			Log.e(TAG, "updating");

			long begin = System.currentTimeMillis();
			ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getPMS(mUser, mPass).extendedStatusList(mMacs);

			if(null==statuses)
			{
				Log.e(TAG, "update failed");
				continue;
			}
			else
			{
				double duration = System.currentTimeMillis()-begin;
				Log.e(TAG, "update took "+(duration/1000)+" seconds");
			}
			
			Log.e(TAG, "received statuses:"+statuses.size());
			
			for(int i = 0;i<statuses.size();i++)
			{
				if(!mUpdating)
				{
					break;
				}

				for(ControllableDevice cd:mDevs)
				{
					if(!mUpdating)
					{
						break;
					}
					if(cd.getMac().equals(statuses.get(i).getMac().toLowerCase()))
					{
						cd.setExtendedPMSStatus(statuses.get(i));
						Log.e(TAG, mDevs.get(i).getHostname()+" updated");
					}
				}
			}
			mUpdateListener.notifyPMSUpdated();
			try 
			{
				Thread.sleep(mUpdatePeriod );
			} 
			catch (InterruptedException e) 
			{
				Log.e(TAG, "interrupted");
			}
		}

		Log.e(TAG, "update thread finished");
	}


	public synchronized void stopUpdating()
	{
		mUpdating = false;
	}
}
