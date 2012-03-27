package at.sesame.fhooe.tablet;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import at.sesame.fhooe.lib2.data.INotificationListener;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;
import at.sesame.fhooe.lib2.data.SesameDataCache.DataSource;
import at.sesame.fhooe.lib2.data.SesameSensor.SensorType;
import at.sesame.fhooe.lib2.ui.MeterWheelFragment;
import at.sesame.fhooe.lib2.ui.PMSRoomsListFragment;

@SuppressWarnings("unused")
public class SesameTabletActivity 
extends FragmentActivity
implements INotificationListener
{
	private static final String TAG = "HD_Fragment";
	private static final long METER_WHEEL_UPDATE_TIMEOUT = 1000;
	private Context mCtx;
	private LayoutInflater mLi;
	private FragmentManager mFragMan;
	
	private SesameDataCache mDataCache;
	
	private Timer mMeterWheelUpdateTimer;
	
	private static MeterWheelFragment mEdv1Frag;
	private static MeterWheelFragment mEdv3Frag;
	private static MeterWheelFragment mEdv6Frag;
	
//	private WheelFragment mEdv1WheelFrag;
//	private WheelFragment mEdv3WheelFrag;
//	private WheelFragment mEdv6WheelFrag;
	
//	private static PMSFragment mPMSFrag;

	private PMSRoomsListFragment mRoomListFrag;
	private static TabbedComparisonFragment mTabFrag;
	
	private Notification mNotification;
	private NotificationManager mNotificationMan;
	private static final String NOTIFICATION_TITLE ="Sesame Notification";
	
	private static final int WHEEL_TEXT_SIZE = 28;
	
	private boolean mShowNotifications = false;
	
	private Handler mUiHandler = new Handler();
	
//	public SesameTabletActivity(Context _ctx, FragmentManager _fm, Handler _uiHandler)
//	{
//		mCtx = _ctx;
//		mLi = LayoutInflater.from(_ctx);
//		mFragMan = _fm;
//		mUiHandler = _uiHandler;
//		initializeNotification();
//		initializeFragments();
//		setContentView(R.layout.hd_layout);
//		addFragments();
////		startMeterWheelUpdates();
//	}
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mDataCache = SesameDataCache.getInstance(DataSource.mock);
		mCtx = getApplicationContext();
		mLi = LayoutInflater.from(mCtx);
		mFragMan = getSupportFragmentManager();
//		mUiHandler = _uiHandler;
		initializeNotification();
		initializeFragments();
		setContentView(R.layout.hd_layout);
		addFragments();
	}
	
//	public void onStart()
//	{
//		addFragments();
//		super.onStart();
//	}
	
	private void initializeNotification()
	{
		int icon = R.drawable.ic_stat_warning;
		CharSequence tickerText = "Hello NOTIFICATION";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);
		mNotificationMan = (NotificationManager)mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	private void initializeFragments()
	{
		String room1Name = mCtx.getString(R.string.global_Room1_name);
		String room3Name = mCtx.getString(R.string.global_Room3_name);
		String room6Name = mCtx.getString(R.string.global_Room6_name);
		
		
		mEdv1Frag = new MeterWheelFragment(mCtx, mUiHandler, room1Name, 20.0f, 14.0f, WHEEL_TEXT_SIZE, 6, 0, true);
		mEdv3Frag = new MeterWheelFragment(mCtx, mUiHandler, room3Name, 20.0f, 14.0f, WHEEL_TEXT_SIZE, 6, 0, true);
		mEdv6Frag = new MeterWheelFragment(mCtx, mUiHandler, room6Name, 20.0f, 14.0f, WHEEL_TEXT_SIZE, 6, 0, true);
//		mEdv1WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
//		mEdv3WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
//		mEdv6WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
//		mUiHandler.post(new Runnable() {
//			
//			@Override
//			public void run() {
				// TODO Auto-generated method stub
//				mPMSFrag = new PMSFragment(mCtx, mUiHandler);
//			}
//		});
		
//		try {
//			Thread.sleep(10);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		mTabFrag = new TabbedComparisonFragment(mCtx);
		
		mRoomListFrag = new PMSRoomsListFragment(mCtx, mUiHandler, mFragMan);
		
	}
	
	private void addFragments()
	{
		FragmentTransaction ft = mFragMan.beginTransaction();
		ft.add(R.id.hd_layout_edv1Frame, mEdv1Frag);
		ft.add(R.id.hd_layout_edv3Frame, mEdv3Frag);
		ft.add(R.id.hd_layout_edv6Frame, mEdv6Frag);
		
//		ft.add(R.id.hd_layout_edv1Frame, mEdv1WheelFrag);
//		ft.add(R.id.hd_layout_edv3Frame, mEdv3WheelFrag);
//		ft.add(R.id.hd_layout_edv6Frame, mEdv6WheelFrag);
		
//		ft.add(R.id.hd_layout_pmsFrame, mPMSFrag);
//		
		ft.add(R.id.hd_layout_chartFrame, mTabFrag);
		ft.add(R.id.hd_layout_pmsFrame, mRoomListFrag);
		ft.commit();
//		mFragMan.executePendingTransactions();
	}
	
	
	
//	@Override
//	public void onAttach(Activity activity) {
//		// TODO Auto-generated method stub
//		super.onAttach(activity);
//		mShowNotifications = true;
//		Log.e(TAG, "onAttach");
//		initializeFragments();
//		startMeterWheelUpdates();
////		addFragments();
////		testNotifications();
//	}
	
	

//	@Override
//	public void onDetach() {
//		mShowNotifications = false;
//		mNotificationMan.cancelAll();
//		stopMeterWheelUpdates();
//		super.onDetach();
//	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		Log.e(TAG, "onCreateView");
//		View v = mLi.inflate(R.layout.hd_layout, null);
//		addFragments();
//		return v;
//	}
	
	public void stopMeterWheelUpdates()
	{
		if(null!=mMeterWheelUpdateTimer)
		{
			mMeterWheelUpdateTimer.cancel();
			mMeterWheelUpdateTimer.purge();
		}
	}
	public void startMeterWheelUpdates()
	{
		stopMeterWheelUpdates();
		mMeterWheelUpdateTimer = new Timer();
		mMeterWheelUpdateTimer.scheduleAtFixedRate(new MeterWheelUpdateTask(), 0, METER_WHEEL_UPDATE_TIMEOUT);
	}
	
	private class MeterWheelUpdateTask extends TimerTask
	{

		@Override
		public void run() {
			
			ArrayList<SesameMeasurementPlace> places = mDataCache.getEnergyMeasurementPlaces();
			double currentAtPlace1 = mDataCache.getLastEnergyReading(places.get(0));
			double overallAtPlace1 = mDataCache.getOverallEnergyConsumtion(places.get(0));
			
			double currentAtPlace3 = mDataCache.getLastEnergyReading(places.get(1));
			double overallAtPlace3 = mDataCache.getOverallEnergyConsumtion(places.get(1));
			
			double currentAtPlace6 = mDataCache.getLastEnergyReading(places.get(2));
			double overallAtPlace6 = mDataCache.getOverallEnergyConsumtion(places.get(2));
			
//			Log.e(TAG, "MeterWheelUpdate:"+currentAtPlace1+", "+currentAtPlace3+", "+currentAtPlace6);
			mEdv1Frag.setMeterValue(currentAtPlace1);
			mEdv1Frag.setWheelValue(overallAtPlace1);
			
			mEdv3Frag.setMeterValue(currentAtPlace3);
			mEdv3Frag.setWheelValue(overallAtPlace3);
			
			mEdv6Frag.setMeterValue(currentAtPlace6);
			mEdv6Frag.setWheelValue(overallAtPlace6);
			
		}
		
	}
	
	private void showNotification(String _title, String _text)
	{
		// UL: in case for HD to correspond with the mocked screenshots for PMS text will be static
		_text =  "Computer 'EDV1-CLIENT-02' in Raum EDV1 seit 3h inaktiv";
		
		Intent notificationIntent = new Intent(mCtx, SesameTabletActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mCtx, 0, notificationIntent, 0);
		mNotification.tickerText = _text;
		mNotification.setLatestEventInfo(mCtx, _title, _text, contentIntent);
	
		
		
		mNotificationMan.notify(1, mNotification);
	}

	@Override
	public void notifyAboutNotification(String _msg) 
	{
		if(mShowNotifications)
		{
//			mPMSFrag.setShowNotification(true);
			showNotification(NOTIFICATION_TITLE, _msg);			
		}
		
	}

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		Log.e(TAG, "onCreate()");
//		super.onCreate(savedInstanceState);
//	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onPause() {
		stopMeterWheelUpdates();
		Log.e(TAG, "onPause()");
		super.onPause();
	}

	@Override
	public void onResume() 
	{
		startMeterWheelUpdates();
		Log.e(TAG, "onResume()");
		super.onResume();
	}
	
	
}
