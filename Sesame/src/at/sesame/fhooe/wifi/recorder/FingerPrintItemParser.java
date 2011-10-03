/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.wifi.recorder;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import at.sesame.fhooe.wifi.recorder.model.AccessPoint;
import at.sesame.fhooe.wifi.recorder.model.FingerPrintItem;
import at.sesame.fhooe.wifi.recorder.model.MeasurementPoint;

/**
 * this class parses MeasurementPoints and AccessPoints from a given XML file
 * @author Peter Riedl
 *
 */
public class FingerPrintItemParser 
{	
	
	/**
	 * parses the xml file containing MeasurementPoints and AccessPoints and returns a
	 * list of contained FingerPrintItems
	 * @param _parser containig the FingerprintItems
	 * @return a list of FingerPrintItems contained in the file
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public ArrayList<FingerPrintItem> parseFingerprintItems(XmlResourceParser _parser) throws XmlPullParserException, IOException
	{
		ArrayList<FingerPrintItem> res = new ArrayList<FingerPrintItem>();
		int eventType = _parser.getEventType();
		FingerPrintItem fpi = null;
		while(eventType != XmlPullParser.END_DOCUMENT)
		{
			
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
					res.add(fpi);
				}
			}
			eventType = _parser.next();
		}
		
		return res;
	}
}
