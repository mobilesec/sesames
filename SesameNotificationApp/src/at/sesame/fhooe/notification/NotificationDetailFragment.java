package at.sesame.fhooe.notification;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import at.sesame.fhooe.lib2.data.EnhancedSesameNotification;

public class NotificationDetailFragment 
extends DialogFragment 
{
	public static final String NOTIFICATION_KEY = "at.sesame.fhooe.notification"; 
	private EnhancedSesameNotification mNotification;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mNotification = (EnhancedSesameNotification) getArguments().getSerializable(NOTIFICATION_KEY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.notification_dialog, null);

		getDialog().setTitle(R.string.notification_dialog_title);
		TextView text = (TextView)v.findViewById(R.id.notification_dialog_notification_label);
		text.setText(mNotification.getMessage());
		
		Button okButt = (Button)v.findViewById(R.id.notification_dialog_ok_butt);
		okButt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			dismiss();
			}
		});
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle arg0) 
	{
		super.onSaveInstanceState(arg0);
		arg0.putSerializable(NOTIFICATION_KEY, mNotification);
	}

	
	
	

}
