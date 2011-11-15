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
 * this class calls the power-off command in the background
 *
 */
public class PowerOffTask 
extends AsyncTask<String, Void, Boolean> 
{

	@Override
	protected Boolean doInBackground(String... params)
	{
		String mac = params[0];
		String state = params[1];
		String os = params[2];
		String user = params[3];
		String pwd = params[4];
		Object res = PMSProvider.getPMS().poweroff(mac, state, os, user, pwd);
		return res==null;
	}

}
