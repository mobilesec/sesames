/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.wifi;

import java.util.ArrayList;

import android.net.wifi.ScanResult;

/**
 * every class that wants to receive wifi scan results from the 
 * WifiAccess has to register with the WifiAccess
 * and implement this interface
 * @author Peter Riedl
 *
 */
public interface IWifiScanReceiver 
{
	/**
	 * called by the WifiAccess whenever ScanResults are available
	 * @param _results the current ScanResults
	 */
	public void setWifiScanResults(ArrayList<ScanResult> _results);
}
