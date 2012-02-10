package at.sesame.fhooe.esmart.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import at.sesame.fhooe.R;

public class WheelFragment 
extends Fragment
implements OnCheckedChangeListener
{
	private static final String TAG = "WheelFragment";
	private int mNumDigits;
	private LinearLayout mLinLayout = null;
	private Context mCtx;
	private static final String[]mDigits = new String[]{"0","1","2","3","4","5","6","7","8","9"};
	private ArrayWheelAdapter<String> mAdapter;
	private ArrayList<WheelView> mWheels;
	private double mValue = 0;
	private ToggleButton mAutoIncrementButt;
	private View mView;
//	private View mSeparatorView;
	private Handler mUiThreadhandler;

	private Thread mAutoIncrementThread;
	private boolean mRunning = false;
	private ArrayList<Integer> mSeparatorPositions = new ArrayList<Integer>();

	private Handler mHandler = new Handler();

	private Runnable mAutoIncrementRunnable = new Runnable() 
	{

		@Override
		public void run() 
		{
			while(mRunning)
			{
				setValue(mValue+1);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.e(TAG, "thread finished");
		}	
	};

	public WheelFragment(Context _ctx, Handler _handler, int _numDigits, ArrayList<Integer> _separatorPositions)
	{
		mCtx = _ctx;
		mNumDigits = _numDigits;
		mUiThreadhandler = _handler;
		mSeparatorPositions = _separatorPositions;
		mAdapter =new ArrayWheelAdapter<String>(mCtx, mDigits);
		setTextSize(200);
		LayoutInflater li = (LayoutInflater)mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = li.inflate(R.layout.wheel_layout, null);
//		mSeparatorView = li.inflate(R.layout.separator_layout, null);

		mLinLayout = (LinearLayout) mView.findViewById(R.id.wheel_lin_layout);

		mAutoIncrementButt = (ToggleButton)mView.findViewById(R.id.toggleButton1);
		mAutoIncrementButt.setOnCheckedChangeListener(this);

		setupWheels();
	}

	private void setupWheels()
	{
		mWheels = new ArrayList<WheelView>();
		for(int i = 0;i<mNumDigits;i++)
		{
			addDigit();
		}
	}

	private void addDigit()
	{
		WheelView wv = new WheelView(mCtx);

		wv.setViewAdapter(mAdapter);
		wv.setCurrentItem(0);
		wv.setVisibleItems(1);
		wv.setCyclic(true);
		wv.setInterpolator(new BounceInterpolator());
		//		wv.invalidateWheel(true);
		mWheels.add(wv);
	}

	public synchronized void setValue(double _value)
	{
		mValue = _value;
		displayValue();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		mLinLayout.removeAllViews();
		for(int i = 0;i<mWheels.size();i++)
		{
			mLinLayout.addView(mWheels.get(i));
//			if(mSeparatorPositions.contains(new Integer(i)))
//			{
//				mLinLayout.addView(ml(R.layout.separator_layout, null));
//			}
		}
		return mView;
	}

	private void displayValue()
	{
		int[] digits = getDigits(mValue);

		if(digits.length>mNumDigits)
		{	
//			mHandler.post(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					addDigit();
//					mNumDigits++;
//					mView.invalidate();
//					mView.buildLayer();
//				}
//			});

			mUiThreadhandler.post(new Runnable() 
			{	
				@Override
				public void run() 
				{
					Toast.makeText(mCtx, "number can't be displayed", Toast.LENGTH_SHORT).show();
				}
			});
			stopAutoIncrement();
			return;
		}

		for(int i = 0;i<digits.length;i++)
		{
			WheelView wv = mWheels.get(mWheels.size()-i-1);
			wv.setCurrentItem(digits[i], true);
		}

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

	public void stopAutoIncrement()
	{
		Log.e(TAG, "thread stopped");
		mRunning = false;
		
		mHandler.post(new Runnable() 
		{	
			@Override
			public void run() 
			{
				mAutoIncrementButt.setOnCheckedChangeListener(null);
				mAutoIncrementButt.setChecked(false);	
				mAutoIncrementButt.setOnCheckedChangeListener(WheelFragment.this);
			}
		});	
	}

	public void startAutoIncrement()
	{
		if(mRunning)
		{
			stopAutoIncrement();
		}

		mAutoIncrementThread = new Thread(mAutoIncrementRunnable);
		mRunning = true;

		mAutoIncrementThread.start();
		Log.e(TAG, "thread started");
	}

	public void setTextSize(int _size)
	{
		mAdapter.setTextSize(_size);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		if(mRunning)
		{
			stopAutoIncrement();
		}
		else
		{
			startAutoIncrement();
		}
	}
}
