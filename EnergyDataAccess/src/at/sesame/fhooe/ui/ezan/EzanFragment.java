package at.sesame.fhooe.ui.ezan;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.sesame.fhooe.R;
import at.sesame.fhooe.ezan.model.EzanMeasurement;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;
import at.sesame.fhooe.ezan.service.EzanDataAccess;

public class EzanFragment 
extends Fragment
implements IEzanMeasurementPlaceSelectionListener, IEzanMeasuementPlaceCheckedListener
{
	private static final String TAG = "EzanFragment";
	private View mView;
	private ArrayList<EzanMeasurementPlace> mPlaces;
	private EzanPlaceListFragment mPlaceList;
	private ArrayList<EzanPlaceDetailFragment> mDetailFrags = new ArrayList<EzanPlaceDetailFragment>();
	private EzanMeasurementPlace mCurPlace;
	private EzanDataViewFragment mDataView;
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
		}

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.ezanPlaceListContainer, mPlaceList);
		ft.add(R.id.ezanChartContainer, mDataView);
		ft.commit();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null==mView)
		{
			mView= inflater.inflate(R.layout.ezan_fragment_layout, container, false);
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
		ArrayList<EzanMeasurementPlace> checkedPlaces = mPlaceList.getCheckedPlaces();
		for(EzanMeasurementPlace emp:checkedPlaces)
		{
			Log.e(TAG, emp.toString());
		}
		
		if(checkedPlaces.size()==0)
		{
			Log.e(TAG, "no places checked");
		}
		mDataView.setShownPlaces(checkedPlaces);
	}

}
