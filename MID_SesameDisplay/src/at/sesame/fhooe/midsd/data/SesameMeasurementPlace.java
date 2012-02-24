package at.sesame.fhooe.midsd.data;

public class SesameMeasurementPlace 
{
	private int mId;
	private String mName;
	
	public SesameMeasurementPlace(int _id, String _name)
	{
		mId = _id;
		mName = _name;
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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeasurementPlace [mId=");
		builder.append(mId);
		builder.append(", mName=");
		builder.append(mName);
		builder.append("]");
		return builder.toString();
	}

}
