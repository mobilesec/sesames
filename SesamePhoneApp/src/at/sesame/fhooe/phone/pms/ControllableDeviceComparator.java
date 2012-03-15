/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 11/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.phone.pms;

import java.util.Comparator;

import at.sesame.fhooe.lib.pms.model.ControllableDevice;

/**
 * this class implements the Comparator Interface to be able to compare ControllableDevices
 * @author Peter Riedl
 *
 */
public class ControllableDeviceComparator 
implements Comparator<ControllableDevice> 
{

	@Override
	public int compare(ControllableDevice _lhs, ControllableDevice _rhs) 
	{	
		if(null==_lhs||null==_rhs)
		{
			return -1;
		}
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
