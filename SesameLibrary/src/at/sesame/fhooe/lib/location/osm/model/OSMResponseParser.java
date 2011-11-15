package at.sesame.fhooe.lib.location.osm.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class OSMResponseParser 
{
	private static final String TAG = "OSMResponseParser";
	private static XmlPullParser mParser;
	private static OSMNode mCurrentnode;
	
	private static final String ID_KEY = "id";
	private static final String  LATITUDE_KEY = "lat";
	private static final String  LONGITUDE_KEY = "lon";
	private static final String  VERSION_KEY = "version";
	private static final String  CHANGESET_KEY = "changeset";
	private static final String  USER_KEY = "user";
	private static final String  UID_KEY = "uid";
	private static final String  VISIBLE_KEY = "visible";
	private static final String  TIMESTAMP_KEY = "timestamp";
	
	public OSMResponseParser()
	{
		init();
	}

	private static void init() 
	{
		XmlPullParserFactory factory;
		try 
		{
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			mParser = factory.newPullParser();
		} 
		catch (XmlPullParserException e) 
		{
			e.printStackTrace();
		}
	}

	public static ArrayList<IndoorLocOSMNode> parse(InputStream _is)
	{
		ArrayList<IndoorLocOSMNode> res = new ArrayList<IndoorLocOSMNode>();
		Log.e(TAG, "parsing started");
		if(null==mParser)
		{
			init();
		}
		try 
		{
			mParser.setInput( _is, "UTF-8" );
			int eventType = mParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				if(eventType == XmlPullParser.START_DOCUMENT) 
				{
					Log.e(TAG, "Start document");
				} 
				else if(eventType == XmlPullParser.START_TAG) 
				{
//					Log.e(TAG, "Start tag "+mParser.getName());
					if(mParser.getName().equals("node"))
					{
						
//						for(int i = 0;i<mParser.getAttributeCount();i++)
//						{
//							Log.e(TAG, "attribute name = "+mParser.getAttributeName(i)+"\n"+
//										"attribute value = "+mParser.getAttributeValue(i)+"\n"+
//										"attribute namespace = "+mParser.getAttributeNamespace(i));
//						}
						
						long id = Long.parseLong(mParser.getAttributeValue("", ID_KEY));
						double lat = Double.parseDouble(mParser.getAttributeValue("",LATITUDE_KEY));
						double lon = Double.parseDouble(mParser.getAttributeValue("", LONGITUDE_KEY));
						int version = Integer.parseInt(mParser.getAttributeValue("", VERSION_KEY));
						long changeSet = Long.parseLong(mParser.getAttributeValue("", CHANGESET_KEY));
						String user = mParser.getAttributeValue("", USER_KEY);
						long uid = Long.parseLong(mParser.getAttributeValue("", UID_KEY));
						boolean visible = Boolean.parseBoolean(mParser.getAttributeValue("", VISIBLE_KEY));
						String timeStamp = mParser.getAttributeValue("", TIMESTAMP_KEY);
						
						mCurrentnode = new OSMNode(id, lat, lon, version, changeSet, user, uid, visible, timeStamp);		
					}
					else if(mParser.getName().equals("tag"))
					{
						if(mParser.getAttributeValue(0).equals(IndoorLocOSMNode.INDOOR_LOCATION_URL_KEY))
						{
							Log.e(TAG, "Indooor localization node recognized");
							String url = mParser.getAttributeValue(1);
							if(null!=url)
							{
								Log.e(TAG, url);
								IndoorLocOSMNode iloNode = new IndoorLocOSMNode(mCurrentnode);
								
								iloNode.addTag(IndoorLocOSMNode.INDOOR_LOCATION_URL_KEY, url);
								res.add(iloNode);
							}
							else
							{
								Log.e(TAG, "URL was null");
							}
							
						}
					}
				}
				eventType = mParser.next();
			}
		} 
		catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

}
