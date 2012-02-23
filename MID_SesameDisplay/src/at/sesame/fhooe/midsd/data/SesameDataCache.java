package at.sesame.fhooe.midsd.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import at.sesame.fhooe.esmart.model.EsmartDataRow;
import at.sesame.fhooe.esmart.model.EsmartMeasurementPlace;
import at.sesame.fhooe.esmart.service.EsmartDataAccess;
import at.sesame.fhooe.ezan.EzanDataAccess;
import at.sesame.fhooe.ezan.model.EzanMeasurement;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;
import at.sesame.fhooe.lib.util.DateHelper;
import at.sesame.fhooe.lib.util.EsmartDateProvider;
import at.sesame.fhooe.midsd.demo.EventSimulator;
import at.sesame.fhooe.midsd.ld.INotificationListener;

public class SesameDataCache
implements ISesameDataProvider
{
	private static final String TAG = "SesameDataCache";

	private Hashtable<Integer, ArrayList<ISesameDataListener>> mEsmartListener = new Hashtable<Integer, ArrayList<ISesameDataListener>>();

	private static ArrayList<EsmartMeasurementPlace> mEsmartMeasurementPlaces = new ArrayList<EsmartMeasurementPlace>();

	private static Hashtable<Integer, ArrayList<EsmartDataRow>> mRawEsmartData = new Hashtable<Integer, ArrayList<EsmartDataRow>>();
	private static Hashtable<EsmartMeasurementPlace, Boolean> mEsmartUpdateTable = new Hashtable<EsmartMeasurementPlace, Boolean>();

	private static ArrayList<EzanMeasurementPlace> mEzanMeasurementPlaces = new ArrayList<EzanMeasurementPlace>();
	private static Hashtable<EzanMeasurementPlace, ArrayList<EzanMeasurement>> mRawEzanData = new Hashtable<EzanMeasurementPlace, ArrayList<EzanMeasurement>>();

	private static ArrayList<INotificationListener> mNotificationListeners = new ArrayList<INotificationListener>();

	private static SesameDataCache mInstance;

	private Timer mEsmartUpdateTimer;
	private static final long ESMART_UPDATE_INTERVAL = 600000; //every 10 minutes

	private static final int mNoEzanMeasurements = 100;

	private Timer mNotificationUpdateTimer;
	private static final long NOTIFICATION_UPDATE_INTERVAL = 5000;//every 20 seconds
	private EventSimulator mEventSim;

	private SesameDataCache()
	{
		init();
	}

	public void startDeepEsmartUpdates()
	{
		stopDeepEsmartUpdates();
		mEsmartUpdateTimer = new Timer();
		mEsmartUpdateTimer.schedule(new EsmartUpdateTask(), 0, ESMART_UPDATE_INTERVAL);
	}

	private void stopDeepEsmartUpdates()
	{
		if(null!=mEsmartUpdateTimer)
		{
			mEsmartUpdateTimer.cancel();
			mEsmartUpdateTimer.purge();
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
		stopDeepEsmartUpdates();
		stopNotificationUpdates();
		Log.e(TAG, "Sesame datacache cleaned up");
	}

	public void init()
	{
		long start = System.currentTimeMillis();
		mEventSim = new EventSimulator();
//		startNotificationUpdates();
		loadEsmartMeasurementPlaces();
		if(null==mEsmartMeasurementPlaces)
		{
			return;
		}
		for(EsmartMeasurementPlace emp:mEsmartMeasurementPlaces)
		{
			
			loadEsmartData(	emp, 
//					EsmartDataRow.getUrlTimeString(mStartYear, mStartMonth, mStartDay), 
					EsmartDateProvider.getUrlTimeStringForXDaysAgo(2), 
					EsmartDateProvider.getUrlTimeStringForXDaysAgo(1));
		}

		mEzanMeasurementPlaces = EzanDataAccess.getEzanPlaces();

		for(EzanMeasurementPlace emp:mEzanMeasurementPlaces)
		{
			ArrayList<EzanMeasurement> data = EzanDataAccess.getEzanMeasurements(emp.getID(), mNoEzanMeasurements);
			mRawEzanData.put(emp, data);
		}
		long duration = System.currentTimeMillis()-start;

		
		Log.e(TAG, "init done ("+DateHelper.convertMStoReadableString(duration, true)+")");
	}

	private void loadEsmartMeasurementPlaces()
	{
		mEsmartMeasurementPlaces = EsmartDataAccess.getMeasurementPlaces();
		resetEsmartUpdateTable();
	}

	private void resetEsmartUpdateTable()
	{
		if(null==mEsmartMeasurementPlaces)
		{
			return;
		}
		for(EsmartMeasurementPlace emp:mEsmartMeasurementPlaces)
		{
			//			Log.e(TAG, "id="+emp.getId());
			//			Log.e(TAG, "name="+emp.getName());
			mEsmartUpdateTable.put(emp, false);
		}
	}

	private void loadEsmartData(EsmartMeasurementPlace _emp, String _from, String _to)
	{
		ArrayList<EsmartDataRow> data = EsmartDataAccess.getLoadProfile(_emp.getId(), 
				_from, 
				_to);
		if(null!=data)
		{
			addEsmartData(_emp, data);
		}
		else
		{
			Log.e(TAG, "data for \""+_emp.getName()+"\" could not be loaded");
		}
	}

	private void addEsmartData(EsmartMeasurementPlace _emp, ArrayList<EsmartDataRow> _data)
	{
		ArrayList<EsmartDataRow> storedRows = mRawEsmartData.get(_emp.getId());
		if(null==storedRows)
		{
			storedRows = _data;
			mEsmartUpdateTable.put(_emp, true);
		}
		else
		{
			ArrayList<Date> storedDates = getDatesFromEsmartDataRows(storedRows);

			for(EsmartDataRow row2Add:_data)
			{
				if(!storedDates.contains(row2Add.getDate()))
				{
					storedRows.add(row2Add);
					mEsmartUpdateTable.put(_emp, true);
				}
			}
		}


		mRawEsmartData.put(_emp.getId(), storedRows);
		updateEsmartData();
	}

	private ArrayList<Date> getDatesFromEsmartDataRows(ArrayList<EsmartDataRow> _rows)
	{
		ArrayList<Date> res = new ArrayList<Date>();
		for(EsmartDataRow edr:_rows)
		{
			res.add(edr.getDate());
		}
		return res;
	}

	public static SesameDataCache getInstance()
	{
		if(null==mInstance)
		{
			mInstance = new SesameDataCache();
		}
		return mInstance;
	}

	@Override
	public void addEsmartDataListener(ISesameDataListener _listener, int _id) 
	{
		ArrayList<ISesameDataListener> listeners = mEsmartListener.get(_id);
		if(null==listeners)
		{
			listeners = new ArrayList<ISesameDataListener>();
		}
		if(!listeners.contains(_listener))
		{
			listeners.add(_listener);
		}
		mEsmartListener.put(_id, listeners);
	}

	@Override
	public void removeEsmartDataListener(ISesameDataListener _listener, int _id) 
	{
		ArrayList<ISesameDataListener> listeners = mEsmartListener.get(_id);
		if(listeners.contains(_listener))
		{
			listeners.remove(_listener);
		}
		mEsmartListener.put(_id, listeners);

	}

	private EsmartMeasurementPlace getEsmartMeasurementPlaceById(int _id)
	{
		for(EsmartMeasurementPlace emp:mEsmartMeasurementPlaces)
		{
			if(emp.getId()==_id)
			{
				return emp;
			}
		}
		return null;
	}

	@Override
	public void updateEsmartData() 
	{
		Enumeration<Integer> listenerIt = mEsmartListener.keys();
		while(listenerIt.hasMoreElements())
		{
			EsmartMeasurementPlace emp = getEsmartMeasurementPlaceById(listenerIt.nextElement());

			if(mEsmartUpdateTable.get(emp)==true)
			{
				//				Log.e(TAG, "updating esmart data for "+emp.getName());
				ArrayList<ISesameDataListener> listeners = mEsmartListener.get(emp.getId());
				ArrayList<EsmartDataRow> results = mRawEsmartData.get(emp.getId());

				ArrayList<SesameDataContainer> data = new ArrayList<SesameDataContainer>();
				data.add(new SesameDataContainer(""+emp.getId(), results));
				for(ISesameDataListener sdl:listeners)
				{
					sdl.notifyAboutData(data);
				}
			}
		}

		resetEsmartUpdateTable();
	}


	

	@Override
	public void addEzanDataListener(ISesameDataListener _listener, int _id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeEzanDataListener(ISesameDataListener _listener, int _id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEzanData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addNotificationListener(INotificationListener _listener) 
	{
		mNotificationListeners.add(_listener);
	}

	@Override
	public void removeNotificationListener(INotificationListener _listener) 
	{
		mNotificationListeners.remove(_listener);
	}

	@Override
	public void updateNotificationListener(String _msg) 
	{
		for(INotificationListener listener:mNotificationListeners)
		{
			listener.notifyAboutNotification(_msg);
		}
	}

	private class EsmartUpdateTask extends TimerTask
	{		
		private int mDays2Load = 1;
		
		@Override
		public void run() 
		{
			if(null==mEsmartMeasurementPlaces)
			{
				return;
			}
			for(EsmartMeasurementPlace emp:mEsmartMeasurementPlaces)
			{
				loadEsmartData(	emp, 
						EsmartDateProvider.getUrlTimeStringForXDaysAgo(mDays2Load+1), 
						EsmartDateProvider.getUrlTimeStringForXDaysAgo(1));
			}
		}
	}

	private class NotificationUpdateTask extends TimerTask
	{
		@Override
		public void run() 
		{
			updateNotificationListener(mEventSim.getNotification());		
		}
	}
}
