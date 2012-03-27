package at.sesame.fhooe.lib2.ui.charts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		mDataset = new XYMultipleSeriesDataset();
//		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		Log.e(TAG, "length of titles="+length);
		for (int i = 0; i < length; i++) {
			TimeSeries series = new TimeSeries(titles[i]);
			Log.e(TAG, "current index = "+i);
			Date[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			mDataset.addSeries(i,series);
		}
		return mDataset;
	}

}
