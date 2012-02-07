package at.sesame.fhooe.viewexperiments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;

public class ViewExperimentsActivity 
extends KioskActivity
{
	private static final String TAG = "ViewExperimentsActivity";
	private ViewPager mPager;
	
	private static final long FLIP_TIMEOUT = 500;
	private AutomaticFlipper mFlipper;
	
	private static final long BRIGHTNESS_PERIOD = 100;
	private AutomaticBrightnessAdjuster mBrightnessAdjuster;
	
	private static final int SCREEN_FLIP_MESSAGE = 0;
	private static final int BRIGHTNESS_ADJUST_MESSAGE = 1;
	
	private static final String BRIGHTNESS_VALUE_KEY = "brightness";
	
	private Handler mHandler = new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what)
			{
			case SCREEN_FLIP_MESSAGE:
				mPager.setCurrentItem(msg.arg1);
				break;
			case BRIGHTNESS_ADJUST_MESSAGE:
				setScreenBrightness(msg.getData().getFloat(BRIGHTNESS_VALUE_KEY));
				break;
			}
			
			return false;
		}
	});
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mPager = (ViewPager)findViewById(R.id.viewPager);
		
		List<Fragment> frags = new ArrayList<Fragment>();
//		frags.add(new ColorFragment(this));
		for(int i = 0;i<3;i++)
		{
			frags.add(new DummyFragment("Dummy"+i));
		}
		frags.add(new ColorFragment(this));

		MyFragmentPagerAdapter mfpa = new MyFragmentPagerAdapter(getSupportFragmentManager(), frags);
		mPager.setAdapter(mfpa);
		

		mFlipper = new AutomaticFlipper(FLIP_TIMEOUT);
		mFlipper.startFlipping();
		
		mBrightnessAdjuster = new AutomaticBrightnessAdjuster(BRIGHTNESS_PERIOD);
		mBrightnessAdjuster.startAdjusting();
//		enableKioskMode();
//		StatusBarRemover.hideStatusBar();
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		StatusBarRemover.showStatusBar();
	}

	@Override
	public void onDestroy() 
	{
		if(null!=mFlipper)
		{
			mFlipper.stopFlipping();
		}
		if(null!=mBrightnessAdjuster)
		{
			mBrightnessAdjuster.stopAdjusting();
		}
		super.onDestroy();
	}
	
	private void setScreenBrightness(float _val)
	{
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.screenBrightness = _val; // set 50% brightness
		getWindow().setAttributes(layoutParams);

	}

	private class AutomaticBrightnessAdjuster extends Thread
	{
		private long mPeriod;
		
		private boolean mRunning;
		private float mBrightness = 1f;
		private float mBrightnessStep = 0.1f;
		
		public AutomaticBrightnessAdjuster(long _period)
		{
			mPeriod = _period;
			assureBrightnessIsNotSetToAuto();
		}
		
		@Override
		public void run()
		{
			while(mRunning)
			{
				if(mBrightness>=1||mBrightness<=0)
				{
					mBrightnessStep*=-1;
				}

				mBrightness+=mBrightnessStep;
				Message m = new Message();
				m.what = BRIGHTNESS_ADJUST_MESSAGE;
				Bundle data = new Bundle();
				data.putFloat(BRIGHTNESS_VALUE_KEY, mBrightness);
				m.setData(data);
				mHandler.sendMessage(m);
				try {
					Thread.sleep(mPeriod);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		private void assureBrightnessIsNotSetToAuto()
		{
			Log.e("", "assuring brightness is not set to auto");
			int brightnessMode =Integer.MIN_VALUE;
			try {
				brightnessMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
			    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			    Log.e("", "brightness mode set to manual");
			}
		}
		
		public void startAdjusting()
		{
			mRunning = true;
			this.start();
		}

		public void stopAdjusting()
		{
			mRunning = false;
		}
	}

	private class AutomaticFlipper extends Thread
	{
		private long mFlipTimeout;

		private boolean mRunning;
		private int mIdx = 0;

		public AutomaticFlipper(long _flipTimeout)
		{
			mFlipTimeout = _flipTimeout;
		}

		private void incrementIndex()
		{
			mIdx++;
			int noFrags = mPager.getAdapter().getCount();
			mIdx %= noFrags;
		}

		public int getIndex()
		{
			return mIdx;
		}

		@Override
		public void run()
		{
			while(mRunning)
			{
				Message m = new Message();
				m.what = SCREEN_FLIP_MESSAGE;
				m.arg1=getIndex();
				mHandler.sendMessage(m);

				incrementIndex();
				
				try 
				{
					Thread.sleep(mFlipTimeout);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}

		public void startFlipping()
		{
			mRunning = true;
			this.start();
		}

		public void stopFlipping()
		{
			mRunning = false;
		}
	}
}