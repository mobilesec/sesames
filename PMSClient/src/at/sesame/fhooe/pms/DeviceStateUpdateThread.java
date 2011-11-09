package at.sesame.fhooe.pms;

import java.util.ArrayList;

import android.util.Log;
import at.sesame.fhooe.lib.pms.model.ControllableDevice;

public class DeviceStateUpdateThread 
extends Thread 
{
	private static final String TAG = "DeviceStateUpdateThread";

	private PMSClientActivity mOwner;
	private ArrayList<ControllableDevice> mDevs;
	private boolean mUpdating = true;
	private final Object mPauseLock = new Object();

	private long mUpdatePeriod = 5000;

	public DeviceStateUpdateThread(PMSClientActivity _owner, ArrayList<ControllableDevice> _devs)
	{
		mOwner = _owner;
		mDevs = _devs;
	}

	@Override
	public void run() 
	{
		//		super.run();
		//		synchronized (mPauseLock) 
		{
			while(mUpdating)
			{
				try 
				{
					Thread.sleep(mUpdatePeriod );
				} 
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//					e.printStackTrace();
					Log.e(TAG, "interrupted");
				}
				if(!mUpdating)
				{
					Log.e(TAG, "paused...continue");
					continue;
				}
				Log.e(TAG, "updating");
				for(ControllableDevice cd:mDevs)
				{
					if(mUpdating)
					{
						cd.updateStatus();
						Log.e(TAG, cd.getHostname()+" updated");
					}
				}
				mOwner.notifyDataUpdated();

			}
		}
	}

	public void pause()
	{
		//		synchronized (mPauseLock)
		{
			Log.e(TAG, "paused");
			//			mUpdating = false;
			//			interrupt();

			//			synchronized(DeviceStateUpdateThread.this)
			{
				mUpdating = false;
			}


		}
	}

	public void resumeAfterPause()
	{
		//		synchronized (this) 
		{
			Log.e(TAG, "resumedAfterPause");
			mUpdating = true;
			Log.e(TAG, "state:"+this.getState().name());
//			start();
			//						run();
		}
		//		notify();
	}

//	public void onResume()
//	{
//		if(thread == null){
//			thread = new Thread()
//			{
//				@Override
//				public void run() 
//				{
//					try {
//
//					}
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//
//		thread.start();
//	}
//}



}
