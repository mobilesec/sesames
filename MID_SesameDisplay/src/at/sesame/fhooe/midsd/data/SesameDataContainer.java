package at.sesame.fhooe.midsd.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.util.Log;
import at.sesame.fhooe.esmart.model.EsmartDataRow;

@SuppressWarnings("unused")
public class SesameDataContainer 
{
	private static final String TAG = "SesameDataContainer";
	
	public enum SesameDataType
	{
		ENERGY,
		TEMPERATURE,
		HUMIDITY,
		LIGHT
	}
	
	private SesameDataType mType;
	
	private String mId;

	private ArrayList<Date> mTimeStamps;

	private ArrayList<Double> mValues;

	public SesameDataContainer(String _id, ArrayList<Date> _timeStamps, ArrayList<Double> _values) 
	{
		super();
		this.mId = _id;
		this.mTimeStamps = _timeStamps;
		this.mValues = _values;
	}

//	public SesameDataContainer(String _id, ArrayList<EsmartDataRow> _edrs)
//	{
//		super();
//		this.mId = _id;
//		//		this.mTimeStamps = _edr.get(0).getDate();
//		this.mValues = new double[_edrs.size()];
//		this.mTimeStamps = new Date[_edrs.size()];
//
//		for(int i = 0 ;i<_edrs.size();i++)
//		{
//			EsmartDataRow edr = _edrs.get(i);
////			Date d = edr.getDate();
////			if(null==d)
////			{
////				Log.e(TAG, edr.getmTimeStamp()+" could not be interpreted");
////			}
////			else
////			{
////				mTimeStamps[i] = d;
////				Log.e(TAG, edr.getmTimeStamp()+" interpreted as "+d.toGMTString());
////			}
//			mTimeStamps[i] = edr.getDate();
//			mValues[i] = edr.getDataValue();
//		}
//	}

	public String getId() {
		return mId;
	}

	public ArrayList<Date> getTimeStamps() {
		return mTimeStamps;
	}

	public ArrayList<Double> getValues() {
		return mValues;
	}
	
	public boolean addData(Date _d, double _val)
	{
		if(mTimeStamps.contains(_d))
		{
			return false;
		}
		
		mTimeStamps.add(_d);
		mValues.add(_val);
		
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SesameDataContainer [mId=");
		builder.append(mId);
		builder.append(", mTimeStamps=");
		builder.append(Arrays.toString((Date[]) mTimeStamps.toArray(new Date[mTimeStamps.size()])));
		builder.append(", mValues=");
		builder.append(Arrays.toString((Double[]) mValues.toArray(new Double[mValues.size()])));
		builder.append("]");
		return builder.toString();
	}
}
