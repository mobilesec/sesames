package at.sesame.fhooe.lib.calendar;

public class CalendarInfo 
{
	private String mName;
	private int mId;
	
	public CalendarInfo(int _id, String _name)
	{
		mId = _id;
		mName = _name;
	}
	
	public int getId()
	{
		return mId;
	}
	
	public String getName()
	{
		return mName;
	}

	@Override
	public String toString() {
		return "CalendarInfo [mName=" + mName + ", mId=" + mId + "]";
	}
	
	

}
