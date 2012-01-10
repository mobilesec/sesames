package at.sesame.fhooe.ui.energy;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.sesame.fhooe.R;
import at.sesame.fhooe.R.id;
import at.sesame.fhooe.R.layout;
import at.sesame.fhooe.energy.model.MeasurementPlace;


public class MeasurementPlaceListAdapter extends ArrayAdapter<MeasurementPlace> 
{
	private List<MeasurementPlace> mPlaces;
	private LayoutInflater mLi;

	public MeasurementPlaceListAdapter(Context context, List<MeasurementPlace> objects) {
		super(context,0 , objects);
		mPlaces = objects;
		mLi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int _pos, View _convertView, ViewGroup _parent)
	{
		View v = mLi.inflate(R.layout.measurement_place_list_entry, null);
		TextView idLabel = (TextView)v.findViewById(R.id.measurementPlaceListEntryIdField);
		TextView nameLabel = (TextView)v.findViewById(R.id.measurementPlaceListEntryNameField);
		
		MeasurementPlace item = mPlaces.get(_pos);
		
		idLabel.setText(""+item.getId());
		nameLabel.setText(item.getName());
		return v;		
	}

}
