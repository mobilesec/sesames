package at.sesame.fhooe.lib2.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateHelper 
{
	public static String convertMStoReadableString(double _millis, boolean _addMillis)
	{
		//		long seconds = _millis/1000;
		double milli = _millis; 
		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		while(milli-3600000>0)
		{
			hours++;
			milli-=3600000;
		}

		while(milli-60000>0)
		{
			minutes++;
			milli-=60000;
		}

		while(milli-1000>0)
		{
			seconds++;
			milli-=1000;
		}

		StringBuilder res = new StringBuilder();
		if(hours>0)
		{
			res.append(hours);
			res.append("h");
		}

		if(minutes>0)
		{
			res.append(" ");
			res.append(minutes);
			res.append("m");
		}

		if(seconds>0)
		{
			res.append(" ");
			res.append(seconds);
			res.append("s");
		}
		if(_addMillis)
		{
			if(milli>0)
			{
				res.append(" ");
				res.append(milli);
				res.append("ms");
			}
		}

		return res.toString();
	}
	
	public static String convertMStoShortReadableString(double _millis)
	{
		//		long seconds = _millis/1000;
		double milli = _millis; 
		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		while(milli-3600000>0)
		{
			hours++;
			milli-=3600000;
		}

		while(milli-60000>0)
		{
			minutes++;
			milli-=60000;
		}

//		while(milli-1000>0)
//		{
//			seconds++;
//			milli-=1000;
//		}

		StringBuilder res = new StringBuilder();
		res.append(hours);
		res.append(",");
		res.append(minutes/60);
		res.append(" Stunden");
		return res.toString();
	}

	public static Date getFirstDateToday()
	{
		GregorianCalendar res = new GregorianCalendar();
		res.set(Calendar.HOUR_OF_DAY, 0);
		res.set(Calendar.MINUTE, 0);
		res.set(Calendar.SECOND, 0);
		return res.getTime();
	}
	
	public static Date getFirstDateXDaysAgo(int _daysAgo)
	{
		GregorianCalendar res = new GregorianCalendar();
		res.setTime(getFirstDateToday());
		res.add(Calendar.DATE, _daysAgo*-1);
		return res.getTime();
	}
	
	public static String getTodaysWeekDayName()
	{
		GregorianCalendar cal = new GregorianCalendar();
		return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.GERMAN);
	}
}
