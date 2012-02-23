package at.sesame.fhooe.midsd.hd.pms;

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
	private Context mCtx;
	public PMSListAdapter(Context context, int textViewResourceId,
			List<ComputerRoomInformation> objects) {
		super(context, textViewResourceId, objects);
		mInfos = new ArrayList<ComputerRoomInformation>(objects);
		mCtx = context;
		mLi = LayoutInflater.from(mCtx);
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
		header.setText(info.getRoomName());

		TextView idle = (TextView)v.findViewById(R.id.textView2);
		idle.setText(mCtx.getString(R.string.pms_room_list_inactive_prefix)+info.getNumIdleComputers());
		
		TextView active = (TextView)v.findViewById(R.id.textView3);
		active.setText(mCtx.getString(R.string.pms_room_list_active_prefix)+info.getNumActiveComputers());
		return v;
	}
	
	

}
