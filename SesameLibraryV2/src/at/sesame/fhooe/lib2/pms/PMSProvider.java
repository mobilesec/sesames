/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib2.pms;

import java.util.ArrayList;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.io.http.HttpClientHttpChannelFactory;
import org.codegist.crest.serializer.jackson.JacksonDeserializer;

import android.util.Base64;
import at.sesame.fhooe.lib2.pms.model.ExtendedPMSStatus;
import at.sesame.fhooe.lib2.pms.model.PMSStatus;
import at.sesame.fhooe.lib2.pms.proxy.ProxyHelper;
import at.sesame.fhooe.lib2.pms.service.IPMSService;

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
//		if(null==mPMSService)
//		{
////			String username = "peter";
////			String password = "thatpeter";
////			String basicAuthDigest = Base64.encodeToString((username + ":" + password).getBytes());
//			createPMS(_user, _pwd);
//		}
		return mPMSService;
	}

	public static void createPMS(String _user, String _pwd) {
		CRest crest = new  CRestBuilder().bindDeserializer(JacksonDeserializer.class,PMSStatus.class, ExtendedPMSStatus.class)
										.setHttpChannelFactory(new HttpClientHttpChannelFactory(ProxyHelper.getProxiedAllAcceptingHttpsClient()))
										.basicAuth(_user, _pwd)
//										.placeholder("basic.auth.digest", Base64.encodeToString((_user + ":" + _pwd).getBytes(), Base64.DEFAULT))
										.build();
		mPMSService = crest.build(IPMSService.class);
	}
	
	/**
	 * retrieves the list of controllable devices from the PMS
	 * @return a list containing mac addresses of all controllable devices
	 */
//	public static ArrayList<String> getDeviceList(String _user, String _pass)
//	{
//		if(null==mPMSService)
//		{
////			mPMSService = getPMS(_user, _pass);
//			createPMS(_user, _pass);
//		}
//		ArrayList<String> macs = new ArrayList<String>();
//		try {
//			String macString = new DeviceListTask().execute(new Void[0]).get();
//			if(macString.equals(Boolean.toString(false)))
//			{
//				return null;
//			}
////			String macString = mPMSService.getClients();
//			Log.e(TAG, macString);
//			JSONObject json = new JSONObject(macString);
//			JSONArray jsonArr = json.getJSONArray("clients");
//			for(int i = 0;i<jsonArr.length();i++)
//			{
//				JSONObject mac=(JSONObject) jsonArr.get(i);
//				macs.add(mac.getString("mac"));
//			}
//			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (ExecutionException e)
//		{
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return macs;
//	}
	
	public static boolean checkCredentials(String _user, String _pass)
	{
		createPMS(_user, _pass);
		String os = null;
		try
		{
			os = mPMSService.knownOs();			
		}
		catch(Exception _e)
		{
			return false;
		}
		
		return null!=os;
	}
	
//	public static ArrayList<ExtendedPMSStatus> getExtendedPMSStatusList(ArrayList<String> _macs)
//	{
//		try {
//			return new ExtendedStatusListTask().execute(_macs).get();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	private static class ExtendedStatusListTask extends AsyncTask<ArrayList<String>, Void, ArrayList<ExtendedPMSStatus>>
//	{
//
//		@Override
//		protected ArrayList<ExtendedPMSStatus> doInBackground(
//				ArrayList<String>... params) {
//			// TODO Auto-generated method stub
//			return getPMS().extendedStatusList(params[0]);
//		}
//		
//	}
}
