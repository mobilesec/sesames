package at.sesame.fhooe.charts;

import java.util.ArrayList;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class BarChartViewFragment
extends Fragment
{
	private XYMultipleSeriesDataset mDataset;
	private XYMultipleSeriesRenderer mRenderer;
	private GraphicalView mView;
	public View onCreateView(LayoutInflater _inflater, ViewGroup _root, Bundle _savedInstance)
	{
		SesameChartHelper sch = new SesameChartHelper();
		
//		double[]x = new double[]{1,2,3,4,5,6,7,8,9,10,11,12};
		
		double[]y1 = new double[]{23,4,5,57,34,4,4,56,6,7,30,8};
		double[]y2 = new double[]{5,7,7,4,3,3,5,67,8,9,8,6};
		
		ArrayList<double[]> values = new ArrayList<double[]>();
		values.add(y1);
		values.add(y2);
		
		mDataset = sch.buildBarDataset(new String[]{"bar1","bar2"}, values);
		mRenderer = sch.buildRenderer(new int[]{Color.BLUE, Color.GREEN}, new PointStyle[]{PointStyle.CIRCLE, PointStyle.DIAMOND});
		mRenderer.setXAxisMin(-1);
		mRenderer.setXAxisMax(13);
		addGradientToRenderer();
		mView = ChartFactory.getBarChartView(getActivity(), mDataset, mRenderer, Type.DEFAULT);
//		BarChart bc;
		return mView;
	}
	
	private void addGradientToRenderer()
	{
		for(int i = 0;i<mRenderer.getSeriesRendererCount();i++)
		{
			SimpleSeriesRenderer ssr = mRenderer.getSeriesRendererAt(i);
			ssr.setGradientEnabled(true);
//			int col = Color.BLUE;
			if(i%2==0)
			{
				ssr.setGradientStart(0.0, Color.BLUE);
				ssr.setGradientStop(40, Color.GREEN);
				
			}
			else
			{
				ssr.setGradientStart(0.0, Color.YELLOW);
				ssr.setGradientStop(40, Color.RED);
			}
			
		}
	}
	
	public void updateData()
	{
		XYSeries[] series = mDataset.getSeries();
		Random r = new Random();
//		synchronized (series) 
		{
			for(XYSeries ser:series)
			{
				int len = ser.getItemCount();
				for(int i = 0;i<ser.getItemCount();i++)
				{
					ser.remove(i);
				}
				for(int i = 0;i<12;i++)
				{
					ser.add(i, r.nextDouble()*50+1);
				}
			}
		}
		
		mView.repaint();
	}
}
