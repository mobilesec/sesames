package at.sesame.fhooe.localizationservice.xml.lm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;
import at.sesame.fhooe.localizationservice.xml.lm.model.AbsolutePosition;
import at.sesame.fhooe.localizationservice.xml.lm.model.AccessPointInformation;
import at.sesame.fhooe.localizationservice.xml.lm.model.Anchor;
import at.sesame.fhooe.localizationservice.xml.lm.model.DbInfo;
import at.sesame.fhooe.localizationservice.xml.lm.model.FileDescriptor;
import at.sesame.fhooe.localizationservice.xml.lm.model.FingerPrintDatabaseInformation;
import at.sesame.fhooe.localizationservice.xml.lm.model.LocalizationMechanismMetaInformation;
import at.sesame.fhooe.localizationservice.xml.lm.model.RelativePosition;


public class LocalizationMechanismMetaFileParser 
{
	private static final String TAG = "LocalizationMechanismMetaFileParser";
	private static XmlPullParser mParser;
	
	/**
	 * -----------------------------------------------------------------------------------------
	 * TAG names
	 * -----------------------------------------------------------------------------------------
	 */
	private static final String FINGERPRINT_DATABASE_TAG = "FingerprintDatabase";
	private static final String DB_INFO_TAG = "DB";
	private static final String LOCALIZATION_MECHANISM_META_TAG = "Localization_Mechanism_Meta";
	private static final String DB_FILEDESCRIPTOR_TAG = "DB_File";
	private static final String MP_FILEDESCRIPTOR_TAG = "MP_File";
	private static final String MAP_FILEDESCRIPTOR_TAG = "Map";
	private static final String ANCHOR_TAG = "Anchor";
	private static final String AP_TAG = "AP";
	private static final String APS_TAG = "APs";
	private static final String NUM_INSTANCES_TAG = "Num_insts";
	
	/**
	 * -----------------------------------------------------------------------------------------
	 * Attribute names
	 * -----------------------------------------------------------------------------------------
	 */
	private static final String LOCALIZATION_MECHANISM_ATTRIBUTE = "LocalizationMechanism";
	
	
	/**
	 * -----------------------------------------------------------------------------------------
	 * helper variables
	 * -----------------------------------------------------------------------------------------
	 */
	private static FingerPrintDatabaseInformation mFPI;
	private static DbInfo mDbInfo;
	private static ArrayList<AccessPointInformation> mAps;

	public LocalizationMechanismMetaFileParser()
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

	public static LocalizationMechanismMetaInformation parse(InputStream _is)
	{
		LocalizationMechanismMetaInformation res = null;
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
					if(mParser.getName().equals(LOCALIZATION_MECHANISM_META_TAG))
					{
						res = new LocalizationMechanismMetaInformation(mParser.getAttributeValue("", LOCALIZATION_MECHANISM_ATTRIBUTE));
					}
					else if(mParser.getName().equals(DB_FILEDESCRIPTOR_TAG))
					{
//						Log.e(TAG, "Db file descriptor");
//						FileDescriptor dbDesc = parseFileDescriptor();
						mDbInfo.setDatabaseFile(parseFileDescriptor());
					}
					else if (mParser.getName().equals(MP_FILEDESCRIPTOR_TAG))
					{
//						Log.e(TAG, "mp file descriptor");
//						FileDescriptor mpDesc = parseFileDescriptor();
						mFPI.setMpFile(parseFileDescriptor());
					}
					else if(mParser.getName().equals(MAP_FILEDESCRIPTOR_TAG))
					{
//						Log.e(TAG, "map file descriptor");
//						FileDescriptor mapDesc = parseFileDescriptor();
						res.setMapFile(parseFileDescriptor());
					}
					else if(mParser.getName().equals(ANCHOR_TAG))
					{
//						Anchor a = parseAnchor();
						mDbInfo.setAnchor(parseAnchor());
					}
					else if(mParser.getName().equals(AP_TAG))
					{
//						AccessPointInformation apInfo = parseAccessPointInformation();
//						Log.e(TAG, apInfo.toString());
						mAps.add(parseAccessPointInformation());
					}
					else if(mParser.getName().equals(FINGERPRINT_DATABASE_TAG))
					{
						mFPI = new FingerPrintDatabaseInformation();
					}
					else if(mParser.getName().equals(DB_INFO_TAG))
					{
						mDbInfo = new DbInfo();
					}
					else if(mParser.getName().equals(NUM_INSTANCES_TAG))
					{
						mDbInfo.setNumberOfInstances(Integer.parseInt(mParser.nextText()));
					}
					else if(mParser.getName().equals(APS_TAG))
					{
						mAps = new ArrayList<AccessPointInformation>();
					}
				}
				else if(eventType == XmlPullParser.END_TAG) 
				{
					if(mParser.getName().equals(APS_TAG))
					{
						mDbInfo.setAccessPointInfos(mAps);
					}
					else if(mParser.getName().equals(DB_INFO_TAG))
					{
						mFPI.setDbInfo(mDbInfo);
					}
					else if(mParser.getName().equals(FINGERPRINT_DATABASE_TAG))
					{
						res.addFingerPrintDataBase(mFPI);
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

	private static AccessPointInformation parseAccessPointInformation() throws XmlPullParserException, IOException {
		AccessPointInformation res = new AccessPointInformation();
		mParser.nextTag();
		res.setBSSID(mParser.nextText());
		mParser.nextTag();
		res.setAvgRSSI(Double.parseDouble(mParser.nextText()));
		return res;
	}

	private static Anchor parseAnchor() throws XmlPullParserException, IOException 
	{
		Anchor res = new Anchor();
		RelativePosition relPos = new RelativePosition();
		AbsolutePosition absPos = new AbsolutePosition();
		
		mParser.nextTag();
		mParser.nextTag();
		relPos.setX(Double.parseDouble(mParser.nextText()));
		mParser.nextTag();
		relPos.setY(Double.parseDouble(mParser.nextText()));
		
		mParser.nextTag();
		mParser.nextTag();
		mParser.nextTag();
		absPos.setLatitude(Double.parseDouble(mParser.nextText()));
		mParser.nextTag();
		absPos.setLongitude(Double.parseDouble(mParser.nextText()));
		
		res.setRelativePosition(relPos);
		res.setAbsolutePosition(absPos);
		return res;
	}

	private static FileDescriptor parseFileDescriptor() throws XmlPullParserException, IOException 
	{
		FileDescriptor res = new FileDescriptor();
		mParser.nextTag();
		res.setName(mParser.nextText());
		mParser.nextTag();
		res.setFileType(mParser.nextText());
		mParser.nextTag();
		res.setUrl(mParser.nextText());
		mParser.nextTag();
		res.setDescription(mParser.nextText());
		return res;
	}
}
