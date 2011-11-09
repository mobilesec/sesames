package at.sesame.fhooe.pms.list;

public class SeparatorListEntry 
implements IListEntry 
{
	private String mTitle;
	
	public SeparatorListEntry(String _title)
	{
		mTitle= _title;
	}

	@Override
	public boolean isSeparator() 
	{
		return true;
	}
	
	public String getTitle()
	{
		return mTitle;
	}
}
