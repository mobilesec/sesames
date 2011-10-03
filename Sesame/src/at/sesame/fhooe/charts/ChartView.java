/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.charts;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import at.sesame.fhooe.lib.charts.SesameChartHelper;

/**
 * this class shows one simple diagram to give an example of a possible
 * implementation of the aChartEngine
 * @author Peter Riedl
 *
 */
public class ChartView
extends Activity
{
	@Override
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		SesameChartHelper sch = new SesameChartHelper();
		
		String[] titles = new String[]{"A", "B", "C", "D"};
		double[]x = new double[]{1,2,3,4,5,6,7,8,9,10,11,12};
		List<double[]> xValues = new ArrayList<double[]>();
		
		xValues.add(x);
		xValues.add(x);
		xValues.add(x);
		xValues.add(x);
		
		double[]yA = new double[]{5,1,2,4,8,7,4,5,6,32,1,45};
		double[]yB = new double[]{6,12,1,98,5,6,4,6,1,3,8,34};
		double[]yC = new double[]{1,2,3,4,5,6,7,8,9,10,11,12};
		double[]yD = new double[]{2,5,9,4,7,8,1,2,3,41,52,13};
		
		List<double[]> yValues = new ArrayList<double[]>();
		
		yValues.add(yA);
		yValues.add(yB);
		yValues.add(yC);
		yValues.add(yD);
		
		int[] colors = new int[]{Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.GREEN};
		PointStyle[] styles = new PointStyle[]{PointStyle.CIRCLE, PointStyle.DIAMOND, PointStyle.SQUARE, PointStyle.TRIANGLE};
		GraphicalView gv  = ChartFactory.getLineChartView(this, sch.buildDataset(titles, xValues, yValues), sch.buildRenderer(colors, styles));
		setContentView(gv);
	}
}
