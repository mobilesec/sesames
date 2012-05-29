/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 11/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib2.pms;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;
import at.sesame.fhooe.lib2.pms.model.ExtendedPMSStatus;

public class DeviceStateUpdater 
{
	private static final String TAG = "DeviceStateUpdater";

	private ArrayList<ControllableDevice> mDevs;
	private ArrayList<String> mMacs;
	private boolean mUpdating = false;
//	private IPMSUpdateListener mUpdateListener;

	private long mUpdatePeriod = 10000;

	private Timer mUpdateTimer = new Timer();
	
	private static volatile boolean mUpdateInProgress = false;

//	private String mUser;
//	private String mPass;

//	private PmsHelper mUiHelper;
	public DeviceStateUpdater(ArrayList<ControllableDevice> _devs)
	{
//		mUser = _user;
//		mPass = _pass;
//		mUpdateListener = _updateListener;
//		mUiHelper = _uiHelper;
		mDevs = _devs;

		mMacs = new ArrayList<String>();
		for(ControllableDevice cd:mDevs)
		{
			mMacs.add(cd.getMac());
		}
	}
	private class UpdateTask extends TimerTask
	{
		@Override
		public void run() 
		{
			updateAllDevices();
		}
	}
	
	public boolean updateAllDevices()
	{
		Log.e(TAG, "*****************updating");
		mUpdateInProgress = true;
		long begin = System.currentTimeMillis();
		ArrayList<ExtendedPMSStatus> statuses = null;
		try
		{
			statuses = PMSProvider.getPMS().extendedStatusList(mMacs);			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			mUpdateInProgress = false;
			return false;
		}

		if(null==statuses || statuses.isEmpty())
		{
			Log.e(TAG, "update failed");
			mUpdateInProgress = false;
			return false;
		}
		else
		{
			double duration = System.currentTimeMillis()-begin;
			Log.i(TAG, "update took "+(duration/1000)+" seconds");
		}

		Log.i(TAG, "received statuses:"+statuses.size());

		for(int i = 0;i<statuses.size();i++)
		{
//			if(!mUpdating)
//			{
//				break;
//			}

			for(ControllableDevice cd:mDevs)
			{
//				if(!mUpdating)
//				{
//					break;
//				}
				if(cd.getMac().equals(statuses.get(i).getMac().toLowerCase()))
				{
					cd.setExtendedPMSStatus(statuses.get(i));
//					if(null!=mUiHelper)
//					{
//						mUiHelper.markDirty(cd, false);								
//					}
//					Log.e(TAG, mDevs.get(i).getHostname()+" updated");
				}
			}
		}
		mUpdateInProgress = false;
		return true;
//		mUpdateListener.notifyPMSUpdated();
//		try 
//		{
//			Thread.sleep(mUpdatePeriod );
//		} 
//		catch (InterruptedException e) 
//		{
//			Log.e(TAG, "interrupted");
//		}
	}
	
	

	public static boolean isUpdateInProgress() {
		return mUpdateInProgress;
	}



//	public void setmUpdateInProgress(boolean mUpdateInProgress) {
//		this.mUpdateInProgress = mUpdateInProgress;
//	}



	public void startUpdating()
	{
		stopUpdating();
		mUpdating = true;
		mUpdateTimer = new Timer();
		mUpdateTimer.schedule(new UpdateTask(), 0, mUpdatePeriod);
	}
	public void stopUpdating()
	{
		mUpdating = false;
		if(null!=mUpdateTimer)
		{
			mUpdateTimer.cancel();
			mUpdateTimer.purge();
		}
	}
}
