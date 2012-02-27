/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms.asynctasks;

import android.os.AsyncTask;
import at.sesame.fhooe.lib.pms.PMSProvider;

/**
 * this class retrieves the list of clients from the PMS in the background
 *
 */
public class DeviceListTask 
extends AsyncTask<Void, Void, String> 
{

	@Override
	protected String doInBackground(Void... params) 
	{
		return (String)PMSProvider.getPMS().getClients();
	}



}
