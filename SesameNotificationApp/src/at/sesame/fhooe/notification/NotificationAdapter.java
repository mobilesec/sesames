package at.sesame.fhooe.notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import at.sesame.fhooe.lib2.data.EnhancedSesameNotification;

public class NotificationAdapter extends
ArrayAdapter<EnhancedSesameNotification>
{
	private static final String TAG = "NotificationAdapter";
//	private ArrayList<EnhancedSesameNotification> mNotifications = new ArrayList<EnhancedSesameNotification>();
	
	private SimpleDateFormat mTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	private FragmentManager mFm;

	public NotificationAdapter(Context context,List<EnhancedSesameNotification> objects, FragmentManager _fm) {
		super(context, 0, objects);
		mFm = _fm;
//		mNotifications = (ArrayList<EnhancedSesameNotification>) objects;
	}

//	@Override
//	public int getCount() {
//		// TODO Auto-generated method stub
//		return mNotifications.size();
//	}
//
//	@Override
//	public EnhancedSesameNotification getItem(int position) {
//		// TODO Auto-generated method stub
//		return mNotifications.get(position);
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		
		EnhancedSesameNotification nonFinalotification;
		try
		{
			nonFinalotification = getItem(position);
		}
		catch(Exception e)
		{
			return convertView;
		}
		final EnhancedSesameNotification notification = nonFinalotification;
		if(null==convertView)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_list_entry, null);
		}
		
		TextView dateView = (TextView)convertView.findViewById(R.id.notification_list_entry_date_label);
		dateView.setText(mTimeFormat.format(notification.getTimeStamp()));
		
		TextView sensorView = (TextView) convertView.findViewById(R.id.notification_list_entry_sensor_label);
		sensorView.setText(notification.getSensor().getId());
		
		ImageButton detailButt = (ImageButton)convertView.findViewById(R.id.notification_list_entry_detail_butt);
		detailButt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NotificationDetailFragment frag = new NotificationDetailFragment();
				Bundle args = new Bundle();
				args.putSerializable(NotificationDetailFragment.NOTIFICATION_KEY, notification);
				frag.setArguments(args);
				frag.show(mFm, null);
				
			}
		});
		return convertView;
	}
	
	
	

}
