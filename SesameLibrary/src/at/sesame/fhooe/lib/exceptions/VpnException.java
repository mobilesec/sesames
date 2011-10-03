/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.exceptions;

import android.content.res.Resources;
import android.util.Log;
import at.sesame.fhooe.lib.R;

/**
 * this class models exceptions concerning vpn issues
 * @author admin
 *
 */
@SuppressWarnings("serial")
public class VpnException 
extends Exception 
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "VpnException";
	
	/**
	 * enumeration containing all possible types of VpnExceptions
	 */
	public enum VpnExceptionType
	{
		USER_NOT_SET,
		PWD_NOT_SET,
		SERVER_NOT_SET,
		SETTINGS_NOT_VALID,
		CONNECTION_MODE_SETTINGS_CONFLICT
	}
	
	/**
	 * the actual type of this exception
	 */
	private VpnExceptionType mType;
	
	/**
	 * creates a new VpnException from the passed type
	 * @param _type the type of the VpnException
	 */
	public VpnException(VpnExceptionType _type)
	{
		mType = _type;
	}
	
	@Override
	public String getMessage()
	{
		Resources r = Resources.getSystem();
		switch (mType) 
		{
			case USER_NOT_SET:
				return r.getString(R.string.VpnException_userNotSet);
			case PWD_NOT_SET:
				return r.getString(R.string.VpnException_passwordNotSet);
			case SERVER_NOT_SET:
				return r.getString(R.string.VpnException_serverNotSet);
			case SETTINGS_NOT_VALID:
				return r.getString(R.string.VpnException_settingsNotValid);
			case CONNECTION_MODE_SETTINGS_CONFLICT:
				return r.getString(R.string.VpnException_settingsConflict);
			default:
				return r.getString(R.string.VpnException_defaultMessage);
		}
	}
	
	@Override
	public void printStackTrace()
	{
		StackTraceElement[] trace = super.getStackTrace();
		StringBuffer sb = new StringBuffer(getMessage());
		
		for(StackTraceElement ste:trace)
		{
			sb.append("Class:");
			sb.append(ste.getClassName());
			sb.append("Method:");
			sb.append(ste.getMethodName());
			sb.append("line:");
			sb.append(ste.getLineNumber());
		}
		
		Log.e(TAG, sb.toString());
	}
}
