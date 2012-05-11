package at.sesame.fhooe.lib2.data;

import java.io.Serializable;
import java.util.ArrayList;

import at.sesame.fhooe.lib2.data.SesameSensor.SensorType;

public class SesameMeasurementPlace
implements Serializable
{
	private static final long serialVersionUID = 328968722349093586L;
	private int mId;
	private String mName;
	private ArrayList<SesameSensor> mSensors = new ArrayList<SesameSensor>();
	
	public SesameMeasurementPlace(String _name)
	{
		this(0,_name);
	}
	
	
	public SesameMeasurementPlace(int _id, String _name)
	{
		mId = _id;
		mName = _name;
	}
	
	
	public ArrayList<SesameSensor> getEnergySensors()
	{
		return getSensorsByType(SensorType.energy);
	}
	
	public ArrayList<SesameSensor> getHumiditySensors()
	{
		return getSensorsByType(SensorType.humidity);
	}
	
	public ArrayList<SesameSensor> getTemperatureSensors()
	{
		return getSensorsByType(SensorType.temperature);
	}
	
	public ArrayList<SesameSensor> getLightSensors()
	{
		return getSensorsByType(SensorType.light);
	}
	
	public ArrayList<SesameSensor> getSensorsByType(SensorType _st)
	{
		ArrayList<SesameSensor> res = new ArrayList<SesameSensor>();
		for(SesameSensor ss:mSensors)
		{
			if(ss.getType().equals(_st))
			{
				res.add(ss);
			}
		}
		return res;
	}
	
	
	
	public int getId() {
		return mId;
	}
	public void setId(int _id) {
		this.mId = _id;
	}
	public String getName() {
		return mName;
	}
	public void setName(String _name) {
		this.mName = _name;
	}
	
	public ArrayList<SesameSensor> getSensors()
	{
		return mSensors;
	}
	
	public void addSensor(SesameSensor _sensor)
	{
		mSensors.add(_sensor);
	}
	
	public void setSensors(ArrayList<SesameSensor> _sensors)
	{
		mSensors = _sensors;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SesameMeasurementPlace [mName=");
		builder.append(mName);
		builder.append(", mSensors=");
		builder.append(mSensors);
		builder.append("]");
		return builder.toString();
	}
	
	

}
