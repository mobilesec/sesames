/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.vpn;

import xink.vpn.VpnActor;
import xink.vpn.wrapper.L2tpIpsecPskProfile;

import com.android.settings.vpn.AuthenticationActor;

import android.content.Context;
import android.net.vpn.L2tpIpsecProfile;
import android.util.Log;
//import android.net.vpn.L2tpIpsecPskProfile;

import at.sesame.fhooe.lib.exceptions.VpnException;
import at.sesame.fhooe.lib.exceptions.VpnException.VpnExceptionType;
import at.sesame.fhooe.lib.vpn.VpnSetting.Type;

/**
 * This class handles everything concerning VPN access. It is used
 * as follows:
 * first the VpnSettings have to be set (setVpnSettings())
 * next the desired ConnectionMode has to be set (setConnectionMode())
 * next the VpnAccess has to be initialized (initialize())
 * now the connect method can be called to actually establish the connection
 * @author Peter Riedl
 *
 */
public class VpnAccess 
{
	/**
	 * Enum containing the two supported modes of connection
	 */
	public enum ConnectionMode
	{
		PSK,
		CRT
	}
	
	/**
	 * the AuthenticationActor for certificate based VPN connections
	 */
	private static AuthenticationActor mCertActor;
	
	/**
	 * the VpnActor for PSK based VPN Connections
	 */
	private static VpnActor mPskActor;
	
	/**
	 * the settings of the VPN connection
	 */
	private static VpnSetting mSetting;
	
	/**
	 * the context of the VpnAccess
	 */
	private static Context mContext;
	
	/**
	 * the actual used vpn mode
	 */
	private static ConnectionMode mMode;
	
	/**
	 * creates a profile for PSK based VPN access
	 * @return a profile for PSK based VPN access
	 * @throws VpnException when the settings are invalid
	 */
	private static L2tpIpsecPskProfile createPskProfile() throws VpnException
	{
  		if(!mSetting.isValid())
  		{
  			throw new VpnException(VpnExceptionType.SETTINGS_NOT_VALID);
  		}
  		
  		L2tpIpsecPskProfile profile = new L2tpIpsecPskProfile(mContext);
  		
  		profile.setId(mSetting.getId());
		profile.setName(mSetting.getName());
		profile.setServerName(mSetting.getServer());
		profile.setSecretEnabled(false);
		
		profile.setUsername(mSetting.getUser());
		profile.setPassword(mSetting.getUserPass());
		Log.e("vpnaccess", "pwd set:"+mSetting.getUserPass());
		profile.setPresharedKey(mSetting.getPSK());
	
		return profile;
	}
  
	/**
	 * creates a profile for certificate based VPN access
	 * @return a profile for certificate based VPN access
	 * @throws VpnException when the settings are invalid
	 */
	private static L2tpIpsecProfile createCertProfile() throws VpnException
	{
		if(!mSetting.isValid())
  		{
  			throw new VpnException(VpnExceptionType.SETTINGS_NOT_VALID);
  		}
  		L2tpIpsecProfile profile = new L2tpIpsecProfile();
  		
  		profile.setId(mSetting.getId());
		profile.setName(mSetting.getName());
		profile.setServerName(mSetting.getServer());
		profile.setSecretEnabled(false);

		profile.setCaCertificate(mSetting.getCaCertName());
		profile.setUserCertificate(mSetting.getUserCertName());

		return profile;
	}
  
	/**
	 * first checks for mode conflicts, then initializes the AuthenticationActor
	 * according to the actual ConnectionMode
	 * @param c the Context for the AuthenticationActor
	 * @throws VpnException when the modes conflict
	 */
	public static void initialize(Context _c) throws VpnException
	{
		checkModeConflict();
		mContext = _c;
		if(mMode == ConnectionMode.CRT)
		{
			mCertActor = new AuthenticationActor(mContext,createCertProfile());
		}
		else if(mMode == ConnectionMode.PSK)
		{
			mPskActor = new VpnActor(mContext);
		}
	}
  
	/**
	 * checks if the actual connection mode (mMode) and the supported modes of the VpnSettings
	 * are in conflict
	 * @throws VpnException when a conflict was found
	 */
	private static void checkModeConflict() throws VpnException
	{
		if(mMode == ConnectionMode.CRT)
		{
			if(mSetting.getType() == Type.PSK)
			{
				throw new VpnException(VpnExceptionType.CONNECTION_MODE_SETTINGS_CONFLICT);
			}
		}
		else if(mMode == ConnectionMode.PSK)
		{
			if(mSetting.getType() == Type.CRT)
			{
				throw new VpnException(VpnExceptionType.CONNECTION_MODE_SETTINGS_CONFLICT);
			}
		}
	}
  
	/**
	 * creates a new VpnBroadcastReceiver for user notification
	 * @param _c the context to be used
	 */
	public static void enableNotifications(Context _c)
	{
		new VpnBroadcastReceiver(_c);
	}

	/**
	 * actually connects to the VPN
	 * @return true if connection is attempted, false if any preconditions
	 * for connection are not satisfied
	 */
	public static boolean connect(ConnectionMode _mode)
	{
		if(_mode ==ConnectionMode.PSK)
		{
			if(null!=mPskActor)
			{
				try {
					mPskActor.connect(createPskProfile());
					return true;
				} catch (VpnException e) 
				{
					e.printStackTrace();
					return false;
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					return false;
				}
			}
		}
		else if(_mode==ConnectionMode.CRT)
		{
			if(null!=mCertActor)
			{
				mCertActor.connect(mSetting.getUser(), mSetting.getUserPass());
				return true;
			}
		}
		return false;
	}
	
	/**
	 * closes the VPN connection
	 */
	public static void disconnect()
	{
		if(null!=mPskActor)
		{
			try {
				mPskActor.disconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(null!=mCertActor)
		{
			mCertActor.disconnect();
		}
	}

	/**
	 * sets the VpnSettings
	 * @param _setting the VpnSettings to set
	 */
	public static void setVpnSetting(VpnSetting _setting)
	{
		Log.e("VPNACCESS", _setting.toString());
		mSetting = _setting;
	}

	/**
	 * sets the ConnectionMode
	 * @param _mode the ConnectionMode to set
	 */
	public static void setConnectionMode(ConnectionMode _mode)
	{
		mMode = _mode;
	}
}
