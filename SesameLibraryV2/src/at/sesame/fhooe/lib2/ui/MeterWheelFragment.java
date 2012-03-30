package at.sesame.fhooe.lib2.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.sesame.fhooe.lib2.Constants;
import at.sesame.fhooe.lib2.R;


public class MeterWheelFragment
extends Fragment
{
	private static final String TAG = "MeterWheelFragment";
	private Context mCtx;
//	private static final int NO_WHEEL_DIGITS = 5;
	private static final long SIMULATION_TIMEOUT = 5000;

	private int mWheelTextSize = 100;

	private EnergyMeter mMeter;
	private FrameLayout mMeterContainer;
	//	private WheelFragment mWheel;


	private Timer mSimulationTimer;

	private static final String[]mDigits = new String[]{"0","1","2","3","4","5","6","7","8","9"};
	private int mNumDigits;
	private ArrayWheelAdapter<String> mAdapter;
	private ArrayList<WheelView> mWheels;
	private LinearLayout mWheelContainer;
	
	private String mHeaderText;
	private float mHeaderTextSize;
	
	private String mBottomText;
	private float mBottomTextSize;

	private Handler mUiHandler;
	
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd.MM.yyyy");
	private boolean mMockValue = false;
	
	private double mDefaultWheelValue;
	
	private double mCurMeterValue;
	
	private EnergyMeterRenderer mRenderer;

	public MeterWheelFragment()
	{
		
	}
	public MeterWheelFragment(Context _ctx, Handler _uiHandler, String _header, float _headerSize, float _bottomSize, int _wheelTextSize, int _numDigits, boolean _variateValue, EnergyMeterRenderer _renderer)
	{
//		this.setRetainInstance(true);
		mCtx = _ctx;
		mUiHandler = _uiHandler;
//		mMeter = new EnergyMeter(_ctx);

		mRenderer = _renderer;
		mHeaderText = _header;
		mHeaderTextSize = _headerSize;
		mBottomText = mCtx.getString(R.string.MeterWheelFrag_bottom_text)+mDateFormat.format(Constants.getStartDate());
		mBottomTextSize = _bottomSize;
		mWheelTextSize = _wheelTextSize;
		mNumDigits = _numDigits;
		mAdapter =new ArrayWheelAdapter<String>(mCtx, mDigits);
		mAdapter.setTextSize(_wheelTextSize);
		mMockValue = _variateValue;
		mDefaultWheelValue = 300000+new Random().nextDouble()*50000;
		mUiHandler.post(new Runnable() {
			
			@Override
			public void run() {
//				 TODO Auto-generated method stub
				createWheels();
			}
		});
		
	}
	
//	public static MeterWheelFragment(Context )

//	@Override
//	public void onAttach(Activity activity) 
//	{
////		mMeter.forceLayout();
//		super.onAttach(activity);
//		
////		startSimulation();
//	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "onCreateView");
		View v = inflater.inflate(R.layout.meter_wheel_layout, container, false);
		TextView header = (TextView)v.findViewById(R.id.meter_wheel_layout_header_text);
		header.setText(mHeaderText);
		header.setTextSize(mHeaderTextSize);
		
		TextView bottom = (TextView)v.findViewById(R.id.meter_wheel_layout_bottom_text);
		bottom.setText(mBottomText);
		bottom.setTextSize(mBottomTextSize);
		
		mMeter = (EnergyMeter)v.findViewById(R.id.energyMeter);
		initializeMeter();
		
//		TextView unit = (TextView)v.findViewById(R.id.meter_wheel_layout_textViewUnit);
//		unit.setTextSize((int)((float)mWheelTextSize - (float)mWheelTextSize * 0.2f));

//		LinearLayout c = (LinearLayout)v.findViewById(R.id.meter_wheel_layout_container);
//		c.setPadding(30, 0, 30, 0);

//		if(null==mMeterContainer)
		{
//			if(null!=mMeterContainer)
//			{
//				mMeterContainer.removeAllViews();
//			}
//			mMeterContainer = (FrameLayout)v.findViewById(R.id.meter_wheel_layout_meterFrame);
////			mMeter = new EnergyMeter(mCtx);
//			mMeterContainer.addView(mMeter);
//			Log.e(TAG, "meter added");
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
				Log.e(TAG, "adding wheelview...");
				mWheelContainer.addView(wv);
			}
		}
//		mWheelContainer.removeAllViews();
		
		return v;
	}

	private void initializeMeter()
	{
//		mRenderer.setMaxValue(2000);
//		mRenderer.setMajorTickSpacing(500);
//		mRenderer.setMinorTickSpacing(100);
		
		mMeter.setEnergyMeterRenderer(mRenderer);

		try {
			mMeter.setValue(mCurMeterValue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	private double variateValue(double _val, double _percentage)
	{

		int fac = 1;
		Random r = new Random(System.currentTimeMillis());
		if(r.nextBoolean())
		{
			fac*=-1;
		}
		double maxVariation = _percentage * _val /100;
		return _val + r.nextDouble()* maxVariation * fac;
		
	}

	private boolean displayWheelValue(double _val)
	{
		if(null==mWheels)
		{
//			Log.e(TAG, "wheel was null");
			return false;
		}
		double value = _val;
		if(mMockValue)
		{
			value = mDefaultWheelValue;
//			value = variateValue(_val,10);
		}
		final int[] digits = getDigits(value);

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

//	public void setColorLabelWidth(float _w) {
//		mMeter.setColorLabelWidth(_w);
//	}
//	
//	public void setTickTextSize(float _s) {
//		mMeter.setTickTextSize(_s);
//	}
//
//	public void setMinorTickLength(int _l) {
//		mMeter.setMinorTickLength(_l);
//	}
//	
//	public void setMajorTickLength(int _l) {
//		mMeter.setMajorTickLength(_l);
//	}
	
	public void setMeterValue(double _val)
	{
//		Log.e(TAG, "meter value set to "+_val);
		if(null==mMeter)
		{
			return;
		}
		double value = _val;
		if(mMockValue)
		{
			value = variateValue(_val,10);
		}
		try {
			mCurMeterValue = value;
			mMeter.setValue(mCurMeterValue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setWheelValue(double _val)
	{
//		Log.e(TAG, "wheel value set to:"+_val);
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
}
