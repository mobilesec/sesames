package at.sesame.fhooe.localizationservice.xml.meta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import at.sesame.fhooe.localizationservice.xml.meta.model.Building;
import at.sesame.fhooe.localizationservice.xml.meta.model.Level;
import at.sesame.fhooe.localizationservice.xml.meta.model.LocalizationMechanism;
import at.sesame.fhooe.localizationservice.xml.meta.model.MetaFileInformation;


public class MetaFileParser 
{
	@SuppressWarnings("unused")
	private static final String TAG = "MetaFileParser";
	private static XmlPullParser mParser;
	
	private static final String META_TAG = "Meta";
	private static final String NAME_ATTRIBUTE_TAG = "Name";
	private static final String EMAIL_ATTRIBUTE_TAG = "EMail";
	private static final String BUILDING_TAG = "Building";
	private static final String LEVEL_TAG = "Level";
	private static final String LOCALIZATION_MECHANISM_TAG = "LocalizationMechanism";
	
	private static Building mBuilding = null;
	private static ArrayList<Level> mLevels = null;
	
	private enum ParserContext
	{
		building,
		level, 
		none
	}
	
	private static ParserContext mParserContext = ParserContext.none;

	
	public MetaFileParser()
	{
		init();
	}
	

	private static void init() 
	{
		mBuilding = null;
		mLevels = new ArrayList<Level>();
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

	public MetaFileInformation parse(InputStream _is)
	{
		MetaFileInformation res = null;
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
				if(eventType == XmlPullParser.START_TAG) 
				{
					if(mParser.getName().equals(META_TAG))
					{
						String name = mParser.getAttributeValue("", NAME_ATTRIBUTE_TAG);
						String email = mParser.getAttributeValue("", EMAIL_ATTRIBUTE_TAG);
						res = new MetaFileInformation(name, email);
					}
					else if(mParser.getName().equals(BUILDING_TAG))
					{
						mBuilding =parseBuilding();
					}
					else if(mParser.getName().equals(LEVEL_TAG))
					{
						Level l = parseLevel();
						if(null!=l)
						{
							mLevels.add(l);
						}
					}
					else if(mParser.getName().equals(LOCALIZATION_MECHANISM_TAG))
					{
						LocalizationMechanism lm = parseLocalizationMechanism();
						switch(mParserContext)
						{
						case building:
							mBuilding.addLocalizationMechanism(lm);
							break;
						case level:
							
							Level l = mLevels.get(mLevels.size()-1);
							l.addLocalizationMechanism(lm);
							break;
						}
					}
					
					
				}
				eventType = mParser.next();
			}
			res.setBuilding(mBuilding);
			res.setLevels(mLevels);
			mBuilding = null;
			mLevels = new ArrayList<Level>();
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


	private static Level parseLevel() throws XmlPullParserException, IOException 
	{
		mParserContext = ParserContext.level;
		Level l = new Level();
//		Log.e(TAG, "parseLevel:"+mParser.getName());
		mParser.nextTag();
		l.setID(Integer.parseInt(mParser.nextText()));
		mParser.nextTag();
		l.setName(mParser.nextText());
		mParser.nextTag();
		l.setLevelNumber(Integer.parseInt(mParser.nextText()));
//		String id = mParser.nextText();
//		Log.e(TAG, "id="+id);
//		Log.e(TAG, "id="+mParser.nextText());//+", name="+mParser.nextText()+", number="+mParser.nextText());
		return l;
	}


	private static Building parseBuilding() throws XmlPullParserException, IOException 
	{
		mParserContext = ParserContext.building;
		Building res = new Building();
		mParser.nextTag();
		res.setID(Integer.parseInt(mParser.nextText()));
		mParser.nextTag();
		res.setName(mParser.nextText());

		return res;
	}
	
	private static LocalizationMechanism parseLocalizationMechanism() throws XmlPullParserException, IOException
	{
		
//		mParser.nextTag();
		if(!mParser.getName().equals(LOCALIZATION_MECHANISM_TAG))
		{
			return null;
		}
		LocalizationMechanism res = new LocalizationMechanism();
		mParser.nextTag();
		res.setName(mParser.nextText());
		mParser.nextTag();
		res.setURL(new URL(mParser.nextText()));
		return res;
	}

}
