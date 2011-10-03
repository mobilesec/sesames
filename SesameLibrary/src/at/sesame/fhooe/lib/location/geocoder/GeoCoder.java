/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.location.geocoder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;

/**
 * this class retrieves the reverse geocoding information from the yahoo webservice
 * @author Peter Riedl
 *
 */
public class GeoCoder 
{
	/**
	 * constant containing the API key for the webservice
	 */
	private static final String APIKey = "5zPSeB38";
	
	/**
	 * the base url for the yahoo webservice
	 */
	private static final String YAHOO_API_BASE_URL = "http://where.yahooapis.com/geocode?";
	
	/**
	 * the flag to indicate usage of the reverse geocoder
	 */
	private static final String YAHOO_API_REVERSEFLAG = "&gflags=R";
	
	/**
	 * the parameter specifying the app ID
	 */
	private static final String YAHOO_API_APPID = "&appid="+APIKey;

	private HttpRetriever httpRetriever = new HttpRetriever();

	/**
	 * creates the URL for the reverse geocoding webservice
	 * @param latitude the latitude to obtain geoinformation from
	 * @param longitude the longitude to obtain geoinformation from
	 * @return the result of the webservice
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public GeoCoderResult reverseGeoCode(double latitude, double longitude) throws XmlPullParserException, IOException 
	{
		String url = YAHOO_API_BASE_URL + "location=" + latitude + ",+"+longitude+ YAHOO_API_REVERSEFLAG + YAHOO_API_APPID;
		AsyncTask<String, Void, String> gct = new GeoCoderTask().execute(url);
		String response = "";
		try {
			response = gct.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return GeoCoderResultParser.getInstance().parseGeoCoderResponse(response);

	}
}
