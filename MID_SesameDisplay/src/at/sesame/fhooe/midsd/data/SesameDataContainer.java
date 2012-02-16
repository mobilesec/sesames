package at.sesame.fhooe.midsd.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.util.Log;
import at.sesame.fhooe.esmart.model.EsmartDataRow;

public class SesameDataContainer 
{
	private static final String TAG = "SesameDataContainer";
	private String mId;

	private Date[] mTimeStamps;

	private double[] mValues;

	public SesameDataContainer(String _id, Date[] _timeStamps, double[] _values) 
	{
		super();
		this.mId = _id;
		this.mTimeStamps = _timeStamps;
		this.mValues = _values;
	}

	public SesameDataContainer(String _id, ArrayList<EsmartDataRow> _edrs)
	{
		super();
		this.mId = _id;
		//		this.mTimeStamps = _edr.get(0).getDate();
		this.mValues = new double[_edrs.size()];
		this.mTimeStamps = new Date[_edrs.size()];

		for(int i = 0 ;i<_edrs.size();i++)
		{
			EsmartDataRow edr = _edrs.get(i);
//			Date d = edr.getDate();
//			if(null==d)
//			{
//				Log.e(TAG, edr.getmTimeStamp()+" could not be interpreted");
//			}
//			else
//			{
//				mTimeStamps[i] = d;
//				Log.e(TAG, edr.getmTimeStamp()+" interpreted as "+d.toGMTString());
//			}
			mTimeStamps[i] = edr.getDate();
			mValues[i] = edr.getDataValue();
		}
	}

	public String getId() {
		return mId;
	}

	public Date[] getTimeStamps() {
		return mTimeStamps;
	}

	public double[] getValues() {
		return mValues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SesameDataContainer [mId=");
		builder.append(mId);
		builder.append(", mTimeStamps=");
		builder.append(Arrays.toString(mTimeStamps));
		builder.append(", mValues=");
		builder.append(Arrays.toString(mValues));
		builder.append("]");
		return builder.toString();
	}
}
