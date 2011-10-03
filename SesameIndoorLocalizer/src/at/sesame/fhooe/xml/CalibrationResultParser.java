package at.sesame.fhooe.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

public class CalibrationResultParser 
{
	private static Context mContext = null;
	
	public static void setContext(Context _c)
	{
		mContext = _c;
	}
	public static double[] parseCalibrationFile()
	{
		if(null==mContext)
		{
			return null;
		}
		double[] res = new double[2];
		try {
			XmlPullParser parser = Xml.newPullParser();
			File f = new File(mContext.getFilesDir().getAbsolutePath()+"/calibration.xml");
			if(!f.exists())
			{
				Log.e("xml", "file does not exist");
			}
			parser.setInput(new InputStreamReader(new FileInputStream(f)));
			
			
			int evtType = parser.getEventType();
			
			while(evtType!=XmlPullParser.END_DOCUMENT)
			{
				if(evtType==XmlPullParser.START_TAG)
				{
					if(parser.getName().equals("a"))
					{
						res[0] = Double.parseDouble(parser.nextText());
					}
					else if(parser.getName().equals("b"))
					{
						res[1] = Double.parseDouble(parser.nextText());
					}
				}
				evtType = parser.next();	
			}
		} catch (XmlPullParserException e) {
			return null;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return res;
	}
}
