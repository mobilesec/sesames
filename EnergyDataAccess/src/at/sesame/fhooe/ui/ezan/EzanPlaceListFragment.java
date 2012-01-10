package at.sesame.fhooe.ui.ezan;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;
import at.sesame.fhooe.ezan.service.EzanDataAccess;

public class EzanPlaceListFragment 
extends ListFragment 
{
	private static final String TAG = "EzanPlaceListFragment";
	private IEzanMeasurementPlaceSelectionListener mSelectionListener;
//	private ListView mList;
	
	private ArrayList<EzanMeasurementPlace> mPlaces;
	
	private ArrayList<EzanPlaceDetailFragment> mDetailFragments = new ArrayList<EzanPlaceDetailFragment>();
	
	
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		
//		setListAdapter(new EzanPlaceListAdapter(getActivity(), 0, EzanDataAccess.getEzanPlaces()));
		List<String> placeTitles = new ArrayList<String>();
		mPlaces = EzanDataAccess.getEzanPlaces();
		
		for(EzanMeasurementPlace emp:mPlaces)
		{
			placeTitles.add(emp.getTitle());
			mDetailFragments.add(new EzanPlaceDetailFragment(emp));
		}
		
		setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item_1, placeTitles));
//		for(EzanMeasurementPlace emp:EzanDataAccess.getEzanPlaces())
//		{
//			Log.e(TAG, emp.toString());
//		}
//		
//		for(EzanMeasurement em:EzanDataAccess.getEzanMeasurements())
//		{
//			Log.e(TAG, em.toString());
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
	
	private EzanPlaceDetailFragment getDetailFragmentByTitle(String _title)
	{
		for(EzanPlaceDetailFragment epdf:mDetailFragments)
		{
			if(epdf.getTitle().equals(_title))
			{
				return epdf;
			}
		}
		return null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		TextView tv = (TextView)v;
//		Log.e(TAG, ""+tv.getText());
		EzanMeasurementPlace emp = getPlaceByTitle(""+tv.getText());
//		if(null!=emp)
//		{
//			Log.e(TAG, emp.toString());
//		}
//		else
//		{
//			Log.e(TAG, "could not resolve \""+tv.getText()+"\" as measurementplace");
//		}
		mSelectionListener.notifyEzanMeasurementPlaceSelected(emp);
//		FragmentTransaction ft = getFragmentManager().beginTransaction();
//		ft.
//		ft.replace(at.sesame.fhooe.R.id.ezanPlaceDetailContainer, getDetailFragmentByTitle(""+tv.getText()));
//		ft.commit();
	}
	
	
}
