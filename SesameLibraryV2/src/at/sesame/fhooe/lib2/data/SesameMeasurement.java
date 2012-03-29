package at.sesame.fhooe.lib2.data;

import java.util.Date;

public class SesameMeasurement 
{
	private Date mTimeStamp;
	private double mVal;
	
	public SesameMeasurement(Date _timeStamp, double _val) {
		super();
		this.mTimeStamp = _timeStamp;
		this.mVal = _val;
	}

	public Date getTimeStamp() {
		return mTimeStamp;
	}

	public void setTimeStamp(Date _timeStamp) {
		this.mTimeStamp = _timeStamp;
	}

	public double getVal() {
		return mVal;
	}

	public void setVal(double _val) {
		this.mVal = _val;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SesameMeasurement [mTimeStamp=");
		builder.append(mTimeStamp);
		builder.append(", mVal=");
		builder.append(mVal);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
