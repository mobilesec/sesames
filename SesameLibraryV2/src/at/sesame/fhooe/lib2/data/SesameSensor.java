package at.sesame.fhooe.lib2.data;

public class SesameSensor 
{
	public enum SensorType
	{
		light,
		energy,
		humidity,
		temperature
	}
	
	private String mId;
	
	private SensorType mType;
	
	public SesameSensor(String _id, SensorType _type)
	{
		mId = _id;
		mType = _type;
	}

	public String getId() {
		return mId;
	}

	public SensorType getType() {
		return mType;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SesameSensor [mId=");
		builder.append(mId);
		builder.append(", mType=");
		builder.append(mType);
		builder.append("]");
		return builder.toString();
	}

}
