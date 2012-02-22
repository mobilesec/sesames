package at.sesame.fhooe.midsd.demo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.achartengine.model.CategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;

import android.util.Log;

public class DataSimulator 
{
	private static final String TAG = "DataSimulator";
	private static final int DEFAULT_TIME_UNIT = Calendar.MINUTE;
	private static final int DEFAULT_INCREMENTATION_STEP = 15;
	
	public static final String[] BAR_TITLES = new String[]{"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"};
	
	public static TimeSeries createTimeSeries(String _title, Date _from, int _numEntries)
	{
		return createTimeSeries(_title, _from, _numEntries, DEFAULT_TIME_UNIT, DEFAULT_INCREMENTATION_STEP);
	}
	
	public static TimeSeries createTimeSeries(String _title, Date _from, int _numEntries, int _timeUnitToIncrement, int _timeUnitIncrementationStep)
	{
		TimeSeries res = new TimeSeries(_title);
		Random r = new Random();
//		new GregorianCalendar();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(_from);
		
		for(int i = 0;i<_numEntries;i++)
		{
			Date d = cal.getTime();
			double val = r.nextDouble()*1000;
			
			res.add(d, val);
			cal.add(_timeUnitToIncrement, _timeUnitIncrementationStep);
		}
		return res;
	}
	
	/**
	   * Builds a bar multiple series dataset using the provided values.
	   * 
	   * @param titles the series titles
	   * @param values the values
	   * @return the XY multiple bar dataset
	   */
	  public static XYMultipleSeriesDataset createBarSeries(ArrayList<String> _titles)
	  {
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    int length = _titles.size();
	    for (int i = 0; i < length; i++) {
	      CategorySeries series = new CategorySeries(_titles.get(i));
	      double[] v = createRandomBarSeriesData();
	      int seriesLength = v.length;
	      for (int k = 0; k < seriesLength; k++) {
	        series.add(v[k]);
	      }
	      dataset.addSeries(series.toXYSeries());
	    }
	    return dataset;
	  }

	private static double[] createRandomBarSeriesData() 
	{
		double[] res = new double[BAR_TITLES.length+1];
		Random r = new Random();
		for(int i = 0;i<BAR_TITLES.length+1;i++)
		{
			res[i] = r.nextDouble()*1000;
		}
		return res;
	}

}
