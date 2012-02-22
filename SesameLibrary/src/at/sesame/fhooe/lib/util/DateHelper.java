package at.sesame.fhooe.lib.util;

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

}
