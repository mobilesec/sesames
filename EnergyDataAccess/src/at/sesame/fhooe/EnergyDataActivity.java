package at.sesame.fhooe;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import at.sesame.fhooe.model.DataRow;
import at.sesame.fhooe.model.MeasurementPlace;

import at.sesame.fhooe.service.EnergyDataAccess;
import at.sesame.fhooe.ui.EnergyDataFragment;
import at.sesame.fhooe.ui.LineChartViewFragment;
import at.sesame.fhooe.ui.MeasurementPlaceFragment;
import at.sesame.fhooe.ui.MeasurementPlaceListAdapter;


public class EnergyDataActivity 
extends Fragment implements OnItemClickListener
{
	private static final String TAG = "EnergyDataRestAccessActivity";
	
//	private ListView mListView;
	private ArrayList<MeasurementPlace> mPlaces;
//	private static final int mp = 15;
//	private static final String from = "2011-11-25";
//	private static final String to = "2011-11-28";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        EnergyDataAccess eda = new EnergyDataAccess();
        
//        mPlaces = EnergyDataAccess.getMeasurementPlaces();
//        mAdapter = new MeasurementPlaceListAdapter(this,  mPlaces);
//        mListView = (ListView)findViewById(R.id.measurementPlacesListView);
//        mListView.setAdapter(mAdapter);
//        mListView.setOnItemClickListener(this);
        	FragmentTransaction ft = getFragmentManager().beginTransaction();
        	ft.add(R.id.measurementPlaceListFrame,new MeasurementPlaceFragment(this));
//        	ft.add(R.id.energyDataFrame, new LineChartViewFragment());
        	ft.commit();
        
//        ArrayList<Service> services=eda.getServices();
//        for(Service s:services)
//        {
//        	Log.e(TAG, s.toString());
//        }
//        ArrayList<MeasurementPlace> places = eda.getMeasurementPlaces();
////        Log.e(TAG, "parsed "+places.size()+" places");
//        for(MeasurementPlace mp:places)
//        {
//        	Log.e(TAG, mp.toString());
//        }
//        eda.getDailyConsumption(mp,from, to);
//        Data data=eda.getLoadProfile(mp, from, to);
//        Log.e(TAG, "starting to query load profile");

//        Gre
//        gc.set(2011, 12, 1);
//        String from = DataRow.getUrlTimeString(2011,11,25);
//        String from = DataRow.getUrlTimeString(new GregorianCalendar(2011, 12, 1));
//        Log.e(TAG, "------------------------------------from string created");
////        gc.add(Calendar.DAY_OF_MONTH, 1);
//        String to = DataRow.getUrlTimeString(2011,12,2);
//        ArrayList<DataRow> data = eda.getLoadProfile(mp, from, to);
//        Log.e(TAG, "number of datarows read:"+data.size());
//        for(DataRow dr:data)
//        {
//        	Log.e(TAG, dr.toString());
//        }
    }
    
    public void notifyMeasurementPlaceSelected(int _id)
    {
    	ArrayList<DataRow> dataRows;
    	if(_id==15)
    	{
    		dataRows = EnergyDataAccess.getLoadProfile(_id, DataRow.getUrlTimeString(new GregorianCalendar(2011,11,25)), DataRow.getUrlTimeString(new GregorianCalendar(2011,12,15)));
    	}
    	else
    	{
    		dataRows = EnergyDataAccess.getLoadProfile(_id, DataRow.getUrlTimeString(new GregorianCalendar(2011,11,25)), DataRow.getUrlTimeString(new GregorianCalendar(2011,11,26)));
    	}
    	 
    	if(null==dataRows)
    	{
    		Log.e(TAG, "data could not be queried");
//    		Toast.makeText(this, "data could not be queried", Toast.LENGTH_LONG).show();
    		return;
    	}
    	double[] data = new double[dataRows.size()];
    	for(int i = 0;i<dataRows.size();i++)
    	{
    		data[i]=dataRows.get(i).getDataValue();
    	}
    	FragmentTransaction ft = getFragmentManager().beginTransaction();
    	ft.replace(R.id.energyDataFrame, new LineChartViewFragment(""+_id, data));
    	ft.commit();
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		MeasurementPlace mp = mPlaces.get(arg2);
		Log.e(TAG, mp.toString());
		Intent i = new Intent();
		i.setClass(getActivity(), EnergyDataFragment.class);
		Bundle b = new Bundle();
		b.putInt(EnergyDataFragment.MP_ID_KEY, mp.getId());
		i.putExtras(b);
		startActivity(i);
	}
}