package at.sesame.fhooe.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import at.sesame.fhooe.R;

public class CalendarSelectionDialog 
extends DialogFragment 
{
	private static final String TAG = "CalendarSelectionDialog";
	private static CalendarView mOwner = null;
	
	public static CalendarSelectionDialog newInstance(CalendarView _owner) 
	{
		mOwner = _owner;
		CalendarSelectionDialog frag = new CalendarSelectionDialog();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
    	Log.e(TAG, "creating dialog");
        final String[] calendarNames = mOwner.getCalendarAccess().getCalendarNames();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.calendarView_calendarSelectionDialogTitle);
		builder.setItems(calendarNames, new DialogInterface.OnClickListener() 
		{
		    public void onClick(DialogInterface dialog, int item) 
		    {
		        mOwner.displaySelectedCalendar(calendarNames[item]);
		    }
		});
		return builder.create();
    }

}
