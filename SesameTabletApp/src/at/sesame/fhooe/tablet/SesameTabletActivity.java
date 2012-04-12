package at.sesame.fhooe.tablet;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.LocalActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TabHost;
import at.fhooe.facedetectionview.model.FacesDetectedEvent;
import at.fhooe.facedetectionviewcomponent.FaceDetectionViewComponent;
import at.sesame.fhooe.lib2.data.INotificationListener;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;
import at.sesame.fhooe.lib2.data.SesameNotification;
import at.sesame.fhooe.lib2.logging.SesameLogger;
import at.sesame.fhooe.lib2.logging.SesameLogger.EntryType;
import at.sesame.fhooe.lib2.logging.export.SesameFileLogExporter;
import at.sesame.fhooe.lib2.logging.export.SesameFileLogExporter.ExportLocation;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory.DialogType;
import at.sesame.fhooe.lib2.ui.EnergyMeterRenderer;
import at.sesame.fhooe.lib2.ui.MeterWheelFragment;

@SuppressWarnings("unused")
public class SesameTabletActivity 
extends FragmentActivity
implements INotificationListener
{
	private static final SimpleDateFormat LOG_FILENAME_DATE_FORMAT = new SimpleDateFormat("dd_MM_yy_HH_mm");
	private static final String TAG = "SesameTabletActivity";
	private static final long METER_WHEEL_UPDATE_TIMEOUT = 1000;
	private static final long FACE_DETECTION_UPDATE_PERIOD = 5000;
	private static final long LOG_EXPORT_PERIOD = 10000;
//	private Context mCtx;
	private LayoutInflater mLi;
//	private FragmentManager mFragMan;
	private LocalActivityManager mLam;

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
	//	private static TabbedComparisonFragment mTabFrag;

	private Notification mNotification;
	private NotificationManager mNotificationMan;
	private static final String NOTIFICATION_TITLE ="Sesame Notification";

	private static final int WHEEL_TEXT_SIZE = 28;
	

	private boolean mShowNotifications = true;

	private Handler mUiHandler = new Handler();
	
	private SesameMeasurementPlace mEdv1Place;
	private SesameMeasurementPlace mEdv3Place;
	private SesameMeasurementPlace mEdv6Place;
	
	private ArrayList<SesameNotification> mLastNotifications;
	private FaceDetectionViewComponent mFaceViewComponent = new FaceDetectionViewComponent();
	
	private Timer mFaceDetectionTimer;

	private FrameLayout mFaceContainer = null;
	
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

	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setTheme(android.R.style.Theme_Holo);
		
		mLam = new LocalActivityManager(this, false);
		mLam.dispatchCreate(savedInstanceState);
		
		new CreationTask().execute();
	}
	
	private class CreationTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) 
		{
			
			mDataCache = SesameDataCache.getInstance(SesameTabletActivity.this);
			mDataCache.registerNotificationListener(SesameTabletActivity.this);
			mDataCache.startEnergyDataUpdates();
			mDataCache.startNotificationUpdates();
			ArrayList<SesameMeasurementPlace> places = mDataCache.getEnergyMeasurementPlaces();
			mEdv1Place = places.get(4);
			mEdv3Place = places.get(3);
			mEdv6Place = places.get(5);
			
			Log.e(TAG, "EDV1:"+mEdv1Place.toString());
			Log.e(TAG, "EDV3:"+mEdv3Place.toString());
			Log.e(TAG, "EDV6:"+mEdv6Place.toString());

			initializeNotification();
			initializeFragments();
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() 
				{	
					setContentView(R.layout.hd_layout);

					mFaceContainer = (FrameLayout)findViewById(R.id.faceContainer1);
					initializeFaceDetectionComponent();
					addFragments();
					createTabs();
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			PMSDialogFactory.dismissCurrentDialog();
			String fileName = "sesameLog"+LOG_FILENAME_DATE_FORMAT.format(new Date())+".csv";

			SesameFileLogExporter exporter = new SesameFileLogExporter(SesameTabletActivity.this,ExportLocation.EXT_PUB_DIR,  fileName);
			SesameLogger.addExporter(exporter);
			
			SesameLogger.startContinuousExport(LOG_EXPORT_PERIOD);
		}

		@Override
		protected void onPreExecute()
		{
			PMSDialogFactory.showDialog(DialogType.NETWORKING_IN_PROGRESS, getSupportFragmentManager(), null, new Object[]{SesameTabletActivity.this});
		}
		
	}
	
	private void initializeFaceDetectionComponent()
	{
		
		if(null!=mFaceContainer)
		{
			mFaceViewComponent.onPause();
		  // CHOICE 1: DEFAULT SETTINGS, THIS IS WHAT SHOULD GET USED NORMALLY
		  mFaceViewComponent.onResume(this, mFaceContainer, true);
		  startFaceDetection();
		}
	}
	private void createTabs()
	{
		
		TabHost th = (TabHost)findViewById(R.id.hd_tab_fragment_layout_tabhost);
		th.setup(mLam);


		Intent intent;  // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(getApplicationContext(), RealTimeActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		TabHost.TabSpec spec = th.newTabSpec("realtime").setIndicator("Echtzeit")
				.setContent(intent);
		th.addTab(spec);
		intent = new Intent().setClass(getApplicationContext(), ComparisonActivity.class);
		TabHost.TabSpec spec2 = th.newTabSpec("comparison").setIndicator("Vergleich")
				.setContent(intent);
		th.addTab(spec2);
		th.setCurrentTab(0);
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
		mNotificationMan = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private void initializeFragments()
	{
		// Setup EnergyMeterRenderer
		EnergyMeterRenderer r = new EnergyMeterRenderer();
		r.setCurrentValueY(0.435f);
		r.setMaxValue(2000);
		r.setMajorTickSpacing(500);
		r.setMinorTickSpacing(100);
		r.setMinorTickLength(16);
		r.setMajorTickLength(20);
		r.setRelativeTickRadius(1.1f);
		r.setTickTextSize(10);
		r.setFullAngle(84);
		r.setPointerBaseWidth(10);
		r.setMajorTickLineWidth(2.0f);
		r.setMinorTickLineWidth(1.0f);

		String room1Name = getString(R.string.global_Room1_name);
		String room3Name = getString(R.string.global_Room3_name);
		String room6Name = getString(R.string.global_Room6_name);

		mEdv1Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, room1Name, 20.0f, 14.0f, WHEEL_TEXT_SIZE, 6, false, r);
		mEdv3Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, room3Name, 20.0f, 14.0f, WHEEL_TEXT_SIZE, 6, false, r);
		mEdv6Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, room6Name, 20.0f, 14.0f, WHEEL_TEXT_SIZE, 6, false, r);
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

		//		mTabFrag = new TabbedComparisonFragment(mCtx);

				mRoomListFrag = new PMSRoomsListFragment(this, mUiHandler, getSupportFragmentManager());

	}

	private void addFragments()
	{
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.hd_layout_edv1Frame, mEdv1Frag);
		ft.add(R.id.hd_layout_edv3Frame, mEdv3Frag);
		ft.add(R.id.hd_layout_edv6Frame, mEdv6Frag);

		//		ft.add(R.id.hd_layout_edv1Frame, mEdv1WheelFrag);
		//		ft.add(R.id.hd_layout_edv3Frame, mEdv3WheelFrag);
		//		ft.add(R.id.hd_layout_edv6Frame, mEdv6WheelFrag);

		//		ft.add(R.id.hd_layout_pmsFrame, mPMSFrag);
		//		
		//		ft.add(R.id.hd_layout_chartFrame, mTabFrag);
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

	private void stopFaceDetection()
	{
		if(null!=mFaceDetectionTimer)
		{
			mFaceDetectionTimer.cancel();
			mFaceDetectionTimer.purge();
		}
	}
	
	private void startFaceDetection() 
	{
		stopFaceDetection();
		mFaceDetectionTimer = new Timer();
		mFaceDetectionTimer.schedule(new FaceDetectionQueryTask(), 0, FACE_DETECTION_UPDATE_PERIOD);
	}
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


			try 
			{
				double currentAtPlace1 = mDataCache.getLastEnergyReading(mEdv1Place).getVal();
				double overallAtPlace1 = mDataCache.getOverallEnergyConsumtion(mEdv1Place);
//				Log.i(TAG, "place1:"+currentAtPlace1+"/"+overallAtPlace1);

				double currentAtPlace3 = mDataCache.getLastEnergyReading(mEdv3Place).getVal();
				double overallAtPlace3 = mDataCache.getOverallEnergyConsumtion(mEdv3Place);
//				Log.i(TAG, "place3:"+currentAtPlace3+"/"+overallAtPlace3);

				double currentAtPlace6 = mDataCache.getLastEnergyReading(mEdv6Place).getVal();
				double overallAtPlace6 = mDataCache.getOverallEnergyConsumtion(mEdv6Place);
//				Log.i(TAG, "place6:"+currentAtPlace6+"/"+overallAtPlace6);

				//			Log.e(TAG, "MeterWheelUpdate:"+currentAtPlace1+", "+currentAtPlace3+", "+currentAtPlace6);
				mEdv1Frag.setMeterValue(currentAtPlace1);
				mEdv1Frag.setWheelValue(overallAtPlace1);

				mEdv3Frag.setMeterValue(currentAtPlace3);
				mEdv3Frag.setWheelValue(overallAtPlace3);

				mEdv6Frag.setMeterValue(currentAtPlace6);
				mEdv6Frag.setWheelValue(overallAtPlace6);
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
			}

		}

	}

	private void showNotification(String _title, String _text)
	{
		// UL: in case for HD to correspond with the mocked screenshots for PMS text will be static
//		_text =  "Computer 'EDV1-CLIENT-02' in Raum EDV1 seit 3h inaktiv";

		Intent notificationIntent = new Intent(getApplicationContext(), SesameTabletActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
		mNotification.tickerText = _text;
		mNotification.setLatestEventInfo(getApplicationContext(), _title, _text, contentIntent);



		mNotificationMan.notify(1, mNotification);
	}

	@Override
	public void notifyAboutNotification(ArrayList<SesameNotification> _notifications) 
	{
		Log.i(TAG, "notified about notifications:"+_notifications.size());
		if(mShowNotifications)
		{
			for(SesameNotification sn:_notifications)
			{
//				if(null==mLastNotifications||!mLastNotifications.contains(sn))
				{
					mRoomListFrag.notifyAboutNotification(sn);
					
				}
//				else
//				{
//					Log.i(TAG, "notification already forwarded, discarded");
//				}
			}
//			//			mPMSFrag.setShowNotification(true);
//			showNotification(NOTIFICATION_TITLE, _notifications);			
		}

	}

	//	@Override
	//	public void onCreate(Bundle savedInstanceState) {
	//		Log.e(TAG, "onCreate()");
	//		super.onCreate(savedInstanceState);
	//	}

	@Override
	public void onDestroy() 
	{
		SesameLogger.stopContinuousExporting();
		stopMeterWheelUpdates();
		mLam.dispatchDestroy(isFinishing());
		mDataCache.cleanUp();
		Log.e(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onPause() 
	{
		mFaceViewComponent.onPause();
		stopFaceDetection();
		stopMeterWheelUpdates();
		mLam.dispatchPause(isFinishing());
		Log.e(TAG, "onPause()");
		super.onPause();
	}

	@Override
	public void onResume() 
	{
		initializeFaceDetectionComponent();
		startMeterWheelUpdates();
		mLam.dispatchResume();
		Log.e(TAG, "onResume()");
		super.onResume();
	}


	private class FaceDetectionQueryTask extends TimerTask
	{
		@Override
		public void run() 
		{
			if(null!=mFaceViewComponent)
			{
				FacesDetectedEvent event = mFaceViewComponent.getLastFaceDetectedEven();
				if(null==event)
				{
					Log.e(TAG, "event was null");
				}
				else
				{
					SesameLogger.log(EntryType.FACE_DETECTION, TAG, ""+event.getAmountOfNearFaces());
				}
				
//				Log.e(TAG, "" + event);
//				System.out.println(event);
			}
		}	
	}

}
