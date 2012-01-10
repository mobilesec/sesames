package at.sesame.fhooe.ezan.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class EzanMeasurement 
{
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

	public String getmId() {
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
