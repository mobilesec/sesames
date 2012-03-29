package at.sesame.fhooe.lib2.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import android.util.Log;
//import at.sesame.fhooe.lib.esmart.model.EsmartDataRow;

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

	//	private String mId;

	//	private ArrayList<Date> mTimeStamps;
	//
	//	private ArrayList<Double> mValues;

	private ArrayList<SesameMeasurement> mMeasurements;

	private SesameMeasurementPlace mPlace;

	public SesameDataContainer(SesameMeasurementPlace _place)
	{
		this(_place, new ArrayList<SesameMeasurement>());
	}

	//	public SesameDataContainer(SesameMeasurementPlace _place, ArrayList<Date> _timeStamps, ArrayList<Double> _values) 
	//	{
	//		super();
	////		this.mId = _id;
	//		this.mPlace = _place;
	//		
	//		if(_timeStamps.size()!=)
	//	}

	public SesameDataContainer(SesameMeasurementPlace _place, ArrayList<SesameMeasurement> _measurements)
	{
		mPlace = _place;
//		mMeasurements = (ArrayList<SesameMeasurement>) Collections.synchronizedList(_measurements);
		mMeasurements = _measurements;
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

	//	public String getId() {
	//		return mId;
	//	}

	//	public ArrayList<Date> getTimeStamps() {
	//		return mTimeStamps;
	//	}
	//
	//	public ArrayList<Double> getValues() {
	//		return mValues;
	//	}

	public boolean addData(SesameMeasurement _sm)
	{
		if(mMeasurements.contains(_sm))
		{
			return false;
		}

		mMeasurements.add(_sm);
		return true;
	}

	public synchronized static ArrayList<SesameMeasurement> filterByDate(final ArrayList<SesameMeasurement> _measurements, final Date _from, final Date _to)
	{
//		synchronized (this)
		{
			final ArrayList<SesameMeasurement> res = new ArrayList<SesameMeasurement>();
			Log.d(TAG, "num measurments to filter:"+_measurements.size());
			Log.d(TAG, "filtering between:"+_from.toString()+" and "+_to.toString());
			for(int i = 0;i<_measurements.size();i++)
			{
				SesameMeasurement sm = _measurements.get(i);
				if(sm.getTimeStamp().after(_from)&&sm.getTimeStamp().before(_to))
				{
//					Log.d(TAG, "added:"+sm.toString());
					res.add(sm);
				}
				else
				{
//					Log.d(TAG, "did not add:"+sm.toString());
				}
			}
			return res;
		}

		//		double[] values = getValuesBetweenDates(_from, _to);
		//		Date[] dates = getDatesBetweenDates(_from, _to);
		//		
		//		ArrayList<Double> valueList = new ArrayList<Double>(values.length);
		//		ArrayList<Date> dateList = new ArrayList<Date>(values.length);
		//		for(int i = 0 ;i<values.length;i++)
		//		{
		//			valueList.add(values[i]);
		//			dateList.add(dates[i]);
		//		}
		//		
		//		return new SesameDataContainer(mPlace, dateList, valueList);
	}

	//	private ArrayList<SesameMeasurement>

	//	public double[] getValuesBetweenDates(Date _from, Date _to)
	//	{
	//		ArrayList<Integer>mSelectedIndexes = new ArrayList<Integer>();
	//		Log.e(TAG, "from:"+_from.toGMTString());
	//		Log.e(TAG, "to:"+_to.toGMTString());
	//		for(int i = 0 ;i<mTimeStamps.size();i++)
	//		{
	//			Date d = mTimeStamps.get(i);
	////			Log.e(TAG, "comparing to:"+d.toGMTString());
	//			if(d.after(_from)&&d.before(_to))
	//			{
	////				Log.e(TAG, d.toGMTString()+" added");
	//				mSelectedIndexes.add(i);
	//			}
	//			else
	//			{
	////				Log.e(TAG, "not added");
	//			}
	//		}
	//		if(mSelectedIndexes.isEmpty())
	//		{
	//			return null;
	//		}
	//		double[] res = new double[mSelectedIndexes.size()];
	//		
	//		for(int i = 0;i<mSelectedIndexes.size();i++)
	//		{
	//			res[i] = mValues.get(mSelectedIndexes.get(i));
	//		}
	//		return res;
	//	}
	//	
	//	public Date[] getDatesBetweenDates(Date _from, Date _to)
	//	{
	//		ArrayList<Date>selectedDates = new ArrayList<Date>();
	//		for(Date d:mTimeStamps)
	//		{
	//			
	//			if(d.after(_from)&&d.before(_to))
	//			{
	//				selectedDates.add(d);
	//			}
	//		}
	//		
	//		return (Date[]) selectedDates.toArray(new Date[selectedDates.size()]);
	//	}

	public static Date[] getTimeStampArray(ArrayList<SesameMeasurement> _measurements)
	{
		Date[] res = new Date[_measurements.size()];

		for(int i = 0;i<_measurements.size();i++)
		{
			res[i] = _measurements.get(i).getTimeStamp();
		}

		return res;
	}

	public static double[] getValueArray(ArrayList<SesameMeasurement> _measurements)
	{
		double[] res = new double[_measurements.size()];
		Log.d(TAG, "number of measurements to process="+_measurements.size());
		for(int i = 0;i<_measurements.size();i++)
		{
			res[i] = _measurements.get(i).getVal();
		}
		return res;
	}

	public SesameMeasurementPlace getMeasurementPlace() {
		return mPlace;
	}

	public void setMeasurementPlace(SesameMeasurementPlace _place) {
		this.mPlace = _place;
	}



	public ArrayList<SesameMeasurement> getMeasurements() {
		return mMeasurements;
	}

	public void setMeasurements(ArrayList<SesameMeasurement> _measurements) {
		this.mMeasurements = _measurements;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SesameDataContainer [mType=");
		builder.append(mType);
		builder.append(", mMeasurements=");
		builder.append(mMeasurements);
		builder.append(", mPlace=");
		builder.append(mPlace);
		builder.append("]");
		return builder.toString();
	}


}
