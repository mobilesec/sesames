/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.location;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * this class is a wrapper for the android location service
 * @author admin
 *
 */
public class LocationAccess 
implements LocationListener
{
	/**
	 * the view to receive location updates
	 */
	private ArrayList<ILocationUpdateReceiver> mRecvs = new ArrayList<ILocationUpdateReceiver>();
	
	/**
	 * creates a new LocationAccess with specified owner
	 * @param _c the current context to get the location service from
	 */
	public LocationAccess(Context _c)
	{	
		LocationManager lm = (LocationManager) _c.getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}
	
	public void registerLocationUpdateReceiver(ILocationUpdateReceiver _recv)
	{
		mRecvs.add(_recv);
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) 
	{
		Log.e(getClass().getSimpleName(), "onStatusChanged:"+arg0);
		
	}
	
	@Override
	public void onProviderEnabled(String arg0) {
		Log.e(getClass().getSimpleName(), "onProviderEnabled:"+arg0);
		
	}
	
	@Override
	public void onProviderDisabled(String arg0) {
		Log.e(getClass().getSimpleName(), "onProviderDisabled:"+arg0);
		
	}
	
	@Override
	public void onLocationChanged(Location arg0) 
	{
		Log.e(getClass().getSimpleName(), "onLocationChanged:Latitude:"+arg0.getLatitude()+" Longitude:"+arg0.getLongitude());
		for(ILocationUpdateReceiver recv:mRecvs)
		{
			recv.updateLocation(arg0);
		}
	}
}
