/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.datacollection;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
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
			HttpHost proxy = new HttpHost("80.120.3.4", 3128);
			mClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			HttpResponse response;
			

			
		} 
		catch (Exception e) 
		{
			Log.e(TAG, "Error in retrieve()", e);
		}

		return null;
	}
	
	public static void testDocTo()
	{
		HttpHost proxy = new HttpHost("80.120.3.4", 3128, "http");

        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

            HttpHost target = new HttpHost("doc.to", 443, "https");
            HttpGet req = new HttpGet("/");

            System.out.println("executing request to " + target + " via " + proxy);
            HttpResponse rsp = httpclient.execute(target, req);
            HttpEntity entity = rsp.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(rsp.getStatusLine());
            Header[] headers = rsp.getAllHeaders();
            for (int i = 0; i<headers.length; i++) {
                System.out.println(headers[i]);
            }
            System.out.println("----------------------------------------");

            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }

        } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
	}
}
