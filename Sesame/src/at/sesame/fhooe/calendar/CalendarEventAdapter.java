/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.calendar;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.sesame.fhooe.R;
import at.sesame.fhooe.lib.calendar.CalendarEvent;

/**
 * the CalendarEventAdapter is necessary to reuse the layout of one CalenarEvent-UI
 * in a list of CalendarEvents
 * @author Peter Riedl
 *
 */
public class CalendarEventAdapter 
extends ArrayAdapter<CalendarEvent> 
{
	/**
	 * a list of all CalendarEvents
	 */
	private ArrayList<CalendarEvent> mEvents = new ArrayList<CalendarEvent>();
	
	/**
	 * the Context of the CalendarEventAdapter
	 */
	private Context mContext;
	
	/**
	 * creates a new CalendarEventAdapter
	 * @param context the context of the CalendarEventAdapter
	 * @param textViewResourceId ID of the view to inflate
	 * @param objects all CalendarEvents
	 */
	public CalendarEventAdapter(Context context, int textViewResourceId, List<CalendarEvent> objects) 
	{
		super(context, textViewResourceId, objects);
		mContext = context;
		mEvents = (ArrayList<CalendarEvent>) objects;
	}

	@Override
	public View getView(int _pos, View _convertView, ViewGroup _parent)
	{
		View v = _convertView;
        if (v == null) 
        {
            LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.calendarevent, null);
        }
        CalendarEvent ce = mEvents.get(_pos);
        if(null!=ce)
        {
        	TextView titleField =(TextView) v.findViewById(R.id.titleField);
        	titleField.setText(ce.getTitle());
        	
        	TextView beginField =(TextView) v.findViewById(R.id.beginField);
        	beginField.setText(ce.getBeginDate().toLocaleString());
        	
        	TextView endField =(TextView) v.findViewById(R.id.endField);
        	endField.setText(ce.getEndDate().toLocaleString());
        	
        	TextView allDayField =(TextView) v.findViewById(R.id.allDayField);
        	allDayField.setText(""+ce.isAllDay());
        	
        	TextView descriptionField =(TextView) v.findViewById(R.id.descriptionField);
        	descriptionField.setText(ce.getDescription());
        	
        	TextView locationField =(TextView) v.findViewById(R.id.locationField);
        	locationField.setText(ce.getEventLocation());
        }
		return v;	
	}
}
