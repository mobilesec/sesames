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

public class SesameDataCache
implements ISesameDataProvider
{
	private static final String TAG = "SesameDataCache";

	private Hashtable<Integer, ArrayList<ISesameDataListener>> mEsmartListener = new Hashtable<Integer, ArrayList<ISesameDataListener>>();

	private static ArrayList<EsmartMeasurementPlace> mEsmartMeasurementPlaces = new ArrayList<EsmartMeasurementPlace>();
	//	private static ArrayList<EsmartDataRow> mRawEsmartData;
	private static Hashtable<Integer, ArrayList<EsmartDataRow>> mRawEsmartData = new Hashtable<Integer, ArrayList<EsmartDataRow>>();
	private static Hashtable<EsmartMeasurementPlace, Boolean> mEsmartUpdateTable = new Hashtable<EsmartMeasurementPlace, Boolean>();

	private static ArrayList<EzanMeasurementPlace> mEzanMeasurementPlaces = new ArrayList<EzanMeasurementPlace>();
	private static Hashtable<EzanMeasurementPlace, ArrayList<EzanMeasurement>> mRawEzanData = new Hashtable<EzanMeasurementPlace, ArrayList<EzanMeasurement>>();
	//	private static HashTabley<EzanM>


	private static SesameDataCache mInstance;

	private static final int mStartYear = 2012;
	private static final int mStartMonth = 1;
	private static final int mStartDay = 10;


	private Timer mEsmartUpdateTimer;
	private static final long ESMART_UPDATE_INTERVAL = 600000; //every 10 minutes

	private static final int mNoEzanMeasurements = 100;

	private SesameDataCache()
	{
		init();
	}

	public void startDeepEsmartUpdates()
	{
		mEsmartUpdateTimer = new Timer();
		mEsmartUpdateTimer.schedule(new EsmartUpdateTask(), 0, ESMART_UPDATE_INTERVAL);
	}

	public void stopDeepEsmartUpdates()
	{
		mEsmartUpdateTimer.cancel();
		mEsmartUpdateTimer.purge();
		Log.e(TAG, "deep esmart updates stopped");
	}

	public void init()
	{
		long start = System.currentTimeMillis();
//		Log.e(TAG, "init");
		loadEsmartMeasurementPlaces();

		for(EsmartMeasurementPlace emp:mEsmartMeasurementPlaces)
		{
			loadEsmartData(	emp, 
					EsmartDataRow.getUrlTimeString(mStartYear, mStartMonth, mStartDay), 
					EsmartDataRow.getUrlTimeString());
		}

		mEzanMeasurementPlaces = EzanDataAccess.getEzanPlaces();

		for(EzanMeasurementPlace emp:mEzanMeasurementPlaces)
		{
			ArrayList<EzanMeasurement> data = EzanDataAccess.getEzanMeasurements(emp.getID(), mNoEzanMeasurements);
			mRawEzanData.put(emp, data);
		}
		long duration = System.currentTimeMillis()-start;
		Log.e(TAG, "init done ("+convertMStoReadableString(duration)+")");
	}

	private void loadEsmartMeasurementPlaces()
	{
		mEsmartMeasurementPlaces = EsmartDataAccess.getMeasurementPlaces();
		resetEsmartUpdateTable();
	}

	private void resetEsmartUpdateTable()
	{
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
			//		storedRows.

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
				//				ArrayList<SesameDataContainer> data = new ArrayList<SesameDataContainer>();

				//TODO create real list of sesamedatacontainers
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


	private static String convertMStoReadableString(long _millis)
	{
		//		long seconds = _millis/1000;
		long milli = _millis; 
		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		while(milli-3600000>0)
		{
			hours++;
			milli-=3600000;
		}

		while(milli-60000>0)
		{
			minutes++;
			milli-=60000;
		}

		while(milli-1000>0)
		{
			seconds++;
			milli-=1000;
		}

		StringBuilder res = new StringBuilder();
		if(hours>0)
		{
			res.append(hours);
			res.append("h");
		}

		if(minutes>0)
		{
			res.append(" ");
			res.append(minutes);
			res.append("m");
		}

		if(seconds>0)
		{
			res.append(" ");
			res.append(seconds);
			res.append("s");
		}

		if(milli>0)
		{
			res.append(" ");
			res.append(milli);
			res.append("ms");
		}
		return res.toString();
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


	private class EsmartUpdateTask extends TimerTask
	{
		private int mUpdateYear = 2011;
		private int mUpdateMonth = 11;
		private int mUpdateDay = 25;

		@Override
		public void run() 
		{
//			Log.e(TAG, "EsmartUpdateTask");
			for(EsmartMeasurementPlace emp:mEsmartMeasurementPlaces)
			{
				loadEsmartData(	emp, 
						EsmartDataRow.getUrlTimeString(mUpdateYear, mUpdateMonth, mUpdateDay), 
						EsmartDataRow.getUrlTimeString(2011,11,28));
			}

		}

	}

}
