package at.sesame.fhooe.pms;

import java.util.Comparator;

import at.sesame.fhooe.lib.pms.model.ControllableDevice;

public class ControllableDeviceComparator implements
		Comparator<ControllableDevice> {

	@Override
	public int compare(ControllableDevice _lhs, ControllableDevice _rhs) 
	{
		if(_lhs.equals(_rhs))
		{
			return 0;
		}
		
		if(_lhs.getIdleSince()<_rhs.getIdleSince())
		{
			return 1;
		}
		else if(_lhs.getIdleSince()>_rhs.getIdleSince())
		{
			return -1;
		}
		else
		{
			return _lhs.getHostname().compareToIgnoreCase(_rhs.getHostname());
		}
	}

}
