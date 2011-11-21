package at.sesame.fhooe.localizationservice.xml.meta.model;

public class Building
extends LocalizationMechanismContainer
{
	private int mId;
	
	private String mName;
	
	

	public int getmId() {
		return mId;
	}

	public void setID(int _id) {
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
		builder.append("Building [mId=");
		builder.append(mId);
		builder.append(", mName=");
		builder.append(mName);
		builder.append(", getLocalizationMechanisms()=");
		builder.append(getLocalizationMechanisms());
		builder.append("]");
		return builder.toString();
	}

	

}
