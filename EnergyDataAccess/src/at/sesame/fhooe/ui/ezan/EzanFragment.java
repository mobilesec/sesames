package at.sesame.fhooe.ui.ezan;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import at.sesame.fhooe.R;
import at.sesame.fhooe.ezan.model.EzanMeasurement;
import at.sesame.fhooe.ezan.model.EzanMeasurement.MeasurementType;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;
import at.sesame.fhooe.ezan.service.EzanDataAccess;

public class EzanFragment 
extends Fragment
implements IEzanMeasurementPlaceSelectionListener, IEzanMeasuementPlaceCheckedListener, OnCheckedChangeListener
{
	private static final String TAG = "EzanFragment";
	private View mView;
	private ArrayList<EzanMeasurementPlace> mPlaces;
	private EzanPlaceListFragment mPlaceList;
	private ArrayList<EzanPlaceDetailFragment> mDetailFrags = new ArrayList<EzanPlaceDetailFragment>();
	private EzanMeasurementPlace mCurPlace;
	private EzanDataViewFragment mDataView;
	private MeasurementUpdateThread mUpdateThread;

	private RadioGroup mRadioGroup;
	//	public EzanFragment()
	//	{
	//		
	//	}


	@Override
	public void onAttach(Activity activity) 
	{
		super.onAttach(activity);
		mPlaces = EzanDataAccess.getEzanPlaces(); 
		mPlaceList = new EzanPlaceListFragment(activity, mPlaces, this, this);
		mDataView = new EzanDataViewFragment();
		for(EzanMeasurementPlace emp:mPlaces)
		{
			EzanPlaceDetailFragment epdf = new EzanPlaceDetailFragment(emp);

			mDetailFrags.add(epdf);
			emp.updateMeasurements();
//			if(null!=measurements)
//			{
//				Log.e(TAG, Arrays.toString(measurements.toArray()));
//			}
//			else
//			{
//				Log.e(TAG, "update of "+emp.getTitle()+" failed");
//			}
		}

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.ezanPlaceListContainer, mPlaceList);
		ft.add(R.id.ezanChartContainer, mDataView);
		ft.commit();
		startUpdates();
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

	private EzanPlaceDetailFragment getDetailFragmentByTitle(String _title)
	{
		for(EzanPlaceDetailFragment epdf:mDetailFrags)
		{
			if(epdf.getTitle().equals(_title))
			{
				return epdf;
			}
		}
		return null;
	}




	@Override
	public void notifyEzanMeasurementPlaceChecked(EzanMeasurementPlace _emp) 
	{
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

	private void updateChart()
	{
		mDataView.setChartData(getDataStart(), getSelectedMeasurementPlaceTitles(), getSelectedData());
	}
	
	private GregorianCalendar getDataStart()
	{
		Date earliest = new Date();
		for(EzanMeasurementPlace emp:mPlaceList.getCheckedPlaces())
		{
			if(emp.getStartDate().before(earliest))
			{
				earliest = emp.getStartDate();
			}
		}
		return new GregorianCalendar(earliest.getYear()+1900, earliest.getMonth(), earliest.getDay());
	}

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

	private ArrayList<Double[]> getSelectedData()
	{
		ArrayList<EzanMeasurementPlace> selectedPlaces = mPlaceList.getCheckedPlaces();
		ArrayList<Double[]> res = new ArrayList<Double[]>();

		for(EzanMeasurementPlace emp:selectedPlaces)
		{
			//TODO add timestamp
			Object[] obVals =  emp.getDisplayedValues(getSelectedMeasurementType()).values().toArray();
			res.add(castObjectArrayToDoubleArray(obVals));
		}
		return res;
	}

	private Double[] castObjectArrayToDoubleArray(Object[] _ob)
	{
		Double[] res = new Double[_ob.length];
		for(int i = 0;i<_ob.length;i++)
		{
			res[i]=(Double)_ob[i];
		}
		return res;
	}

	private ArrayList<String> getSelectedMeasurementPlaceTitles()
	{
		ArrayList<String> res = new ArrayList<String>();
		for(EzanMeasurementPlace emp:mPlaceList.getCheckedPlaces())
		{
			res.add(emp.getTitle());
		}
		return res;
	}
	
	private void startUpdates()
	{
		mUpdateThread = new MeasurementUpdateThread();
		mUpdateThread.start();
	}
	
	private void stopUpdates()
	{
		mUpdateThread.stopUpdates();
	}
	
	

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		stopUpdates();
	}



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
					emp.updateMeasurements();
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
