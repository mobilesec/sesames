package at.sesame.fhooe.lib2.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import at.sesame.fhooe.lib2.Constants;
import at.sesame.fhooe.lib2.R;
import at.sesame.fhooe.lib2.config.ConfigLoader;
import at.sesame.fhooe.lib2.config.SesameConfigData;
import at.sesame.fhooe.lib2.data.provider.EsmartDataProvider;
import at.sesame.fhooe.lib2.data.provider.EzanDataProvider;
import at.sesame.fhooe.lib2.data.semantic.SemanticSesameDataSource;
import at.sesame.fhooe.lib2.data.simulation.DataSimulator;
import at.sesame.fhooe.lib2.data.simulation.NotificationSimulator;
import at.sesame.fhooe.lib2.logging.SesameLogger;
import at.sesame.fhooe.lib2.logging.SesameLogger.EntryType;
import at.sesame.fhooe.lib2.pms.DeviceStateUpdater;
import at.sesame.fhooe.lib2.pms.PMSController;
import at.sesame.fhooe.lib2.pms.PMSProvider;
import at.sesame.fhooe.lib2.pms.hosts.EDV1Hosts;
import at.sesame.fhooe.lib2.pms.hosts.EDV3Hosts;
import at.sesame.fhooe.lib2.pms.hosts.EDV6Hosts;
import at.sesame.fhooe.lib2.pms.hosts.HostList;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;
import at.sesame.fhooe.lib2.util.DateHelper;

public class SesameDataCache
implements ISesameDataProvider
{
	
	private enum DataSource
	{
		mock,
		semantic_repo,
		webservices
	}

	//	private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
	//		
	//		@Override
	//		public void onReceive(Context context, Intent intent) 
	//		{
	//			// TODO Auto-generated method stub
	//			boolean connected = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
	//			if(!connected)
	//			{
	//				notifyConnectionLost();
	//			}
	//		}
	//	};

	private ArrayList<ISesameUpdateListener> mUpdateListeners = new ArrayList<ISesameUpdateListener>();

//	private Runnable mUpdateRunnable = new Runnable()
//	{
//		@Override
//		public void run()
//		{
//			while(mRunning)
//			{
//				update();
//				if(mRunning)
//				{
//					try
//					{
//						Thread.sleep(mUpdateTimeout);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}					
//				}
//			}
//			Log.i(TAG, "update thread finished");
//		}
//	};
	private boolean mRunning = false;
	private long mUpdateTimeout = 10000;
	private static Timer mUpdateTimer;
//	private Thread mUpdateThread;
	
	private void startUpdates()
	{
//		mRunning = true;
//		new Thread(mUpdateRunnable).start();
		stopUpdates();
		mUpdateTimer = new Timer("update timer");
		mUpdateTimer.schedule(new UpdateTask(), 0, mUpdateTimeout);
	}
	
	private static void stopUpdates()
	{
//		mRunning = false;
		if(null!=mUpdateTimer)
		{
			mUpdateTimer.cancel();
			mUpdateTimer.purge();
		}
	}
	public void registerSesameUpdateListener(ISesameUpdateListener _listener)
	{
		if(!mUpdateListeners.contains(_listener))
		{
			mUpdateListeners.add(_listener);
		}
	}

	public void unregisterSesameUpdateListener(ISesameUpdateListener _listener)
	{
		mUpdateListeners.remove(_listener);
	}

	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "SesameDataCache";

	/**
	 * hashtable containing information which energy listener is interested in which measurementplace
	 */
	private HashMap<SesameMeasurementPlace, ArrayList<ISesameDataListener>> mEnergyDataListener = new HashMap<SesameMeasurementPlace, ArrayList<ISesameDataListener>>();

	/**
	 * hashtable containing information which temperature listener is interested in which measurementplace
	 */
	private HashMap<SesameMeasurementPlace, ArrayList<ISesameDataListener>> mTemperatureDataListener = new HashMap<SesameMeasurementPlace, ArrayList<ISesameDataListener>>();

	/**
	 * hashtable containing information which humidity listener is interested in which measurementplace
	 */
	private HashMap<SesameMeasurementPlace, ArrayList<ISesameDataListener>> mHumidityDataListener = new HashMap<SesameMeasurementPlace, ArrayList<ISesameDataListener>>();

	/**
	 * hashtable containing information which light listener is interested in which measurementplace
	 */
	private HashMap<SesameMeasurementPlace, ArrayList<ISesameDataListener>> mLightDataListener = new HashMap<SesameMeasurementPlace, ArrayList<ISesameDataListener>>();

	/**
	 * list of all notification listeners
	 */
	private static ArrayList<INotificationListener> mNotificationListeners = new ArrayList<INotificationListener>();

	private static ArrayList<SesameMeasurementPlace> mEnergyMeasurementPlaces;
	private static ArrayList<SesameMeasurementPlace> mTemperatureMeasurementPlaces;
	private static ArrayList<SesameMeasurementPlace> mHumidityMeasurementPlaces;
	private static ArrayList<SesameMeasurementPlace> mLightMeasurementPlaces;


	private static IEnergyDataSource mEnergyDataSource;
	private static ITemperatureDataSource mTemperatureDataSource;
	private static IHumidityDataSource mHumidityDataSource;
	private static ILightDataSource mLightDataSource;
	private static INotificationSource mNotificationSource;

	private static HashMap<SesameMeasurementPlace, Boolean> mEnergyDataUpdateMap = new HashMap<SesameMeasurementPlace, Boolean>();
	private static HashMap<SesameMeasurementPlace, Boolean> mTemperatureDataUpdateMap = new HashMap<SesameMeasurementPlace, Boolean>();
	private static HashMap<SesameMeasurementPlace, Boolean> mHumidityDataUpdateMap = new HashMap<SesameMeasurementPlace, Boolean>();
	private static HashMap<SesameMeasurementPlace, Boolean> mLightDataUpdateMap = new HashMap<SesameMeasurementPlace, Boolean>();

	private static HashMap<SesameMeasurementPlace, SesameDataContainer> mEnergyData = new HashMap<SesameMeasurementPlace, SesameDataContainer>();
//	private static HashMap<SesameMeasurementPlace, SesameDataContainer> mHumidityData = new HashMap<SesameMeasurementPlace, SesameDataContainer>();
//	private static HashMap<SesameMeasurementPlace, SesameDataContainer> mTemperatureData = new HashMap<SesameMeasurementPlace, SesameDataContainer>();
//	private static HashMap<SesameMeasurementPlace, SesameDataContainer> mLightData = new HashMap<SesameMeasurementPlace, SesameDataContainer>();

	//	private static Date mFirstEnergyDate;
	//	private static Date mLastEnergyDate;

	private static DataSource mDataSource = DataSource.semantic_repo;

	private static PMSController mController;
	private static DeviceStateUpdater mDeviceStateUpdater;

	public static SesameMeasurementPlace EDV1_PLACE;
	public static SesameMeasurementPlace EDV3_PLACE;
	public static SesameMeasurementPlace EDV6_PLACE;

	//	private static ArrayList<EsmartMeasurementPlace> mEsmartMeasurementPlaces = new ArrayList<EsmartMeasurementPlace>();

	//	private static Hashtable<Integer, ArrayList<EsmartDataRow>> mRawEsmartData = new Hashtable<Integer, ArrayList<EsmartDataRow>>();
	//	private static Hashtable<EsmartMeasurementPlace, Boolean> mEsmartUpdateTable = new Hashtable<EsmartMeasurementPlace, Boolean>();

	//	private static ArrayList<EzanMeasurementPlace> mEzanMeasurementPlaces = new ArrayList<EzanMeasurementPlace>();
	//	private static Hashtable<EzanMeasurementPlace, ArrayList<EzanMeasurement>> mRawEzanData = new Hashtable<EzanMeasurementPlace, ArrayList<EzanMeasurement>>();


	/**
	 * instance for singleton
	 */
	private static SesameDataCache mInstance;

	private static Timer mEnergyUpdateTimer;
	private static final long ENERGY_DATA_UPDATE_INTERVAL = 20000; //every 20 seconds


	private static Timer mNotificationUpdateTimer;

	private static final long NOTIFICATION_UPDATE_INTERVAL = 20000;//every 20 seconds

	private Context mCtx;

	private HostList mEdv1Hosts = new EDV1Hosts();
	private HostList mEdv3Hosts = new EDV3Hosts();
	private HostList mEdv6Hosts = new EDV6Hosts();

	private SesameConfigData mConfigData;

	private int mNumDays2LoadEnergyData = 1;

	//	private NotificationSimulator mEventSim;

	private SesameDataCache(DataSource _source, Context _ctx)
	{
		//		_ctx.getSystemService(Context.c)
		mCtx = _ctx;
		switch(_source)
		{
		case mock:
			DataSimulator ds = new DataSimulator();
			//			EDV1_PLACE = ds.getEnergyMeasurementPlaces().get(0);
			//			Log.e(TAG, "edv1 place set to:"+EDV1_PLACE.toString());
			//			EDV3_PLACE = ds.getEnergyMeasurementPlaces().get(1);
			//			Log.e(TAG, "edv3 place set to:"+EDV3_PLACE.toString());
			//			EDV6_PLACE = ds.getEnergyMeasurementPlaces().get(2);
			//			Log.e(TAG, "edv6 place set to:"+EDV6_PLACE.toString());
			mEnergyDataSource = ds;
			mHumidityDataSource = ds;
			mTemperatureDataSource = ds;
			mLightDataSource = ds;

			mNotificationSource = new NotificationSimulator();
			break;
		case semantic_repo:
			SemanticSesameDataSource ssds = new SemanticSesameDataSource();
			//			EDV1_PLACE = ssds.getEnergyMeasurementPlaces().get(0);
			//			Log.e(TAG, "edv1 place set to:"+EDV1_PLACE.toString());
			//			EDV3_PLACE = ssds.getEnergyMeasurementPlaces().get(3);
			//			Log.e(TAG, "edv3 place set to:"+EDV3_PLACE.toString());
			//			EDV6_PLACE = ssds.getEnergyMeasurementPlaces().get(2);
			//			Log.e(TAG, "edv6 place set to:"+EDV6_PLACE.toString());
			mEnergyDataSource = ssds;
			mHumidityDataSource = ssds;
			mLightDataSource = ssds;
			mTemperatureDataSource = ssds;
			mNotificationSource = ssds;
			break;
		case webservices:
			mEnergyDataSource = new EsmartDataProvider();

			EzanDataProvider edp = new EzanDataProvider();
			mHumidityDataSource = edp;
			mTemperatureDataSource = edp;
			mLightDataSource = edp;
			mNotificationSource = new NotificationSimulator();
			break;
		}

		init();
	}

	private void startEnergyDataUpdates()
	{
		stopEnergyDataUpdates();
		mEnergyUpdateTimer = new Timer();
		mEnergyUpdateTimer.schedule(new EnergyUpdateTask(), 0, ENERGY_DATA_UPDATE_INTERVAL);
	}

	private static void stopEnergyDataUpdates()
	{
		if(null!=mEnergyUpdateTimer)
		{
			mEnergyUpdateTimer.cancel();
			mEnergyUpdateTimer.purge();
		}
	}

	public void scheduleSingleNotification(Date _d)
	{
		if(null!=mNotificationUpdateTimer)
		{
			mNotificationUpdateTimer.cancel();
			mNotificationUpdateTimer.purge();
		}

		mNotificationUpdateTimer = new Timer();
		mNotificationUpdateTimer.schedule(new NotificationUpdateTask(), _d);
	}

	private void startNotificationUpdates()
	{

		mNotificationUpdateTimer = new Timer();
		mNotificationUpdateTimer.schedule(new NotificationUpdateTask(), 0, NOTIFICATION_UPDATE_INTERVAL);
	}

	private static void stopNotificationUpdates()
	{
		if(null!=mNotificationUpdateTimer)
		{
			mNotificationUpdateTimer.cancel();
			mNotificationUpdateTimer.purge();
		}
	}

	public static void cleanUp()
	{
		SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "clean up");
//		if(null==getInstance())
//		{
//			return;
//		}
		stopUpdates();
		stopEnergyDataUpdates();
		stopNotificationUpdates();
		//		mController.stopAutoUpdate();
		Log.i(TAG, "Sesame datacache cleaned up");
	}

	public void notifyConnectionLost()
	{
		Log.e(TAG, "notified about connection loss");
//		cleanUp();
	}

	public SesameConfigData getConfigData()
	{
		return mConfigData;
	}
	
	public void init()
	{
		long start = System.currentTimeMillis();
		mConfigData = ConfigLoader.loadConfig();
		PMSProvider.createPMS(mConfigData.getUser(), mConfigData.getPass());
		//		mEventSim = new NotificationSimulator();
		//		startNotificationUpdates();
		HostList allHosts = new HostList();
		allHosts.addAll(mEdv1Hosts.getHosts());
		allHosts.addAll(mEdv3Hosts.getHosts());
		allHosts.addAll(mEdv6Hosts.getHosts());
		//		allHosts.addAll(new EDV1Hosts());
		mController = new PMSController(mCtx, allHosts);
		try {
			boolean devicesLoaded = mController.new QueryDevsTask().execute(allHosts).get();
			if(!devicesLoaded)
			{
				Log.e(TAG, "devices could not be loaded");
				exitAppAndNotify();
			}
			else
			{
				mDeviceStateUpdater = new DeviceStateUpdater(getAllDevices());
			}
			//			mController.startAutoUpdate();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadMeasurementPlaces();
		if(null!=mEnergyMeasurementPlaces)
		{
			for(SesameMeasurementPlace smp:mEnergyMeasurementPlaces)
			{

				loadEnergyData(	smp, 
						//					EsmartDataRow.getUrlTimeString(mStartYear, mStartMonth, mStartDay), 
						DateHelper.getFirstDateXDaysAgo(30), 
						new Date());
			}

		}

		//		mEzanMeasurementPlaces = EzanDataAccess.getEzanPlaces();
		//
		//		for(EzanMeasurementPlace emp:mEzanMeasurementPlaces)
		//		{
		//			ArrayList<EzanMeasurement> data = EzanDataAccess.getEzanMeasurements(emp.getID(), mNoEzanMeasurements);
		//			mRawEzanData.put(emp, data);
		//		}
		startUpdates();
		long duration = System.currentTimeMillis()-start;


		Log.e(TAG, "init done ("+DateHelper.convertMStoReadableString(duration, true)+")");
	}

	public ArrayList<ControllableDevice> getAllDevices()
	{
		return mController.getAllDevices();
	}

	public ControllableDevice getDeviceByMac(String _mac)
	{
		return mController.getDeviceFromMac(_mac);
	}

	public ArrayList<ControllableDevice> getDevices(String _roomName, boolean _active)
	{
		ArrayList<ControllableDevice> roomDevs = getDevicesForRoom(_roomName);
		ArrayList<ControllableDevice>res = new ArrayList<ControllableDevice>();

		for(ControllableDevice cd:roomDevs)
		{
			if(cd.isAlive()==_active)
			{
				res.add(cd);
			}				
		}
		return res;
	}

	public PMSController getController()
	{
		return mController;
	}

	public ArrayList<ControllableDevice> getDevicesForRoom(String _roomName)
	{
		ArrayList<ControllableDevice> res = new ArrayList<ControllableDevice>();

		HostList hl = null;
		if(_roomName.equals(mCtx.getString(R.string.global_Room1_name)))
		{
			hl = mEdv1Hosts;
		}
		else if(_roomName.equals(mCtx.getString(R.string.global_Room3_name)))
		{
			hl = mEdv3Hosts;
		}
		else if(_roomName.equals(mCtx.getString(R.string.global_Room6_name)))
		{
			hl = mEdv6Hosts;
		}
		else
		{
			Log.e(TAG, "room not found, returning empty list");
			return res;
		}

		for(ControllableDevice cd:mController.getAllDevices())
		{
			if(hl.getMacList().contains(cd.getMac()))
			{
				res.add(cd);				
			}
		}
		return res;
	}

	private void loadMeasurementPlaces()
	{
		mEnergyMeasurementPlaces = mEnergyDataSource.getEnergyMeasurementPlaces();
		mTemperatureMeasurementPlaces = mTemperatureDataSource.getTemperatureMeasurementPlaces();
		mHumidityMeasurementPlaces = mHumidityDataSource.getHumidityMeasurementPlaces();
		mLightMeasurementPlaces = mLightDataSource.getLightMeasurementPlaces();
		switch(mDataSource)
		{
		case mock:
			EDV1_PLACE = mEnergyMeasurementPlaces.get(0);
			EDV3_PLACE = mEnergyMeasurementPlaces.get(1);
			EDV6_PLACE = mEnergyMeasurementPlaces.get(2);
			break;
		case webservices:
			break;
		case semantic_repo:
//			Collections.sort(mEnergyMeasurementPlaces, new SesameMeasurementPlaceComparator());
//			Log.e(TAG, Arrays.toString((SesameMeasurementPlace[]) mEnergyMeasurementPlaces.toArray(new SesameMeasurementPlace[mEnergyMeasurementPlaces.size()])));
//			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "PlaceList:");
//			int cnt = 0;
//			for(SesameMeasurementPlace smp:mEnergyMeasurementPlaces)
//			{
//				SesameLogger.log(EntryType.APPLICATION_INFO, TAG, ""+cnt+":"+smp);
//				cnt++;
//			}
			if(null == mEnergyMeasurementPlaces || mEnergyMeasurementPlaces.size()<4)
			{
				exitAppAndNotify();
			}
			EDV1_PLACE = mEnergyMeasurementPlaces.get(0);
			EDV3_PLACE = mEnergyMeasurementPlaces.get(3);
			EDV6_PLACE = mEnergyMeasurementPlaces.get(2);
//			EDV1_PLACE = mEnergyMeasurementPlaces.get(4);
//			EDV3_PLACE = mEnergyMeasurementPlaces.get(3);
//			EDV6_PLACE = mEnergyMeasurementPlaces.get(5);
//			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "PlaceList:");
//			int cnt = 0;
//			for(SesameMeasurementPlace smp:mEnergyMeasurementPlaces)
//			{
//				SesameLogger.log(EntryType.APPLICATION_INFO, TAG, ""+cnt+":"+smp);
//				cnt++;
//			}
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "edv1:"+EDV1_PLACE.toString());
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "edv3:"+EDV3_PLACE.toString());
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "edv6:"+EDV6_PLACE.toString());

			//			for(SesameMeasurementPlace smp:mEnergyMeasurementPlaces)
			//			{
			//				Log.i(TAG, smp.toString());
			//			}
			break;
		}
		resetAllUpdateTables();
	}

	private void exitAppAndNotify() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			
			@Override
			public void run() 
			{
				Toast.makeText(mCtx, "Daten konnten nicht geladen werden, Anwendung wird beendet", Toast.LENGTH_LONG).show();
			}
		});
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
		
	}

	private void resetAllUpdateTables()
	{
		resetUpdateMap(mEnergyDataUpdateMap, mEnergyMeasurementPlaces);
		resetUpdateMap(mHumidityDataUpdateMap, mHumidityMeasurementPlaces);
		resetUpdateMap(mLightDataUpdateMap, mLightMeasurementPlaces);
		resetUpdateMap(mTemperatureDataUpdateMap, mTemperatureMeasurementPlaces);
	}

	private void resetUpdateMap(HashMap<SesameMeasurementPlace, Boolean> _updateTable, ArrayList<SesameMeasurementPlace> _measurementPlaces)
	{
		if(null==_measurementPlaces)
		{
			return;
		}
		for(SesameMeasurementPlace smp:_measurementPlaces)
		{
			_updateTable.put(smp, false);
		}
	}

	private boolean loadEnergyData(SesameMeasurementPlace _smp, Date _from, Date _to)
	{
		SesameDataContainer data = mEnergyDataSource.getEnergyData(_smp, _from, _to);
		if(null!=data)
		{
			addEnergyData(_smp, data);
			return true;
		}
		else
		{
			Log.e(TAG, "data for \""+_smp.getName()+"\" could not be loaded");
			return false;
		}
	}

	private void addEnergyData(SesameMeasurementPlace _smp, SesameDataContainer _data)
	{
		//		Log.i(TAG, "adding energy data for smp:"+_smp);
		SesameDataContainer storedData = mEnergyData.get(_smp);
		if(null==storedData)
		{
			storedData = _data;
			Log.i(TAG, "stored data were null, adding new ("+_data.getMeasurements().size()+")");
			//			mEnergyDataUpdateTable.put(_smp, true);
		}
		else
		{
			//			Log.i(TAG, "number of measurements in container:"+_data.getMeasurements().size());
			for(int i = 0;i<_data.getMeasurements().size();i++)
			{
				boolean alreadyStored = false;
				SesameMeasurement sm = _data.getMeasurements().get(i);
				Date timeStamp = sm.getTimeStamp();
				for(SesameMeasurement storedSm:storedData.getMeasurements())
				{
					if(timeStamp.equals(storedSm.getTimeStamp()))
					{
						alreadyStored = true;
						break;
					}
				}
				if(!alreadyStored)
				{
					storedData.addData(sm);
					//					Log.i(TAG, "added measurement:"+sm.toString());
				}
				//				else
				//				{
				//					Log.i(TAG, "did not add measurement:"+sm.toString());
				//				}
			}
			//			for(int i = 0;i<_data.getTimeStamps().size();i++)
			//			{
			//				Date d = _data.getTimeStamps().get(i);
			//				double val = _data.getValues().get(i);
			//				storedData.addData(d, val);
			//				mEnergyDataUpdateTable.put(_smp, true);
			//			}

			//			for(EsmartDataRow row2Add:_data)
			//			{
			//				if(!storedDates.contains(row2Add.getDate()))
			//				{
			//					storedRows.add(row2Add);
			//					mEsmartUpdateTable.put(_emp, true);
			//				}
			//			}
		}
		mEnergyDataUpdateMap.put(_smp, true);
		Collections.sort(storedData.getMeasurements(), new SesameMeasurementComparator());
		mEnergyData.put(_smp, storedData);
		updateEnergyData();
	}

	//	private ArrayList<Date> getDatesFromEsmartDataRows(ArrayList<EsmartDataRow> _rows)
	//	{
	//		ArrayList<Date> res = new ArrayList<Date>();
	//		for(EsmartDataRow edr:_rows)
	//		{
	//			res.add(edr.getDate());
	//		}
	//		return res;
	//	}

	public static void createInstance(Context _ctx)
	{
		mInstance = new SesameDataCache(mDataSource, _ctx);
	}
	
	public static SesameDataCache getInstance()
	{
		return mInstance;
	}

	//	public static SesameDataCache getInstance()
	//	{
	//		return mInstance;
	//	}

	public void updateEnergyData() 
	{
		Iterator<SesameMeasurementPlace> listenerIt = mEnergyDataListener.keySet().iterator();
		while(listenerIt.hasNext())
		{
			//			EsmartMeasurementPlace emp = getEsmartMeasurementPlaceById(listenerIt.nextElement());
			SesameMeasurementPlace smp = listenerIt.next();


			if(mEnergyDataUpdateMap.get(smp)==true)
			{
				//				Log.e(TAG, "updating esmart data for "+emp.getName());
				ArrayList<ISesameDataListener> listeners = mEnergyDataListener.get(smp);
				SesameDataContainer results = mEnergyData.get(smp);

				ArrayList<SesameDataContainer> data = new ArrayList<SesameDataContainer>();
				data.add(results);
				//				data.add(new SesameDataContainer(""+emp.getId(), results));
				for(ISesameDataListener sdl:listeners)
				{
					Log.e(TAG, "++++++++notifying listener");
					sdl.notifyAboutData(data);
				}
			}
		}

		resetUpdateMap(mEnergyDataUpdateMap, mEnergyMeasurementPlaces);
	}

	public SesameDataContainer getAllEnergyReadings(SesameMeasurementPlace _smp)
	{
		return mEnergyData.get(_smp);
	}

	public SesameDataContainer getEnergyReadings(SesameMeasurementPlace _smp, Date _from, Date _to, boolean _pad)
	{
		if(null==_smp)
		{
			return null;
		}
		return new SesameDataContainer(_smp, SesameDataContainer.filterByDate(mEnergyData.get(_smp).getMeasurements(),_from, _to, _pad));
	}

	public synchronized SesameMeasurement getLastEnergyReading(SesameMeasurementPlace _smp) throws Exception
	{
		if(null==_smp)
		{
			Log.d(TAG, "passed mp in getLastReading was null");
		}
		else
		{
			//			Log.d(TAG, "passed mp:"+_smp.toString());
		}
		if(null==mEnergyData)
		{
			Log.d(TAG, "energyData in getLastReading was null");
		}
		else
		{
			//			Log.d(TAG, "energy data ok");
		}
		SesameDataContainer energyData = null;
		try
		{
			energyData = mEnergyData.get(_smp);
		}
		catch(NullPointerException _npe)
		{
			Log.d(TAG, "no data found for "+_smp.toString());
			return null;
		}

		//		Date last = new Date(0);
		//		int latestDateIdx = -1;
		//		for(int i = 0;i<energyData.getTimeStamps().size();i++)
		//		{
		//			Date current = energyData.getTimeStamps().get(i);
		//			if(current.after(last))
		//			{
		//				last = current;
		//				latestDateIdx =i;
		//			}
		//		}
		//
		//		if(latestDateIdx==-1)
		//		{
		//			return Double.NaN;
		//		}
		final ArrayList<SesameMeasurement> measurements = energyData.getMeasurements();
		Collections.sort(measurements, new SesameMeasurementComparator());
		//		energyData.getMeasurements().get(0).
		//		return energyData.getValues().get(latestDateIdx);
		int idx = measurements.size()-1;
		if(idx<0)
		{
			throw new Exception("no last reading available");
		}
		SesameMeasurement sm = energyData.getMeasurements().get(idx);
		//		Log.i(TAG, "last measurement:"+sm.toString());
		return sm;
	}

	public synchronized double getOverallEnergyConsumtion(SesameMeasurementPlace _smp)
	{
		synchronized (this) 
		{
			final SesameDataContainer energyData = mEnergyData.get(_smp);
			ArrayList<SesameMeasurement> measurementsSinceTrialStart = SesameDataContainer.filterByDate(energyData.getMeasurements(), Constants.getTrialStartDate(), new Date(), false);
			double overall = 0;

//			for(int i = 0;i<energyData.getMeasurements().size();i++)
//			{
//				overall += energyData.getMeasurements().get(i).getVal();
//			}
			
			for(SesameMeasurement sm:measurementsSinceTrialStart)
			{
				overall += sm.getVal();
			}
			return overall/4000; //sum of all measurements (every 15 min) division by 4==> 15 min->1 h, division by 1000==>Wh->kWh			
		}
	}

	//	private Date[] calculateMinMaxDate(Hashtable<Date, SesameDataContainer> _source)
	//	{
	//		Date earliest = new Date(Long.MAX_VALUE);
	//		Date latest = new Date(0);
	//		Iterator<Date> keys = _source.keySet().iterator();
	//		while(keys.hasNext())
	//		{
	//			Date current  = keys.next();
	//			if(current.before(earliest))
	//			{
	//				earliest = current;
	//			}
	//
	//			if(current.after(latest))
	//			{
	//				latest = current;
	//			}
	//		}
	//		return new Date[]{earliest, latest};
	//	}

	public void updateNotificationListener(ArrayList<SesameNotification> _notifications) 
	{
		for(INotificationListener listener:mNotificationListeners)
		{
			listener.notifyAboutNotification(_notifications);
		}
	}

	private class EnergyUpdateTask extends TimerTask
	{		
		private int mDays2Load = 1;

		@Override
		public void run() 
		{
			//			Log.i(TAG, "updating energy data");
			refreshEnergyData(mDays2Load);
		}

	}
	private boolean refreshEnergyData(int _days2Load) {
		if(null==mEnergyMeasurementPlaces)
		{
			return false;
		}
		boolean res = true;
		for(SesameMeasurementPlace smp:mEnergyMeasurementPlaces)
		{
			if(!loadEnergyData(	smp, 
					DateHelper.getFirstDateXDaysAgo(_days2Load), 
					new Date()))
			{
				res = false;
			}
		}
		return res;
	}

	private class NotificationUpdateTask extends TimerTask
	{
		@Override
		public void run() 
		{
			updateNotificationListener(mNotificationSource.getNotifications());		
		}
	}

	@Override
	public void registerEnergyDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp) 
	{
		Log.d(TAG, "registered listener...");
		registerListenerInHashMap(mEnergyDataListener, _listener, _smp);
		//		ArrayList<SesameDataContainer> data = new ArrayList<SesameDataContainer>();
		//		data.add(mEnergyData.get(_smp));
		//		_listener.notifyAboutData(data);
	}

	@Override
	public void unregisterEnergyDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp) 
	{
		unregisterListenerInHashMap(mEnergyDataListener, _listener, _smp);
	}

	@Override
	public void registerTemperatureDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp) 
	{
		registerListenerInHashMap(mTemperatureDataListener, _listener, _smp);
	}

	@Override
	public void unregisterTemperatureDataListener(ISesameDataListener _listener,SesameMeasurementPlace _smp) 
	{
		unregisterListenerInHashMap(mTemperatureDataListener, _listener, _smp);
	}

	@Override
	public void registerLightDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp) 
	{
		registerListenerInHashMap(mLightDataListener, _listener, _smp);
	}

	@Override
	public void unregisterLightDataListener(ISesameDataListener _listener,SesameMeasurementPlace _smp) 
	{
		unregisterListenerInHashMap(mLightDataListener, _listener, _smp);

	}

	@Override
	public void registerHumidityDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp)
	{
		registerListenerInHashMap(mHumidityDataListener, _listener, _smp);
	}

	@Override
	public void unregisterHumidityDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp) 
	{
		unregisterListenerInHashMap(mHumidityDataListener, _listener, _smp);
	}

	@Override
	public void registerNotificationListener(INotificationListener _listener) 
	{
		mNotificationListeners.add(_listener);
	}

	@Override
	public void unregisterNotificationListener(INotificationListener _listener) 
	{
		mNotificationListeners.remove(_listener);
	}

	private void registerListenerInHashMap(HashMap<SesameMeasurementPlace, ArrayList<ISesameDataListener>> _table, ISesameDataListener _listener, SesameMeasurementPlace _smp)
	{
		ArrayList<ISesameDataListener> listeners = _table.get(_smp);
		if(null==listeners)
		{
			listeners = new ArrayList<ISesameDataListener>();
		}
		if(!listeners.contains(_listener))
		{
			listeners.add(_listener);
		}
		_table.put(_smp, listeners);
	}

	private void unregisterListenerInHashMap(HashMap<SesameMeasurementPlace, ArrayList<ISesameDataListener>> _table, ISesameDataListener _listener, SesameMeasurementPlace _smp)
	{
		ArrayList<ISesameDataListener> listeners = _table.get(_smp);
		if(listeners.contains(_listener))
		{
			listeners.remove(_listener);
		}
		_table.put(_smp, listeners);
	}

	@Override
	public ArrayList<SesameMeasurementPlace> getEnergyMeasurementPlaces() 
	{
		return mEnergyMeasurementPlaces;
	}

	@Override
	public ArrayList<SesameMeasurementPlace> getHumidityMeasurementPlaces() 
	{
		return mHumidityMeasurementPlaces;
	}

	@Override
	public ArrayList<SesameMeasurementPlace> getTemperatureMeasurementPlaces() 
	{
		return mTemperatureMeasurementPlaces;
	}

	@Override
	public ArrayList<SesameMeasurementPlace> getLightMeasurementPlaces() 
	{
		return mLightMeasurementPlaces;
	}
	
	public Date getLastEnergyDataTimeStamp()
	{
		Date res = new Date(0);
		Date lastEdv1MeasurementTime = res;
		Date lastEdv3MeasurementTime = res;
		Date lastEdv6MeasurementTime = res;
		
		
		try 
		{
			lastEdv1MeasurementTime = getLastEnergyReading(EDV1_PLACE).getTimeStamp();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try 
		{
			lastEdv3MeasurementTime = getLastEnergyReading(EDV3_PLACE).getTimeStamp();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try 
		{
			lastEdv6MeasurementTime = getLastEnergyReading(EDV6_PLACE).getTimeStamp();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(lastEdv1MeasurementTime.after(res))
		{
			res = lastEdv1MeasurementTime;
		}
		
		if(lastEdv3MeasurementTime.after(res))
		{
			res = lastEdv3MeasurementTime;
		}
		
		if(lastEdv6MeasurementTime.after(res))
		{
			res = lastEdv6MeasurementTime;
		}
		
		return res;
	}

	/**
	 * checks if the device currently is connected to the internet
	 * @return true if the device is connected, false otherwise
	 */
	private boolean checkConnectivity() 
	{
		Log.e(TAG, "checkConnectivity");
		if(null==mCtx)
		{
			Log.e(TAG, "context was null");
		}
		else
		{
			Log.i(TAG, "context was ok");
		}
		ConnectivityManager cm = (ConnectivityManager) mCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	
	private class UpdateTask extends TimerTask
	{

		@Override
		public void run()
		{
			update();
		}
		
	}

	private void update() {
		SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "updating");
		if(checkConnectivity())
		{

			if(!PMSController.isConnectionInUse())
			{
				boolean energyDataLoaded = refreshEnergyData(mNumDays2LoadEnergyData);
				
				for(ISesameUpdateListener listener:mUpdateListeners)
				{
					listener.notifyEnergyUpdate(energyDataLoaded);
				}
				try
				{
					boolean devicesUpdated = SesameDataCache.mDeviceStateUpdater.updateAllDevices();
					for(ISesameUpdateListener listener:mUpdateListeners)
					{
						listener.notifyPmsUpdate(devicesUpdated);
					}		
				}
				catch(Exception e)
				{
//					e.printStackTrace();
					SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "PMS update failed");
					//ignore null pointer due to uninitialized updater
				}
				
			}
		}
		else
		{
			
			for(ISesameUpdateListener listener:mUpdateListeners)
			{
				listener.notifyConnectivityLoss();
			}
		}
	}
}
