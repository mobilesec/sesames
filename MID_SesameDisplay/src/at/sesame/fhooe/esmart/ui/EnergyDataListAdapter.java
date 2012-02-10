package at.sesame.fhooe.esmart.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.sesame.fhooe.esmart.model.EsmartDataRow;
import at.sesame.fhooe.midsd.R;

public class EnergyDataListAdapter 
extends ArrayAdapter<EsmartDataRow>
{
	private LayoutInflater mLi;
	private List<EsmartDataRow> mData;
	public EnergyDataListAdapter(Context context, List<EsmartDataRow> objects) {
		super(context, 0, objects);
		mData = objects;
		mLi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int _pos, View _convertView, ViewGroup _parent)
	{
		View v = mLi.inflate(R.layout.data_row_entry, null);
		TextView timeStampLabel = (TextView)v.findViewById(R.id.energyDataRowEntryTimeStampField);
		TextView valueLabel = (TextView)v.findViewById(R.id.energyDataRowEntryValueField);
		
		EsmartDataRow item = mData.get(_pos);
		
		timeStampLabel.setText(item.getDate().toLocaleString());
		valueLabel.setText(""+item.getDataValue());
		return v;		
	}
}
