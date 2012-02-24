package at.sesame.fhooe.lib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EsmartDateProvider 
{	
	private static final SimpleDateFormat mFormat= new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * returns the string representation of the current date in the appropriate format for the URL
	 * @return string representation of the current date
	 */
	public static String getTodayUrlTimeString()
	{
		return getUrlTimeString(new GregorianCalendar());
	}
	
	/**
	 * returns the string representation of the date X days ago
	 * in the appropriate format for the URL
	 * @param _numDaysAgo number of days before the current day
	 * @return string representation of X days ago
	 */
	public static String getUrlTimeStringForXDaysAgo(int _numDaysAgo)
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -1*_numDaysAgo);
		return getUrlTimeString(cal);
	}
	
	/**
	 * returns the string representation of the passed GregorianCalendar
	 *  in the appropriate format for the URL
	 * @param _cal the calendar to use the date from
	 * @return string representation of the passed date
	 */
	public static String getUrlTimeString(GregorianCalendar _gc)
	{
		return mFormat.format(_gc.getTime());
	}
	
	public static GregorianCalendar createGregorianCalendar(int _year, int _month, int _day)
	{
		return new GregorianCalendar(_year, _month-1, _day);
	}
	
	public static Date getDateFromEsmartString(String _esmart)
	{
		try {
			return mFormat.parse(_esmart);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static GregorianCalendar getGregorianCalendarFromEsmartString(String _esmart)
	{
		GregorianCalendar res = new GregorianCalendar();
		res.setTime(getDateFromEsmartString(_esmart));
		return res;
	}
	
}
