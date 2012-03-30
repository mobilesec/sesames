package at.sesame.fhooe.lib2;


import java.util.Date;
import java.util.GregorianCalendar;

public class Constants 
{
	public static Date getStartDate() {
		GregorianCalendar cal = new GregorianCalendar(2011, 10, 04);
		return cal.getTime();
	}
}
