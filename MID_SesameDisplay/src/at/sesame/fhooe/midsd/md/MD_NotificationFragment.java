package at.sesame.fhooe.midsd.md;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.sesame.fhooe.midsd.R;

public class MD_NotificationFragment 
extends Fragment 
{
	private String mNotification;
	private Handler mUiHandler;
	private TextView mNotificationView;
	
	public MD_NotificationFragment(Handler _uiHandler)
	{
		mUiHandler = _uiHandler;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.md_notification_layout, null);
		mNotificationView = (TextView)v.findViewById(R.id.md_notification_textview);
		mNotificationView.setText(mNotification);
		
		return v;
	}
	
	@Override
	public void onDetach() 
	{
		mNotification = "";
		super.onDetach();
	}
	
	public String getNotification()
	{
		return mNotification;
	}

	public synchronized void setNotification(String _notification)
	{
		mNotification = _notification;
		if(null==mNotificationView)
		{
			return;
		}
		mUiHandler.post(new Runnable() {
			
			@Override
			public void run() 
			{
				mNotificationView.setText(mNotification);
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
}