package at.sesame.fhooe.lib2.data;

public class SesameNotification 
{
	public enum NotificationType
	{
		Type40,
		Type60,
		Type80
	}
	
	private NotificationType mType;
	private String mMac = "";
	
	public SesameNotification(NotificationType _type, String _mac)
	{
		mType = _type;
		mMac = _mac;
	}

	public NotificationType getType() {
		return mType;
	}

	public String getMac() {
		return mMac;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mMac == null) ? 0 : mMac.hashCode());
		result = prime * result + ((mType == null) ? 0 : mType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SesameNotification other = (SesameNotification) obj;
		if (mMac == null) {
			if (other.mMac != null)
				return false;
		} else if (!mMac.equals(other.mMac))
			return false;
		if (mType != other.mType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SesameNotification [mType=");
		builder.append(mType);
		builder.append(", mMac=");
		builder.append(mMac);
		builder.append("]");
		return builder.toString();
	}
}
