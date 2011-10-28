package at.sesame.fhooe.pms;

import at.sesame.fhooe.lib.pms.model.ControllableDevice;

public class ControllableDeviceListEntry 
implements IListEntry 
{
	private ControllableDevice mDevice;
	
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

}
