package at.sesame.fhooe.energydisplay.ezan.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.sesame.fhooe.energydisplay.R;
import at.sesame.fhooe.energydisplay.ezan.model.EzanMeasurementPlace;


public class EzanPlaceDetailFragment 
extends Fragment 
{
	private static final String TAG = "EzanPlaceDetailFragment";
	private EzanMeasurementPlace mEmp;
	
	public EzanPlaceDetailFragment(EzanMeasurementPlace emp)
	{
		mEmp = emp;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.ezan_place_detail_layout, container, false);
		TextView id = (TextView)v.findViewById(R.id.idField);
		TextView title = (TextView)v.findViewById(R.id.titleField);
		TextView description = (TextView)v.findViewById(R.id.descriptionField);
		TextView address = (TextView)v.findViewById(R.id.addressField);
		
		id.setText(mEmp.getID());
		title.setText(mEmp.getTitle());
		description.setText(mEmp.getDescription());
		address.setText(mEmp.getAddress());
		return v;
	}
	
	
	public String getTitle()
	{
		return mEmp.getTitle();
	}
}
