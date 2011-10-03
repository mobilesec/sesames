/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.vpn.xml;

import java.io.IOException;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import android.content.res.XmlResourceParser;
import android.util.Log;
import at.sesame.fhooe.lib.vpn.VpnSetting;


/**
 * in this class the content of the vpn_config.xml file is parsed and returned
 * as a VpnSetting
 * @author Peter Riedl
 *
 */
public class VpnSettingsParser 
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "VpnSettingsParser";
	
	/**
	 * parses the VPN configuration and returns the according VpnSetting
	 * @param _parser a parser filled with the content of the config file
	 * @return the VpnSetting represented by the xml file
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public VpnSetting parseVpnSettings(XmlResourceParser _parser) throws XmlPullParserException, IOException
	{
		VpnSetting res = new VpnSetting();
		int eventType = _parser.getEventType();
		Log.e(TAG, "parsing started");
		while (eventType != XmlPullParser.END_DOCUMENT) 
		{
			if(eventType == XmlPullParser.START_TAG) 
			{
				if(_parser.getName().equals("type"))
				{
					String type = _parser.nextText();
					res.setType(at.sesame.fhooe.lib.vpn.VpnSetting.Type.valueOf(type));
				}
				if(_parser.getName().equals("id"))
				{
					res.setId(_parser.nextText());
				}
				if(_parser.getName().equals("name"))
				{
					res.setName(_parser.nextText());
				}
				if(_parser.getName().equals("savedUsername"))
				{
					res.setUser(_parser.nextText());
				}
				if(_parser.getName().equals("userPass"))
				{
					res.setUserPass(_parser.nextText());
				}
				if(_parser.getName().equals("server"))
				{
					res.setServer(_parser.nextText());
				}
				if(_parser.getName().equals("PSK-key"))
				{
					res.setPSK(_parser.nextText());
				}
				if(_parser.getName().equals("CA-certificateName"))
				{
					res.setCaCertName(_parser.nextText());
				}
				if(_parser.getName().equals("user-certificateName"))
				{
					res.setUserCertName(_parser.nextText());
				}
			}
			eventType = _parser.next();
		}
		return res;	
	}
}
