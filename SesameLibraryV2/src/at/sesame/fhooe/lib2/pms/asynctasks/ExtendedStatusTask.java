/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib2.pms.asynctasks;

import android.os.AsyncTask;
import at.sesame.fhooe.lib2.pms.PMSProvider;

/**
 * this class retrieves the extended status of a device specified by 
 * mac-address and optional username and password
 *
 */
public class ExtendedStatusTask 
extends AsyncTask<String, Void, Object> 
{

	@Override
	protected Object doInBackground(String... arg0) 
	{
		String mac = arg0[0];
		String user = arg0[1];
		String pass = arg0[2];
		try
		{
//			Object res = PMSProvider.getPMS().extendedStatus(mac, user, pass);
			return new Object();
		}
		catch(Exception e)
		{
			return new Boolean(false);
		}

	}

}
