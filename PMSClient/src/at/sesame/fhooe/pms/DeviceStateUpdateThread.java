/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 11/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.pms;

import java.util.ArrayList;
import java.util.Arrays;

import org.codehaus.jackson.mrbean.MrBeanModule;

import android.util.Log;
import at.sesame.fhooe.lib.pms.PMSProvider;
import at.sesame.fhooe.lib.pms.model.ControllableDevice;
import at.sesame.fhooe.lib.pms.model.ExtendedPMSStatus;

public class DeviceStateUpdateThread 
extends Thread 
{
	private static final String TAG = "DeviceStateUpdateThread";

	private PMSClientActivity mOwner;
	private ArrayList<ControllableDevice> mDevs;
	private ArrayList<String> mMacs;
	private boolean mUpdating = true;

	private long mUpdatePeriod = 15000;

	public DeviceStateUpdateThread(PMSClientActivity _owner, ArrayList<ControllableDevice> _devs)
	{
		mOwner = _owner;
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
		{
			while(mUpdating)
			{
				Log.e(TAG, "updating");
				try 
				{
					Thread.sleep(mUpdatePeriod );
				} 
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//					e.printStackTrace();
					Log.e(TAG, "interrupted");
				}
				
//				Log.e(TAG, "updating");
				
				ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getPMS().extendedStatusList(mMacs);
				
				if(null==statuses)
				{
					Log.e(TAG, "update failed");
					continue;
				}
//				Log.e(TAG, "pms status list:"+Arrays.toString(statuses.toArray()));
				Log.e(TAG, "received statuses:"+statuses.size());
				for(int i = 0;i<statuses.size();i++)
				{
					if(!mUpdating)
					{
						break;
					}
//					boolean valueSet = false;
//					Log.e(TAG)
//					if(mUpdating)
					{
//						cd.updateStatus();
//						cd.setExtendedPMSStatus(_status)
//						Log.e(TAG, cd.getHostname()+" updated");
						
//						mDevs.get(i).setExtendedPMSStatus(statuses.get(i));
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
//								valueSet = true;
							}
						}
						
//						if(!valueSet)
//						{
//							Log.e
//						}
						
						
					}
				}
				mOwner.notifyDataUpdated();

			}
		}
		Log.e(TAG, "update thread finished");
	}

//	public void pause()
//	{
//		//		synchronized (mPauseLock)
//		{
//			Log.e(TAG, "paused");
//			//			mUpdating = false;
//			//			interrupt();
//
//			//			synchronized(DeviceStateUpdateThread.this)
//			{
//				mUpdating = false;
//			}
//
//
//		}
//	}
	
	public synchronized void stopUpdating()
	{
		mUpdating = false;
	}

//	public void resumeAfterPause()
//	{
//		//		synchronized (this) 
//		{
//			Log.e(TAG, "resumedAfterPause");
//			mUpdating = true;
//			Log.e(TAG, "state:"+this.getState().name());
//			this.
////			start();
//			//						run();
//		}
//		//		notify();
//	}
}
