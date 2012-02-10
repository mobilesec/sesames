package at.sesame.fhooe.lib.ui.kiosk;

import android.app.ActionBar;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.graphics.RectF;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class KioskActivity
extends FragmentActivity
{	
	private static final String TAG = "KioskActivity";
	private static final String QUIT_PASSWORD = "Close_Sesame";

	private static final float QUIT_REGION_WIDTH = 100;
	private static final float QUIT_REGION_HEIGHT = 100;

	private static final int NO_QUIT_TOUCHES = 5;

	private int mQuitTouchCount = 0;

	private boolean mStatusBarHidden = false;

	private enum QuitRegion
	{
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}

	private QuitRegion mQuitRegion = QuitRegion.TOP_LEFT;

	private static final int NO_BACKPRESSES_FOR_QUIT_DIALOG = 10;

	private int mBackCounter = 0;

	
	

	@Override
	protected void onResume() 
	{
		super.onResume();
		mStatusBarHidden = enableKioskMode();
		if(!mStatusBarHidden)
		{
			enableLowProfileMode();
		}
	}



	protected void enableLowProfileMode()
	{
		hideActionbar();
		makeStatusbarAmbient();
		disableKeyGuard();
		Log.e(TAG, "low profile mode enabled");
	}

	protected boolean enableKioskMode()
	{
		if(!hideActionbar())
		{
			return false;
		}
		if(!StatusBarRemover.hideStatusBar())
		{
			return false;
		}
		Log.e(TAG, "kiosk mode enabled");
		return true;
	}

	private void disableKeyGuard()
	{
		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
		lock.disableKeyguard();
	}

	private boolean hideActionbar()
	{
		try
		{
			ActionBar ab = super.getActionBar();
			ab.hide();
			return true;
		}
		catch(Exception _e)
		{
			Log.e(TAG, "could not hide AB, "+_e.getLocalizedMessage());
			return false;
		}
	}

	@Override
	public void onAttachedToWindow()
	{  
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);     
		super.onAttachedToWindow();  
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		switch(keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			mBackCounter++;
			if(mBackCounter>=NO_BACKPRESSES_FOR_QUIT_DIALOG)
			{
				mBackCounter = 0;
				showQuitDialog();
			}
			break;
		default:
			mBackCounter = 0;
		}
		makeStatusbarAmbient();
		return false;
	}

	@Override
	public void onDestroy()
	{
//		if(mStatusBarHidden)
		{
			StatusBarRemover.showStatusBar();
		}
		super.onDestroy();
	}

	private void makeStatusbarAmbient()
	{
		getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) 
	{
		switch(ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if(areCoordinatesInQuitRegion(ev.getRawX(), ev.getRawY()))
			{
				mQuitTouchCount++;
				if(mQuitTouchCount>=NO_QUIT_TOUCHES)
				{
					mQuitTouchCount=0;
					showQuitDialog();
				}
			}
			else
			{
				mQuitTouchCount = 0;
			}
			break;
		}
		return true;
	}



	private boolean areCoordinatesInQuitRegion(float _x, float _y)
	{
		float[] screenSize = getScreenSize();

		RectF quitRegion = new RectF();

		switch(mQuitRegion)
		{
		case TOP_LEFT:
			quitRegion = new RectF(0,0,QUIT_REGION_WIDTH, QUIT_REGION_HEIGHT);
			break;
		case TOP_RIGHT:
			quitRegion = new RectF(screenSize[0]-QUIT_REGION_WIDTH, 0, screenSize[0], QUIT_REGION_HEIGHT);
			break;
		case BOTTOM_LEFT:
			quitRegion = new RectF(0,screenSize[1]-QUIT_REGION_HEIGHT, QUIT_REGION_WIDTH,screenSize[1]);
			break;
		case BOTTOM_RIGHT:
			quitRegion = new RectF(screenSize[0]-QUIT_REGION_WIDTH,screenSize[1]-QUIT_REGION_HEIGHT,screenSize[0], screenSize[1]);
			break;
		}

		return quitRegion.contains(_x, _y);
	}

	private float[] getScreenSize()
	{
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float width = dm.widthPixels; //320
		float height = dm.heightPixels; //480

		return new float[]{width, height};
	}

	private void showQuitDialog()
	{
		AppQuitDialog.newInstance("Kennwort zum Beenden eingeben",QUIT_PASSWORD).show(getSupportFragmentManager(), null);
	}
}
