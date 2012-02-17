package at.sesame.fhooe.midsd.ld;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import at.sesame.fhooe.midsd.R;
import at.sesame.fhooe.midsd.demo.EventSimulator;

public class LD_Fragment 
extends Fragment
implements INotificationListener
{

	@SuppressWarnings("unused")
	private static final String TAG = "LD_Fragment";

	private LayoutInflater mLi;
	private LdState mState;

	private View mView;

	private Button mButt;
	private TextView mNotificationTextView;

	private ArrayList<Integer> mBackgroundColors = new ArrayList<Integer>();
	private ArrayList<Integer> mForegroundColors = new ArrayList<Integer>();

	private ColorInterpolationManager mBackgroundColMan;
	private ColorInterpolationManager mForegroundColMan;


	private InterpolationThread mInterThread;

	private Handler mUiHandler;

	private String mNotificationText;

	public enum LdState
	{
		background,
		foreground
	}

	public LD_Fragment(Context _ctx, Handler _uiHandler)
	{
		mLi = LayoutInflater.from(_ctx);

		fillBackgroundColors();
		fillForegroundColors();

		mBackgroundColMan = new ColorInterpolationManager(mBackgroundColors);
		mForegroundColMan = new ColorInterpolationManager(mForegroundColors);
		mUiHandler = _uiHandler;
	}

	@Override
	public void onAttach(Activity activity) 
	{
		super.onAttach(activity);
		
		buildView();
		
		setState(LdState.background);

		mInterThread = new InterpolationThread();
		mInterThread.startInterpolating();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		return buildView();
	}

	private View buildView()
	{
		mView = mLi.inflate(R.layout.ld_layout, null, false);


		mButt = (Button)mView.findViewById(R.id.interactionButt);
		mButt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleState();

			}
		});

		mNotificationTextView = (TextView)mView.findViewById(R.id.notificationText);

		return mView;
	}

	private synchronized void setState(LdState _state)
	{
		mState = _state;

		switch(mState)
		{
		case background:
			if(null!=mNotificationTextView)
			{
				mNotificationTextView.setText("");
			}
			break;
		case foreground:
			if(null!=mNotificationTextView)
			{
				mNotificationTextView.setText(mNotificationText);
			}
			break;
		}
	}


	private void toggleState()
	{
		switch(mState)
		{
		case foreground:
			setState(LdState.background);
			break;
		case background:
			setState(LdState.foreground);
			break;
		}
	}

	private void fillBackgroundColors()
	{
		mBackgroundColors.add(Color.BLUE);
		mBackgroundColors.add(Color.CYAN);
		mBackgroundColors.add(Color.GREEN);
	}

	private void fillForegroundColors()
	{
		mForegroundColors.add(Color.RED);
		mForegroundColors.add(Color.argb(255, 255, 165, 0));//orange
		mForegroundColors.add(Color.YELLOW);
	}

	@Override
	public void notifyAboutNotification(String _msg) 
	{
		mNotificationText = _msg;

		mUiHandler.post(new Runnable() {

			@Override
			public void run() 
			{
				setState(LdState.foreground);
			}
		});
	}

	@Override
	public void onDestroyView() 
	{
		if(null!=mInterThread)
		{
			mInterThread.stopInterpolating();
		}

		super.onDestroyView();
	}

	private void setBackgroundColor(final int _col)
	{
		mUiHandler.post(new Runnable() 
		{	
			@Override
			public void run() 
			{
				mView.setBackgroundColor(_col);
				mView.invalidate();
			}
		});
	}

	private synchronized ColorInterpolationManager getCurrentInterpolationManager()
	{
		switch(mState)
		{
		case foreground:
			return mForegroundColMan;
		case background:
			return mBackgroundColMan;
		default:
			return null;
		}
	}

	private class InterpolationThread extends Thread
	{
		private boolean mRunning = false;
		private long mTimeout = 100;

		@Override
		public void run()
		{
			while(mRunning)
			{
				setBackgroundColor(getCurrentInterpolationManager().getNextColor());
				try 
				{
					Thread.sleep(mTimeout);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}

		public void startInterpolating()
		{
			mRunning = true;
			this.start();
		}

		public void stopInterpolating()
		{
			mRunning = false;
		}
	}
}
