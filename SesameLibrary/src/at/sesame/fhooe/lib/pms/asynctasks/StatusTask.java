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
import at.sesame.fhooe.lib.pms.model.PMSStatus;
/**
 * this class retrieves the status of a device specified by its mac-address
 * in the background
 *
 */
public class StatusTask 
extends AsyncTask<String, Void, PMSStatus> {

	@Override
	protected PMSStatus doInBackground(String... params) {
		String mac = params[0];
//		return PMSProvider.getPMS().getStatus(mac);
		return new PMSStatus();
	}

}
