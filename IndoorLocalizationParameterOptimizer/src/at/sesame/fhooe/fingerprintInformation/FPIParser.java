package at.sesame.fhooe.fingerprintInformation;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class FPIParser 
{
	private ArrayList<Room> mRooms;
	private ArrayList<FingerPrintItem> mFPIs;
	
	public void parse(InputStream _is)
	{
		KXmlParser parser = new KXmlParser();
		
		mRooms = new ArrayList<Room>();
		mFPIs = new ArrayList<FingerPrintItem>();
		
		int type;
		
		try 
		{
			parser.setInput(new InputStreamReader(_is));
			while((type=parser.next())!=KXmlParser.END_DOCUMENT)
			{
				if(type == KXmlParser.START_TAG)
				{
					String tag = parser.getName();
					
					if(tag.equals("Room"))
					{
						mRooms.add(parseRoom(parser));
					}
					if(tag.equals("FingerPrintItem"))
					{
						mFPIs.add(parseFingerPrintItem(parser));
					}
				}
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Room parseRoom(KXmlParser _parser) throws XmlPullParserException, IOException 
	{
		int type;
		ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
		Room r;
		String name = null;
		while(true)
		{
			type = _parser.next();
			if(type==KXmlParser.START_TAG)
			{
				if(_parser.getName().equals("Name"))
				{
					name = _parser.nextText();
				}
				if(_parser.getName().equals("Point"))
				{
					points.add(parsePoint(_parser));
				}
			}
			if(type==KXmlParser.END_TAG)
			{
				if(_parser.getName().equals("Room"))
				{
					r = new Room(name, points);
					return r;
				}
			}
		}
	}
	private Point2D.Double parsePoint(KXmlParser _parser) throws NumberFormatException, XmlPullParserException, IOException 
	{
		_parser.nextTag();
		double x = Double.parseDouble(_parser.nextText());
		_parser.nextTag();
		double y = Double.parseDouble(_parser.nextText());
		return new Point2D.Double(x, y);
	}

	private FingerPrintItem parseFingerPrintItem(KXmlParser _parser) throws XmlPullParserException, IOException 
	{
		int eventType;
		FingerPrintItem fpi = null;
		while(true)
		{
			eventType = _parser.next();
			if(eventType == XmlPullParser.START_TAG)
			{
				if(_parser.getName().equals("Type"))
				{
					String type = _parser.nextText();
					if(type.equals("MP"))
					{
						fpi = new MeasurementPoint();
					}
					else if(type.equals("AP"))
					{
						fpi = new AccessPoint();
					}
				}
				else if(_parser.getName().equals("Name"))
				{
					fpi.setName(_parser.nextText());
				}
				else if(_parser.getName().equals("Description"))
				{
					fpi.setRoom(_parser.nextText());
				}
				else if(_parser.getName().equals("X"))
				{
					fpi.setX(Double.parseDouble(_parser.nextText()));
				}
				else if(_parser.getName().equals("Y"))
				{
					fpi.setY(Double.parseDouble(_parser.nextText()));
				}
			}
			else if(eventType==XmlPullParser.END_TAG)
			{
				if(_parser.getName().equals("FingerPrintItem"))
				{
					return fpi;
				}
			}
		}
	}

	public ArrayList<Room> getRooms()
	{
		return mRooms;
	}
	
	public ArrayList<FingerPrintItem> getFingerPrintItems()
	{
		return mFPIs;
	}
}
