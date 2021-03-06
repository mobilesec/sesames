/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib2.pms.model;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.util.Log;
import at.sesame.fhooe.lib2.R;
import at.sesame.fhooe.lib2.logging.SesameLogger;
import at.sesame.fhooe.lib2.logging.SesameLogger.EntryType;
import at.sesame.fhooe.lib2.pms.PMSProvider;
import at.sesame.fhooe.lib2.pms.asynctasks.ExtendedStatusTask;
import at.sesame.fhooe.lib2.pms.asynctasks.PowerOffTask;
import at.sesame.fhooe.lib2.pms.asynctasks.StatusTask;
import at.sesame.fhooe.lib2.pms.asynctasks.WakeupTask;
import at.sesame.fhooe.lib2.pms.service.IPMSService;

/**
 * this class wraps all calls to the PMS and represents a device that
 * can provide status information, can be shut down, sent to sleep and woken up.
 *
 */
public class ControllableDevice
implements Runnable
{	
	private static final String TAG = "ControllableDevice";

	private static final int IDLE_DISPLAY_THRESHOLD = 5;
	public static final int IDLE_NOTIFICATION_THRESHOLD = 30;
	private static final int HOUR_FORMAT_THRESHOLD = 180;
	private static final int SHORT_INACTIVITY_INTERVAL = 10;
	private static final int LONG_INACTIVITY_INTERVAL = 30; 

	private static final int NUM_UPDATES_BEFORE_DIRTY_RESET = 3;


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

	//	private String mBasicAuthName = "peter";
	//	private String mBasicAuthPass = "thatpeter";

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

	private Context mCtx;

	private int mNumDirtyUpdates = 0;

	private boolean mDesiredAlive;

	//	private boolean mConsumerThreadRunning = true;
	//	
	//	private Thread mConsumerThread;

	private boolean mValid = false;

	private boolean mUseHostnameFromStatus = false;

	/**
	 * instance of the PMS
	 */
	private IPMSService mPms;

	/**
	 * creates a new ControllableDevice
	 * @param _c the current execution context (only used for localized strings)
	 * @param _mac the MAC address of the device
	 * @param _user the username of the device
	 * @param _password the password of the device
	 * @param _useCredentials if true, credentials are used otherwise not
	 */
	public ControllableDevice(Context _c, String _mac, String _hostName, String _user, String _password, boolean _useCredentials)
	{
		if(null==_hostName || _hostName.isEmpty())
		{
			mUseHostnameFromStatus = true;
		}
		else
		{
			mHostname = _hostName;
		}
		mCtx = _c;
		mMac = _mac;
		mUser = _user;
		mPassword = _password;
		mUseCredentials = _useCredentials;

		mPms= PMSProvider.getPMS();
		//		mQueue = new LinkedBlockingQueue();
		updateStatus();
	}

	public ControllableDevice(Context _ctx, ExtendedPMSStatus _status, String _hostName, String _user, String _pwd, boolean _useCred)
	{
		mCtx = _ctx;
		mUseCredentials = _useCred;
		mUser = _user;
		mPassword = _pwd;
		mMac = _status.getMac();

		if(null==_hostName || _hostName.isEmpty())
		{
			mUseHostnameFromStatus = true;
		}
		else
		{
			mHostname = _hostName;
		}
		mPms = PMSProvider.getPMS();
		setDesiredStatus(_status.getAlive().equals("1")?true:false);
		setExtendedPMSStatus(_status);
	}

	private void setDesiredStatus(boolean _alive)
	{
		//		Log.e(TAG, "desired status set to:"+_alive);
		mDesiredAlive = _alive;
	}
	public void setExtendedPMSStatus(ExtendedPMSStatus _status)
	{
		if(null==mMac||mMac.isEmpty())
		{
			mMac = _status.getMac();
		}
		else
		{
			if(!_status.getMac().equals(mMac))
			{
				Log.e(TAG, "wrong status passed -> not set");
				return;
			}
		}

		if(mUseHostnameFromStatus)
		{
			mHostname = _status.getHostname();
			//			Log.e(TAG, "hostname="+getHostname());
		}
		else
		{
			//			Log.e(TAG, "hostname not set");
			//			Log.e(TAG, "hostname="+getHostname());
		}
		mIp = _status.getIp();
		mIdleSince = _status.getIdleSince();
		mOs = OS.valueOf(_status.getOs());
		mAlive = _status.getAlive().equals("1")?true:false;
		mValid = true;

		if(isAlive()!=mDesiredAlive)
		{
			mNumDirtyUpdates++;
		}
		if(mNumDirtyUpdates>=NUM_UPDATES_BEFORE_DIRTY_RESET)
		{
			Log.e(TAG, "dirty flag reset after "+NUM_UPDATES_BEFORE_DIRTY_RESET+" dirty updates");
			mNumDirtyUpdates = 0;
			setDesiredStatus(isAlive());
		}
		//		Log.e(TAG, toString());
	}

	/**
	 * wakes the device up
	 * @return true if the wakeup was successful, false otherwise
	 */
	public boolean wakeUp()
	{
		SesameLogger.log(EntryType.PMS, getMac(), "wake-up");
		boolean res = false;
		try
		{
			setDesiredStatus(true);
			mIdleSince = 0;
			Log.e(TAG, "waking up "+getHostname());
			//		try
			//		{
			//			return mPms.wakeup(mMac);
			res = mPms.wakeup(mMac);
			//			return new WakeupTask().execute(mMac).get();
			//		} 
			//		catch (InterruptedException e) 
			//		{
			//			e.printStackTrace();
			//		}
			//		catch (ExecutionException e) 
			//		{
			//			e.printStackTrace();
			//		}
		}
		catch(Exception e)
		{
			return false;
		}
		return res;
	}

	/**
	 * puts the device to sleep or shuts it down depending on the passed parameter
	 * @param _state determines whether to shut down the device or put it to sleep
	 * @return true if the command was successful, false otherwise
	 */
	public boolean powerOff(PowerOffState _state)
	{
		SesameLogger.log(EntryType.PMS, getMac(), _state.name());
		setDesiredStatus(false);
		if(null==mPms)
		{
			System.out.println("pms was null");
			Log.i(TAG,"pms was null");
		}
		else
		{
			System.out.println("pms was ok");
			Log.i(TAG,"pms was ok");
		}
		if(null==mMac)
		{
			System.out.println("mac was null");
			Log.i(TAG,"mac was null");
		}
		else
		{
			System.out.println("mac was ok");
			Log.i(TAG,"mac was ok");
		}
		if(null==_state)
		{
			System.out.println("state was null");
			Log.i(TAG,"state was null");
		}
		else
		{
			System.out.println("state was ok");
			Log.i(TAG,"state was ok");
		}
		if(null==mUser)
		{
			System.out.println("user was null");
			Log.i(TAG,"user was null");
		}
		else
		{
			System.out.println("user was ok");
			Log.i(TAG,"user was ok");
		}
		if(null==mPassword)
		{
			System.out.println("password was null");
			Log.i(TAG,"password was null");
		}
		else
		{
			System.out.println("password was ok");
			Log.i(TAG, "password was ok");
		}
		try 
		{
			if(mUseCredentials)
			{
				//				return PMSProvider.getPMS(mBasicAuthName, mBasicAuthPass).poweroff(mMac, _state.name(), "", mUser, mPassword);
				return mPms.poweroff(mMac, _state.name(), "", mUser, mPassword);
				//				return new PowerOffTask().execute(mMac, _state.name(), "", mUser, mPassword).get();
			}
			else
			{
				return mPms.poweroff(mMac, _state.name(), "", "", "");
				//				return new PowerOffTask().execute(mMac, _state.name(), "", "", "").get();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		//		catch (ExecutionException e) 
		//		{
		//			e.printStackTrace();
		//		}
		//		return false;
	}

	/**
	 * queries the device's status and sets all members accordingly
	 */
	public void updateStatus()
	{
		ExtendedPMSStatus extStat = getExtendedStatus();
		if(null==extStat)
		{
			mValid = false;
			return;
		}
		if(mUseHostnameFromStatus)
		{
			mHostname = extStat.getHostname();

		}
		mIp = extStat.getIp();
		mOs = translateOsToEnum(extStat.getOs());
		mAlive = extStat.getAlive().equals("1")?true:false;
		mIdleSince = extStat.getIdleSince();
		mValid = true;
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
				res = new ExtendedStatusTask().execute(mMac, mUser, mPassword).get();
				//				res = mPms.extendedStatus(mMac, mUser, mPassword);
				if(null==res)
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
		//		return 185;
	}

	public boolean isValid()
	{
		return mValid;
	}

	public String getIdleString()
	{
		int idleMins = getIdleSinceMinutes(); 
		if(idleMins<=IDLE_DISPLAY_THRESHOLD)
		{
			return "";
		}
		else if(idleMins<HOUR_FORMAT_THRESHOLD)
		{
			//			return ""+roundShortInterval(idleMins);
			return mCtx.getString(R.string.ControllableDevice_idleString_prefix)+roundShortInterval(idleMins)+mCtx.getString(R.string.ControllableDevice_minuteString);
		}
		else
		{
			DecimalFormat df = new DecimalFormat("#.#");
			String idleString = df.format(roundLongInterval(idleMins));
			//			return idleString;
			return mCtx.getString(R.string.ControllableDevice_idleString_prefix)+idleString+mCtx.getString(R.string.controllableDevice_hourString);
		}
	}

	private int roundShortInterval(int _val)
	{

		return round(_val, SHORT_INACTIVITY_INTERVAL);
	}


	private double roundLongInterval(int _val)
	{
		int hours = 0;
		while(_val-60>0)
		{
			hours++;
			_val-=60;
		}
		return hours+(double)((double)_val/60);
	}
	private int round(int _val, int _interval)
	{
		int intervalCount = 0;
		while(_val-_interval>=0)
		{
			_val-=_interval;
			intervalCount++;
		}

		return intervalCount*_interval;
	}

	private int getIdleHours() {
		int idleMins = getIdleSinceMinutes();
		int mins = idleMins%60;
		idleMins-=mins;
		int hours = idleMins/60;
		return 0;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("\n------------------------\n");
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

	public boolean isDirty()
	{
		return mDesiredAlive != isAlive();
	}

	public boolean equals(ControllableDevice _cd)
	{
		return mMac.equals(_cd.getMac());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}
