package at.sesame.fhooe.midsd;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import at.sesame.fhooe.midsd.data.provider.EsmartDateHelper;
import at.sesame.fhooe.midsd.ui.MeterWheelFragment;

public class TestActivity
extends FragmentActivity
{
	private static final String TAG = "TESTACTIVITY";
	private Handler mUiHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		testView();
	}
	
	private void testView()
	{
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.contentFrame, new MeterWheelFragment(getApplicationContext(), mUiHandler,  "asdf", 20.0f,16.0f, 100, 5, 0, true));
		ft.commit();
	}
	
	private void testDate()
	{
		Date nowDate = new Date();
		GregorianCalendar nowGC = new GregorianCalendar(TimeZone.getTimeZone("Europe/Vienna"));
		GregorianCalendar yesterday = new GregorianCalendar(TimeZone.getTimeZone("Europe/Vienna"));
		yesterday.roll(Calendar.DAY_OF_MONTH, false);
		
		String[] timeZones = TimeZone.getAvailableIDs();
		
//		for(String zone:timeZones)
//		{
//			Log.e(TAG, zone);
//		}
		Log.e(TAG, "date:"+nowDate);
		Log.e(TAG, "calendar:"+nowGC.getTime());
		Log.e(TAG, "equal:"+nowDate.equals(nowGC.getTime()));
		Log.e(TAG, "yesterday:"+yesterday.getTime());
		
		String urlStringNow = EsmartDateHelper.getTodayUrlTimeString();
		String urlYesterday = EsmartDateHelper.getUrlTimeStringForXDaysAgo(1);
		GregorianCalendar tomorrow = new GregorianCalendar();
		tomorrow.roll(Calendar.DAY_OF_MONTH, 1);
		String urlFuture = EsmartDateHelper.getUrlTimeString(tomorrow);
		
		Log.e(TAG, "urlNow="+urlStringNow);
		Log.e(TAG, "urlYesterday="+urlYesterday);
		Log.e(TAG, "urlTomorrow="+urlFuture);
		
		GregorianCalendar gc = EsmartDateHelper.createGregorianCalendar(2012, 12, 2);
		Log.e(TAG, "custom gc="+EsmartDateHelper.getUrlTimeString(gc));
	}

}
