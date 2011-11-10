/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms.model;

import java.util.concurrent.ExecutionException;

import android.util.Log;
import at.sesame.fhooe.lib.pms.PMSProvider;
import at.sesame.fhooe.lib.pms.asynctasks.ExtendedStatusTask;
import at.sesame.fhooe.lib.pms.asynctasks.PowerOffTask;
import at.sesame.fhooe.lib.pms.asynctasks.StatusTask;
import at.sesame.fhooe.lib.pms.asynctasks.WakeupTask;
import at.sesame.fhooe.lib.pms.service.IPMSService;

/**
 * this class wraps all calls to the PMS and represents a device that
 * can provide status information, can be shut down, sent to sleep and woken up.
 *
 */
public class ControllableDevice
implements Runnable
{
//	private enum PMSCalls
//	{
//		sleep,
//		shutDown,
//		status,
//		extendedStatus
//	}
//	private LinkedBlockingQueue mQueue;
	
	private static final String TAG = "ControllableDevice";
	/**
	 * enumeration of possible power-off commands
	 *
	 */
	public enum PowerOffState
	{
		shutdown,
		sleep
	}
	
	public enum OS
	{
		windows,
		linux,
		mac,
		unknown
	}
	
	/**
	 * the MAC address of the device
	 */
	private String mMac;
	
	/**
	 * the hostname of the device
	 */
	private String mHostname;
	
	/**
	 * the ip of the device
	 */
	private String mIp;
	
	/**
	 * the operating system of the device
	 */
	private OS mOs;
	
	/**
	 * the username of the device
	 */
	private String mUser;
	
	/**
	 * the password of the device
	 */
	private String mPassword;
	
	/**
	 * flag indicating whether the device is turned on or off
	 */
	private boolean mAlive;
	
	/**
	 * flag indicating whether to use the specified credentials
	 * or rely on the auto-detect mode of the PMS
	 */
	private boolean mUseCredentials;
	
	private int mIdleSince;
	
//	private boolean mConsumerThreadRunning = true;
//	
//	private Thread mConsumerThread;

	/**
	 * instance of the PMS
	 */
	private IPMSService mPms;

	/**
	 * creates a new ControllableDevice
	 * @param _mac the MAC address of the device
	 * @param _user the username of the device
	 * @param _password the password of the device
	 * @param _useCredentials if true, credentials are used otherwise not
	 */
	public ControllableDevice(String _mac, String _user, String _password, boolean _useCredentials)
	{
		mMac = _mac;
		mUser = _user;
		mPassword = _password;
		mUseCredentials = _useCredentials;

		mPms= PMSProvider.getPMS();
//		mQueue = new LinkedBlockingQueue();
		updateStatus();
	}

	/**
	 * wakes the device up
	 * @return true if the wakeup was successful, false otherwise
	 */
	public boolean wakeUp()
	{
//		try
		{
			return mPms.wakeup(mMac);
//			return new WakeupTask().execute(mMac).get();
		} 
//		catch (InterruptedException e) 
//		{
//			e.printStackTrace();
//		}
//		catch (ExecutionException e) 
//		{
//			e.printStackTrace();
//		}
//		return false;
	}
	
	/**
	 * puts the device to sleep or shuts it down depending on the passed parameter
	 * @param _state determines whether to shut down the device or put it to sleep
	 * @return true if the command was successful, false otherwise
	 */
	public boolean powerOff(PowerOffState _state)
	{
		try 
		{
			if(mUseCredentials)
			{
				return new PowerOffTask().execute(mMac, _state.name(), "", mUser, mPassword).get();
			}
			else
			{
				return new PowerOffTask().execute(mMac, _state.name(), "", "", "").get();
			}
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * queries the device's status and sets all members accordingly
	 */
	public void updateStatus()
	{
		ExtendedPMSStatus extStat = getExtendedStatus();
		if(null==extStat)
		{
			return;
		}
		mHostname = extStat.getHostname();
		mIp = extStat.getIp();
		mOs = translateOsToEnum(extStat.getOs());
		mAlive = extStat.getAlive().equals("1")?true:false;
		mIdleSince = extStat.getIdleSince();
	}

	private OS translateOsToEnum(String os) 
	{
//		if(os.toLowerCase().equals("windows"))
//		{
//			return OS.windows;
//		}
//		else if(os.toLowerCase().equals("linux"))
//		{
//			return OS.linux;
//		}
//		return null;
		return OS.valueOf(os);
	}

	/**
	 * queries the device's current status
	 * @return a PMSStatus object containing all status information
	 */
	public PMSStatus getStatus()
	{
		try 
		{
			return new StatusTask().execute(mMac).get();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
			return null;
		} 
		catch (ExecutionException e) 
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * queries the device's current extended status (status + idle time)
	 * @return a ExtendedPMSStatus object containing all extended status
	 * information
	 */
	public ExtendedPMSStatus getExtendedStatus()
	{
		if(null==mPms)
		{
			return null;
		}
		if(mUseCredentials)
		{
			Object res;
			try 
			{
//				res = new ExtendedStatusTask().execute(mMac, mUser, mPassword).get();
				res = mPms.extendedStatus(mMac, mUser, mPassword);
				if(res instanceof Boolean)
				{
					return null;
				}
				else 
				{
					return (ExtendedPMSStatus) res;
				}
			} 
//			catch (InterruptedException e) 
//			{
//				
////				e.printStackTrace();
//				Log.e(TAG, "extendedStatus was interrupted. "+e.getMessage());
//				return null;
//			} 
//			catch (ExecutionException e) 
//			{
//				e.printStackTrace();
//				return null;
//			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			return mPms.extendedStatus(mMac, "", "");
		}
	}

	public String getMac() {
		return mMac;
	}

	public String getHostname() {
		return mHostname;
	}

	public String getIp() {
		return mIp;
	}

	public OS getOs() {
		return mOs;
	}

	public String getUser() {
		return mUser;
	}

	public String getPassword() {
		return mPassword;
	}

	public boolean isAlive()
	{
		return mAlive;
	}
	
	public int getIdleSince()
	{
		return mIdleSince;
	}
	
	public int getIdleSinceMinutes()
	{
		return getIdleSince()/60;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("------------------------\n");
		sb.append("MAC: ");
		sb.append(mMac);
		sb.append("\nIP: ");
		sb.append(mIp);
		sb.append("\nHostname: ");
		sb.append(mHostname);
		sb.append("\nOS: ");
		sb.append(mOs);
		sb.append("\nAlive: ");
		sb.append(mAlive);
		sb.append("\n------------------------");

		return sb.toString();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
