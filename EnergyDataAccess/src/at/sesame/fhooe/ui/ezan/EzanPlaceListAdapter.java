package at.sesame.fhooe.ui.ezan;

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

	public EzanPlaceListAdapter(Context context, int textViewResourceId,
			List<EzanMeasurementPlace> _objects) {
		super(context, textViewResourceId, _objects);
		mPlaces = _objects;
		mLi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	public View getView(int _pos, View _convertView, ViewGroup _parent)
	{
		View v = mLi.inflate(R.layout.checkboxed_listitem, null);
		CheckBox cb = (CheckBox)v.findViewById(R.id.checkBox1);
		cb.setOnCheckedChangeListener(this);
		cb.setOnTouchListener(this);
		EzanMeasurementPlace emp = mPlaces.get(_pos);
		cb.setTag(emp);
		cb.setText(emp.getTitle());
		return v;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		EzanMeasurementPlace emp = (EzanMeasurementPlace)buttonView.getTag();
		Log.e(TAG, emp.getTitle()+" checked");	
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.e(TAG, ((EzanMeasurementPlace)v.getTag()).getTitle()+" touched");
		return false;
	}
	
}
