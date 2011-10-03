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
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import at.sesame.fhooe.R;
import at.sesame.fhooe.lib.calendar.CalendarAccess;
import at.sesame.fhooe.lib.calendar.CalendarEvent;

/**
 * this class implements the view for a list of CalendarEvents
 * @author Peter Riedl
 *
 */
public class CalendarView 
extends ListActivity
implements OnClickListener
{
	
	/**
	 * the CalendarAcces to get the calendars and events from
	 */
	private CalendarAccess mCalAccess= null;

	/**
	 * a list of all CalendarEvents
	 */
	private ArrayList<CalendarEvent> mEvents = new ArrayList<CalendarEvent>();
	
	/**
	 * the CalendarEventAdapter to reuse the layout of one CalendarEvent 
	 * in a list
	 */
	private CalendarEventAdapter mCalendarEventAdapter;
	
	/**
	 * a list of the names of all synchronized calendars to be displayed 
	 * in a selection dialog
	 */
	private String[] mCalendarNames;
	
	/**
	 * the ID for the calendar selectio dialog
	 */
	private static final int CALENDAR_SELECTION_DIALOG = 0;
	
	@Override
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.calendar);
		
		mCalAccess = new CalendarAccess(this);
		mCalendarNames = mCalAccess.getCalendarNames();
		
		mCalendarEventAdapter = new CalendarEventAdapter(getApplicationContext(), R.layout.calendarevent, mEvents);
		setListAdapter(mCalendarEventAdapter);
		
		Button b = (Button)findViewById(R.id.retrieveCalendarButton);
		b.setOnClickListener(this);
	}
	
	@Override
	public Dialog onCreateDialog(int id)
	{
		AlertDialog ad = null;
		switch(id)
		{
			case CALENDAR_SELECTION_DIALOG:
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.calendarView_calendarSelectionDialogTitle);
				builder.setItems(mCalendarNames, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				        displaySelectedCalendar(mCalendarNames[item]);
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
				break;
			default:
				break;
		}
		return ad;
	}

	@Override
	public void onClick(View arg0) 
	{
		if(null==mCalendarNames||mCalendarNames.length==0)
		{
			Toast.makeText(this, "no calendars available", Toast.LENGTH_LONG).show();
			return;
		}
		mCalendarEventAdapter.clear();
		mEvents.clear();
		showDialog(CALENDAR_SELECTION_DIALOG);
		
	}

	/**
	 * retrieves all events of one calendar specified by its name and displays it
	 * @param selCalName the name of the calendar to show
	 */
	private void displaySelectedCalendar(String selCalName) 
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
}
