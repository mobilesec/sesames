package at.sesame.fhooe.tablet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.LocalActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.Toast;
import at.fhooe.facedetectionview.model.FacesDetectedEvent;
import at.fhooe.facedetectionviewcomponent.FaceDetectionViewComponent;
import at.sesame.fhooe.lib2.config.ConfigLoader;
import at.sesame.fhooe.lib2.data.INotificationListener;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.data.SesameMeasurement;
import at.sesame.fhooe.lib2.data.SesameNotification;
import at.sesame.fhooe.lib2.esmart.service.response.GetServicesResponseHandler;
import at.sesame.fhooe.lib2.logging.SesameLogger;
import at.sesame.fhooe.lib2.logging.SesameLogger.EntryType;
import at.sesame.fhooe.lib2.logging.export.SesameFileLogExporter;
import at.sesame.fhooe.lib2.logging.export.SesameFileLogExporter.ExportLocation;
import at.sesame.fhooe.lib2.mail.SesameMail;
import at.sesame.fhooe.lib2.mail.SesameMail.NotificationType;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory.DialogType;
import at.sesame.fhooe.lib2.ui.EnergyMeterRenderer;
import at.sesame.fhooe.lib2.ui.MeterWheelFragment;
import at.sesame.fhooe.lib2.data.ISesameUpdateListener;
import at.sesame.fhooe.lib2.logging.export.SesameFileLogExporter;


public class SesameTabletActivity 
extends FragmentActivity 
implements INotificationListener, ISesameUpdateListener 
{
	private static final SimpleDateFormat	LOG_FILENAME_DATE_FORMAT		= new SimpleDateFormat("dd_MM_yy_HH_mm");
	private static final String				TAG								= "SesameTabletActivity";
	private static final long				METER_WHEEL_UPDATE_TIMEOUT		= 10000;
	private static final long				FACE_DETECTION_UPDATE_PERIOD	= 5000;
	private static final long				LOG_EXPORT_PERIOD				= 10000;
	private static int						mNotificationId;
	// private Context mCtx;
	private LayoutInflater					mLi;
	// private FragmentManager mFragMan;
	private LocalActivityManager			mLam;

	private SesameDataCache					mDataCache;

	private Timer							mMeterWheelUpdateTimer;

	private static MeterWheelFragment		mEdv1Frag;
	private static MeterWheelFragment		mEdv3Frag;
	private static MeterWheelFragment		mEdv6Frag;
	
	private boolean mRepoAvailable = false;
	private boolean mPmsAvailable = false;
	private boolean mRepoDataNew = false;

	// private WheelFragment mEdv1WheelFrag;
	// private WheelFragment mEdv3WheelFrag;
	// private WheelFragment mEdv6WheelFrag;

	// private static PMSFragment mPMSFrag;

	private PMSRoomsListFragment			mRoomListFrag;
	// private static TabbedComparisonFragment mTabFrag;

	private Notification					mNotification;
	private NotificationManager				mNotificationMan;
//	private static final String				NOTIFICATION_TITLE				= R.string.sesame_notification_title;

	private static final int				WHEEL_TEXT_SIZE					= 28;

	private boolean							mShowNotifications				= true;

	private Handler							mUiHandler						= new Handler();

	// private SesameMeasurementPlace mEdv1Place;
	// private SesameMeasurementPlace mEdv3Place;
	// private SesameMeasurementPlace mEdv6Place;

	private ArrayList<SesameNotification>	mLastNotifications;
	private FaceDetectionViewComponent		mFaceViewComponent				= new FaceDetectionViewComponent();

	private Timer							mFaceDetectionTimer;
	private Timer mHeartBeatTimer;
	private static final long HEARTBEAT_PERIOD = 3600000;
	private FrameLayout						mFaceContainer					= null;
	
	private Date mLastEnergyUpdate;
//	private Date mLastEnergyUpdateTimeStamp;
	private Date mLastPmsUpdate;
	
	private int mNumHoursBeforeRepoFailNotification = 3;
//	private int mNumMinutesBeforePmsFailNotification = 5;
	
	private int mPmsUpdateFailCount = 0;
	private int mMaxPmsUpdateFailCount = 5;
	
	private int mEnergyUpdateFailCount = 0;
	private int mMaxEnergyUpdateFailCount = 5;
	
	private Date mLastPmsFailNotification;
	private Date mLastRepoOldNotification;
	private Date mLastRepoConnectionLostNotification;
	
	private int mNumHoursBeforeNextNotification = 1;
	
	private Timer mShutdownTimer;
	private int mShutdownHour = 20;

	private SesameFileLogExporter mExporter;
	// public SesameTabletActivity(Context _ctx, FragmentManager _fm, Handler
	// _uiHandler)
	// {
	// mCtx = _ctx;
	// mLi = LayoutInflater.from(_ctx);
	// mFragMan = _fm;
	// mUiHandler = _uiHandler;
	// initializeNotification();
	// initializeFragments();
	// setContentView(R.layout.hd_layout);
	// addFragments();
	// // startMeterWheelUpdates();
	// }

	
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setTheme(android.R.style.Theme_Holo_Light);

		mLam = new LocalActivityManager(this, false);
		mLam.dispatchCreate(savedInstanceState);
		String fileName = "sesameLog" + LOG_FILENAME_DATE_FORMAT.format(new Date()) + ".csv";

		mExporter = new SesameFileLogExporter(SesameTabletActivity.this, ExportLocation.EXT_PUB_DIR,
				fileName);
		SesameLogger.setExporter(mExporter);

		SesameLogger.startContinuousExport(LOG_EXPORT_PERIOD);
		
		if(!checkConnectivity())
		{
			Toast.makeText(this, "Netzwerk nicht verbunden.\n Anwendung wird beendet.\n Bitte stellen sie eine Internetverbindung her und starten sie die Anwendung erneut", Toast.LENGTH_LONG).show();
			new Timer().schedule(new ShutdownTask(), 5000);
		}
		else
		{
			
//			startShutdownTask();
//		Log.i(TAG, ConfigLoader.loadConfig().toString());
			new CreationTask().execute();			
		}
	}
	
	/**
	 * checks if the device currently is connected to the internet
	 * @return true if the device is connected, false otherwise
	 */
	private boolean checkConnectivity() 
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private class CreationTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) 
		{

			SesameDataCache.createInstance(SesameTabletActivity.this);
			mDataCache = SesameDataCache.getInstance();
			mDataCache.registerSesameUpdateListener(SesameTabletActivity.this);
//			mDataCache.registerNotificationListener(SesameTabletActivity.this);
//			mDataCache.startEnergyDataUpdates();
//			mDataCache.startNotificationUpdates();
			// ArrayList<SesameMeasurementPlace> places =
			// mDataCache.getEnergyMeasurementPlaces();
			// mEdv1Place = places.get(4);
			// mEdv3Place = places.get(3);
			// mEdv6Place = places.get(5);

			// Log.e(TAG, "EDV1:"+mEdv1Place.toString());
			// Log.e(TAG, "EDV3:"+mEdv3Place.toString());
			// Log.e(TAG, "EDV6:"+mEdv6Place.toString());

//			initializeNotification();
			initializeFragments();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setContentView(R.layout.hd_layout);

					mFaceContainer = (FrameLayout) findViewById(R.id.faceContainer1);
					initializeFaceDetectionComponent();
					addFragments();
					createTabs();
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			PMSDialogFactory.dismissCurrentDialog();
//			startHeartBeat();
		}

		@Override
		protected void onPreExecute() {
			PMSDialogFactory.showDialog(DialogType.NETWORKING_IN_PROGRESS, getSupportFragmentManager(), null,
					new Object[] { SesameTabletActivity.this });
		}

	}

	private void initializeFaceDetectionComponent() {

		if (null != mFaceContainer) 
		{
			try
			{
				mFaceViewComponent.pause();
				// CHOICE 1: DEFAULT SETTINGS, THIS IS WHAT SHOULD GET USED NORMALLY
				mFaceViewComponent.resume(this, mFaceContainer, true);
				startFaceDetection();
			}
			catch(Exception e)
			{
				
			}
		}
	}
	
	private void startHeartBeat()
	{
		stopHeartBeat();
		mHeartBeatTimer = new Timer("heartbeat");
		mHeartBeatTimer.schedule(new HeartBeatTask(), 60000, HEARTBEAT_PERIOD);
	}
	
	private void stopHeartBeat()
	{
		if(null!=mHeartBeatTimer)
		{
			mHeartBeatTimer.cancel();
			mHeartBeatTimer.purge();
		}
	}
	
	
	private class HeartBeatTask extends TimerTask
	{
		@Override
		public void run()
		{
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "heartbeat");
			Log.i(TAG, "################HEARTBEAT################");
			SesameMail mail = new SesameMail();
			try {
				mail.addAttachment(mExporter.getMailLogFilePath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StringBuffer bodyBuffer = new StringBuffer();
			
			bodyBuffer.append("PMS available: "+mPmsAvailable);
			bodyBuffer.append("\nRepo available: "+mRepoAvailable);
			bodyBuffer.append("\nRepo data new: "+mRepoDataNew);
			
			if(null!=mLastPmsUpdate)
			{
				bodyBuffer.append("\nlast pms update:");
				bodyBuffer.append(mLastPmsUpdate.toString());				
			}
			else
			{
				bodyBuffer.append("\nno pms update yet...");
			}
			
			if(null!=mLastEnergyUpdate)
			{
				bodyBuffer.append("\nlast energy update:");
				bodyBuffer.append(mLastEnergyUpdate.toString());				
			}
			else
			{
				bodyBuffer.append("\nno energy update yet...");
			}
			bodyBuffer.append("\nlast meter reading:");
			try
			{
				bodyBuffer.append(mDataCache.getLastEnergyDataTimeStamp().toString());
				
			}
			catch(Exception e)
			{
				
			}
			
			boolean res = mail.send(mDataCache.getConfigData(), bodyBuffer.toString());
		}
	}

	private void createTabs() {

		TabHost th = (TabHost) findViewById(R.id.hd_tab_fragment_layout_tabhost);
		th.setup(mLam);

		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(getApplicationContext(), RealTimeActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		TabHost.TabSpec spec = th.newTabSpec("today").setIndicator(getString(R.string.today_tab_title)).setContent(intent);
		th.addTab(spec);
		intent = new Intent().setClass(getApplicationContext(), ComparisonActivity.class);
		TabHost.TabSpec spec2 = th.newTabSpec("comparison").setIndicator(getString(R.string.comparison_tab_title)).setContent(intent);
		th.addTab(spec2);
		th.setCurrentTab(0);
	}

	// public void onStart()
	// {
	// addFragments();
	// super.onStart();
	// }

	private void initializeNotification() {
		int icon = R.drawable.ic_stat_warning;
		CharSequence tickerText = "Hello NOTIFICATION";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);
		mNotificationMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private void initializeFragments() {
		// Setup EnergyMeterRenderer
		EnergyMeterRenderer r = new EnergyMeterRenderer();
		r.setCurrentValueY(0.435f);
		r.setMaxValue(2000);
		r.setMajorTickSpacing(750);
		r.setMinorTickSpacing(150);
		r.setMinorTickLength(16);
		r.setMajorTickLength(20);
		r.setRelativeTickRadius(1.1f);
		r.setTickTextSize(10);
		r.setFullAngle(84);
		r.setPointerBaseWidth(10);
		r.setMajorTickLineWidth(2.0f);
		r.setMinorTickLineWidth(1.0f);
		r.setMaxValue(3000.0f);

		String room1Name = getString(R.string.global_Room1_name);
		String room3Name = getString(R.string.global_Room3_name);
		String room6Name = getString(R.string.global_Room6_name);

		mEdv1Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, room1Name, 20.0f, 14.0f, WHEEL_TEXT_SIZE, 6,
				false, r);
		mEdv3Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, room3Name, 20.0f, 14.0f, WHEEL_TEXT_SIZE, 6,
				false, r);
		mEdv6Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, room6Name, 20.0f, 14.0f, WHEEL_TEXT_SIZE, 6,
				false, r);
		// mEdv1WheelFrag = new WheelFragment(mCtx, null, 5, null,
		// WHEEL_TEXT_SIZE);
		// mEdv3WheelFrag = new WheelFragment(mCtx, null, 5, null,
		// WHEEL_TEXT_SIZE);
		// mEdv6WheelFrag = new WheelFragment(mCtx, null, 5, null,
		// WHEEL_TEXT_SIZE);
		// mUiHandler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// TODO Auto-generated method stub
		// mPMSFrag = new PMSFragment(mCtx, mUiHandler);
		// }
		// });

		// try {
		// Thread.sleep(10);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// mTabFrag = new TabbedComparisonFragment(mCtx);

		mRoomListFrag = new PMSRoomsListFragment(this, mUiHandler, getSupportFragmentManager());

	}

	private void addFragments() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.hd_layout_edv1Frame, mEdv1Frag);
		ft.add(R.id.hd_layout_edv3Frame, mEdv3Frag);
		ft.add(R.id.hd_layout_edv6Frame, mEdv6Frag);

		// ft.add(R.id.hd_layout_edv1Frame, mEdv1WheelFrag);
		// ft.add(R.id.hd_layout_edv3Frame, mEdv3WheelFrag);
		// ft.add(R.id.hd_layout_edv6Frame, mEdv6WheelFrag);

		// ft.add(R.id.hd_layout_pmsFrame, mPMSFrag);
		//
		// ft.add(R.id.hd_layout_chartFrame, mTabFrag);
		ft.add(R.id.hd_layout_pmsFrame, mRoomListFrag);
		ft.commit();
		// mFragMan.executePendingTransactions();
	}

	// @Override
	// public void onAttach(Activity activity) {
	// // TODO Auto-generated method stub
	// super.onAttach(activity);
	// mShowNotifications = true;
	// Log.e(TAG, "onAttach");
	// initializeFragments();
	// startMeterWheelUpdates();
	// // addFragments();
	// // testNotifications();
	// }

	// @Override
	// public void onDetach() {
	// mShowNotifications = false;
	// mNotificationMan.cancelAll();
	// stopMeterWheelUpdates();
	// super.onDetach();
	// }

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// Log.e(TAG, "onCreateView");
	// View v = mLi.inflate(R.layout.hd_layout, null);
	// addFragments();
	// return v;
	// }

	private void stopFaceDetection() {
		if (null != mFaceDetectionTimer) {
			mFaceDetectionTimer.cancel();
			mFaceDetectionTimer.purge();
		}
	}

	private void startFaceDetection() {
		stopFaceDetection();
		mFaceDetectionTimer = new Timer();
		mFaceDetectionTimer.schedule(new FaceDetectionQueryTask(), 0, FACE_DETECTION_UPDATE_PERIOD);
	}

	public void stopMeterWheelUpdates() {
		if (null != mMeterWheelUpdateTimer) {
			mMeterWheelUpdateTimer.cancel();
			mMeterWheelUpdateTimer.purge();
		}
	}

	public void startMeterWheelUpdates() {
		stopMeterWheelUpdates();
		mMeterWheelUpdateTimer = new Timer();
		mMeterWheelUpdateTimer.schedule(new MeterWheelUpdateTask(), 0, METER_WHEEL_UPDATE_TIMEOUT);
	}

	private class MeterWheelUpdateTask extends TimerTask {

		@Override
		public void run() {

			try {
				SesameMeasurement lastEdv1Measurement = mDataCache.getLastEnergyReading(SesameDataCache.EDV1_PLACE);
				double overallAtPlace1 = mDataCache.getOverallEnergyConsumtion(SesameDataCache.EDV1_PLACE);
//				Log.e(TAG, "last measurement of EDV1 set to:"+lastEdv1Measurement.toString());

				SesameMeasurement lastEdv3Measurement = mDataCache.getLastEnergyReading(SesameDataCache.EDV3_PLACE);
				double overallAtPlace3 = mDataCache.getOverallEnergyConsumtion(SesameDataCache.EDV3_PLACE);
//				Log.e(TAG, "last measurement of EDV3 set to:"+lastEdv3Measurement.toString());

				SesameMeasurement lastEdv6Measurement = mDataCache.getLastEnergyReading(SesameDataCache.EDV6_PLACE);
				double overallAtPlace6 = mDataCache.getOverallEnergyConsumtion(SesameDataCache.EDV6_PLACE);
//				Log.e(TAG, "last measurement of EDV6 set to:"+lastEdv6Measurement.toString());

				mEdv1Frag.setLastMeasurementDate(lastEdv1Measurement.getTimeStamp());
				mEdv1Frag.setMeterValue(lastEdv1Measurement.getVal());				
				mEdv1Frag.setWheelValue(overallAtPlace1);

				mEdv3Frag.setLastMeasurementDate(lastEdv3Measurement.getTimeStamp());
				mEdv3Frag.setMeterValue(lastEdv3Measurement.getVal());
				mEdv3Frag.setWheelValue(overallAtPlace3);

				mEdv6Frag.setLastMeasurementDate(lastEdv6Measurement.getTimeStamp());
				mEdv6Frag.setMeterValue(lastEdv6Measurement.getVal());
				mEdv6Frag.setWheelValue(overallAtPlace6);

				// mEdv3Frag.setMeterValue(currentAtPlace3);
				// mEdv3Frag.setWheelValue(overallAtPlace3);
				//
				// mEdv6Frag.setMeterValue(currentAtPlace6);
				// mEdv6Frag.setWheelValue(overallAtPlace6);
			} catch (Exception e) {
//				e.printStackTrace();
				// TODO Auto-generated catch block
			}

		}

	}
	
	private class ShutdownTask extends TimerTask
	{

		@Override
		public void run() 
		{
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "automatic shutdown");
			finish();
		}
		
	}
	
	private void startShutdownTask()
	{
		stopShutdownTask();
		GregorianCalendar shutdownCal = new GregorianCalendar();
		shutdownCal.set(Calendar.HOUR_OF_DAY, mShutdownHour);
		shutdownCal.set(Calendar.MINUTE, 30);
		shutdownCal.set(Calendar.SECOND, 0);
		SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "automatic shutdown scheduled for: "+shutdownCal.getTime().toString());
		mShutdownTimer = new Timer("shutdown timer");
		mShutdownTimer.schedule(new ShutdownTask(), shutdownCal.getTime());
	}
	
	private void stopShutdownTask()
	{
		if(null!=mShutdownTimer)
		{
			mShutdownTimer.cancel();
			mShutdownTimer.purge();
		}
	}

	private void showNotification(String _title, String _text) {
		// UL: in case for HD to correspond with the mocked screenshots for PMS
		// text will be static
		// _text = "Computer 'EDV1-CLIENT-02' in Raum EDV1 seit 3h inaktiv";

		Intent notificationIntent = new Intent(getApplicationContext(), SesameTabletActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), mNotificationId, notificationIntent, 0);
		mNotification.tickerText = _text;
		mNotification.setLatestEventInfo(getApplicationContext(), _title, _text, contentIntent);

		mNotificationMan.notify(mNotificationId, mNotification);
		mNotificationId++;
	}

	@Override
	public void notifyAboutNotification(ArrayList<SesameNotification> _notifications) {
		Log.i(TAG, "notified about notifications:" + _notifications.size());
		if (mShowNotifications) {
			if (null != mRoomListFrag) {
				// mRoomListFrag.notifyAboutNotifications(_notifications);
			}

			for (SesameNotification sn : _notifications) {
				// showNotification(NOTIFICATION_TITLE, sn.toString());
				// if(null==mLastNotifications||!mLastNotifications.contains(sn))
				{
					// mRoomListFrag.notifyAboutNotifications(sn);

				}
				// else
				// {
				// Log.i(TAG, "notification already forwarded, discarded");
				// }
			}
			// // mPMSFrag.setShowNotification(true);
		}

	}

	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// Log.e(TAG, "onCreate()");
	// super.onCreate(savedInstanceState);
	// }

	@Override
	public void onDestroy()
	{
		stopShutdownTask();
		stopHeartBeat();
		stopMeterWheelUpdates();
		if(null!=mLam)
		{
			mLam.dispatchDestroy(isFinishing());			
		}
		if(null!=mDataCache)
		{
			mDataCache.cleanUp();			
		}
		SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "application closed");
		SesameLogger.export();
		SesameMail mail = new SesameMail();
		try {
			mail.addAttachment(mExporter.getMailLogFilePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		mail.setBody("app was shut down");
		
		try {
			mail.send(mDataCache.getConfigData(), "app was shut down");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SesameLogger.stopContinuousExporting();
		super.onDestroy();
//		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public void onPause() {
		mFaceViewComponent.pause();
		stopFaceDetection();
		stopMeterWheelUpdates();
		mLam.dispatchPause(isFinishing());
		super.onPause();
	}

	@Override
	public void onResume() {
		initializeFaceDetectionComponent();
		startMeterWheelUpdates();
		mLam.dispatchResume();
		super.onResume();
	}

	private class FaceDetectionQueryTask extends TimerTask {
		@Override
		public void run() {
			updateFaceDetectionInfo();
			// runOnUiThread(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// }
			// });
		}

		private void updateFaceDetectionInfo() {
			if (null != mFaceViewComponent) {
				FacesDetectedEvent event = mFaceViewComponent.getLastFaceDetectedEvent();
				if (null == event) {
//					Log.e(TAG, "event was null");
				} else {
					// Log.e(TAG, "near faces:" + event.getAmountOfNearFaces());
					SesameLogger.log(EntryType.FACE_DETECTION, TAG, "" + event.getAmountOfNearFaces());
				}
				// Log.e(TAG, "" + event);
				// System.out.println(event);
			}
		}
	}

	@Override
	public void notifyPmsUpdate(boolean _success) 
	{
		Log.i(TAG, "notified about pms update");
		if(_success)
		{
			mPmsUpdateFailCount = 0;
			mLastPmsUpdate = new Date();
			mPmsAvailable = true;
			mRoomListFrag.notifyPMSUpdated();
		}
		else
		{
			mPmsUpdateFailCount++;
		}
		if(mPmsUpdateFailCount>=mMaxPmsUpdateFailCount)
		{
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "connection to pms potentially lost...");
			if(mPmsAvailable)
			{
				new SesameMail().send(mDataCache.getConfigData(), NotificationType.PMS_FAILED);
			}
			mPmsAvailable = false;
//			if(shouldNotify(mLastPmsFailNotification))
//			{
//				new SesameMail().send(mDataCache.getConfigData(), NotificationType.PMS_FAILED);
//				mLastPmsFailNotification = new Date();
//			}
		}
	}

	@Override
	public void notifyEnergyUpdate(boolean _success)
	{
		Log.i(TAG, "notified about energy update");
		if(_success)
		{
			mRepoAvailable = true;
			mEnergyUpdateFailCount = 0;
			mLastEnergyUpdate = new Date();
			
		}
		else
		{
			mEnergyUpdateFailCount++;
		}
		
		if(mEnergyUpdateFailCount>=mMaxEnergyUpdateFailCount)
		{
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "connection to repository lost");
			if(mRepoAvailable)
			{
				new SesameMail().send(mDataCache.getConfigData(), NotificationType.REPO_FAILED);
			}
			mRepoAvailable = false;
//			if(shouldNotify(mLastRepoConnectionLostNotification))
//			{
//				new SesameMail().send(mDataCache.getConfigData(), NotificationType.REPO_FAILED);
//				mLastRepoConnectionLostNotification = new Date();
//			}
		}
		
		long lastUpdateMillis = mDataCache.getLastEnergyDataTimeStamp().getTime();
		long nowMillis = new Date().getTime();
		long diff = nowMillis-lastUpdateMillis;
		if(diff<0)
		{
			Log.e(TAG, "error computing diff for energy update times");
		}
		
		if(diff>3600000*mNumHoursBeforeRepoFailNotification)
		{
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "last update of energy data is older than "+mNumHoursBeforeRepoFailNotification+" hours");
			if(mRepoDataNew)
			{
				new SesameMail().send(mDataCache.getConfigData(), NotificationType.REPO_OLD);
			}
			mRepoDataNew = false;
//			if(shouldNotify(mLastRepoOldNotification))
//			{
//				new SesameMail().send(mDataCache.getConfigData(), NotificationType.REPO_OLD);
//				mLastRepoOldNotification = new Date();
//			}
		}
		else
		{
			mRepoDataNew = true;
		}
		
	}
	
//	private boolean shouldNotify(Date _d)
//	{
//		if(null==_d)
//		{
//			return true;
//		}
//		GregorianCalendar checkCal = new GregorianCalendar();
//		checkCal.add(Calendar.HOUR_OF_DAY, -1* mNumHoursBeforeNextNotification);
//		
//		return _d.before(checkCal.getTime());
//	}

	@Override
	public void notifyConnectivityLoss() 
	{
		Log.i(TAG, "notified about connection loss");
		runOnUiThread(new Runnable()
		{	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "connection lost");
				Toast.makeText(SesameTabletActivity.this, getString(R.string.connection_loss_message), Toast.LENGTH_LONG).show();
//				mDataCache.cleanUp();
			}
		});
		
	}

}
