package at.sesame.fhooe.lib.calendar;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.EventsEntity;
import android.util.Log;
import at.sesame.fhooe.lib.calendar.CalendarEvent;
import at.sesame.fhooe.lib.calendar.CalendarInfo;

public class CalendarAccessICS
implements ICalendarAccess
{
	private static final String TAG = "CAlendarAccessICS";
	private Context mContext;
	
	public CalendarAccessICS(Context _ctx)
	{
		mContext = _ctx;
	}
	private void getColumnData(Cursor cur)
	{ 
		if (cur.moveToFirst()) 
		{
			int idIdx = cur.getColumnIndex(EventsEntity._ID);

			Log.e(TAG, "column count = "+cur.getColumnCount());
			do 
			{
				Log.e(TAG, "**********id = "+cur.getString(idIdx));
				for(int i = 0;i<cur.getColumnCount();i++)
				{
					String colName = cur.getColumnName(i);
					String colVal = cur.getString(i);

					Log.e(TAG+i, "name="+colName+", value="+colVal);
				}
			} while (cur.moveToNext());
		}
	}
	
	public ArrayList<CalendarInfo> getAllCalendars()
	{
		Cursor calendarCursor = mContext.getContentResolver().query(Calendars.CONTENT_URI, null, null, null, null);
		ArrayList<CalendarInfo> cals = new ArrayList<CalendarInfo>();
		if (calendarCursor.moveToFirst()) 
		{
			do 
			{
				cals.add(new CalendarInfo(	calendarCursor.getInt(calendarCursor.getColumnIndex(Calendars._ID)), 
											calendarCursor.getString(calendarCursor.getColumnIndex(Calendars.ACCOUNT_NAME))));
			} while (calendarCursor.moveToNext());
		}
		return cals;
	}
	
	public ArrayList<CalendarEvent> getEventsFromCalendar(String _calName)
	{
		int calId = getCalendarIdByName(_calName);
		return getCalendarEventsByCalendarId(calId);
	}
	
	@Override
	public int getCalendarIdByName(String _calName) 
	{
		ArrayList<CalendarInfo> allCals = getAllCalendars();
		for(CalendarInfo ci:allCals)
		{
			if(ci.getName().equals(_calName))
			{
				return ci.getId();
			}
		}
		return -1;
	}
	
	public String[]getCalendarNames()
	{
		
		ArrayList<CalendarInfo> cals = getAllCalendars();
		String[] calNames = new String[cals.size()];
		
		for(int i = 0;i<cals.size();i++)
		{
			calNames[i]=cals.get(i).getName();
		}
		return calNames;
	}

	private ArrayList<CalendarEvent> getCalendarEventsByCalendarId(int _calId)
	{
		ArrayList<CalendarEvent> filteredEvents = new ArrayList<CalendarEvent>();
		ArrayList<CalendarEvent> allEvents = getAllCalendarEvents();
		
		for(CalendarEvent ce:allEvents)
		{
			if(ce.getCalendarId()==_calId)
			{
				filteredEvents.add(ce);
			}
		}
		return filteredEvents;
	}


	private ArrayList<CalendarEvent> getAllCalendarEvents()
	{
		Cursor eventCursor = mContext.getContentResolver().query(CalendarContract.EventsEntity.CONTENT_URI, null, null, null, null);
		ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>();
		if (eventCursor.moveToFirst()) 
		{
			do 
			{
				CalendarEvent ce = new CalendarEvent();

				ce.setId(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity._ID)));
				ce.setAccessLevel(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.ACCESS_LEVEL)));
				ce.setAllDay(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.ALL_DAY)));
				ce.setAvaialbility(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.AVAILABILITY)));
				ce.setCalendarId(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.CALENDAR_ID)));
				ce.setCanInviteOthers(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.GUESTS_CAN_INVITE_OTHERS)));
				ce.setDescription(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.DESCRIPTION)));
				ce.setDtend(eventCursor.getLong(eventCursor.getColumnIndex(EventsEntity.DTEND)));
				ce.setDtstart(eventCursor.getLong(eventCursor.getColumnIndex(EventsEntity.DTSTART)));
				ce.setDuration(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.DURATION)));
				ce.setEventColor(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.EVENT_COLOR)));
				ce.setEventEndTimezone(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.EVENT_END_TIMEZONE)));
				ce.setEventLocation(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.EVENT_LOCATION)));
				ce.setEventTimeZone(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.EVENT_TIMEZONE)));
				ce.setExdate(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.EXDATE)));
				ce.setExrule(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.EXRULE)));
				ce.setCanInviteOthers(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.GUESTS_CAN_INVITE_OTHERS)));
				ce.setGuestsCanModify(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.GUESTS_CAN_MODIFY)));
				ce.setGuestsCanSeeGuests(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.GUESTS_CAN_SEE_GUESTS)));
				ce.setHasAlarm(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.HAS_ALARM)));
				ce.setHasAttendeeData(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.HAS_ATTENDEE_DATA)));
				ce.setHasExtendedProperties(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.HAS_EXTENDED_PROPERTIES)));
				ce.setLastDate(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.LAST_DATE)));
				ce.setLastSynced(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.LAST_SYNCED)));
				ce.setOrganizer(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.ORGANIZER)));
				ce.setOriginalAllDay(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.ORIGINAL_ALL_DAY)));
				ce.setOriginalAllDay(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.ORIGINAL_ALL_DAY)));
				ce.setOriginalInstanceTime(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.ORIGINAL_INSTANCE_TIME)));
				ce.setOriginalSyncId(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.ORIGINAL_SYNC_ID)));
				ce.setRDate(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.RDATE)));
				ce.setRRule(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.RRULE)));
				ce.setSelfAttendeeStatus(eventCursor.getInt(eventCursor.getColumnIndex(EventsEntity.SELF_ATTENDEE_STATUS)));
				ce.setStatus(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.STATUS)));
				ce.setTitle(eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.TITLE)));

				events.add(ce);

			} while (eventCursor.moveToNext());

		}
		return events;
	}

}
