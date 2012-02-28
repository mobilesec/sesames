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
import at.sesame.fhooe.midsd.MID_SesameDisplayActivity;
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
	private FrameLayout mMeterContainer;
	//	private WheelFragment mWheel;


	private Timer mSimulationTimer;
	private FragmentManager mFragMan;

	private static final String[]mDigits = new String[]{"0","1","2","3","4","5","6","7","8","9"};
	private int mNumDigits;
	private ArrayWheelAdapter<String> mAdapter;
	private ArrayList<WheelView> mWheels;
	private LinearLayout mWheelContainer;
	
	private String mHeaderText;
	private float mHeaderTextSize;
	
	private String mBottomText;
	private float mBottomTextSize;
	
	private int mSidePadding;

	private Handler mUiHandler;
	


	public MeterWheelFragment(Context _ctx, Handler _uiHandler, String _header, float _headerSize, float _bottomSize, int _wheelTextSize, int _numDigits, int _sidePadding)
	{
		this.setRetainInstance(true);
		mCtx = _ctx;
		mUiHandler = _uiHandler;
		
		mHeaderText = _header;
		mHeaderTextSize = _headerSize;
		mBottomText = mCtx.getString(R.string.MeterWheelFrag_bottom_text)+MID_SesameDisplayActivity.getStartDate().toLocaleString();
		mBottomTextSize = _bottomSize;
		mSidePadding = _sidePadding;
		mMeter = new EnergyMeter(mCtx);
		mMeter.setMaxValue(2000);
		mMeter.setMajorTickSpacing(500);
		mMeter.setMinorTickSpacing(100);
		mWheelTextSize = _wheelTextSize;
		mNumDigits = _numDigits;
		mAdapter =new ArrayWheelAdapter<String>(mCtx, mDigits);
		mAdapter.setTextSize(_wheelTextSize);
		mUiHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				createWheels();
			}
		});
		
	}

	@Override
	public void onAttach(Activity activity) 
	{
		super.onAttach(activity);
//		startSimulation();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.meter_wheel_layout, null);
		TextView header = (TextView)v.findViewById(R.id.meter_wheel_layout_header_text);
		header.setText(mHeaderText);
		header.setTextSize(mHeaderTextSize);
		
		TextView bottom = (TextView)v.findViewById(R.id.meter_wheel_layout_bottom_text);
		bottom.setText(mBottomText);
		bottom.setTextSize(mBottomTextSize);
		
		TextView unit = (TextView)v.findViewById(R.id.meter_wheel_layout_textViewUnit);
		unit.setTextSize((int)((float)mWheelTextSize - (float)mWheelTextSize * 0.2f));

		LinearLayout c = (LinearLayout)v.findViewById(R.id.meter_wheel_layout_container);
		c.setPadding(mSidePadding, 0, mSidePadding, 0);

//		if(null==mMeterContainer)
		{
			if(null!=mMeterContainer)
			{
				mMeterContainer.removeAllViews();
			}
			mMeterContainer = (FrameLayout)v.findViewById(R.id.meter_wheel_layout_meterFrame);
//			mMeter = new EnergyMeter(mCtx);
			mMeterContainer.addView(mMeter);
		}
//		ViewGroup parent = (ViewGroup)mMeter.getParent();
//		if(null!=parent)
//		{
//			parent.removeAllViews();
//		}
//		mMeter = new EnergyMeter(mCtx);
//		mMeterContainer.removeAllViews();
		

//		if(null==mWheelContainer)
		{
			if(null==mWheels)
			{
				createWheels();
			}
			if(null!=mWheelContainer)
			{
				mWheelContainer.removeAllViews();
			}
			mWheelContainer = (LinearLayout)v.findViewById(R.id.meter_wheel_layout_wheelContainer);

			for(WheelView wv:mWheels)
			{
				
				mWheelContainer.addView(wv);
			}
		}
//		mWheelContainer.removeAllViews();
		
		return v;
	}


	private void createWheels()
	{
		if(null!=mWheels)
		{
			return;
		}
		else
		{
//			Log.e(TAG, "mWheels was null, creating new...");
		}
		mWheels = new ArrayList<WheelView>();
		for(int i = 0;i<mNumDigits;i++)
		{
			addDigit();
		}
	}

	private void addDigit()
	{
//		Log.e(TAG, "addDigit...");
//		mUiHandler.post(new Runnable() {
//
//			@Override
//			public void run() {
				// TODO Auto-generated method stub
				WheelView wv = new WheelView(mCtx);

				wv.setViewAdapter(mAdapter);
				wv.setCurrentItem(0);
				wv.setVisibleItems(1);
				wv.setCyclic(true);
				wv.setInterpolator(new BounceInterpolator());
				//		wv.invalidateWheel(true);
				mWheels.add(wv);
//				Log.e(TAG, "wheels size:"+mWheels.size());
//			}
//		});
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private boolean displayWheelValue(double _val)
	{
		if(null==mWheels)
		{
//			Log.e(TAG, "wheel was null");
			return false;
		}
		final int[] digits = getDigits(_val);

		if(digits.length>mNumDigits)
		{	
			return false;
		}
//		Log.e(TAG, "displayWheelVal, size of wheelList:"+mWheels.size());
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

	public void setColorLabelWidth(float _w) {
		mMeter.setColorLabelWidth(_w);
	}
	
	public void setTickTextSize(float _s) {
		mMeter.setTickTextSize(_s);
	}

	public void setMinorTickLength(int _l) {
		mMeter.setMinorTickLength(_l);
	}
	
	public void setMajorTickLength(int _l) {
		mMeter.setMajorTickLength(_l);
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
