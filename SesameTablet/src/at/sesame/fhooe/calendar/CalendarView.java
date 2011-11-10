/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.calendar;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import at.sesame.fhooe.R;
import at.sesame.fhooe.lib.SesameFactory;
import at.sesame.fhooe.lib.calendar.CalendarEvent;
import at.sesame.fhooe.lib.calendar.ICalendarAccess;

/**
 * this class implements the view for a list of CalendarEvents
 * @author Peter Riedl
 *
 */
public class CalendarView 
extends ListActivity
implements OnClickListener
{
	private static final String TAG = "CalendarView";
	private static final int CALENDAR_SELECTION_DIALOG = 0;
	private ICalendarAccess mCalAccess= null;
	
	private ArrayList<CalendarEvent> mEvents = new ArrayList<CalendarEvent>();
	private CalendarEventAdapter mCalendarEventAdapter;
	
	
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.calendar);
		Log.e(TAG, "on Create");
		mCalAccess = SesameFactory.getCalendarAccess(this);
		mCalendarEventAdapter = new CalendarEventAdapter(this, R.layout.calendarevent, mEvents);
		setListAdapter(mCalendarEventAdapter);
		Log.e(TAG, "onActivityCreated");
		Button retrieveCal = (Button)findViewById(R.id.retrieveCalendarButton);
		try
		{
			retrieveCal.setOnClickListener(this);
		}catch(NullPointerException _npe)
		{
			Log.e(TAG, "retrieveCal Button was null");
		}
	}
//	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstance)
//	{
//		super.onCreateView(_inflater, _container, _savedInstance);
//		Log.e(TAG, "onCreateView");
////		super.onCreate(_savedInstance);
////		setContentView(R.layout.calendar);
//		View v = _inflater.inflate(R.layout.calendar, _container, false);
//		
//		Log.e(TAG, "owning activity:"+getActivity().toString());
//		
//		mCalendarEventAdapter = new CalendarEventAdapter(getActivity().getApplicationContext(), R.layout.calendarevent, mEvents);
//		setListAdapter(mCalendarEventAdapter);
//		
//		
//		return v;
//	}
	
//	public void onActivityCreated(Bundle _savedInstance)
//	{
//		super.onActivityCreated(_savedInstance);
//		Log.e(TAG, "onActivityCreated");
//		Button retrieveCal = (Button)getActivity().findViewById(R.id.retrieveCalendarButton);
//		try
//		{
//			retrieveCal.setOnClickListener(this);
//		}catch(NullPointerException _npe)
//		{
//			Log.e(TAG, "retrieveCal Button was null");
//		}
//	}

	public void onClick(View arg0) 
	{
		mCalendarEventAdapter.clear();
		mEvents.clear();
		Log.e(TAG, "button clicked");
		FragmentTransaction ft = getFragmentManager().beginTransaction();

        DialogFragment newFragment = CalendarSelectionDialog.newInstance(this);
        newFragment.show(ft, "dialog");
	}
	
	/**
	 * retrieves all events of one calendar specified by its name and displays it
	 * @param selCalName the name of the calendar to show
	 */
	public void displaySelectedCalendar(String selCalName) 
	{
		mEvents = mCalAccess.getEventsFromCalendar(selCalName);
		
		if(mEvents.isEmpty())
		{
			Toast.makeText(this, R.string.calendarView_emptyCalendarToastMsg, Toast.LENGTH_LONG).show();
		}
		
		for(CalendarEvent ce:mEvents)
		{
			mCalendarEventAdapter.add(ce);
		}

        mCalendarEventAdapter.notifyDataSetChanged();
	}
	
	public ICalendarAccess getCalendarAccess()
	{
		return mCalAccess;
	}
	
	@Override
	public Dialog onCreateDialog(int id)
	{
		final String[] calendarNames = mCalAccess.getCalendarNames();
		AlertDialog ad = null;
		switch(id)
		{
			case CALENDAR_SELECTION_DIALOG:
				
	            break;
//				Log.e(TAG, "showing alertdialog");
//				AlertDialog.Builder builder = new AlertDialog.Builder(this);
//				builder.setTitle(R.string.calendarView_calendarSelectionDialogTitle);
//				builder.setItems(calendarNames, new DialogInterface.OnClickListener() {
//				    public void onClick(DialogInterface dialog, int item) {
//				        displaySelectedCalendar(calendarNames[item]);
//				    }
//				});
//				AlertDialog alert = builder.create();
//				alert.show();
//				return alert;
			default:
				break;
		}
		return ad;
	}
}
