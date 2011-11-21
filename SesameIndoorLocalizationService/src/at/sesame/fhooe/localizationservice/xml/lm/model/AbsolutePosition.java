package at.sesame.fhooe.localizationservice.xml.lm.model;

public class AbsolutePosition 
{
	private double mLatitude;
	private double mLongitude;
	
	public double getLatitude() {
		return mLatitude;
	}
	public void setLatitude(double _latitude) {
		this.mLatitude = _latitude;
	}
	public double getLongitude() {
		return mLongitude;
	}
	public void setLongitude(double _longitude) {
		this.mLongitude = _longitude;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbsolutePosition [mLatitude=");
		builder.append(mLatitude);
		builder.append(", mLongitude=");
		builder.append(mLongitude);
		builder.append("]");
		return builder.toString();
	}
	
	

}
