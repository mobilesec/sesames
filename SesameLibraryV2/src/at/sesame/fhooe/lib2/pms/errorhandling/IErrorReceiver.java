/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib2.pms.errorhandling;

public interface IErrorReceiver 
{
	public enum RequestType
	{
		getStatus,
		extendedStatus, 
		poweroff, 
		wakeup,
		unknown
	}
	/**
	 * called by ErrorForwarder when it is informed about an error. 
	 * @param _msg the message describing the error
	 */
	public void notifyError(RequestType _reqType, String _mac, int _code, String _msg);

}
