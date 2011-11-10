/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.calendar;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import at.sesame.fhooe.lib.R;


/**
 * this class accesses all calendars that are synchronized with the device
 * @author Peter Riedl
 *
 */
public class CalendarAccessHC 
implements ICalendarAccess
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private final String TAG = getClass().getSimpleName();
	
	/**
	 * a list of possible URIs for different android versions.
	 */
	public static final Uri[] mCalUris = new Uri[]{	Uri.parse("content://calendar"), 
													Uri.parse("content://calendar/calendars"), 
													Uri.parse("content://com.android.calendar/calendars")};
	
	/**
	 * the context of the CalendarAccess
	 */
	private Context mContext = null;
	
	/**
	 * the actual URI for accessing the calendars
	 */
	private Uri mCalendarsUri = Uri.parse("content://com.android.calendar/calendars");
	
	/**
	 * the actual URI for accessing CalendarEvents
	 */
	private Uri mEventsUri = Uri.parse("content://com.android.calendar/events");

	/**
	 * creates a new CalendarAccess instance
	 * @param _c the context of the CalendarAccess
	 */
	public CalendarAccessHC(Context _c)
	{
		mContext = _c;
	}
	

	public String[] getCalendarNames()
	{
		ArrayList<String>buff = new ArrayList<String>();
		
		Cursor c = getCalendarCursor();
		if(null==c)
		{
			return new String[]{};
		}
		while(c.moveToNext())
		{
			buff.add(c.getString(1));
		}
		String[]res = new String[buff.size()];
		for(int i = 0;i < buff.size(); i++)
		{
			res[i]=buff.get(i);
		}
//		res[buff.size()] = mContext.getString(at.sesame.fhooe.lib.R.string.calendarAccess_allCalendarListentry);
		return res;
	}
	
	
	public int getCalendarIdByName(String _name)
	{
		Cursor c = getCalendarCursor();
		Log.e(TAG, "name to check:"+_name);
		while(c.moveToNext())
		{
			String calName = c.getString(1);
			Log.e(TAG, "name in getCalendar...:"+calName);
			if(calName.equals(_name))
			{
				Log.e(TAG, "match found, returning:"+c.getInt(0));
				return c.getInt(0);
			}
		}
		return -1;
	}
	
	/**
	 * returns a cursor to iterate over all available calendars
	 * @return the database cursor for iteration
	 */
	private Cursor getCalendarCursor() 
	{
		String[] projection = new String[] { "_id", "displayName","selected" };
		Cursor cursor = mContext.getContentResolver().query(mCalendarsUri, projection, null, null, null);
		return cursor;
	}
	

	public ArrayList<CalendarEvent> getEventsFromCalendar(String _calName)
	{
		ArrayList<CalendarEvent> res = new ArrayList<CalendarEvent>();
		
		String[] eventProjection = new String[]{"_id","_sync_account","_sync_account_type",
												"_sync_id","_sync_version","_sync_time",
												"_sync_local_id","_sync_dirty",
												"calendar_id","htmlUri","title","eventLocation",
												"description","eventStatus","selfAttendeeStatus",
												"commentsUri","dtstart","dtend","eventTimezone",
												"duration","allDay","visibility","transparency",
												"hasAlarm","hasExtendedProperties","rrule",
												"rdate","exrule","exdate","originalEvent",
												"originalInstanceTime","originalAllDay",
												"lastDate","hasAttendeeData","guestsCanModify",
												"guestsCanInviteOthers","guestsCanSeeGuests",
												"organizer","deleted"};
		
		Cursor eventCursor = mContext.getContentResolver().query(mEventsUri, eventProjection, null, null, null);
		
		String[]dateProjection = new String[]{"event_id","begin","end"};
		Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
		long now = new Date().getTime();
		ContentUris.appendId(builder, now - DateUtils.YEAR_IN_MILLIS);
		ContentUris.appendId(builder, now + DateUtils.YEAR_IN_MILLIS);
		
		
		while(eventCursor.moveToNext())
		{
			String sync_account = eventCursor.getString(1);
			Log.e(TAG, "sync_account:"+sync_account);
			if(!_calName.equals(mContext.getString(R.string.calendarAccess_allCalendarListentry)))
			{
				if(!sync_account.equals(_calName))
				{
					continue;
				}
			}
			Cursor instanceCursor = mContext.getContentResolver().query(builder.build(),
					dateProjection,null,null, null);
			int eventId = eventCursor.getInt(0);
			
			String sync_account_type = eventCursor.getString(2);
			String sync_id = eventCursor.getString(3);
			String sync_version = eventCursor.getString(4);
			String sync_time = eventCursor.getString(5);
			int sync_local_id = eventCursor.getInt(6);
			int sync_dirty= eventCursor.getInt(7);
			int calendar_id = eventCursor.getInt(8);
			String html_uri = eventCursor.getString(9);
			String title = eventCursor.getString(10);
			String event_location = eventCursor.getString(11);
			String description = eventCursor.getString(12);
			int event_status = eventCursor.getInt(13);
			int self_attendee_status = eventCursor.getInt(14);
			String comments_uri = eventCursor.getString(15);
			int dtstart = eventCursor.getInt(16);
			int dtend = eventCursor.getInt(17);
			String eventTimeZone = eventCursor.getString(18);
			String duration = eventCursor.getString(19);
			int allDay = eventCursor.getInt(20);
			int visibility = eventCursor.getInt(21);
			int transparency = eventCursor.getInt(22);
			int has_alarm = eventCursor.getInt(23);
			int has_extended_properties = eventCursor.getInt(24);
			String rrule = eventCursor.getString(25);
			String exrule = eventCursor.getString(26);
			String exdate = eventCursor.getString(27);
			String original_event = eventCursor.getString(28);
			int original_instance_time = eventCursor.getInt(29);
			int original_all_day = eventCursor.getInt(30);
			int last_date = eventCursor.getInt(31);
			int has_attendee_data = eventCursor.getInt(32);
			int guests_can_modify = eventCursor.getInt(33);
			int guests_can_invite_others = eventCursor.getInt(34);
			int guests_can_see_guests = eventCursor.getInt(35);
			String organizer = eventCursor.getString(36);
			int deleted = eventCursor.getInt(37);
			
			long begin = Long.MAX_VALUE;
			long end = Long.MAX_VALUE;
			
//			Log.e(TAG, "checking times for event:id="+eventId+", title:"+title);
			while(instanceCursor.moveToNext())
			{
				
				int instanceId = instanceCursor.getInt(0);
				
//				Log.e(TAG, "checking instance:"+instanceId);
				if(instanceId==eventId)
				{
					
					begin = instanceCursor.getLong(1);
					end = instanceCursor.getLong(2);
					
//					Log.e(TAG, "IDs match, setting times: begin:"+begin+", end:"+end);
					break;
				}
			}
			if(begin == Long.MAX_VALUE || end == Long.MAX_VALUE)
			{
				Log.e(TAG, "begin or start is incorrect");
			}
				
			
			res.add(new CalendarEvent(	eventId, sync_account, sync_account_type, sync_id, 
								sync_version, sync_time, sync_local_id, 
								sync_dirty, calendar_id, html_uri, title, 
								event_location, description, event_status, 
								self_attendee_status, comments_uri, dtstart, 
								dtend, eventTimeZone, duration, allDay, visibility, 
								transparency, has_alarm, has_extended_properties, 
								rrule, exrule, exdate, original_event, 
								original_instance_time, original_all_day, 
								last_date, has_attendee_data, guests_can_modify, 
								guests_can_invite_others, guests_can_see_guests, 
								organizer, deleted, begin, end));
		}
		return res;
	}
}
