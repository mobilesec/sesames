package at.sesame.fhooe.lib2.data;

import java.io.Serializable;
import java.util.Date;

public class EnhancedSesameNotification
implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3287555789336282940L;
	private SesameMeasurementPlace mPlace;
	private Date mTimeStamp;
	private SesameSensor mSensor;
	private String mMessage;
	
	public EnhancedSesameNotification(SesameMeasurementPlace _mp,
			Date _timeStamp, SesameSensor _sensor, String _message)
	{
		this.mPlace = _mp;
		this.mTimeStamp = _timeStamp;
		this.mSensor = _sensor;
		this.mMessage = _message;
	}

	public SesameMeasurementPlace getMeasurementPlace() {
		return mPlace;
	}

	public Date getTimeStamp() {
		return mTimeStamp;
	}

	public SesameSensor getSensor() {
		return mSensor;
	}

	public String getMessage() {
		return mMessage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EnhancedSesameNotification [mPlace=");
		builder.append(mPlace);
		builder.append(", mTimeStamp=");
		builder.append(mTimeStamp);
		builder.append(", mSensor=");
		builder.append(mSensor);
		builder.append(", mMessage=");
		builder.append(mMessage);
		builder.append("]");
		return builder.toString();
	}
}
