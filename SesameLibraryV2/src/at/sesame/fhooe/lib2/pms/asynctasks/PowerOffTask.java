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
//		Object res = PMSProvider.getPMS().poweroff(mac, state, os, user, pwd);
		Object res = new Object();
		return res==null;
	}
//	[14:43] Vikash Kumar: select ?x { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.sesame-s.ftw.at/ontologies/2012/1/SmartBuilding.owl#Location>}
}
