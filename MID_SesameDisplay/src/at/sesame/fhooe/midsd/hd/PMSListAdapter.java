package at.sesame.fhooe.midsd.hd;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.sesame.fhooe.midsd.R;

public class PMSListAdapter 
extends ArrayAdapter<ComputerRoomInformation> 
{
	private static final String TAG = "PMSListAdapter";
	private ArrayList<ComputerRoomInformation> mInfos;
	private LayoutInflater mLi;
	
	public PMSListAdapter(Context context, int textViewResourceId,
			List<ComputerRoomInformation> objects) {
		super(context, textViewResourceId, objects);
		mInfos = new ArrayList<ComputerRoomInformation>(objects);
		mLi = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		Log.e(TAG, "getView");
		View v = convertView;
		if(null==v)
		{
			v = mLi.inflate(R.layout.hd_computer_room_info_listentry, null);
		}
		
		ComputerRoomInformation info = mInfos.get(position);
		Log.e(TAG, info.toString());
		TextView header = (TextView)v.findViewById(R.id.textView1);
		header.setText(info.getName());

		TextView idle = (TextView)v.findViewById(R.id.textView2);
		idle.setText("IDLE:"+info.getNumIdleComputers());
		
		TextView active = (TextView)v.findViewById(R.id.textView3);
		active.setText("ACTIVE:"+info.getNumActiveComputers());
		return v;
	}
	
	

}
