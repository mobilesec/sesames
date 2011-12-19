package at.sesame.fhooe.xml;

import java.util.ArrayList;

import android.util.Log;
import at.sesame.fhooe.lib.util.GenericXMLParser;
import at.sesame.fhooe.model.Service;

public class ServicesResponseParser
extends GenericXMLParser<ArrayList<Service>>
{
	private static final String TAG = "ServicesResponseParser";
	//	private static XmlPullParser mParser;

	private static final String  SERVICE_KEY = "Service";
	private static final String  SERVICE_NAME_KEY = "ServiceName";
	private static final String  METHOD_NAME_KEY = "MethodName";
	private Service mCurrentService;

	//	private static void init() 
	//	{
	//		XmlPullParserFactory factory;
	//		try 
	//		{
	//			factory = XmlPullParserFactory.newInstance();
	//			factory.setNamespaceAware(true);
	//			mParser = factory.newPullParser();
	//		} 
	//		catch (XmlPullParserException e) 
	//		{
	//			e.printStackTrace();
	//		}
	//	}
	//
	//	public ArrayList<Service> parse(InputStream _is)
	//	{
	//		ArrayList<Service> res = new ArrayList<Service>();
	//		Service currentService = null;
	////		Log.e(TAG, "parsing started");
	//		if(null==mParser)
	//		{
	//			init();
	//		}
	//		try 
	//		{
	//			mParser.setInput( _is, "UTF-8" );
	//			int eventType = mParser.getEventType();
	//			while (eventType != XmlPullParser.END_DOCUMENT) 
	//			{
	//				if(eventType == XmlPullParser.START_DOCUMENT) 
	//				{
	////					Log.e(TAG, "Start document");
	//				} 
	//				else if(eventType == XmlPullParser.START_TAG) 
	//				{
	////					Log.e(TAG, "Start tag "+mParser.getName());
	//					if(mParser.getName().equals(SERVICE_KEY))
	//					{		
	//						currentService = new Service();
	//					}
	//					else if(mParser.getName().equals(SERVICE_NAME_KEY))
	//					{
	//						String serviceName = mParser.nextText();
	////						Log.e(TAG, "service name found:"+serviceName);
	//						currentService.setName(serviceName);
	//					}
	//					else if(mParser.getName().equals(METHOD_NAME_KEY))
	//					{
	//						String methodName = mParser.nextText();
	////						Log.e(TAG, "service name found:"+methodName);
	//						currentService.setMethodName(methodName);
	//					}
	//				}
	//				else if(eventType == XmlPullParser.END_TAG)
	//				{
	//					if(mParser.getName().equals(SERVICE_KEY))
	//					{
	//						res.add(currentService);
	//					}
	//				}
	//				eventType = mParser.next();
	//			}
	//		} 
	//		catch (XmlPullParserException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//
	//		return res;
	//	}

	@Override
	protected void onEndDocument() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onEndTag()
	{
		if(isParserNameEqualTo(SERVICE_KEY))
		{
			mResult.add(mCurrentService);
		}

	}

	@Override
	protected void onStartDocument() {
		mResult = new ArrayList<Service>();

	}

	@Override
	protected void onStartTag()
	{
		if(isParserNameEqualTo(SERVICE_KEY))
		{		
			mCurrentService = new Service();
		}
		else if(isParserNameEqualTo(SERVICE_NAME_KEY))
		{
			String serviceName = getNextText();
			//			Log.e(TAG, "service name found:"+serviceName);
			mCurrentService.setName(serviceName);
		}
		else if(isParserNameEqualTo(METHOD_NAME_KEY))
		{
			String methodName = getNextText();
			//			Log.e(TAG, "service name found:"+methodName);
			mCurrentService.setMethodName(methodName);
		}

	}
}
