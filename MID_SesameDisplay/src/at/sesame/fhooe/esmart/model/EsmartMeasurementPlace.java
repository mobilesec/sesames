package at.sesame.fhooe.esmart.model;

public class EsmartMeasurementPlace 
{
	private int mId;
	private String mName;
	

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
