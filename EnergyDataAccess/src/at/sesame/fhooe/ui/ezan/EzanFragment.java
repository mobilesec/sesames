package at.sesame.fhooe.ui.ezan;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.sesame.fhooe.R;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;

public class EzanFragment 
extends Fragment
implements IEzanMeasurementPlaceSelectionListener
{
	private View mView;
	private EzanPlaceListFragment mPlaceList;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null==mView)
		{
			mView= inflater.inflate(R.layout.ezan_fragment_layout, container, false);
		}
		if(null==mPlaceList)
		{
			mPlaceList = (EzanPlaceListFragment) getFragmentManager().findFragmentById(R.id.fragment1);
			mPlaceList.setSelectionListener(this);
		}
		return mView;
	}
	@Override
	public void notifyEzanMeasurementPlaceSelected(EzanMeasurementPlace _emp) 
	{
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.ezanPlaceDetailContainer, new EzanPlaceDetailFragment(_emp));
		ft.commit();	
	}



}
