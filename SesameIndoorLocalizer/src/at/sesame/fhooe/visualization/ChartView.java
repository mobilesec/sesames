/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.visualization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import at.sesame.fhooe.util.ArrayHelper;

/**
 * this class shows one simple diagram to give an example of a possible
 * implementation of the aChartEngine
 * @author Peter Riedl
 *
 */
public class ChartView
extends Activity
{
	private static final String TAG = "ChartView";
	@Override
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		
//		String[]titles = new String[]{"A", "B", "C", "D"};
		String[] titles = getIntent().getExtras().getStringArray("titles");
//		ArrayList<double[]> allXValues = getDataFromBundle("reference");
//		ArrayList<double[]> allYValues = getDataFromBundle("calibration");
		

//		int dataLen = xValues.size();
//		Log.e(TAG, "datalen="+dataLen);
		Bundle b = getIntent().getExtras();
		double[] x = b.getDoubleArray("x");
		double[] y = b.getDoubleArray("y");
		
		double[] linRegX = b.getDoubleArray("linRegX");
		double[] linRegY = b.getDoubleArray("linRegY");
		
//		double[] logRegX = b.getDoubleArray("logRegX");
//		double[] logRegY = b.getDoubleArray("logRegY");
		
		ArrayList<double[]> xValues = new ArrayList<double[]>();
		ArrayList<double[]> yValues = new ArrayList<double[]>();
		
//		ArrayHelper.printArray(TAG, logRegX);
//		ArrayHelper.printArray(TAG, logRegY);

		xValues.add(x);
		xValues.add(linRegX);
//		xValues.add(logRegX);
		
		yValues.add(y);
		yValues.add(linRegY);
//		yValues.add(logRegY);
		
		
		int[] availableColors = new int[]{Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.GREEN};
		PointStyle[] availableStyles = new PointStyle[]{PointStyle.CIRCLE, PointStyle.DIAMOND, PointStyle.SQUARE, PointStyle.TRIANGLE};
		int[] colors = new int[xValues.size()];
		PointStyle[] styles = new PointStyle[xValues.size()];
		
		for(int i = 0;i<xValues.size();i++)
		{
			colors[i]=availableColors[i%availableColors.length];
			styles[i]=availableStyles[i%availableStyles.length];
		}
		SesameChartHelper sch = new SesameChartHelper();
		GraphicalView gv  = ChartFactory.getScatterChartView(this, sch.buildDataset(titles, xValues, yValues), sch.buildRenderer(colors, styles));
		setContentView(gv);
	}
	
	private ArrayList<double[]> getDataFromBundle(String _baseName)
	{
		Bundle b = getIntent().getExtras();
		ArrayList<double[]> res = new ArrayList<double[]>();
		int cnt = 0;
		while(true)
		{
			String name = _baseName+cnt;
			double[] curData;
			if((curData=b.getDoubleArray(name))!=null)
			{
				res.add(curData);
				Log.e(TAG, "data series length:"+curData.length);
			}
			else
			{
				return res;
			}
			cnt++;
		}
	}
}
