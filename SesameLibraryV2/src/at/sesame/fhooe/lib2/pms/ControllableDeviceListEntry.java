package at.sesame.fhooe.lib2.pms;

import at.sesame.fhooe.lib2.pms.model.ControllableDevice;

public class ControllableDeviceListEntry 
implements IListEntry 
{
	private ControllableDevice mDevice;
	private boolean mSelected = false;
	private boolean mDirty = false;
	
	public ControllableDeviceListEntry(ControllableDevice _cd)
	{
		mDevice = _cd;
	}

	@Override
	public boolean isSeparator() 
	{
		return false;
	}
	
	public ControllableDevice getControllableDevice()
	{
		return mDevice;
	}
	
	public void setSelection(boolean _selected)
	{
		mSelected = _selected;
	}

	public boolean isSelected() 
	{
		return mSelected;
	}

	public void setDirty(boolean _dirty)
	{
		mDirty = _dirty;
	}
	
	public boolean isDirty()
	{
		return mDirty;
	}
}
