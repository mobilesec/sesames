package at.sesame.fhooe.xml;

import java.util.ArrayList;


import android.util.Log;
import at.sesame.fhooe.lib.util.GenericXMLParser;
import at.sesame.fhooe.model.MeasurementPlace;

public class MeasurementPlacesParser
extends GenericXMLParser<ArrayList<MeasurementPlace>>
{
	private static final String TAG = "MeasurementPlacesParser";
//	private static XmlPullParser mParser;

	private static final String  MEASUREMENT_PLACE_KEY = "MeasurementPlace";
	private static final String  ID_MP_KEY = "ID_MP";
	private static final String  NAME_KEY = "Name";
	
	private MeasurementPlace mCurrentPlace;

	@Override
	protected void onEndDocument() {
		// TODO Auto-generated method stub

	}
	@Override
	protected void onEndTag()
	{
		if(isParserNameEqualTo(MEASUREMENT_PLACE_KEY))
		{
			mResult.add(mCurrentPlace);
		}

	}
	@Override
	protected void onStartDocument() 
	{
		mResult = new ArrayList<MeasurementPlace>();

	}
	@Override
	protected void onStartTag()
	{
		if(isParserNameEqualTo(MEASUREMENT_PLACE_KEY))
		{	
			mCurrentPlace = new MeasurementPlace();
		}
		else if(isParserNameEqualTo(ID_MP_KEY))
		{
			mCurrentPlace.setId(Integer.parseInt(getNextText()));
		}
		else if(isParserNameEqualTo(NAME_KEY))
		{
			mCurrentPlace.setName(getNextText());
		}

	}
}
