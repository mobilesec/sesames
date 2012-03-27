package at.sesame.fhooe.lib2.pms;

import android.content.Context;
import at.sesame.fhooe.lib2.R;

public class SeparatorListEntry 
implements IListEntry 
{
	public enum ListType
	{
		active,
		inactive
	}
	private String mTitle;
	
	private ListType mType;
	
	private boolean mSelected = false;
	
	public SeparatorListEntry(Context _c, ListType _type, int _numDevs)
	{
		mType = _type;
		switch(mType)
		{
			case active:
				mTitle = _c.getString(R.string.pms_activeDeviceSeparatorText)+_numDevs+")";
				break;
			case inactive:
				mTitle = _c.getString(R.string.pms_inactiveDeviceSeparatorText)+_numDevs+")";
				break;
		}
//		mTitle= _title;
	}

	@Override
	public boolean isSeparator() 
	{
		return true;
	}
	
	public ListType getType()
	{
		return mType;
	}
	
	public String getTitle()
	{
		return mTitle;
	}

	public boolean isSelected() {
		return mSelected;
	}

	public void setSelected(boolean _selected) {
		this.mSelected = _selected;
	}
	
	
}
