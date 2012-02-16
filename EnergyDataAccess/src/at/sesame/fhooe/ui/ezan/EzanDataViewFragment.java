package at.sesame.fhooe.ui.ezan;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import at.sesame.fhooe.R;
import at.sesame.fhooe.ezan.model.EzanMeasurement;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;

public class EzanDataViewFragment 
extends Fragment
implements OnCheckedChangeListener
{
	private enum DataType
	{
		humidity,
		temp,
		light,
		voltage
	}
	private static final String TAG = "EzanDataViewFragment";
	private View mView;
//	private CheckBox mHumidityCB;
//	private CheckBox mTempCB;
//	private CheckBox mLightCB;
//	private CheckBox mVoltageCB;
	
	
	private EzanDataChartViewFragment mChartFrag;
	
	private FrameLayout mDataContainer;
	
	private ArrayList<EzanMeasurement> mMeasurements;
	
	private ArrayList<EzanMeasurementPlace> mShownPlaces = new ArrayList<EzanMeasurementPlace>();
	
	public EzanDataViewFragment()
	{
//		mLineFrag = new LineChartViewFragment("asdf", new double[]{1,2,3,4,5,6,7});
		mChartFrag = new EzanDataChartViewFragment();
//		queryData();
//		Log.e(TAG, "data loaded");
	}
	
//	private void queryData()
//	{
//		mMeasurements = EzanDataAccess.getEzanMeasurements();
//		Log.e(TAG, "measurements read:"+mMeasurements.size());
//	}
	
	public void setShownPlaces(ArrayList<EzanMeasurementPlace> _places)
	{
		mShownPlaces = _places;
		for(EzanMeasurementPlace emp:mShownPlaces)
		{
			Log.e(TAG, emp.toString());
		}
		displayValues();
	}
	
	private void displayValues()
	{
		Log.e(TAG, "displaying values.........");
		HashMap<EzanMeasurementPlace, Double[]> values = new HashMap<EzanMeasurementPlace, Double[]>();
		Log.e(TAG, "number of shown places:"+mShownPlaces.size());
		
//		switch(mRadioGroup.getCheckedRadioButtonId())
//		{
//		case R.id.humidityRadioButt:
//			values = filterData(mShownPlaces, DataType.humidity);
//			break;
//		case R.id.temperatureRadioButt:
//			values = filterData(mShownPlaces, DataType.temp);
//			break;
//		case R.id.lightRadioButt:
//			values = filterData(mShownPlaces, DataType.light);
//			break;
//		case R.id.voltageRadioButt:
//			values = filterData(mShownPlaces, DataType.voltage);
//			break;
//		}
		Log.e(TAG, "number of data entries:"+values.size());
//		setChartData(values);
		
	}
	
	
	
	public void setChartData(GregorianCalendar _start, ArrayList<String> _titles, ArrayList<Double[]> _data) 
	{
		//TODO display timestamps
//		ArrayList<String> titles = new ArrayList<String>();
//		ArrayList<Double[]> data = new ArrayList<Double[]>();
		
//		for(EzanMeasurementPlace emp:_values.keySet())
//		{
//			titles.add(emp.getTitle());
//			data.add(_values.get(emp));
//		}
		
//		for(HashMap<String, Double> singlePlaceMeasures:_data)
//		{
//			data.add((Double[]) singlePlaceMeasures.values().toArray());
//		}
		EzanDataChartViewFragment edcvf = new EzanDataChartViewFragment();
		edcvf.setData(_start, _titles, _data);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
//		try{
//		ft.remove(mChartFrag);
//		
//		}
//		catch(Exception e)
//		{
//			
//		}
		try{
		ft.replace(R.id.ezanDataContainer, edcvf);
		}
		catch (Exception e) {
			ft.add(R.id.ezanDataContainer, mChartFrag);
		}
		ft.commit();
	}

//	private HashMap<EzanMeasurementPlace, Double[]> filterData(ArrayList<EzanMeasurementPlace> _emps, DataType _type)
//	{
//		HashMap<EzanMeasurementPlace, Double[]> res = new HashMap<EzanMeasurementPlace, Double[]>();
////		ArrayList<EzanMeasurement> humidities = new ArrayList<EzanMeasurement>();
//		for(EzanMeasurementPlace emp:_emps)
//		{
////			Log.e(TAG, "place ID="+emp.getID());
//			ArrayList<Double> data = new ArrayList<Double>();
////			Log.e(TAG, "---------------------------------");
////			Log.e(TAG, emp.getID());
//			for(EzanMeasurement em:mMeasurements)
//			{
//				Log.e(TAG, "measure place id="+em.getPlaceId());
////				Log.e(TAG, "measurement ID="+em.getID());
////				Log.e(TAG, "meter ID="+em.getMeterId());
////				Log.e(TAG, "comparing:");
////				Log.e(TAG, em.getPlaceId()+"\n"+emp.getID());
//				if(em.getPlaceId().equals(emp.getID()))
//				{
////					Log.e(TAG, "###match of meter and measure found...");
//					switch(_type)
//					{
//					case humidity:
//						data.add(em.getHumidity());
////						Log.e(TAG, "humidity added");
//						break;
//					case temp:
//						data.add(em.getTemp());
////						Log.e(TAG, "temp added");
//						break;
//					case light:
//						data.add(em.getLight());
////						Log.e(TAG, "light added");
//						break;
//					case voltage:
//						data.add(em.getVoltage());
////						Log.e(TAG, "voltage added");
//						break;
//					}
//				}
//			}
//			Double[]temp = new Double[data.size()];
//			for(int i = 0;i<data.size();i++)
//			{
//				temp[i]=data.get(i);
////				Log.e(TAG, "value @"+i+" = "+temp[i]);
//			}
//			res.put(emp, temp);
//		}
//			
//		return res;
//	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null==mView)
		{
			mView= inflater.inflate(R.layout.ezan_data_view_fragment_layout, container, false);
			
			mDataContainer = (FrameLayout)mView.findViewById(R.id.ezanDataContainer);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.ezanDataContainer, mChartFrag);
			ft.commit();
		}
		
		
		return mView;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) 
	{
		displayValues();
	}
	

}
