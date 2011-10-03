/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.datacollection;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * in this class the URL of the yahoo geocoder provided by the GeoCoder class
 * is called and the resulting string is returned.
 * @author Peter Riedl
 *
 */
public class HttpRetriever 
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private final String TAG = getClass().getSimpleName();

	/**
	 * the client to call the passed URL
	 */
	private DefaultHttpClient mClient = new DefaultHttpClient();

	/**
	 * calls the webservice specified by the passed URL and returns the
	 * result of the call as string
	 * @param url the URL to call
	 * @return the result of the call as string
	 */
	public String retrieve(String url) 
	{
		
		try 
		{
			HttpGet get = new HttpGet(url);
			HttpResponse getResponse = mClient.execute(get);
			HttpEntity getResponseEntity = getResponse.getEntity();

			if (getResponseEntity != null) 
			{
				return EntityUtils.toString(getResponseEntity);
			}
		} 
		catch (Exception e) 
		{
			Log.e(TAG, "Error in retrieve()", e);
		}

		return null;
	}
}
