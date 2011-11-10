package at.sesame.fhooe.lib.calendar;

import java.util.ArrayList;

public interface ICalendarAccess 
{
	/**
	 * returns the names of all calendars present on the device
	 * @return names of all present calendars
	 */
	public String[] getCalendarNames();
	
	/**
	 * returns the id of a calendar specified by its name
	 * @param _name the name of the calendar
	 * @return the id of the calendar
	 */
	public int getCalendarIdByName(String _name);
	
	/**
	 * returns a list of all CalendarEvents associated wit a passed calendar name
	 * @param _calName the name of the calendar
	 * @return a list of CalendarEvents
	 */
	public ArrayList<CalendarEvent> getEventsFromCalendar(String _calName);
}
