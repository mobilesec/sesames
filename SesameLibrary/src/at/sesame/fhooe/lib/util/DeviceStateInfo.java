/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;

public class DeviceStateInfo 
{
	/**
	 * the tag to identify the logger output of this class
	 */
	@SuppressWarnings("unused")
	private static final String TAG = "DeviceStateInfo";
	
	/**
	 * id that identifies a mobile connection
	 */
	private static final int MOBILE_CONNECTION = 0;
	
	/**
	 * id that identifies wifi connection
	 */
	private static final int WIFI_CONNECTION = 1;
	
	/**
	 * the context for the PhoneStateInfo
	 */
	private static Context mContext;
	
	/**
	 * the ConnectivityManager to retrieve connectivity
	 * information from
	 */
	private static ConnectivityManager mCm;
	
	
	/**
	 * the LocationManager to retrieve location
	 * information from
	 */
	private static LocationManager mLm;
	
	public static void setContext(Context _c)
	{
		mContext = _c;
		setupManagers();
	}
	
	private static void setupManagers()
	{
		mCm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		mLm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
	}
	
	/**
	 * returns whether any Internet connection is available
	 * @return true if connection is available, false otherwise
	 */
	public static boolean isInternetConnected()
	{
		 return mCm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
	/**
	 * checks whether mobile connection is established or not
	 * @return true if mobile is connected, false otherwise
	 */
	public static boolean isMobileConnected()
	{
		return getActiveNetworkType() == MOBILE_CONNECTION;
	}
	
	/**
	 * checks whether wifi connection is established or not
	 * @return true if wifi is connected, false otherwise
	 */
	public static boolean isWifiConnected()
	{
		return getActiveNetworkType() == WIFI_CONNECTION;
	}
	
	/**
	 * returns the type of the currently active network
	 * @return the type of the currently active network
	 */
	private static int getActiveNetworkType()
	{
		if(null==mCm)
		{
			return -1;
		}
		if(null==mCm.getActiveNetworkInfo())
		{
			return -1;
		}
		return mCm.getActiveNetworkInfo().getType();
	}
	
	/**
	 * returns if a location from the network location provider is available
	 * @return true if the network location provider is available, false otherwise
	 */
	public static boolean isNetworkLocationAvailable()
	{
		return mLm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	/**
	 * returns if a location from the gps location provider is available
	 * @return true if the gps location provider is available, false otherwise
	 */
	public static boolean isGPSLocationAvailable()
	{
		return mLm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}
