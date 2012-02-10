package at.sesame.fhooe.midsd.md;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.sesame.fhooe.esmart.model.EsmartMeasurementPlace;
import at.sesame.fhooe.esmart.service.EsmartDataAccess;
import at.sesame.fhooe.midsd.R;

public class MD_Fragment 
extends Fragment 
{
	@SuppressWarnings("unused")
	private static final String TAG = "MD_Fragment";

	private ViewPager mPager;
	private MyFragmentPagerAdapter mFragPageAdapter;

	private static final long FLIP_TIMEOUT = 500000;
	private AutomaticFlipper mFlipper;

	private LayoutInflater mLi;

	private Handler mHandler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) 
		{
			mPager.setCurrentItem(msg.arg1);
			return false;
		}
	});

	public MD_Fragment(Context _ctx)
	{
		mLi = LayoutInflater.from(_ctx);


	}

	@Override
	public void onAttach(Activity activity) 
	{
		super.onAttach(activity);

		buildView();

		
		mFragPageAdapter = new MyFragmentPagerAdapter(getFragmentManager(), buildFragments());
		new setAdapterTask().execute();
	}
	
	private List<Fragment> buildFragments()
	{
		List<Fragment> frags = new ArrayList<Fragment>();
		
		for(EsmartMeasurementPlace emp:EsmartDataAccess.getMeasurementPlaces())
		{
			frags.add(new MD_chartFragment(getActivity().getApplicationContext(), emp));
			frags.add(new MD_meterFragment());
		}
		return frags;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return buildView();
	}

	private View buildView()
	{
		View v =  mLi.inflate(R.layout.md_layout, null);
		mPager = (ViewPager)v.findViewById(R.id.viewPager);

		return v;
	}

	@Override
	public void onDestroyView() 
	{
		if(null!=mFlipper)
		{
			mFlipper.stopFlipping();
		}
		super.onDestroyView();
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

	private class setAdapterTask 
	extends AsyncTask<Void,Void,Void>
	{
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			mPager.setAdapter(mFragPageAdapter);
			if(null!=mFlipper)
			{
				mFlipper.stopFlipping();
			}
			mFlipper = new AutomaticFlipper(FLIP_TIMEOUT);
			mFlipper.startFlipping();
		}
	}

}
