package at.sesame.fhooe.energydisplay.ezan.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.mrbean.MrBeanModule;

import android.util.Log;

public class EzanMeasurement 
{
	private static final String TAG = "EzanMeasurement";
	//2012-01-11T14:02:55+00:00
	private SimpleDateFormat mFormat = 
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
	public enum MeasurementType
	{
		humidity,
		light,
		temperature,
		temperature2,
		voltage,
		nA
	}
	
	@JsonProperty("_id")
	private String mId;
	
	@JsonProperty("humidity")
	private double mHumidity;
	
	@JsonProperty("light")
	private double mLight;
	
	@JsonProperty("measured_at")
	private String mTimeStamp;
	
	@JsonProperty("meter_id")
	private String mMeterId;
	
	@JsonProperty("place_id")
	private String mPlaceId;
	
	@JsonProperty("sleeptime")
	private String mSleepTime;
	
	@JsonProperty("temperature")
	private double mTemp;
	
	@JsonProperty("temperature2")
	private double mTemp2;
	
	@JsonProperty("voltage")
	private double mVoltage;
	
	

	public String getID() {
		return mId;
	}

	public double getHumidity() {
		return mHumidity;
	}

	public double getLight() {
		return mLight;
	}

	public String getTimeStamp() {
		return mTimeStamp;
	}
	public String getMeterId() {
		return mMeterId;
	}
	public String getPlaceId() {
		return mPlaceId;
	}

	public String getSleepTime() {
		return mSleepTime;
	}

	public double getTemp() {
		return mTemp;
	}

	public double getTemp2() {
		return mTemp2;
	}

	public double getVoltage() {
		return mVoltage;
	}
	
	public Date getTimeStampDate()
	{
		String shortTimeString = getTimeStamp();//.substring(0, getTimeStamp().indexOf('+'));
//		Log.e(TAG, "short time string="+shortTimeString);
		try {
			return mFormat.parse(shortTimeString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EzanMeasurement [mId=");
		builder.append(mId);
		builder.append(", mHumidity=");
		builder.append(mHumidity);
		builder.append(", mLight=");
		builder.append(mLight);
		builder.append(", mTimeStamp=");
		builder.append(mTimeStamp);
		builder.append(", mMeterId=");
		builder.append(mMeterId);
		builder.append(", mPlaceId=");
		builder.append(mPlaceId);
		builder.append(", mSleepTime=");
		builder.append(mSleepTime);
		builder.append(", mTemp=");
		builder.append(mTemp);
		builder.append(", mTemp2=");
		builder.append(mTemp2);
		builder.append(", mVoltage=");
		builder.append(mVoltage);
		builder.append("]");
		return builder.toString();
	}
}
