package at.sesame.fhooe.energydisplay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;

import android.util.Log;
import at.sesame.fhooe.lib.ui.charts.AbstractDatasetProvider;
import at.sesame.fhooe.lib.ui.charts.exceptions.DatasetCreationException;

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
			XYSeries series = new XYSeries(titles.get(i));
			for(int j = 0;j<yValues.length;j++)
			{
				series.add(j, yValues[j]);
			}
			mDataset.addSeries(series);
		}
		return mDataset.getSeriesCount();
		
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
		Log.e(TAG, "building dataset");
//		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		mDataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) 
		{
			TimeSeries series = new TimeSeries(titles[i]);
			Log.e(TAG, "current index = "+i);
			Date[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			if(xV.length==0||yV.length==0)
			{
				continue;
			}
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			mDataset.addSeries(series);
		}
		Log.e(TAG, "number of series in dataset:"+mDataset.getSeriesCount());
		return mDataset;
	}

}
