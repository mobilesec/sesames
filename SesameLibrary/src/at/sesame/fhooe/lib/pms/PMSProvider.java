/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.io.http.HttpClientHttpChannelFactory;
import org.codegist.crest.serializer.jackson.JacksonDeserializer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import at.sesame.fhooe.lib.pms.asynctasks.DeviceListTask;
import at.sesame.fhooe.lib.pms.model.ExtendedPMSStatus;
import at.sesame.fhooe.lib.pms.model.PMSStatus;
import at.sesame.fhooe.lib.pms.proxy.ProxyHelper;
import at.sesame.fhooe.lib.pms.service.IPMSService;

/**
 * this class provides access to the power management service. 
 * @author admin
 *
 */
public class PMSProvider 
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "PMSProvider";
	
	/**
	 * the instance of the pms service (singleton)
	 */
	private static IPMSService mPMSService;
	
	/**
	 * initializes the crest library and provides the implementation of the service interface
	 * @return the implementation of the PMS interface
	 */
	public static IPMSService getPMS()
	{
		if(null==mPMSService)
		{
			
			CRest crest = new CRestBuilder().bindDeserializer(JacksonDeserializer.class,PMSStatus.class, ExtendedPMSStatus.class)
											.setHttpChannelFactory(new HttpClientHttpChannelFactory(ProxyHelper.getProxiedAllAcceptingHttpsClient()))
											.build();
	        
	        mPMSService = crest.build(IPMSService.class);
		}
		return mPMSService;
	}
	
	/**
	 * retrieves the list of controllable devices from the PMS
	 * @return a list containing mac addresses of all controllable devices
	 */
	public static ArrayList<String> getDeviceList()
	{
		if(null==mPMSService)
		{
			mPMSService = getPMS();
		}
		ArrayList<String> macs = new ArrayList<String>();
		try {
			String macString = new DeviceListTask().execute(new Void[0]).get();
			if(macString.equals(Boolean.toString(false)))
			{
				return null;
			}
//			String macString = mPMSService.getClients();
			Log.e(TAG, macString);
			JSONObject json = new JSONObject(macString);
			JSONArray jsonArr = json.getJSONArray("clients");
			for(int i = 0;i<jsonArr.length();i++)
			{
				JSONObject mac=(JSONObject) jsonArr.get(i);
				macs.add(mac.getString("mac"));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return macs;
	}
	
	public static ArrayList<ExtendedPMSStatus> getExtendedPMSStatusList(ArrayList<String> _macs)
	{
		try {
			return new ExtendedStatusListTask().execute(_macs).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static class ExtendedStatusListTask extends AsyncTask<ArrayList<String>, Void, ArrayList<ExtendedPMSStatus>>
	{

		@Override
		protected ArrayList<ExtendedPMSStatus> doInBackground(
				ArrayList<String>... params) {
			// TODO Auto-generated method stub
			return getPMS().extendedStatusList(params[0]);
		}
		
	}
}
