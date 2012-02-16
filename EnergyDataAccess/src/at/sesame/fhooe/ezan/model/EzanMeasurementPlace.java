package at.sesame.fhooe.ezan.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonProperty;

import android.util.Log;
import at.sesame.fhooe.ezan.model.EzanMeasurement.MeasurementType;
import at.sesame.fhooe.ezan.service.EzanDataAccess;

public class EzanMeasurementPlace 
{
	private static final String TAG = "EzanMeasurementPlace";
	
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
	
	public synchronized ArrayList<EzanMeasurement> getMeasurements()
	{
		return mMeasurements;
	}
	
	public synchronized ArrayList<EzanMeasurement> updateMeasurements()
	{
		mMeasurements = EzanDataAccess.getEzanMeasurements(getID());
//		if(mMeasurements.size()>0)
//		{
//			Log.e(TAG, mMeasurements.get(0).getTimeStampDate().toGMTString());
//		}
//		for(EzanMeasurement em:mMeasurements)
//		{
//			Log.e(TAG, em.getTimeStamp());
//			Log.e(TAG, em.getTimeStampDate().toGMTString());
//		}
		updateStartDate();
		return mMeasurements;
	}
	
	public HashMap<String, Double> getDisplayedValues(MeasurementType _type)
	{
//		ArrayList<String> timeStamps = new ArrayList<String>();
		HashMap<String, Double> res = new HashMap<String, Double>();
		
		for(EzanMeasurement em:mMeasurements)
		{
			String timeStamp = em.getTimeStamp();
			switch(_type)
			{
			case humidity:
				res.put(timeStamp, em.getHumidity());
				break;
			case light:
				res.put(timeStamp, em.getLight());
				break;
			case temperature:
				res.put(timeStamp, em.getTemp());
				break;
			case temperature2:
				res.put(timeStamp, em.getTemp2());
				break;
			case voltage:
				res.put(timeStamp, em.getVoltage());
				break;
			}
		}
		if(res.keySet().size()!=mMeasurements.size())
		{
			Log.e(TAG, "!!!number of measurements and size of result don't match!!!");
		}
		return res;
	}
	
	private Date updateStartDate()
	{
		if(null==mMeasurements)
		{
			return null;
		}
		Date start = new Date();
		for(EzanMeasurement em:mMeasurements)
		{
			if(em.getTimeStampDate().before(start))
			{
				start = em.getTimeStampDate();
			}
		}
		mStartDate = start;
		return mStartDate;
	}
	
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
