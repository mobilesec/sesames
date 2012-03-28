package at.sesame.fhooe.lib2.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import at.sesame.fhooe.lib2.Constants;
import at.sesame.fhooe.lib2.data.provider.EsmartDataProvider;
import at.sesame.fhooe.lib2.data.provider.EzanDataProvider;
import at.sesame.fhooe.lib2.data.semantic.SemanticSesameDataSource;
import at.sesame.fhooe.lib2.data.simulation.DataSimulator;
import at.sesame.fhooe.lib2.data.simulation.NotificationSimulator;
import at.sesame.fhooe.lib2.util.DateHelper;

public class SesameDataCache
implements ISesameDataProvider
{
	public enum DataSource
	{
		mock,
		semantic_repo,
		webservices
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

	private static Hashtable<SesameMeasurementPlace, SesameDataContainer> mEnergyData = new Hashtable<SesameMeasurementPlace, SesameDataContainer>();
	private static HashMap<SesameMeasurementPlace, SesameDataContainer> mHumidityData = new HashMap<SesameMeasurementPlace, SesameDataContainer>();
	private static HashMap<SesameMeasurementPlace, SesameDataContainer> mTemperatureData = new HashMap<SesameMeasurementPlace, SesameDataContainer>();
	private static HashMap<SesameMeasurementPlace, SesameDataContainer> mLightData = new HashMap<SesameMeasurementPlace, SesameDataContainer>();

	private static Date mFirstEnergyDate;
	private static Date mLastEnergyDate;

	//	private static ArrayList<EsmartMeasurementPlace> mEsmartMeasurementPlaces = new ArrayList<EsmartMeasurementPlace>();

	//	private static Hashtable<Integer, ArrayList<EsmartDataRow>> mRawEsmartData = new Hashtable<Integer, ArrayList<EsmartDataRow>>();
	//	private static Hashtable<EsmartMeasurementPlace, Boolean> mEsmartUpdateTable = new Hashtable<EsmartMeasurementPlace, Boolean>();

	//	private static ArrayList<EzanMeasurementPlace> mEzanMeasurementPlaces = new ArrayList<EzanMeasurementPlace>();
	//	private static Hashtable<EzanMeasurementPlace, ArrayList<EzanMeasurement>> mRawEzanData = new Hashtable<EzanMeasurementPlace, ArrayList<EzanMeasurement>>();


	/**
	 * instance for singleton
	 */
	private static SesameDataCache mInstance;

	private Timer mEnergyUpdateTimer;
	private static final long ENERGY_DATA_UPDATE_INTERVAL = 20000; //every 10 minutes


	private Timer mNotificationUpdateTimer;

	private static final long NOTIFICATION_UPDATE_INTERVAL = 5000;//every 20 seconds

	//	private NotificationSimulator mEventSim;

	private SesameDataCache(DataSource _source)
	{
		switch(_source)
		{
		case mock:
			DataSimulator ds = new DataSimulator();
			mEnergyDataSource = ds;
			mHumidityDataSource = ds;
			mTemperatureDataSource = ds;
			mLightDataSource = ds;

			mNotificationSource = new NotificationSimulator();
			break;
		case semantic_repo:
			SemanticSesameDataSource ssds = new SemanticSesameDataSource();
			mEnergyDataSource = ssds;
			mHumidityDataSource = ssds;
			mLightDataSource = ssds;
			mTemperatureDataSource = ssds;
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

	public void startEnergyDataUpdates()
	{
		stopEnergyDataUpdates();
		mEnergyUpdateTimer = new Timer();
		mEnergyUpdateTimer.schedule(new EnergyUpdateTask(), 0, ENERGY_DATA_UPDATE_INTERVAL);
	}

	private void stopEnergyDataUpdates()
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

	public void startNotificationUpdates()
	{

		mNotificationUpdateTimer = new Timer();
		mNotificationUpdateTimer.schedule(new NotificationUpdateTask(), 0, NOTIFICATION_UPDATE_INTERVAL);
	}

	private void stopNotificationUpdates()
	{
		if(null!=mNotificationUpdateTimer)
		{
			mNotificationUpdateTimer.cancel();
			mNotificationUpdateTimer.purge();
		}
	}

	public void cleanUp()
	{
		stopEnergyDataUpdates();
		stopNotificationUpdates();
		Log.e(TAG, "Sesame datacache cleaned up");
	}

	public void init()
	{
		long start = System.currentTimeMillis();
		//		mEventSim = new NotificationSimulator();
		//		startNotificationUpdates();
		loadMeasurementPlaces();
		if(null!=mEnergyMeasurementPlaces)
		{
			for(SesameMeasurementPlace smp:mEnergyMeasurementPlaces)
			{

				loadEnergyData(	smp, 
						//					EsmartDataRow.getUrlTimeString(mStartYear, mStartMonth, mStartDay), 
						DateHelper.getFirstDateXDaysAgo(50), 
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
		long duration = System.currentTimeMillis()-start;


		Log.e(TAG, "init done ("+DateHelper.convertMStoReadableString(duration, true)+")");
	}

	private void loadMeasurementPlaces()
	{
		mEnergyMeasurementPlaces = mEnergyDataSource.getEnergyMeasurementPlaces();
		mTemperatureMeasurementPlaces = mTemperatureDataSource.getTemperatureMeasurementPlaces();
		mHumidityMeasurementPlaces = mHumidityDataSource.getHumidityMeasurementPlaces();
		mLightMeasurementPlaces = mLightDataSource.getLightMeasurementPlaces();
		resetAllUpdateTables();
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

	private void loadEnergyData(SesameMeasurementPlace _smp, Date _from, Date _to)
	{
		SesameDataContainer data = mEnergyDataSource.getEnergyData(_smp, _from, _to);
		if(null!=data)
		{
			addEnergyData(_smp, data);
		}
		else
		{
			Log.e(TAG, "data for \""+_smp.getName()+"\" could not be loaded");
		}
	}

	private void addEnergyData(SesameMeasurementPlace _smp, SesameDataContainer _data)
	{
		SesameDataContainer storedData = mEnergyData.get(_smp);
		if(null==storedData)
		{
			storedData = _data;
//			mEnergyDataUpdateTable.put(_smp, true);
		}
		else
		{
			for(int i = 0;i<_data.getMeasurements().size();i++)
			{
				SesameMeasurement sm = _data.getMeasurements().get(i);
				if(!storedData.getMeasurements().contains(sm))
				{
					storedData.addData(sm);
					
				}
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

	public static SesameDataCache getInstance(DataSource _source)
	{
		if(null==mInstance)
		{
			mInstance = new SesameDataCache(_source);
		}
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
	
	public SesameDataContainer getEnergyReadings(SesameMeasurementPlace _smp, Date _from, Date _to)
	{
		if(null==_smp)
		{
			return null;
		}
		return new SesameDataContainer(_smp, SesameDataContainer.filterByDate(mEnergyData.get(_smp).getMeasurements(),_from, _to));
	}

	public SesameMeasurement getLastEnergyReading(SesameMeasurementPlace _smp) throws Exception
	{
		if(null==_smp)
		{
			Log.d(TAG, "passed mp in getLastReading was null");
		}
		else
		{
			Log.d(TAG, "passed mp:"+_smp.toString());
		}
		if(null==mEnergyData)
		{
			Log.d(TAG, "energyData in getLastReading was null");
		}
		else
		{
			Log.d(TAG, "energy data ok");
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
//		return energyData.getValues().get(latestDateIdx);
		int idx = energyData.getMeasurements().size()-1;
		if(idx<0)
		{
			throw new Exception("no last reading available");
		}
		return energyData.getMeasurements().get(idx);
	}

	public synchronized double getOverallEnergyConsumtion(SesameMeasurementPlace _smp)
	{
		synchronized (this) 
		{
			final SesameDataContainer energyData = mEnergyData.get(_smp);
			double overall = 0;
			
			for(int i = 0;i<energyData.getMeasurements().size();i++)
			{
				overall += energyData.getMeasurements().get(i).getVal();
			}
			return overall;			
		}
	}

	private Date[] calculateMinMaxDate(Hashtable<Date, SesameDataContainer> _source)
	{
		Date earliest = new Date(Long.MAX_VALUE);
		Date latest = new Date(0);
		Iterator<Date> keys = _source.keySet().iterator();
		while(keys.hasNext())
		{
			Date current  = keys.next();
			if(current.before(earliest))
			{
				earliest = current;
			}

			if(current.after(latest))
			{
				latest = current;
			}
		}
		return new Date[]{earliest, latest};
	}

	public void updateNotificationListener(String _msg) 
	{
		for(INotificationListener listener:mNotificationListeners)
		{
			listener.notifyAboutNotification(_msg);
		}
	}

	private class EnergyUpdateTask extends TimerTask
	{		
		private int mDays2Load = 1;

		@Override
		public void run() 
		{
			if(null==mEnergyMeasurementPlaces)
			{
				return;
			}
			for(SesameMeasurementPlace smp:mEnergyMeasurementPlaces)
			{
				loadEnergyData(	smp, 
						DateHelper.getFirstDateXDaysAgo(mDays2Load), 
						new Date());
			}
		}
	}

	private class NotificationUpdateTask extends TimerTask
	{
		@Override
		public void run() 
		{
			updateNotificationListener(mNotificationSource.getNotification());		
		}
	}

	@Override
	public void registerEnergyDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp) 
	{
		Log.d(TAG, "registered listener...");
		registerListenerInHashMap(mEnergyDataListener, _listener, _smp);
		ArrayList<SesameDataContainer> data = new ArrayList<SesameDataContainer>();
		data.add(mEnergyData.get(_smp));
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

}
