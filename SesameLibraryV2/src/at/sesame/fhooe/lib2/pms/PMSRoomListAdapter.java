package at.sesame.fhooe.lib2.pms;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.sesame.fhooe.lib2.R;
import at.sesame.fhooe.lib2.data.SesameDataCache;


public class PMSRoomListAdapter 
extends ArrayAdapter<ComputerRoomInformation> 
{
	private static final String TAG = "PMSListAdapter";
	private static final DecimalFormat LIST_VALUE_FORMAT = new DecimalFormat("#.##");
	private ArrayList<ComputerRoomInformation> mInfos;
	private LayoutInflater mLi;
	private Context mCtx;
	
	private View mView;
	public PMSRoomListAdapter(Context context, int textViewResourceId,
			List<ComputerRoomInformation> objects) {
		super(context, textViewResourceId, objects);
		mInfos = new ArrayList<ComputerRoomInformation>(objects);
		mCtx = context;
		mLi = LayoutInflater.from(mCtx);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
//		Log.e(TAG, "getView");
//		mView = convertView;
//		if(null==v)
		{
			mView = mLi.inflate(R.layout.hd_computer_room_info_listentry, null);
		}
		
		ComputerRoomInformation info = mInfos.get(position);
//		Log.e(TAG, info.toString());
		TextView header = (TextView)mView.findViewById(R.id.computerRoomListNameLabel);
		header.setText(info.getRoomName());
		header.setTextColor(Color.WHITE);

		TextView idle = (TextView)mView.findViewById(R.id.computerRoomListInactiveLabel);
		idle.setText(mCtx.getString(R.string.pms_room_list_inactive_prefix)+info.getNumIdleComputers());
		idle.setTextColor(Color.WHITE);
		
		TextView active = (TextView)mView.findViewById(R.id.computerRoomListActiveLabel);
		active.setText(mCtx.getString(R.string.pms_room_list_active_prefix)+info.getNumActiveComputers());
		active.setTextColor(Color.WHITE);
		
		TextView notifications = (TextView)mView.findViewById(R.id.notificationLabel);
		notifications.setVisibility(View.INVISIBLE);
		
		TextView currentConsumption = (TextView)mView.findViewById(R.id.computerRoomListCurrentConsumptionLabel);
		try {
			double val = SesameDataCache.getInstance().getLastEnergyReading(info.getMeasurementPlace()).getVal();
			currentConsumption.setText(mCtx.getString(R.string.pms_room_list_current_consumption_prefix)+LIST_VALUE_FORMAT.format(val));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TextView overallConsumption = (TextView)mView.findViewById(R.id.computerRoomListOverallConsumptionLabel);
		double val = SesameDataCache.getInstance().getOverallEnergyConsumtion(info.getMeasurementPlace());
		overallConsumption.setText(mCtx.getString(R.string.pms_room_list_overall_consumption_prefix)+LIST_VALUE_FORMAT.format(val));
		
		// if there's no notification but operation in progress use another background color
		if (info.getNumNotifications()>0) {
			notifications.setBackgroundResource(R.drawable.notification_background);
		} else {
			notifications.setBackgroundResource(R.drawable.waiting_background);
		}
		
		if(info.isDirty())
		{
			notifications.setText("...");
			notifications.setVisibility(View.VISIBLE);
		}
		else
		{
			notifications.setText(""+info.getNumNotifications());
			notifications.setTextColor(Color.WHITE);
			if(info.getNumNotifications()>0)
			{
//			Log.e(TAG, "notification set....");
//			ImageView notification = (ImageView)mView.findViewById(R.id.hd_computer_room_info_listentry_notification);
				//notifications.setBackgroundResource(R.drawable.ic_pms_list_notification);
				notifications.setVisibility(View.VISIBLE);
			}
			else
			{
//			Log.e(TAG, "notification not set...");
				notifications.setVisibility(View.INVISIBLE);
			}			
		}
		
		return mView;
	}
	
	
	
	@Override
	public int getCount() {
		return mInfos.size();
	}

	public View getView()
	{
		return mView;
	}

}
