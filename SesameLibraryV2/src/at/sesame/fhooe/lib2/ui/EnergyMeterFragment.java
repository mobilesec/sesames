package at.sesame.fhooe.lib2.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EnergyMeterFragment 
extends Fragment 
{
	private static final String TAG = "EnergyMeterFragment";

	private EnergyMeter mMeter;
	private boolean mRunning = true;
	private Runnable autoInValiate = new Runnable() {
		//		Random r = new Random();
		private double val = 0.0f;
		@Override
		public void run() 
		{
			while(mRunning)
			{
				try 
				{
					mMeter.setValue(val);
					val+=5;
					Thread.sleep(1000);
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
					val = 0.0f;
				}
			}

		}
	};

	public EnergyMeterFragment(Context _c)
	{
		mMeter = new EnergyMeter(_c);

	}

	public void onResume()
	{
		super.onResume();
		mRunning = true;
		new Thread(autoInValiate).start();
	}
	public void onPause()
	{
		super.onPause();
		mRunning = false;
	}

	public void onDestroy()
	{
		super.onDestroy();
		mRunning = false;
	}

	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstance)
	{
		super.onCreateView(_inflater, _container, _savedInstance);
		Log.e(TAG, "onCreateView");
		//		return _inflater.inflate(R.layout.energy_meter_fragment_layout, null);
		return mMeter;
	}

}
