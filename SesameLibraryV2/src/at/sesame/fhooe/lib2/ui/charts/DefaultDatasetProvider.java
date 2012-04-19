package at.sesame.fhooe.lib2.ui.charts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.model.CategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;

import android.util.Log;
import at.sesame.fhooe.lib2.ui.charts.exceptions.DatasetCreationException;

public class DefaultDatasetProvider 
extends AbstractDatasetProvider
{
	private static final String TAG = "DefaultDatasetProvider";
	@Override
	public int createDataset(Object... _data) throws DatasetCreationException 
	{
		mDataset = new XYMultipleSeriesDataset();
		if(_data.length!=2)
		{
			throw new DatasetCreationException("passed parameters had wrong length");
		}
		ArrayList<String> titles;
		
		try
		{
			titles = (ArrayList<String>)_data[0];
		}
		catch (ClassCastException e) 
		{
			throw new DatasetCreationException("first parameter has wrong type (ArrayList<String> expected)");
		}
		
		
		ArrayList<Double[]> rawData;
		try
		{
			rawData = (ArrayList<Double[]>)_data[1];
		}
		catch (ClassCastException e) {
			throw new DatasetCreationException("second parameter has wrong type (ArrayList<Double[]> expected)");
		}
		
		if(titles.size()!=rawData.size())
		{
			throw new DatasetCreationException("sizes of passed ArrayLists does not match");
		}
		
		for(int i =0;i<rawData.size();i++)
		{
			Double[] yValues = rawData.get(i);
			XYSeries series = new XYSeries(titles.get(i),0);
			for(int j = 0;j<yValues.length;j++)
			{
				series.add(j, yValues[j]);
			}
			mDataset.addSeries(i, series);
		}
		return mDataset.getSeriesCount();
		
	}
	
	
	public void createBarDataSet(ArrayList<String> _titles, ArrayList<double[]> _values) throws DatasetCreationException
	{
//		for(int i =0;i<mDataset.getSeriesCount();i++)
//		{
//			mDataset.removeSeries(i);
//		}
		mDataset = new XYMultipleSeriesDataset();
		if(_titles.size()!=_values.size())
		{
			throw new DatasetCreationException("passed arguments must have same size");
		}
		
		for(int i = 0;i<_titles.size();i++)
		{
//			Log.e(TAG, "creating series:"+_titles.get(i));
			
			CategorySeries series = new CategorySeries(_titles.get(i));
			double[] seriesValues = _values.get(i);
//			Log.e(TAG, "number of values in series:"+seriesValues.length);
//			double[] expanded = new double[seriesValues.length+1];
			
			for(int j = 0;j<seriesValues.length;j++)
			{
//				Log.e(TAG, "value "+j+" = "+seriesValues[j]);
				series.add(seriesValues[j]);
			}
//			series.add(0);
			mDataset.addSeries(series.toXYSeries());
		}
	}
	
	/**
	 * Builds an XY multiple time dataset using the provided values.
	 * 
	 * @param titles the series titles
	 * @param xValues the values for the X axis
	 * @param yValues the values for the Y axis
	 * @return the XY multiple time dataset
	 * @throws DatasetCreationException 
	 */
	public XYMultipleSeriesDataset buildDateDataset(String[] titles, List<Date[]> xValues,
			List<double[]> yValues) throws DatasetCreationException {
		if(titles.length==0||xValues.isEmpty()||yValues.isEmpty())
		{
			throw new DatasetCreationException("passed data were empty");
		}
		if(titles.length!=xValues.size())
		{
			throw new DatasetCreationException("length of titles and y values does not match...");
		}
		else
		{
			if(titles.length!=yValues.size())
			{
				throw new DatasetCreationException("length of titles and x values does not match...");
			}
		}
		if(xValues.size()!= yValues.size())
		{
			throw new DatasetCreationException("x- and y- sizes dont match (x="+xValues.size()+", y="+yValues.size()+")");
		}
//		for(int i = 0;i<xValues.size();i++)
//		{
//			int xLen = xValues.get(i).length;
//			int yLen = yValues.get(i).length;
//			if(xLen!=yLen)
//			{
//				throw new DatasetCreationException("lengths of x- and y-values don't match (x="+xLen+", y="+yValues.get(i).length+").");							
//			}
//		}
		mDataset = new XYMultipleSeriesDataset();
//		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
//		Log.e(TAG, "length of titles="+length);
		for (int i = 0; i < length; i++) {
			TimeSeries series = new TimeSeries(titles[i]);
//			Log.e(TAG, "current index = "+i);
			Date[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			
			for (int k = 0; k < seriesLength; k++) 
			{
				try
				{
					series.add(xV[k], yV[k]);					
				}
				catch(ArrayIndexOutOfBoundsException _aioobe)
				{
					//don't add data to series, just continue
				}
			}
			mDataset.addSeries(i,series);
		}
		return mDataset;
	}

}
