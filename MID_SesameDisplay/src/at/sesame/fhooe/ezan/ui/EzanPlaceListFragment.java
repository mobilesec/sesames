package at.sesame.fhooe.ezan.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.sesame.fhooe.ezan.IEzanMeasuementPlaceCheckedListener;
import at.sesame.fhooe.ezan.IEzanMeasurementPlaceSelectionListener;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;


public class EzanPlaceListFragment 
extends android.support.v4.app.ListFragment 
{
	private static final String TAG = "EzanPlaceListFragment";
	private IEzanMeasurementPlaceSelectionListener mSelectionListener;
	
	private EzanPlaceListAdapter mAdapter;
	
	private ArrayList<EzanMeasurementPlace> mPlaces;
	
	private ArrayList<EzanPlaceDetailFragment> mDetailFragments = new ArrayList<EzanPlaceDetailFragment>();
	
	
	public EzanPlaceListFragment(	Context _ctx, 
									ArrayList<EzanMeasurementPlace> _places, 
									IEzanMeasurementPlaceSelectionListener _selectionListener,
									IEzanMeasuementPlaceCheckedListener _checkedListener)
	{
		mPlaces = _places;
//		List<String> placeTitles = new ArrayList<String>();
		
		for(EzanMeasurementPlace emp:mPlaces)
		{
//			placeTitles.add(emp.getTitle());
			mDetailFragments.add(new EzanPlaceDetailFragment(emp));
		}
		mAdapter = new EzanPlaceListAdapter(_ctx, 0, mPlaces, _selectionListener, _checkedListener);
		setListAdapter(mAdapter);
//		if(null!= _selectionListener)
//		{
//			mSelectionListener = _selectionListener;
//		}
	}
	
	


//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
//	}
	
	
	public void setSelectionListener(IEzanMeasurementPlaceSelectionListener _listener)
	{
		mSelectionListener = _listener;
	}
	
	
	private EzanMeasurementPlace getPlaceByTitle(String _title)
	{
		for(EzanMeasurementPlace emp:mPlaces)
		{
			if(emp.getTitle().equals(_title))
			{
				return emp;
			}
		}
		return null;
	}
	
	

//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id) {
//		// TODO Auto-generated method stub
////		super.onListItemClick(l, v, position, id);
//		Log.e(TAG, "onListItemClick");
//		TextView tv = (TextView)v;
////		Log.e(TAG, ""+tv.getText());
//		EzanMeasurementPlace emp = getPlaceByTitle(""+tv.getText());
////		if(null!=emp)
////		{
////			Log.e(TAG, emp.toString());
////		}
////		else
////		{
////			Log.e(TAG, "could not resolve \""+tv.getText()+"\" as measurementplace");
////		}
//		mSelectionListener.notifyEzanMeasurementPlaceSelected(emp);
////		FragmentTransaction ft = getFragmentManager().beginTransaction();
////		ft.
////		ft.replace(at.sesame.fhooe.R.id.ezanPlaceDetailContainer, getDetailFragmentByTitle(""+tv.getText()));
////		ft.commit();
//	}
	
	public ArrayList<EzanMeasurementPlace> getCheckedPlaces()
	{
		return mAdapter.getCheckedPlaces();
	}
	
}
