package at.sesame.fhooe.lib;

import android.content.Context;
import at.sesame.fhooe.lib.calendar.CalendarAccessHC;
import at.sesame.fhooe.lib.calendar.CalendarAccessICS;
import at.sesame.fhooe.lib.calendar.ICalendarAccess;
import at.sesame.fhooe.lib.vpn.IVpnAccess;
import at.sesame.fhooe.lib.vpn.VpnAccess;

public class SesameFactory 
{
	private static final int PRE_HONEYCOMB = 0;
	private static final int HONEYCOMB = 1;
	private static final int ICECREAM_SANDWICH = 2;
	
	public static ICalendarAccess getCalendarAccess(Context _c)
	{
		switch (getVersion()) {
		case PRE_HONEYCOMB:
		case HONEYCOMB:
			return new CalendarAccessHC(_c);
		case ICECREAM_SANDWICH:
			return new CalendarAccessICS(_c);

		default:
			return null;
		}
	}
	
	public static IVpnAccess getVpnAccess()
	{
		switch (getVersion()) {
		case PRE_HONEYCOMB:
		case HONEYCOMB:
			return new VpnAccess();
		case ICECREAM_SANDWICH:
//			return new CalendarAccessICS(_c);

		default:
			return null;
		}
	}
	
	private static int getVersion()
	{
		int res = -1;
		switch(android.os.Build.VERSION.SDK_INT)
		{
		case android.os.Build.VERSION_CODES.BASE:
		case android.os.Build.VERSION_CODES.BASE_1_1:
		case android.os.Build.VERSION_CODES.CUPCAKE:
		case android.os.Build.VERSION_CODES.DONUT:
		case android.os.Build.VERSION_CODES.ECLAIR:
		case android.os.Build.VERSION_CODES.ECLAIR_0_1:
		case android.os.Build.VERSION_CODES.ECLAIR_MR1:
		case android.os.Build.VERSION_CODES.FROYO:
		case android.os.Build.VERSION_CODES.GINGERBREAD:
		case android.os.Build.VERSION_CODES.GINGERBREAD_MR1:
			res = PRE_HONEYCOMB;
			break;
		case android.os.Build.VERSION_CODES.HONEYCOMB:
		case android.os.Build.VERSION_CODES.HONEYCOMB_MR1:
		case android.os.Build.VERSION_CODES.HONEYCOMB_MR2:
			res = HONEYCOMB;
			break;
		case android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH:
			res = ICECREAM_SANDWICH;
		}
		return res;
	}

}
