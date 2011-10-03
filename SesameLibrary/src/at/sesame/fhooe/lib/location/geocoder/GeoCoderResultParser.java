/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.location.geocoder;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

/**
 * this class parses the xml response of the yahoo geocodig webservice
 * and stores the information in a GeoCoderResult
 * @author Peter Riedl
 *
 */
public class GeoCoderResultParser 
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "GeoCoderResultParser";
	
	/**
	 * the actual instance used by the singleton
	 */
	private static GeoCoderResultParser mGcrp = null;
	
	/**
	 * the parser for the xml response
	 */
	private XmlPullParser mParser;
	
	/**
	 * boolean flag indicating whether the "Result" tag in the
	 * response is reached
	 */
	private boolean resultReached = false;
	
	/**
	 * creates a new instance of GeoCoderResultParser
	 * @throws XmlPullParserException
	 */
	private GeoCoderResultParser() throws XmlPullParserException 
	{
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    mParser = factory.newPullParser();
	}
	
	/**
	 * singleton implementation to ensure there is only one GeoCoderResultParser
	 * @return the instance of the GeoCoderResultParser
	 */
	public static GeoCoderResultParser getInstance()
	{
		if(null==mGcrp)
		{
			try {
				mGcrp = new GeoCoderResultParser();
			} catch (XmlPullParserException e) {
				Log.e(TAG, "Error in getInstance()", e);
			}
		}
		return mGcrp;
	}
	
	/**
	 * parses the response of the yahoo geocoding webservice and
	 * creates the according GeoCoderResult
	 * @param response a string containing the xml response
	 * @return a GeoCodeResult filled with all information from the response
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public GeoCoderResult parseGeoCoderResponse(String response) throws XmlPullParserException, IOException
	{
		if(null==response)
		{
			return null;
		}
		mParser.setInput( new StringReader ( response ) );
		int eventType = mParser.getEventType();
		GeoCoderResult res = new GeoCoderResult();
		resultReached = false;
		while (eventType != XmlPullParser.END_DOCUMENT) 
		{
			if(eventType == XmlPullParser.START_TAG) 
			{
				if(mParser.getName().equals("Result"))
				{
					resultReached = true;
				}
				if(resultReached)
				{
					if(mParser.getName().equals("quality"))
					{
						res.setQuality(Double.parseDouble(mParser.nextText()));
					}
					else if(mParser.getName().equals("latitude"))
					{
						res.setLatitude(Double.parseDouble(mParser.nextText()));
					}
					else if(mParser.getName().equals("longitude"))
					{
						res.setLongitude(Double.parseDouble(mParser.nextText()));
					}
					else if(mParser.getName().equals("offsetlat"))
					{
						res.setOffsetLat(Double.parseDouble(mParser.nextText()));
					}
					else if(mParser.getName().equals("offsetlon"))
					{
						res.setOffsetLon(Double.parseDouble(mParser.nextText()));
					}
					else if(mParser.getName().equals("name"))
					{
						res.setName(mParser.nextText());
					}
					else if(mParser.getName().equals("radius"))
					{
						res.setRadius(Double.parseDouble(mParser.nextText()));
					}
					else if(mParser.getName().equals("line1"))
					{
						res.setLine1(mParser.nextText());
					}
					else if(mParser.getName().equals("line2"))
					{
						res.setLine2(mParser.nextText());
					}
					else if(mParser.getName().equals("line3"))
					{
						res.setLine3(mParser.nextText());
					}
					else if(mParser.getName().equals("line4"))
					{
						res.setLine4(mParser.nextText());
					}
					else if(mParser.getName().equals("house"))
					{
						res.setHouse(mParser.nextText());
					}
					else if(mParser.getName().equals("street"))
					{
						res.setStreet(mParser.nextText());
					}
					else if(mParser.getName().equals("xstreet"))
					{
						res.setXstreet(mParser.nextText());
					}
					else if(mParser.getName().equals("unittype"))
					{
						res.setUnittype(mParser.nextText());
					}
					else if(mParser.getName().equals("unit"))
					{
						res.setUnit(mParser.nextText());
					}
					else if(mParser.getName().equals("postal"))
					{
						res.setPostal(mParser.nextText());
					}
					else if(mParser.getName().equals("neighborhood"))
					{
						res.setNeighborhood(mParser.nextText());
					}
					else if(mParser.getName().equals("city"))
					{
						res.setCity(mParser.nextText());
					}
					else if(mParser.getName().equals("county"))
					{
						res.setCounty(mParser.nextText());
					}
					else if(mParser.getName().equals("state"))
					{
						res.setState(mParser.nextText());
					}
					else if(mParser.getName().equals("country"))
					{
						res.setCountry(mParser.nextText());
					}
					else if(mParser.getName().equals("countrycode"))
					{
						res.setCountryCode(mParser.nextText());
					}
					else if(mParser.getName().equals("statecode"))
					{
						res.setStateCode(mParser.nextText());
					}
					else if(mParser.getName().equals("countycode"))
					{
						res.setCountyCode(mParser.nextText());
					}
					else if(mParser.getName().equals("hash"))
					{
						res.setHash(mParser.nextText());
					}
					else if(mParser.getName().equals("woeid"))
					{
						res.setWoeid(mParser.nextText());
					}
					else if(mParser.getName().equals("woetype"))
					{
						res.setWoetype(mParser.nextText());
					}
					else if(mParser.getName().equals("uzip"))
					{
						res.setUzip(mParser.nextText());
					}
				}
			} 
			
			eventType = mParser.next();
		}
		return res;
	}
}

