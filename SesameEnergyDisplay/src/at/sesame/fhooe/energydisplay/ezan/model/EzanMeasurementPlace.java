package at.sesame.fhooe.energydisplay.ezan.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonProperty;

import android.util.Log;
import at.sesame.fhooe.energydisplay.ezan.EzanDataAccess;
import at.sesame.fhooe.energydisplay.ezan.model.EzanMeasurement.MeasurementType;


public class EzanMeasurementPlace 
{
	private static final String TAG = "EzanMeasurementPlace";
	
	public enum TimePeriod
	{
		hours,
		days,
		all
	}

	@JsonProperty("_id")
	private String mId;

	@JsonProperty("address")
	private String mAddress;

	@JsonProperty("description")
	private String mDescription;

	@JsonProperty("latitude")
	private double mLatitude;

	@JsonProperty("longitude")
	private double mLongitude;

	@JsonProperty("title")
	private String mTitle;

	private ArrayList<EzanMeasurement> mMeasurements;

	private Date mStartDate;
	
	private static final int MEASUREMENTS_PER_HOUR = 4;

	public synchronized ArrayList<EzanMeasurement> getMeasurements()
	{
		return mMeasurements;
	}

	public synchronized ArrayList<EzanMeasurement> updateMeasurements(int _amount, TimePeriod _period )
	{
		mMeasurements = EzanDataAccess.getEzanMeasurements(getID(), getNumberOfMeasurementsForPeriod(_amount, _period));
		//		if(mMeasurements.size()>0)
		//		{
		//			Log.e(TAG, mMeasurements.get(0).getTimeStampDate().toGMTString());
		//		}
		//		for(EzanMeasurement em:mMeasurements)
		//		{
		//			Log.e(TAG, em.getTimeStamp());
		//			Log.e(TAG, em.getTimeStampDate().toGMTString());
		//		}
		//		updateStartDate();
		return mMeasurements;
	}
	
	private int getNumberOfMeasurementsForPeriod(int _amount, TimePeriod _period)
	{
		int measurementsPerPeriod = 0;
		switch(_period)
		{
		case hours:
			measurementsPerPeriod = MEASUREMENTS_PER_HOUR;
			break;
		case days:
			measurementsPerPeriod = MEASUREMENTS_PER_HOUR * 24;
			break;
		case all:
//			Date now = new Date();
//			Date first = new Date(2011, 12, 14, 12, 45, 0);
//			Date diff = new Date(now.getTime()-first.getTime());
//			Log.e(TAG, diff.toGMTString());
			return 0; 
		default:	
			measurementsPerPeriod = 0;
		}
		return measurementsPerPeriod*_amount;
	}

	public double[] getFilteredMeasurements(MeasurementType _type, Date _start, Date _end)
	{
		//		ArrayList<String> timeStamps = new ArrayList<String>();
		//		HashMap<String, Double> res = new HashMap<String, Double>();
		ArrayList<Double> bufferList = new ArrayList<Double>();

		for(EzanMeasurement em:mMeasurements)
		{
			if(em.getTimeStampDate().after(_start)&&em.getTimeStampDate().before(_end))
			{
				switch(_type)
				{
				case humidity:
					bufferList.add(em.getHumidity());
					break;
				case light:
					bufferList.add(em.getLight());
					break;
				case temperature:
					bufferList.add(em.getTemp());
					break;
				case temperature2:
					bufferList.add(em.getTemp2());
					break;
				case voltage:
					bufferList.add(em.getVoltage());
					break;
				}
			}
		}
		//		if(res.keySet().size()!=mMeasurements.size())
		//		{
		//			Log.e(TAG, "!!!number of measurements and size of result don't match!!!");
		//		}
		double[]res = new double[bufferList.size()];
		Log.e(TAG, "size of buffer list:"+bufferList.size());
		for(int i = 0;i<bufferList.size();i++)
		{
			res[i]=bufferList.get(i);
		}
		return res;
	}

	//	private Date updateStartDate()
	//	{
	//		if(null==mMeasurements)
	//		{
	//			return null;
	//		}
	//		Date start = new Date();
	//		for(EzanMeasurement em:mMeasurements)
	//		{
	//			if(em.getTimeStampDate().before(start))
	//			{
	//				start = em.getTimeStampDate();
	//			}
	//		}
	//		mStartDate = start;
	//		return mStartDate;
	//	}

	public Date getStartDate()
	{
		return mStartDate;
	}

	public String getID() {
		return mId;
	}

	public String getAddress() {
		return mAddress;
	}

	public String getDescription() {
		return mDescription;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public String getTitle() {
		return mTitle;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EzanMeasurementPlace [mId=");
		builder.append(mId);
		builder.append(", mAddress=");
		builder.append(mAddress);
		builder.append(", mDescription=");
		builder.append(mDescription);
		builder.append(", mLatitude=");
		builder.append(mLatitude);
		builder.append(", mLongitude=");
		builder.append(mLongitude);
		builder.append(", mTitle=");
		builder.append(mTitle);
		builder.append("]");
		return builder.toString();
	}
}
