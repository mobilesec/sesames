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
import at.sesame.fhooe.lib.util.EsmartDateProvider;
import at.sesame.fhooe.midsd.data.IEnergyDataSource;
import at.sesame.fhooe.midsd.data.IHumidityDataSource;
import at.sesame.fhooe.midsd.data.ILightDataSource;
import at.sesame.fhooe.midsd.data.ITemperatureDataSource;
import at.sesame.fhooe.midsd.data.SesameDataContainer;
import at.sesame.fhooe.midsd.data.SesameMeasurementPlace;

public class DataSimulator
implements IEnergyDataSource, ITemperatureDataSource, IHumidityDataSource, ILightDataSource
{
	private static final String TAG = "DataSimulator";
	private static final int DEFAULT_TIME_UNIT = Calendar.MINUTE;
	private static final int DEFAULT_INCREMENTATION_STEP = 15;
	private static final int DEFAULT_NUM_DATASETS = 100;
	
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

	@Override
	public ArrayList<SesameMeasurementPlace> getLightMeasurementPlaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SesameDataContainer getLightData(Date _from, Date _to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SesameMeasurementPlace> getHumidityMeasurementPlaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SesameDataContainer getHumidityData(Date _from, Date _to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SesameMeasurementPlace> getTemperatureMeasurementPlaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SesameDataContainer getTemperatureData(Date _from, Date _to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SesameMeasurementPlace> getEnergyMeasurementPlaces() {
		ArrayList<SesameMeasurementPlace> res = new ArrayList<SesameMeasurementPlace>(3);
		res.add(new SesameMeasurementPlace(15, "EDV 1"));
		res.add(new SesameMeasurementPlace(18, "EDV 3"));
		res.add(new SesameMeasurementPlace(17, "EDV 6"));
		return res;
	}

	@Override
	public SesameDataContainer getEnergyData(int _id, String _from, String _to) {
//		SesameDataContainer res = new TimeSeries(_title);
		Random r = new Random();
//		new GregorianCalendar();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(EsmartDateProvider.getDateFromEsmartString(_from));
		ArrayList<Date> dates = new ArrayList<Date>();
		ArrayList<Double> values = new ArrayList<Double>();
		for(int i = 0;i<DEFAULT_NUM_DATASETS;i++)
		{
			dates.add(cal.getTime());
			values.add(r.nextDouble()*1000);
			
//			res.add(d, val);
			cal.add(DEFAULT_TIME_UNIT, DEFAULT_INCREMENTATION_STEP);
		}
		return new SesameDataContainer(""+_id, dates, values);
	}

}
