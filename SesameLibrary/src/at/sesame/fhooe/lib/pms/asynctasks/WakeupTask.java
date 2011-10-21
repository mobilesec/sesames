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
 * this class wakes up a device specified by its mac-address
 * in the background
 *
 */
public class WakeupTask 
extends AsyncTask<String, Void, Boolean> 
{

	@Override
	protected Boolean doInBackground(String... params) {
		String mac = params[0];
		return PMSProvider.getPMS().wakeup(mac);
	}

}
