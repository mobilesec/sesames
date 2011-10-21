/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms.errorhandling;

import java.util.ArrayList;
/**
 * this class is notified about any errors resulting from http calls and informs
 * all registered listeners about those errors
 *
 */
public class ErrorForwarder 
{
	/**
	 * a list of all receivers of errors
	 */
	private ArrayList<IErrorReceiver> mRecvs = new ArrayList<IErrorReceiver>();
	
	/**
	 * instance of error forwarder (singleton)
	 */
	private static ErrorForwarder mInstance;
	
	/**
	 * provides the actual ErrorForwarder instance
	 * @return the actual ErrorForwarder instance
	 */
	public static ErrorForwarder getInstance()
	{
		if(null==mInstance)
		{
			mInstance = new ErrorForwarder();
		}
		return mInstance;
	}
	
	/**
	 * registers a new IErrorReceiver to be notified about errors
	 * @param _recv the receiver to be added
	 */
	public void register(IErrorReceiver _recv)
	{
		mRecvs.add(_recv);
	}
	
	/**
	 * unregisters a IErrorReceiver
	 * @param _recv the receiver to be unregistered
	 */
	public void unregister(IErrorReceiver _recv)
	{
		mRecvs.remove(_recv);
	}
	
	/**
	 * can be called by any class that receives http errors. all registered
	 * receivers are notified about the error
	 * @param _msg the message to be passed to all error receivers
	 */
	public void notifyError(String _msg)
	{
		for(IErrorReceiver recv:mRecvs)
		{
			recv.notifyError(_msg);
		}
	}

}
