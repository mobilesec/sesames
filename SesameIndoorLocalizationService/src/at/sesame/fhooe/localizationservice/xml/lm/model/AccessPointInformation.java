package at.sesame.fhooe.localizationservice.xml.lm.model;

public class AccessPointInformation 
{
	private String mBSSID;
	private double mAvgRSSI;
	
	public String getBSSID() {
		return mBSSID;
	}
	public void setBSSID(String _BSSID) {
		this.mBSSID = _BSSID;
	}
	public double getAvgRSSI() {
		return mAvgRSSI;
	}
	public void setAvgRSSI(double _avgRSSI) {
		this.mAvgRSSI = _avgRSSI;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccessPointInformation [mBSSID=");
		builder.append(mBSSID);
		builder.append(", mAvgRSSI=");
		builder.append(mAvgRSSI);
		builder.append("]");
		return builder.toString();
	}
}
