/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms.errorhandling;

public interface IErrorReceiver 
{
	/**
	 * called by ErrorForwarder when it is informed about an error. 
	 * @param _msg the message describing the error
	 */
	public void notifyError(String _msg);

}
