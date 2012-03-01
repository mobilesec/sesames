package at.sesame.fhooe.midsd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Observer;

import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import at.fhooe.facedetectionview.model.FaceDetector;
import at.fhooe.facedetectionview.model.FacesDetectedEvent;
import at.fhooe.facedetectionview.model.FaceDetector.Feature;
import at.fhooe.facedetectionviewcomponent.FaceDetectionViewComponent;
import at.sesame.fhooe.lib.ui.ProgressFragmentDialog;
import at.sesame.fhooe.midsd.data.ISesameDataListener;
import at.sesame.fhooe.midsd.data.ISesameDataProvider;
import at.sesame.fhooe.midsd.data.SesameDataCache;
import at.sesame.fhooe.midsd.data.SesameMeasurementPlace;
import at.sesame.fhooe.midsd.hd.HD_Fragment;

import at.sesame.fhooe.midsd.ld.INotificationListener;
import at.sesame.fhooe.midsd.ld.LD_Fragment;
import at.sesame.fhooe.midsd.md.MD_Fragment;

@SuppressWarnings("unused")
public class MID_SesameDisplayActivity extends FragmentActivity implements
		Observer {
	private static final String TAG = "MID_SesameDisplay";

	private static final boolean USE_MOCK_DATA = true;

	// public static final Date START_DATE = new Date(2011, 11, 04);
	/** shows the frontal camera, detects faces periodically. */
	private FaceDetectionViewComponent mFaceViewComponent = new FaceDetectionViewComponent();

	public static final int EDV_1_ID = 15;
	public static final int EDV_3_ID = 18;
	public static final int EDV_6_ID = 17;

	private Fragment mLdFrag;
	private Fragment mMdFrag;
	private Fragment mHdFrag;

	private DialogFragment mNotificationFrag;

	private Fragment mCurFrag;

	private SesameDataCache mDataCache;

	private Handler mUiHandler = new Handler();
	
	private boolean mFaceDetected = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		final DialogFragment loadingDialog = ProgressFragmentDialog.newInstance(getString(R.string.mid_sesame_display_loading_dialog_title), getString(R.string.mid_sesame_display_loading_dialog_message));
		loadingDialog.show(getSupportFragmentManager(), null);
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{

				mLdFrag = new LD_Fragment(getApplicationContext(), mUiHandler);
				mMdFrag = new MD_Fragment(getSupportFragmentManager(),getApplicationContext(), mUiHandler);
				mHdFrag = new HD_Fragment(getApplicationContext(), getSupportFragmentManager(), mUiHandler);

				mNotificationFrag = new NotificationTimeSelectionFragment();
				
				mDataCache = SesameDataCache.createInstance(USE_MOCK_DATA);
							
				mDataCache.registerNotificationListener((INotificationListener)mLdFrag);
				mDataCache.registerNotificationListener((INotificationListener)mMdFrag);
				mDataCache.registerNotificationListener((INotificationListener)mHdFrag);
				
				ArrayList<SesameMeasurementPlace> energyPlaces = mDataCache.getEnergyMeasurementPlaces();
				if(null!=energyPlaces)
				{
					mDataCache.registerEnergyDataListener((ISesameDataListener)mMdFrag, energyPlaces.get(0));
					mDataCache.registerEnergyDataListener((ISesameDataListener)mMdFrag, energyPlaces.get(1));
					mDataCache.registerEnergyDataListener((ISesameDataListener)mMdFrag, energyPlaces.get(2));
					
					mDataCache.startEnergyDataUpdates();
				}
				else
				{
					runOnUiThread(new Runnable() 
					{	
						@Override
						public void run() 
						{
							Toast.makeText(getApplicationContext(), "Energy data source not available...", Toast.LENGTH_LONG).show();
						}
					});		
				}
				if(null!=loadingDialog)
				{
					loadingDialog.dismiss();
				}
				else
				{
					Log.e("","loading dialog dismissing FAILed");
				}
			}
		}).start();
	}

	@Override
	protected void onPause() {
		mFaceViewComponent
				.onPause((FrameLayout) findViewById(R.id.camera_preview));
		super.onPause();
	}

	@Override
	protected void onResume() {

		mFaceViewComponent.onResume(this,
				(FrameLayout) findViewById(R.id.camera_preview), true);
		mFaceViewComponent.addObserver(this);

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (null != mDataCache) {
			mDataCache.cleanUp();
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mid_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.low_density:
			setShownFragment(mLdFrag);
			break;
		case R.id.medium_density:
			setShownFragment(mMdFrag);
			break;
		case R.id.high_density:
			setShownFragment(mHdFrag);
			break;
		case R.id.notification_time_selection:
			mNotificationFrag.show(getSupportFragmentManager(), null);
			break;
		}
		return true;
	}

	// public void registerEsmartListener(ISesameDataListener _listener, int
	// _id)
	// {
	// if(null!=mDataCache)
	// {
	// mDataCache.addEsmartDataListener(_listener, _id);
	// }
	// }

	private void setShownFragment(Fragment _frag) {
		if (_frag.equals(mCurFrag)) {
			return;
		}
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		if (null != mCurFrag) {
			ft.remove(mCurFrag);
		}

		ft.add(R.id.contentFrame, _frag);
		ft.commit();
		mCurFrag = _frag;
	}

	public static Date getStartDate() {
		GregorianCalendar cal = new GregorianCalendar(2011, 10, 04);
		return cal.getTime();
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof FacesDetectedEvent) {
			// update came from faceviewcomponent
			FacesDetectedEvent e = (FacesDetectedEvent) data;
			if(e.amountOfFaces()>0)
			{
				setFaceDetected(true);
			}
			else
			{
				setFaceDetected(false);
			}
//			Log.i("FACES", "totalFoundFaces=" + e.amountOfFaces());
		}

	}
	
	private void setFaceDetected(boolean _detected)
	{
		if(_detected!=mFaceDetected)
		{
			mFaceDetected = _detected;
			Log.e(TAG, "face detected:"+mFaceDetected);
		}
	}

	// public String getStartDateText()
	// {
	// String dateText =
	// return dateText;
	// }
}