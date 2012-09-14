package at.sesame.fhooe.lib2;


import java.util.Date;
import java.util.GregorianCalendar;

import android.graphics.Color;

public class Constants 
{
	// every room is color-coded
	// historical data of a room will be shades of the base color
	public final static int COLOR_EDV1 = Color.GREEN;
	public final static int COLOR_EDV3 = Color.CYAN;
	public final static int COLOR_EDV6 = Color.MAGENTA;
	
	public static Date getStartDate() {
		GregorianCalendar cal = new GregorianCalendar(2011, 10, 04);
		return cal.getTime();
	}
	
	public static Date getTrialStartDate()
	{
		GregorianCalendar cal = new GregorianCalendar(2012, 8, 17);
		return cal.getTime();
	}
}
