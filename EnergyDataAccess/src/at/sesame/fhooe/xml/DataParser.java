package at.sesame.fhooe.xml;

import java.util.ArrayList;

import at.sesame.fhooe.lib.util.GenericXMLParser;
import at.sesame.fhooe.model.DataRow;

public class DataParser
extends GenericXMLParser<ArrayList<DataRow>>
{
	private static final String TAG = "DataParser";
	private static final String DATA_ROW_KEY = "DataRow";
	private static final String TIME_STAMP_KEY = "TimeStamp";
	private static final String DATA_VALUE_KEY = "DataValue";

	private DataRow mCurrentRow;
	
	@Override
	protected void onEndTag()
	{
		if(isParserNameEqualTo(DATA_ROW_KEY))
		{
			mResult.add(mCurrentRow);
		}
	}

	@Override
	protected void onStartTag() 
	{
		if(isParserNameEqualTo(DATA_ROW_KEY))
		{
			mCurrentRow = new DataRow();
		}
		else if(isParserNameEqualTo(TIME_STAMP_KEY))
		{
			mCurrentRow.setTimeStamp(getNextText());
		}
		else if(isParserNameEqualTo(DATA_VALUE_KEY))
		{
			mCurrentRow.setDataValue(Double.parseDouble(getNextText()));
		}
	}

	@Override
	protected void onStartDocument()
	{
		mResult = new ArrayList<DataRow>();
	}
	@Override
	protected void onEndDocument(){
	}
}
