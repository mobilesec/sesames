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
	
	private SesameMeasurementPlace mPlace;
	
	public SesameDataContainer(SesameMeasurementPlace _place, String _id)
	{
		this(_place, _id, new ArrayList<Date>(), new ArrayList<Double>());
	}

	public SesameDataContainer(SesameMeasurementPlace _place, String _id, ArrayList<Date> _timeStamps, ArrayList<Double> _values) 
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
	
	public SesameDataContainer filterByDate(Date _from, Date _to)
	{
		double[] values = getValuesBetweenDates(_from, _to);
		Date[] dates = getDatesBetweenDates(_from, _to);
		
		ArrayList<Double> valueList = new ArrayList<Double>(values.length);
		ArrayList<Date> dateList = new ArrayList<Date>(values.length);
		for(int i = 0 ;i<values.length;i++)
		{
			valueList.add(values[i]);
			dateList.add(dates[i]);
		}
		
		return new SesameDataContainer(mPlace, mId, dateList, valueList);
	}
	
	public double[] getValuesBetweenDates(Date _from, Date _to)
	{
		ArrayList<Integer>mSelectedIndexes = new ArrayList<Integer>();
		Log.e(TAG, "from:"+_from.toGMTString());
		Log.e(TAG, "to:"+_to.toGMTString());
		for(int i = 0 ;i<mTimeStamps.size();i++)
		{
			Date d = mTimeStamps.get(i);
//			Log.e(TAG, "comparing to:"+d.toGMTString());
			if(d.after(_from)&&d.before(_to))
			{
//				Log.e(TAG, d.toGMTString()+" added");
				mSelectedIndexes.add(i);
			}
			else
			{
//				Log.e(TAG, "not added");
			}
		}
		if(mSelectedIndexes.isEmpty())
		{
			return null;
		}
		double[] res = new double[mSelectedIndexes.size()];
		
		for(int i = 0;i<mSelectedIndexes.size();i++)
		{
			res[i] = mValues.get(mSelectedIndexes.get(i));
		}
		return res;
	}
	
	public Date[] getDatesBetweenDates(Date _from, Date _to)
	{
		ArrayList<Date>selectedDates = new ArrayList<Date>();
		for(Date d:mTimeStamps)
		{
			
			if(d.after(_from)&&d.before(_to))
			{
				selectedDates.add(d);
			}
		}
		
		return (Date[]) selectedDates.toArray(new Date[selectedDates.size()]);
	}
	
	

	public SesameMeasurementPlace getMeasurementPlace() {
		return mPlace;
	}

	public void setMeasurementPlace(SesameMeasurementPlace _place) {
		this.mPlace = _place;
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
