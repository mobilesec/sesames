package at.sesame.fhooe.lib2.pms;

public class PmsUiInfo 
{
	private boolean mSelected;
	private boolean mDirty;
	
	public PmsUiInfo(boolean _selected, boolean _dirty)
	{
		mSelected = _selected;
		mDirty = _dirty;
	}
	
	public boolean isSelected()
	{
		return mSelected;
	}
	
	public boolean isDirty()
	{
		return mDirty;
	}
}
