package at.sesame.fhooe.notification;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

public class FilterDialog 
extends DialogFragment 
{
	private static final String TAG = "FilterDialog";
	private IFilterResultReceiver mResultReceiver;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.filter_dialog, null);
		getDialog().setTitle(R.string.filter_dialog_title);
		final DatePicker fromDp = (DatePicker)v.findViewById(R.id.filter_dialog_from_date_picker);
		
		Date userFilterStart = NotificationCache.getUserFilterStartDate();
		if(null!=userFilterStart)
		{
			GregorianCalendar startCal = new GregorianCalendar();
			startCal.setTime(userFilterStart);
			fromDp.init(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH), null);			
		}
		
		final DatePicker toDp = (DatePicker)v.findViewById(R.id.filter_dialog_to_date_picker);
		
		Date userFilterEnd = NotificationCache.getUserFilterEndDate();
		if(null!=userFilterEnd)
		{
			GregorianCalendar startCal = new GregorianCalendar();
			startCal.setTime(userFilterEnd);
			toDp.init(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH), null);			
		}
		
		Button okButt = (Button)v.findViewById(R.id.filter_dialog_ok_butt);
		okButt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			Log.e(TAG, "from:"+getDateFromDatePicker(fromDp).getTime());
			Log.e(TAG, "to:"+getDateFromDatePicker(toDp).getTime());
			dismiss();
			if(null!=mResultReceiver)
			{
				mResultReceiver.notifyFilterSet(getDateFromDatePicker(fromDp).getTime(), getDateFromDatePicker(toDp).getTime());
			}
			}
		});
		
		Button cancelButt = (Button)v.findViewById(R.id.filter_dialog_cancel_butt);
		cancelButt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		return v;
	}
	
	public void setResultReceiver(IFilterResultReceiver _receiver)
	{
		mResultReceiver = _receiver;
	}
	
	private GregorianCalendar getDateFromDatePicker(DatePicker _dp)
	{
		GregorianCalendar res = new GregorianCalendar();
		res.set(Calendar.YEAR, _dp.getYear());
		res.set(Calendar.MONTH, _dp.getMonth());
		res.set(Calendar.DAY_OF_MONTH, _dp.getDayOfMonth());
		return res;
	}

}
