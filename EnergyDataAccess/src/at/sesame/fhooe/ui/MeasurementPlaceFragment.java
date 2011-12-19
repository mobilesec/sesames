package at.sesame.fhooe.ui;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import at.sesame.fhooe.EnergyDataActivity;
import at.sesame.fhooe.model.DataRow;
import at.sesame.fhooe.model.MeasurementPlace;
import at.sesame.fhooe.service.EnergyDataAccess;

public class MeasurementPlaceFragment 
extends ListFragment 
{
	private static final String TAG = "MeasurementPlaceFragment";
	private ArrayList<MeasurementPlace> mPlaces;
	private MeasurementPlaceListAdapter mAdapter;
	private EnergyDataActivity mOwner;
	public MeasurementPlaceFragment(EnergyDataActivity _owner)
	{
		mOwner= _owner;
		mPlaces = EnergyDataAccess.getMeasurementPlaces();
		if(null==getActivity())
		{
			Log.e(TAG, "activity is null");
		}
		else
		{
			Log.e(TAG, "activity is not null");
		}
		if(null==mPlaces)
		{
			Log.e(TAG, "places are null");
		}
		else
		{
			Log.e(TAG, "places are not null");
		}
		mAdapter = new MeasurementPlaceListAdapter(mOwner,  mPlaces);
		setListAdapter(mAdapter);
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
//	}

	@Override
	public void onListItemClick(ListView _l, View _v, int _position, long _id) {
		// TODO Auto-generated method stub
		super.onListItemClick(_l, _v, _position, _id);
		int id = mAdapter.getItem(_position).getId();
		Log.e(TAG, "id="+id);
//		ArrayList<DataRow> data = EnergyDataAccess.getLoadProfile(id, DataRow.getUrlTimeString(new GregorianCalendar(2011,11,25)), DataRow.getUrlTimeString(new GregorianCalendar(2011,11,27)));
//		if(null==data)
//		{
//			return;
//		}
//		Log.e(TAG, "length of data for id "+id+": "+data.size());
		mOwner.notifyMeasurementPlaceSelected(id);
	}
	
	
}
