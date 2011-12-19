/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.charts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.util.Log;

/**
 * this class is used to specify data series and renderers to be used with aChartEngine 
 * @author Peter Riedl
 *
 */
public class SesameChartHelper
{
	private static final String TAG = "SesameChartHelper";
	public XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,List<double[]> yValues) 
	{
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) 
		{
			XYSeries series = new XYSeries(titles[i]);
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) 
			{
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	/**
	 * Builds an XY multiple series renderer.
	 * 
	 * @param colors the series rendering colors
	 * @param styles the series point styles
	 * @return the XY multiple series renderers
	 */
	public XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) 
	{
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	/**
	 * Sets a few of the series renderer settings.
	 * 
	 * @param renderer the renderer to set the properties to
	 * @param title the chart title
	 * @param xTitle the title for the X axis
	 * @param yTitle the title for the Y axis
	 * @param xMin the minimum value on the X axis
	 * @param xMax the maximum value on the X axis
	 * @param yMin the minimum value on the Y axis
	 * @param yMax the maximum value on the Y axis
	 * @param axesColor the axes color
	 * @param labelsColor the labels color
	 */
	protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
			String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

	/**
	 * Builds an XY multiple time dataset using the provided values.
	 * 
	 * @param titles the series titles
	 * @param xValues the values for the X axis
	 * @param yValues the values for the Y axis
	 * @return the XY multiple time dataset
	 */
	protected XYMultipleSeriesDataset buildDateDataset(String[] titles, List<Date[]> xValues,
			List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			TimeSeries series = new TimeSeries(titles[i]);
			Date[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	/**
	 * Builds a category series using the provided values.
	 * 
	 * @param titles the series titles
	 * @param values the values
	 * @return the category series
	 */
	protected CategorySeries buildCategoryDataset(String title, double[] values) {
		CategorySeries series = new CategorySeries(title);
		int k = 0;
		for (double value : values) {
			series.add("Project " + ++k, value);
		}

		return series;
	}

	/**
	 * Builds a multiple category series using the provided values.
	 * 
	 * @param titles the series titles
	 * @param values the values
	 * @return the category series
	 */
	protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
			List<String[]> titles, List<double[]> values) {
		MultipleCategorySeries series = new MultipleCategorySeries(title);
		int k = 0;
		for (double[] value : values) {
			series.add(2007 + k + "", titles.get(k), value);
			k++;
		}
		return series;
	}

	/**
	 * Builds a category renderer to use the provided colors.
	 * 
	 * @param colors the colors
	 * @return the category renderer
	 */
	protected DefaultRenderer buildCategoryRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	/**
	 * Builds a bar multiple series dataset using the provided values.
	 * 
	 * @param titles the series titles
	 * @param values the values
	 * @return the XY multiple bar dataset
	 */
	public XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			CategorySeries series = new CategorySeries(titles[i]);
			double[] v = values.get(i);
			int seriesLength = v.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(v[k]);
			}
			dataset.addSeries(series.toXYSeries());
		}
		return dataset;
	}

	/**
	 * Builds a bar multiple series renderer to use the provided colors.
	 * 
	 * @param colors the series renderers colors
	 * @return the bar multiple series renderer
	 */
	protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setShowGrid(true);
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}
	
	/**
	 * creates a renderer for the energy data graph
	 * @param _data the data to be rendered (needed for correct x-labels)
	 * @param _xSpacingInHours the spacing in hours between two x-labels
	 * @return a renderer for energy data graphs
	 */
	public XYMultipleSeriesRenderer buildEnergyDataRenderer(GregorianCalendar _startDate, double[] _data, int _xSpacingInHours)
	{
		Log.e(TAG, "------SESAME------");
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		//-----------------------------------------------------------------------------------------------------
		//general setup
		//-----------------------------------------------------------------------------------------------------
		performGeneralEnergyDataRendererSetup(renderer);
		//-----------------------------------------------------------------------------------------------------
		//x-labels
		//-----------------------------------------------------------------------------------------------------
		if(_data.length<100)//1 measurement every 15 minutes --> 4 measurements per hour --> 92 measurements per day
		{
			layoutEnergyDataRendererXLabelsHour(renderer, _data, _xSpacingInHours);
		}
		else
		{
			layoutEnergyDataRendererXLabelsDay(renderer, _startDate, _data, _xSpacingInHours);
		}
		//-----------------------------------------------------------------------------------------------------
		//y-labels
		//-----------------------------------------------------------------------------------------------------
		layoutEnergyDataRendererYLabels(renderer, _data);
		//-----------------------------------------------------------------------------------------------------
		//finish
		//-----------------------------------------------------------------------------------------------------
		XYSeriesRenderer r = new XYSeriesRenderer();
		renderer.addSeriesRenderer(r);
		return renderer;
	}
	
	private XYMultipleSeriesRenderer performGeneralEnergyDataRendererSetup(XYMultipleSeriesRenderer _renderer)
	{
		_renderer.setAxisTitleTextSize(16);
		_renderer.setChartTitleTextSize(20);
		_renderer.setLabelsTextSize(15);
		_renderer.setLegendTextSize(15);
		_renderer.setPointSize(5f);
		_renderer.setGridColor(Color.WHITE);
		_renderer.setShowGrid(true);
		return _renderer;
	}
	
	private XYMultipleSeriesRenderer layoutEnergyDataRendererXLabelsHour(XYMultipleSeriesRenderer _renderer, double[] _data, int _xSpacingInHours)
	{
		_renderer.setXLabels(0);
		
		int len = _data.length;
		int interval = 4*_xSpacingInHours; //a data value is present every 15 minutes --> four times per hour.
		for(int i = 0;i<len;i+=interval)
		{
			_renderer.addXTextLabel(i+1, getXLabelForEnergyDataHour(i));//i+1 because x-indexing starts with 1 in achartengine
		}
		return _renderer;
	}
	
	private String getXLabelForEnergyDataHour(int _idx)
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE,0);
		for(int i = 0;i<_idx;i++)
		{
			gc.add(Calendar.MINUTE, 15);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//		String res = ;
		return sdf.format(gc.getTime());
	}
	
	private XYMultipleSeriesRenderer layoutEnergyDataRendererXLabelsDay(XYMultipleSeriesRenderer _renderer, GregorianCalendar _startDate, double[] _data, int _xSpacingInHours)
	{
		_renderer.setXLabels(0);
		
		int len = _data.length;
//		int interval = 4*_xSpacingInHours; //a data value is present every 15 minutes --> four times per hour.
		for(int i = 0;i<len;i+=92)//1 measurement every 15 minutes --> 4 measurements per hour --> 92 measurements per day
		{
			_renderer.addXTextLabel(i+1, getXLabelForEnergyDataDay(_startDate, i));//i+1 because x-indexing starts with 1 in achartengine
		}
		return _renderer;
	}
	
	private String getXLabelForEnergyDataDay(GregorianCalendar _startDate, int _idx) 
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.YEAR, _startDate.get(Calendar.YEAR));
		gc.set(Calendar.MONTH, _startDate.get(Calendar.MONTH));
		gc.set(Calendar.DAY_OF_MONTH, _startDate.get(Calendar.DAY_OF_MONTH));
//		Log.e(TAG, gc.toString());
		for(int i = 0;i<_idx;i++)
		{
//			if(i>0)
			{
				if(i%92==0)
				{
					gc.add(Calendar.DAY_OF_YEAR, 1);
				}
			}
			
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return sdf.format(gc.getTime());
	}

	private XYMultipleSeriesRenderer layoutEnergyDataRendererYLabels(XYMultipleSeriesRenderer _renderer, double[] _data)
	{
		_renderer.setYLabels(0);
		ArrayList<Double> dataArrList = new ArrayList<Double>();
		for(int i = 0;i<_data.length;i++)
		{
			dataArrList.add(new Double(_data[i]));
		}
		double maxY =  Collections.max(dataArrList);
		Log.e(TAG, "max of data="+maxY);
		for(int i = 100;i<maxY;i+=100)
		{
			_renderer.addYTextLabel(i, ""+i);
		}
		return _renderer;
	}
}
