package at.sesame.fhooe.lib2.esmart.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;

public class EsmartDataRow 
{
	private static final String TAG = "DataRow";
	private static final String URL_TIME_STRING_SEPARATOR = "-";
//	private static final SimpleDateFormat mFormat= new SimpleDateFormat("yyyy-MM-dd");
	//<TimeStamp>11/25/2011 2:30:00 AM</TimeStamp><DataValue>44</DataValue>
	private String mTimeStamp;
	private double mDataValue;
	public String getmTimeStamp() {
		return mTimeStamp;
	}
	public void setTimeStamp(String _timeStamp) {
		this.mTimeStamp = _timeStamp;
	}
	public double getDataValue() {
		return mDataValue;
	}
	public void setDataValue(double _dataValue) {
		this.mDataValue = _dataValue;
	}
	
	public Date getDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Vienna"));
//		String[] zones = TimeZone.getAvailableIDs();
//		for(String s:zones)
//		{
//			Log.e(TAG, s);
//		}
//		SimpleDateFormat sdf = new SimpleDateFormat("M/d/y H:m:s a");
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");
//		DateFormat df = new DateFormat();
//		df.
		try {
			String corruptedTime = getmTimeStamp();
			String readableTime = corruptedTime;
			
//			if(	corruptedTime.toLowerCase().endsWith("am")||
//				corruptedTime.toLowerCase().endsWith("pm"))
//			{
//				readableTime = corruptedTime.substring(0, corruptedTime.length()-3);
//			}
			return sdf.parse(readableTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
//	/**
//	 * returns the string representation of the current date in the appropriate format for the URL
//	 * @return string representation of the current date
//	 */
//	public static String getUrlTimeString()
//	{
//		GregorianCalendar gc = new GregorianCalendar();
////		gc.add(Calendar.MONTH, 1);
////		return getUrlTimeString(gc);
//		return mFormat.format(gc.getTime());
//	}
	
//	/**
//	 * returns the string representation of the passed date in the appropriate format for the URL
//	 * @param _cal the calendar to use the date from
//	 * @return string representation of the passed date
//	 */
//	public static String getUrlTimeString(GregorianCalendar _gc)
//	{
////		Log.e(TAG, _cal.toString());
//		
////		int year = _cal.get(Calendar.YEAR);
////		int month = _cal.get(Calendar.MONTH);
//////		Log.e(TAG, "month="+month);
////		int day = _cal.get(Calendar.DAY_OF_MONTH);
//		_gc.add(Calendar.MONTH, 1);
////		return getUrlTimeString(year, month, day);
//		String res =  mFormat.format(_gc.getTime());
//		Log.e(TAG, "++++++++++++"+res);
//		return res;
//	}
	
//	public static String getUrlTimeString(int _year, int _month, int _day)
//	{
////		Log.e(TAG, "year="+_year+", day ="+_day+", month="+_month);
//		StringBuilder sb = new StringBuilder();
//		sb.append(_year);
//		sb.append(URL_TIME_STRING_SEPARATOR);
//		if(_month<10)
//		{
//			sb.append(0);
//		}
//		sb.append(_month);
//		sb.append(URL_TIME_STRING_SEPARATOR);
//		if(_day<10)
//		{
//			sb.append(0);
//		}
//		sb.append(_day);
//		Log.e(TAG, "result="+sb.toString());
//		return sb.toString();
////		String res = mFormat.format(new Date(new GregorianCalendar(_year, _month, _year).getTimeInMillis()));
////		Log.e(TAG, "result from date conversion:"+res);
////		return res;
//	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataRow [mTimeStamp=");
		builder.append(mTimeStamp);
		builder.append(", mDataValue=");
		builder.append(mDataValue);
		builder.append(", "+getDate().toGMTString());
		builder.append("]");
		return builder.toString();
	}
	
	

}
