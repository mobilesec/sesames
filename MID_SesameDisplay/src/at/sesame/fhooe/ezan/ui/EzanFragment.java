package at.sesame.fhooe.ezan.ui;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import at.sesame.fhooe.ezan.EzanDataAccess;
import at.sesame.fhooe.ezan.IEzanMeasuementPlaceCheckedListener;
import at.sesame.fhooe.ezan.IEzanMeasurementPlaceSelectionListener;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;
import at.sesame.fhooe.ezan.model.EzanMeasurement.MeasurementType;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace.TimePeriod;
import at.sesame.fhooe.lib.ui.ProgressFragmentDialog;
import at.sesame.fhooe.midsd.R;


public class EzanFragment 
extends Fragment
implements IEzanMeasurementPlaceSelectionListener, IEzanMeasuementPlaceCheckedListener, OnCheckedChangeListener
{
	private static final String TAG = "EzanFragment";
	
	private static final int INITIAL_DATA_LOAD_AMOUNT = 2;
	private static final TimePeriod INITIAL_DATA_LOAD_PERIOD = TimePeriod.days;
	
	private static final int UPDATE_DATA_LOAD_AMOUNT = 2;
	private static final TimePeriod UPDATE_DATA_LOAD_PERIOD = TimePeriod.days; 
	
	private View mView;
	private ArrayList<EzanMeasurementPlace> mPlaces;
	private EzanPlaceListFragment mPlaceList;
	
	private ArrayList<EzanPlaceDetailFragment> mDetailFrags = new ArrayList<EzanPlaceDetailFragment>();
	private EzanMeasurementPlace mCurPlace;
	
//	private EzanDataViewFragment mDataView;
	private MeasurementUpdateThread mUpdateThread;

	private RadioGroup mRadioGroup;
	
	private DialogFragment mDataProcessingDialog;// = new EzanDataProcessingDialogFragment();
	private DialogFragment mDataLoadingDialog;
	
	
	//	public EzanFragment()
	//	{
	//		
	//	}


	@Override
	public void onAttach(final Activity activity) 
	{
		super.onAttach(activity);
//		mDataProcessingDialog = new EzanDataProcessingDialogFragment();
		showDataLoadingDialog();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				loadData(activity);
				dismissDataLoadingDialog();
			}
		}).start();
		
//		startUpdates();
	}
	
	private void loadData(final Activity _a)
	{
//		showDataProcessingDialog();
		
//		Looper.prepare();
		// TODO Auto-generated method stub
		mPlaces = EzanDataAccess.getEzanPlaces();
//		Looper.prepare();Looper.getMainLooper().loop();
		mPlaceList = new EzanPlaceListFragment(_a, mPlaces, this, this);
		
//		mDataView = new EzanDataViewFragment();
		for(EzanMeasurementPlace emp:mPlaces)
		{
			EzanPlaceDetailFragment epdf = new EzanPlaceDetailFragment(emp);

			mDetailFrags.add(epdf);
			emp.updateMeasurements(INITIAL_DATA_LOAD_AMOUNT,INITIAL_DATA_LOAD_PERIOD);
//			if(null!=measurements)
//			{
//				Log.e(TAG, Arrays.toString(measurements.toArray()));
//			}
//			else
//			{
//				Log.e(TAG, "update of "+emp.getTitle()+" failed");
//			}
		}
//		Looper.loop();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.ezanPlaceListContainer, mPlaceList);
//		ft.add(R.id.ezanChartContainer, mDataView);
		ft.commit();
//		dismissDataLoadingDialog();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null==mView)
		{
			mView= inflater.inflate(R.layout.ezan_fragment_layout, container, false);
			mRadioGroup = (RadioGroup)mView.findViewById(R.id.radioGroup1);
			mRadioGroup.setOnCheckedChangeListener(this);
		}
		//		if(null==mPlaceList)
		//		{
		//			mPlaceList = (EzanPlaceListFragment) getFragmentManager().findFragmentById(R.id.fragment1);
		//			mPlaceList.setSelectionListener(this);
		//		}
		return mView;
	}
	@Override
	public void notifyEzanMeasurementPlaceSelected(EzanMeasurementPlace _emp) 
	{
		Log.e(TAG, "notified about place selection");


		if(!_emp.equals(mCurPlace))
		{
			mCurPlace = _emp;
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.ezanPlaceDetailContainer, new EzanPlaceDetailFragment(mCurPlace));
			ft.commit();
		}

		//		getFragmentManager().
		//		mCurDetailFrag = getDetailFragmentByTitle(_emp.getTitle());

	}

//	private EzanPlaceDetailFragment getDetailFragmentByTitle(String _title)
//	{
//		for(EzanPlaceDetailFragment epdf:mDetailFrags)
//		{
//			if(epdf.getTitle().equals(_title))
//			{
//				return epdf;
//			}
//		}
//		return null;
//	}




	@Override
	public void notifyEzanMeasurementPlaceChecked(EzanMeasurementPlace _emp) 
	{
		Log.e(TAG, "notified about place checked");
		//		ArrayList<EzanMeasurementPlace> checkedPlaces = mPlaceList.getCheckedPlaces();
		//		for(EzanMeasurementPlace emp:checkedPlaces)
		//		{
		//			Log.e(TAG, emp.toString());
		//		}
		//
		//		if(checkedPlaces.size()==0)
		//		{
		//			Log.e(TAG, "no places checked");
		//		}
		//		mDataView.setShownPlaces(checkedPlaces);
		updateChart();
	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		// TODO Auto-generated method stub
		//		mDataView.displayValues();
		
		
		updateChart();
		
	}
	
	private void showDataProcessingDialog()
	{	
		mDataProcessingDialog = ProgressFragmentDialog.newInstance("Bitte warten...","Daten werden verarbeitet.");
		mDataProcessingDialog.show(getFragmentManager(), null);
	}
	private void dismissDataProcessingDialog()
	{
		if(null!=mDataLoadingDialog)
		{
			mDataProcessingDialog.dismiss();
		}
	}
	
	private void showDataLoadingDialog()
	{
		mDataLoadingDialog = ProgressFragmentDialog.newInstance("Bitte warten...", "Daten werden geladen.");
		mDataLoadingDialog.show(getFragmentManager(), null);
	}
	
	private void dismissDataLoadingDialog()
	{
		if(null!=mDataLoadingDialog)
		{
			mDataLoadingDialog.dismiss();
		}
	}

	private void updateChart()
	{

		showDataProcessingDialog();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateData();
				dismissDataProcessingDialog();
			}
		}).start();
		
		
	}
	
//	private GregorianCalendar getDataStart()
//	{
//		Date earliest = new Date();
//		for(EzanMeasurementPlace emp:mPlaceList.getCheckedPlaces())
//		{
//			if(emp.getStartDate().before(earliest))
//			{
//				earliest = emp.getStartDate();
//			}
//		}
//		return new GregorianCalendar(earliest.getYear()+1900, earliest.getMonth(), earliest.getDay());
//	}

	private MeasurementType getSelectedMeasurementType()
	{
		switch(mRadioGroup.getCheckedRadioButtonId())
		{
		case R.id.humidityRadioButt:
			return MeasurementType.humidity;
		case R.id.lightRadioButt:
			return MeasurementType.light;
		case R.id.temperatureRadioButt:
			return MeasurementType.temperature;
		case R.id.voltageRadioButt:
			return MeasurementType.voltage;
		default:
			return MeasurementType.nA;

		}
	}

//	private ArrayList<Double[]> getSelectedData()
//	{
//		ArrayList<EzanMeasurementPlace> selectedPlaces = mPlaceList.getCheckedPlaces();
//		ArrayList<Double[]> res = new ArrayList<Double[]>();
//
//		for(EzanMeasurementPlace emp:selectedPlaces)
//		{
//			//TODO add timestamp
//			Object[] obVals =  emp.getDisplayedValues(getSelectedMeasurementType()).values().toArray();
//			res.add(castObjectArrayToDoubleArray(obVals));
//		}
//		return res;
//	}

//	private Double[] castObjectArrayToDoubleArray(Object[] _ob)
//	{
//		Double[] res = new Double[_ob.length];
//		for(int i = 0;i<_ob.length;i++)
//		{
//			res[i]=(Double)_ob[i];
//		}
//		return res;
//	}

//	private ArrayList<String> getSelectedMeasurementPlaceTitles()
//	{
//		ArrayList<String> res = new ArrayList<String>();
//		for(EzanMeasurementPlace emp:mPlaceList.getCheckedPlaces())
//		{
//			res.add(emp.getTitle());
//		}
//		return res;
//	}
	
	private void startUpdates()
	{
		mUpdateThread = new MeasurementUpdateThread();
		mUpdateThread.start();
	}
	
	private void stopUpdates()
	{
		if(null!=mUpdateThread)
		{
			mUpdateThread.stopUpdates();
		}
		
	}
	
	private void updateData()
	{
		Log.e(TAG, "updating chart...");
//		mDataView.setChartData(getDataStart(), getSelectedMeasurementPlaceTitles(), getSelectedData());
		Log.e(TAG, "++++++++++++++++updating:"+getSelectedMeasurementType().name());
		EzanDataViewFragment edvf = new EzanDataViewFragment();
//		mDataView.setData(getSelectedMeasurementType(), mPlaceList.getCheckedPlaces(), null, null);
		
		GregorianCalendar startCal = new GregorianCalendar(2011, 11, 20, 0, 0, 0);
		GregorianCalendar endCal = new GregorianCalendar(2012,2,10,0,0,0);
		Log.e(TAG, "start:"+startCal.getTime().toGMTString());
		Log.e(TAG, "end:"+endCal.getTime().toGMTString());
//		edvf.setData(getSelectedMeasurementType(), mPlaceList.getCheckedPlaces(), startCal.getTime(), endCal.getTime());
		edvf.setData(getSelectedMeasurementType(), mPlaceList.getCheckedPlaces(), null, null);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
////		ft.add(R.id.ezanPlaceListContainer, mPlaceList);
//		try
//		{
//			ft.remove(mDataView);
//		}
//		catch(IllegalStateException ise)
//		{
//			
//		}
		ft.replace(R.id.ezanChartContainer, edvf);
		ft.commit();
	}
	
	

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		stopUpdates();
	}
	
//	private class DataViewUpdateTask extends AsyncTask<Void, Void, Void>
//	{
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			Log.e(TAG, "updating chart...");
////			mDataView.setChartData(getDataStart(), getSelectedMeasurementPlaceTitles(), getSelectedData());
//			Log.e(TAG, "++++++++++++++++updating:"+getSelectedMeasurementType().name());
//			EzanDataViewFragment edvf = new EzanDataViewFragment();
////			mDataView.setData(getSelectedMeasurementType(), mPlaceList.getCheckedPlaces(), null, null);
//			
//			GregorianCalendar startCal = new GregorianCalendar(2011, 11, 20, 0, 0, 0);
//			GregorianCalendar endCal = new GregorianCalendar(2012,2,10,0,0,0);
//			Log.e(TAG, "start:"+startCal.getTime().toGMTString());
//			Log.e(TAG, "end:"+endCal.getTime().toGMTString());
////			edvf.setData(getSelectedMeasurementType(), mPlaceList.getCheckedPlaces(), startCal.getTime(), endCal.getTime());
//			edvf.setData(getSelectedMeasurementType(), mPlaceList.getCheckedPlaces(), null, null);
//			FragmentTransaction ft = getFragmentManager().beginTransaction();
//////			ft.add(R.id.ezanPlaceListContainer, mPlaceList);
////			try
////			{
////				ft.remove(mDataView);
////			}
////			catch(IllegalStateException ise)
////			{
////				
////			}
//			ft.replace(R.id.ezanChartContainer, edvf);
//			ft.commit();
//			return null;
//		}
//		
//	}



	private class MeasurementUpdateThread extends Thread
	{
		private static final String TAG = "MeasurementUpdateThread";
		private boolean mRunning = true;
		private long mSleepPeriod = 1000;
		public void run()
		{
			while(mRunning)
			{
				for(EzanMeasurementPlace emp:mPlaces)
				{
					if(!mRunning)
					{
						break;
					}
					Log.e(TAG, "updating "+emp.getTitle());
					emp.updateMeasurements(UPDATE_DATA_LOAD_AMOUNT, UPDATE_DATA_LOAD_PERIOD);
				}
				if(!mRunning)
				{
					break;
				}
				try {
					Thread.sleep(mSleepPeriod);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.e(TAG, "update stopped");
		}
		
		public synchronized void stopUpdates()
		{
			mRunning = false;
			Log.e(TAG, "stopping update");
		}
	}
}
