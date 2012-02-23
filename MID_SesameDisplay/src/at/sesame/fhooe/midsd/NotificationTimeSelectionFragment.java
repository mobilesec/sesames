package at.sesame.fhooe.midsd;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TimePicker;
import at.sesame.fhooe.midsd.data.SesameDataCache;

public class NotificationTimeSelectionFragment 
extends DialogFragment 
{
	private static final String TAG = "NotificationTimeSelectionFragement";
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		return new TimePickerDialog(getActivity(), new OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				Log.e(TAG, "hours:"+hourOfDay+", minute:"+minute);
				GregorianCalendar notificationTime = new GregorianCalendar();
				notificationTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
				notificationTime.set(Calendar.MINUTE, minute);
				notificationTime.set(Calendar.SECOND, 0);
				SesameDataCache.getInstance().scheduleSingleNotification(notificationTime.getTime());
			}
		}, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		View v = inflater.inflate(R.layout.notification_time_selection_layout, null);
//		return v;
//	}
	
	
	
}
