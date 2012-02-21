package at.sesame.fhooe.midsd.demo;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import org.achartengine.model.TimeSeries;

import android.util.Log;

public class DataSimulator 
{
	private static final String TAG = "DataSimulator";
	private static final int DEFAULT_TIME_UNIT = Calendar.MINUTE;
	private static final int DEFAULT_INCREMENTATION_STEP = 15;
	
	public static TimeSeries createTimeSeries(String _title, Date _from, int _numEntries)
	{
		return createTimeSeries(_title, _from, _numEntries, DEFAULT_TIME_UNIT, DEFAULT_INCREMENTATION_STEP);
	}
	
	public static TimeSeries createTimeSeries(String _title, Date _from, int _numEntries, int _timeUnitToIncrement, int _timeUnitIncrementationStep)
	{
		TimeSeries res = new TimeSeries(_title);
		Random r = new Random();
//		new GregorianCalendar();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(_from);
		
		for(int i = 0;i<_numEntries;i++)
		{
			Date d = cal.getTime();
			double val = r.nextDouble()*1000;
			
			res.add(d, val);
			cal.add(_timeUnitToIncrement, _timeUnitIncrementationStep);
		}
		return res;
	}

}
