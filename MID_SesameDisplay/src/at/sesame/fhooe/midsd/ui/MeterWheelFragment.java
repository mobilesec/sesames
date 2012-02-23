package at.sesame.fhooe.midsd.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.sesame.fhooe.lib.ui.EnergyMeter;
import at.sesame.fhooe.midsd.R;

public class MeterWheelFragment
extends Fragment
{
	private static final String TAG = "MeterWheelFragment";
	private Context mCtx;
	private static final int NO_WHEEL_DIGITS = 5;
	private static final long SIMULATION_TIMEOUT = 5000;

	private int mWheelTextSize = 100;

	private EnergyMeter mMeter;
	//	private WheelFragment mWheel;


	private Timer mSimulationTimer;
	private FragmentManager mFragMan;

	private static final String[]mDigits = new String[]{"0","1","2","3","4","5","6","7","8","9"};
	private int mNumDigits;
	private ArrayWheelAdapter<String> mAdapter;
	private ArrayList<WheelView> mWheels;
	private LinearLayout mWheelContainer;
	
	private String mHeaderText;
	
	private String mBottomText;

	private Handler mUiHandler;
	@Override
	public void onAttach(Activity activity) 
	{
		startSimulation();
		super.onAttach(activity);
	}


	public MeterWheelFragment(Context _ctx, Handler _uiHandler, String _header, String _bottom, int _wheelTextSize, int _numDigits)
	{
		mCtx = _ctx;
		mUiHandler = _uiHandler;
		
		mHeaderText = _header;
		mBottomText = _bottom;
		mMeter = new EnergyMeter(mCtx);

		mWheelTextSize = _wheelTextSize;
		mNumDigits = _numDigits;
		mAdapter =new ArrayWheelAdapter<String>(mCtx, mDigits);
		mAdapter.setTextSize(_wheelTextSize);
		createWheels();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.meter_wheel_layout, null);
		TextView header = (TextView)v.findViewById(R.id.meter_wheel_layout_header_text);
		header.setText(mHeaderText);
		
		TextView bottom = (TextView)v.findViewById(R.id.meter_wheel_layout_bottom_text);
		bottom.setText(mBottomText);
		
		FrameLayout fl = (FrameLayout)v.findViewById(R.id.meter_wheel_layout_meterFrame);
		fl.addView(mMeter);

		mWheelContainer = (LinearLayout)v.findViewById(R.id.meter_wheel_layout_wheelContainer);
		for(WheelView wv:mWheels)
		{
			mWheelContainer.addView(wv);
		}
		return v;
	}


	private void createWheels()
	{
		mWheels = new ArrayList<WheelView>();
		for(int i = 0;i<mNumDigits;i++)
		{
			addDigit();
		}
	}

	private void addDigit()
	{
		mUiHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				WheelView wv = new WheelView(mCtx);

				wv.setViewAdapter(mAdapter);
				wv.setCurrentItem(0);
				wv.setVisibleItems(1);
				wv.setCyclic(true);
				wv.setInterpolator(new BounceInterpolator());
				//		wv.invalidateWheel(true);
				mWheels.add(wv);
			}
		});

	}

	private boolean displayWheelValue(double _val)
	{
		final int[] digits = getDigits(_val);

		if(digits.length>mNumDigits)
		{	
			return false;
		}

		for(int i = 0;i<digits.length;i++)
		{
			final int val = digits[i];
			final int idx = i;
//			mUiHandler.post(new Runnable() {
//
//				@Override
//				public void run() {
					WheelView wv = mWheels.get(mWheels.size()-idx-1);
					wv.setCurrentItem(val, true);
//					wv.invalidate();
//				}
//			});

		}

		return true;
	}

	private int[] getDigits(double _val)
	{
		int n = (int) Math.floor(Math.log10(_val) + 1);
		int i;
		int[] res = new int[n];
		for ( i = 0; i < n; ++i, _val /= 10 )
		{
			res[i] =(int) _val % 10;
		}
		return res;
	}


	//	@Override
	//	public void onDestroyView() {
	//		mWheel = null;
	//		super.onDestroyView();
	//	}

	//	private void createView(LayoutInflater _li, ViewGroup _container)
	//	{
	//		mView =  _li.inflate(R.layout.md_meter_layout, null, false);
	//		
	//		mMeterContainer = (FrameLayout)mView.findViewById(R.id.md_meter_layout_metercontainer);
	////		mMeterContainer.removeAllViews();
	//
	//		mWheelContainer = (FrameLayout)mView.findViewById(R.id.md_meter_layout_wheelcontainer);
	////		mWheelContainer.removeAllViews();
	//	}
	//
	//	private void fillMeterContainer()
	//	{
	//	
	//		Log.e(TAG, "fillMeterContainer");
	//		Log.e(TAG, "number of views attached to meterContainer:"+mMeterContainer.getChildCount());
	//		mMeterContainer.removeAllViews();
	//		Log.e(TAG, "number of views after removeAllViews:"+mMeterContainer.getChildCount());
	//		mMeterContainer.addView(mMeter);
	//		Log.e(TAG, "number of views after addView:"+mMeterContainer.getChildCount());
	//	}





	@Override
	public void onDestroy() {
		stopSimulation();
		super.onDestroy();
	}



	public void setMeterValue(double _val)
	{
		try {
			mMeter.setValue(_val);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setWheelValue(double _val)
	{
		displayWheelValue(_val);
	}

	private void startSimulation()
	{
		stopSimulation();
		mSimulationTimer = new Timer();
		mSimulationTimer.schedule(new ValueSetterTask(), 0, SIMULATION_TIMEOUT);
	}

	private void stopSimulation()
	{
		if(null!=mSimulationTimer)
		{
			mSimulationTimer.cancel();
			mSimulationTimer.purge();
		}
	}

	private class ValueSetterTask extends TimerTask
	{
		Random r = new Random();
		@Override
		public void run() 
		{
			if(null!=mMeter&&null!=mWheels)
			{
				try {
					setMeterValue(r.nextDouble()*100);
					setWheelValue(r.nextDouble()*1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public EnergyMeter getMeter() {
		return mMeter;
	}

}
