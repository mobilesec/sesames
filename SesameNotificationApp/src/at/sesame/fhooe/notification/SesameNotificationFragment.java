package at.sesame.fhooe.notification;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import at.sesame.fhooe.lib2.data.EnhancedSesameNotification;
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;
import at.sesame.fhooe.lib2.data.SesameSensor;

public class SesameNotificationFragment 
extends Fragment 
{
	public static final String MEASUREMENT_PLACE_BUNDLE_KEY = "at.sesame.fhooe.mp";
	private SesameMeasurementPlace mPlace;
	private Timer mUpdateTimer;

	private static final long UPDATE_PERIOD = 3600000;
	//	private ArrayList<EnhancedSesameNotification> mNotifications;

	private ArrayList<EnhancedSesameNotification> mNotifications = new ArrayList<EnhancedSesameNotification>();

	private NotificationAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mNotifications.clear();
		String placeName = getArguments().getString(MEASUREMENT_PLACE_BUNDLE_KEY);
		mPlace = NotificationCache.getLightMeasurementPlaceByName(placeName);
		//		new Timer().schedule(new UpdateTask(), delay)
	}

	@Override
	public void onPause() {
		stopUpdates();
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startUpdates();
	}

	private void startUpdates()
	{
		stopUpdates();
		mUpdateTimer = new Timer("NotificationUpdateTimer");
		mUpdateTimer.schedule(new UpdateTask(), 0, UPDATE_PERIOD);
	}

	private void stopUpdates()
	{
		if(null!=mUpdateTimer)
		{
			mUpdateTimer.cancel();
			mUpdateTimer.purge();
		}
	}
	
	public void performSingleUpdate()
	{
		new Timer().schedule(new UpdateTask(), 0);
	}


	private class UpdateTask extends TimerTask
	{

		@Override
		public void run() 
		{
			final ArrayList<EnhancedSesameNotification> notifications = new ArrayList<EnhancedSesameNotification>();
			for(SesameSensor ss:mPlace.getLightSensors())
			{
				notifications.addAll(NotificationCache.getInstance().getEnhancedNotifications(mPlace.getName(), ss.getId()));
			}

			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					if(null!=mAdapter)
					{
						mNotifications.clear();

						mNotifications.addAll(notifications);
						mAdapter.notifyDataSetChanged();						
					}
				}
			});
		}

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.notification_fragment_layout, null);

		TextView title = (TextView) v.findViewById(R.id.notification_fragment_title_label);
		title.setText(mPlace.getName());

		mAdapter = new NotificationAdapter(getActivity(), mNotifications, getFragmentManager());

		ListView lv = (ListView)v.findViewById(R.id.notification_fragment_notification_list);
		lv.setAdapter(mAdapter);

		return v;
	}


}
