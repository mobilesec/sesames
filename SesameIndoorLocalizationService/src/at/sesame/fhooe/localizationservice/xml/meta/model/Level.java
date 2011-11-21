package at.sesame.fhooe.localizationservice.xml.meta.model;

public class Level
extends LocalizationMechanismContainer
{
	private int mId;
	private String mName;
	private int mNr;
	

	public int getID() {
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

	public int getLevelNumber() {
		return mNr;
	}

	public void setLevelNumber(int _nr) {
		this.mNr = _nr;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Level [mId=");
		builder.append(mId);
		builder.append(", mName=");
		builder.append(mName);
		builder.append(", mNr=");
		builder.append(mNr);
		builder.append(", getLocalizationMechanisms()=");
		builder.append(getLocalizationMechanisms());
		builder.append("]");
		return builder.toString();
	}
	
	
}
