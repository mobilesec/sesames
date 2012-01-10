package at.sesame.fhooe.ezan.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class EzanMeasurementPlace 
{
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
