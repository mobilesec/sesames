package at.sesame.fhooe.midsd.md;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import at.sesame.fhooe.lib.ui.EnergyMeter;
import at.sesame.fhooe.midsd.R;

public class MD_meterFragment
extends Fragment
{
	private static final String TAG = "MD_meterFragment";
	private Context mCtx;
	private static final int NO_WHEEL_DIGITS = 5;

	private FrameLayout mMeterContainer;
	private FrameLayout mWheelContainer;
	private EnergyMeter mMeter;
	private WheelFragment mWheel;

	private EnergyWheelView mEnergyWheel;

	private View mView;
	private LayoutInflater mLi;

	private Timer mMeterSimulationTimer;

	private boolean mWheelAdded = false;

	public MD_meterFragment(FragmentManager _fm, Context _ctx)
	{
		mCtx = _ctx;
		mLi = LayoutInflater.from(mCtx);
		//		mMeter = new EnergyMeter(_ctx);
		mWheel = new WheelFragment(_ctx, null, NO_WHEEL_DIGITS, null);
		//		LayoutInflater li = LayoutInflater.from(mCtx);
		//		mView = li.inflate(R.layout.md_meter_layout, null, false);
		//		FrameLayout fl = (FrameLayout)mView.findViewById(R.id.frameLayout1);

	}

	//	@Override
	//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	//			Bundle savedInstanceState) {
	//		Log.e(TAG, "onCreateView");
	//		if(null==mView || null==mMeterContainer || null==mWheelContainer)
	//		{
	//			Log.e(TAG, "creating view...");
	//			createView(inflater, container);
	//		}
	//		else
	//		{
	//			Log.e(TAG, "view was not created...");
	//		}
	//		////		if(null==mView)
	//		//		{
	//		//			mView =  inflater.inflate(R.layout.md_meter_layout, null);
	//		//			FrameLayout meterContainer = (FrameLayout)mView.findViewById(R.id.md_meter_layout_metercontainer);
	//		////			meterContainer.
	//		//			meterContainer.removeAllViews();
	//		//			meterContainer.addView(mMeter);
	//		////			FragmentTransaction ft = getFragmentManager().beginTransaction();
	//		//////			ft.remove(mWheel);
	//		////			ft.replace(R.id.md_meter_layout_wheelcontainer, mWheel);
	//		////			ft.commit();
	//		//		}
	//
	//		fillMeterContainer();
	//		Log.e(TAG, "fillMeterContainer returned successfully");
	////		addWheel();
	//		return mView;
	//	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//		container.removeAllViews();
		Log.e(TAG, "onCreateView");
		
//		if(null!=container)
//		{
//			container.removeAllViews();
//		}
		View v = inflater.inflate(R.layout.md_meter_layout, container, false);
		mMeter = (EnergyMeter)v.findViewById(R.id.energyMeter);
		startMeterSimulation();
		FrameLayout fl = (FrameLayout)v.findViewById(R.id.frameLayout1);
		fl.removeAllViews();
		
//		if(!mWheelAdded)
		{
			addWheel();
		}

		//		FrameLayout fl = (FrameLayout)v.findViewById(R.id.frameLayout1);
		//		fl.addView(new WheelView(mCtx));

		//		mEnergyWheel = (EnergyWheelView)v.findViewById(R.id.wheelView);
		//		mEnergyWheel.setValue(10);
		//		mWheel = (WheelFragment) getFragmentManager().findFragmentById(R.id.fragment1);
		//		mWheel.init(mCtx, null, NO_WHEEL_DIGITS, null);
		//		mWheel.startAutoIncrement();
		//		FrameLayout meterCont = (FrameLayout) v.findViewById(R.id.md_meter_layout_metercontainer);
		//		meterCont.removeAllViews();
		//		
		//		meterCont.addView(new EnergyMeter(mCtx));
		////		meterCont.addView(mMeter);
		//		
		//		FragmentTransaction ft = getFragmentManager().beginTransaction();
		////		ft.remove(mWheel);
		//		ft.add(R.id.md_meter_layout_wheelcontainer, new WheelFragment(mCtx, null, NO_WHEEL_DIGITS, null));
		////		ft.add(R.id.md_meter_layout_wheelcontainer, mWheel);
		//		ft.commit();

		return v;
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



	//	@Override
	//	public void onViewCreated(View view, Bundle savedInstanceState) {
	//		// TODO Auto-generated method stub
	//		Log.e(TAG, "onViewCreated");
	//		//		super.onViewCreated(view, savedInstanceState);
	//		if(!mWheelAdded)
	//		{
	//			mWheelAdded = true;
	//			FragmentTransaction ft = getFragmentManager().beginTransaction();
	//			ft.remove(mWheel);
	//			ft.replace(R.id.frameLayout1, mWheel);
	//			ft.commit();
	//			Log.e(TAG, "committed");
	//		}
	//	}

	@Override
	public void onDestroy() {
		stopMeterSimulation();
		super.onDestroy();
	}

	private void addWheel()
	{
		mWheelAdded = true;
		Log.e(TAG, "adding wheel");
		// TODO Auto-generated method stub
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		//		ft.remove(mWheel);
		WheelFragment wf = new WheelFragment(mCtx, null, NO_WHEEL_DIGITS, null);
		ft.replace(R.id.frameLayout1, wf);
		ft.commit();
		Log.e(TAG, "adding wheel commited");

	}

	private void startMeterSimulation()
	{
		stopMeterSimulation();
		mMeterSimulationTimer = new Timer();
		mMeterSimulationTimer.schedule(new MeterValueSetterTask(), 0, 1000);
	}

	private void stopMeterSimulation()
	{
		if(null!=mMeterSimulationTimer)
		{
			mMeterSimulationTimer.cancel();
			mMeterSimulationTimer.purge();
		}
	}

	private class MeterValueSetterTask extends TimerTask
	{
		Random r = new Random();
		@Override
		public void run() 
		{
			if(null!=mMeter)
			{
				try {
					mMeter.setValue(r.nextDouble()*100);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
