package at.sesame.fhooe.ui.ezan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import at.sesame.fhooe.R;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;

public class EzanPlaceListAdapter 
extends ArrayAdapter<EzanMeasurementPlace>
implements OnCheckedChangeListener, OnTouchListener
{
	private static final String TAG = "EzanPlaceListAdapter";
	private List<EzanMeasurementPlace> mPlaces;
	private LayoutInflater mLi;
	private IEzanMeasurementPlaceSelectionListener mSelectionListener;
	private IEzanMeasuementPlaceCheckedListener mCheckedListener;
	
	private HashMap<EzanMeasurementPlace, Boolean> mCheckedPlaces = new HashMap<EzanMeasurementPlace, Boolean>();

	public EzanPlaceListAdapter(Context context, int textViewResourceId,
			List<EzanMeasurementPlace> _objects, 
			IEzanMeasurementPlaceSelectionListener _selectionListener,
			IEzanMeasuementPlaceCheckedListener _checkedListener) {
		super(context, textViewResourceId, _objects);
		mPlaces = _objects;
		mLi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mSelectionListener = _selectionListener;
		mCheckedListener = _checkedListener;
	}
	
	public View getView(int _pos, View _convertView, ViewGroup _parent)
	{
		View v = mLi.inflate(R.layout.checkboxed_listitem, null);
		CheckBox cb = (CheckBox)v.findViewById(R.id.checkBox1);
		cb.setOnCheckedChangeListener(this);
//		cb.setOnTouchListener(this);
		v.setOnTouchListener(this);
		
		EzanMeasurementPlace emp = mPlaces.get(_pos);
		v.setTag(emp);
		cb.setTag(emp);
		cb.setText(emp.getTitle());
		return v;
	}
	
	public ArrayList<EzanMeasurementPlace> getCheckedPlaces()
	{
		ArrayList<EzanMeasurementPlace> res = new ArrayList<EzanMeasurementPlace>();
		for(EzanMeasurementPlace emp:mCheckedPlaces.keySet())
		{
			if(mCheckedPlaces.get(emp))
			{
				res.add(emp);
			}
		}
		return res;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		EzanMeasurementPlace emp = (EzanMeasurementPlace)buttonView.getTag();
		mCheckedPlaces.put(emp, isChecked);
		Log.e(TAG, emp.getTitle()+" +++checked");
		mCheckedListener.notifyEzanMeasurementPlaceChecked(emp);
			
	}
	
	

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		Log.e(TAG, ((EzanMeasurementPlace)v.getTag()).getTitle()+" ---touched");
		v.animate();
		mSelectionListener.notifyEzanMeasurementPlaceSelected((EzanMeasurementPlace)v.getTag());
		
		return true;
	}

}
