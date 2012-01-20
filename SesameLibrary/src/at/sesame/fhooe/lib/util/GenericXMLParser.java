package at.sesame.fhooe.lib.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;


public abstract class GenericXMLParser <T>
{
	private static final String TAG = "GenericXMLParser";
	private XmlPullParser mParser;
	protected T mResult;
	
	public GenericXMLParser()
	{
		init();
	}
	
	private void init() 
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
	
	public T parse(InputStream _is)
	{
		Log.e(TAG, "parsing");
		if(null==mParser)
		{
			Log.e(TAG, "parser was null, calling init");
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
					onStartDocument();
				} 
				else if(eventType == XmlPullParser.START_TAG) 
				{
					onStartTag();
				}
				else if(eventType == XmlPullParser.END_TAG)
				{
					onEndTag();
				}
				eventType = mParser.next();
			}
			onEndDocument();
			
		} 
		catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mResult;
	}
	
	protected String getNextText()
	{
		try {
			return mParser.nextText();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected boolean isParserNameEqualTo(Object _name)
	{
		return mParser.getName().equals(_name);
	}
	
	protected abstract void onStartDocument();
	protected abstract void onStartTag();
	protected abstract void onEndTag();
	protected abstract void onEndDocument();
	
//	protected abstract T getResult();
	

}
