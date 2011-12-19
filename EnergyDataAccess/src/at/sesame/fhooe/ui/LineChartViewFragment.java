package at.sesame.fhooe.ui;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.tools.FitZoom;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import at.sesame.fhooe.lib.charts.SesameChartHelper;


public class LineChartViewFragment 
extends Fragment 
{
	private static final String TAG = "LineChartViewFragment";
	private String mTitle ="default";
	private XYMultipleSeriesDataset mDataSet;
	private GraphicalView mView;
	private XYMultipleSeriesRenderer mRenderer;
	private double[] mData;
//	public ChartViewFragment(Context _c, String _title)
//	{
//		mContext = _c;
//		mTitle = _title;
//	}

	private double mWinSize = 100;
	
	public LineChartViewFragment()
	{
		setTitle("Engergy consumption");
	}
	
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setHasOptionsMenu(true);
	}
	
	public LineChartViewFragment(String _title, double[] _data)
	{
		Log.e("ChartViewFragment", "title set to:"+_title);
		mTitle = _title;
		mData = _data;
	}
	
	public void setTitle(String _title)
	{
		Log.e("ChartViewFragment", "title set to:"+_title);
		mTitle = _title;
		
	}
	
	public void onCreateOptionsMenu(Menu _menu, MenuInflater _inflater)
	{
		Log.e("ChartViewFragment","onCreateOptionsMenu");
		super.onCreateOptionsMenu(_menu, _inflater);
	}
	
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstance)
	{
		super.onCreateView(_inflater, _container, _savedInstance);
		if(null==mData)
		{
			return null;
		}
//		Log.e(TAG, )
		SesameChartHelper sch = new SesameChartHelper();
//		setHasOptionsMenu(true);
		String[] titles = new String[]{mTitle};
		
		double[]x = new double[mData.length];
		for(int i = 0;i<mData.length;i++)
		{
			x[i]=i+1;
		}
		List<double[]> xValues = new ArrayList<double[]>();
		
		xValues.add(x);
//		xValues.add(x);
//		xValues.add(x);
//		xValues.add(x);
		
//		double[]yA = new double[]{5,1,2,4,8,7,4,5,6,32,1,45};
//		double[]yB = new double[]{6,12,1,98,5,6,4,6,1,3,8,34};
//		double[]yC = new double[]{1,2,3,4,5,6,7,8,9,10,11,12};
//		double[]yD = new double[]{2,5,9,4,7,8,1,2,3,41,52,13};
		
		List<double[]> yValues = new ArrayList<double[]>();
		
		yValues.add(mData);
//		yValues.add(yA);
//		yValues.add(yB);
//		yValues.add(yC);
//		yValues.add(yD);
		
//		if(null==_container)
//		{
//			Log.e("ChartViewFragment", "container was null");
//		}
		
		int[] colors = new int[]{Color.GREEN};
		PointStyle[] styles = new PointStyle[]{PointStyle.POINT};
		mDataSet = sch.buildDataset(titles, xValues, yValues);
	
		mRenderer = sch.buildEnergyDataRenderer(new GregorianCalendar(2011, 10, 25),mData,2);
//		if(null==mRenderer)
//		{
//			Log.e(TAG, "renderer was null");
//		}
//		else
//		{
//			Log.e(TAG, "renderer was ok");
//		}
//		if(null==mDataSet)
//		{
//			Log.e(TAG, "dataset was null");
//		}
//		else
//		{
//			Log.e(TAG, "dataset was ok");
//		}
//		Log.e(TAG, "dataset:"+mDataSet.getSeriesCount()+", renderer:"+mRenderer.getSeriesRendererCount());
		mView = ChartFactory.getLineChartView(getActivity(), mDataSet, mRenderer);
//		mView.setBackgroundColor(Color.WHITE);
		return mView;
	}
	
	
	public void updateDataSet(double _x, double _y)
	{
//		if(!mWindowKnown)
//		{
//			mWindowKnown = true;
//			mWinSize = mRenderer.getXAxisMax()-mRenderer.getXAxisMin();
//			Log.e("ChartViewFragment", "max:"+mRenderer.getXAxisMax()+", min:"+mRenderer.getXAxisMin());
//		}
		if(null==mDataSet||null==mView)
		{
			return;
		}
		Random r = new Random();
		for(XYSeries series:mDataSet.getSeries())
		{
			series.add(_x, r.nextDouble()*50);
		}
		mRenderer.setXAxisMax(_x+10);
		mRenderer.setXAxisMin(_x-mWinSize);
//		mView.zoomOut();
		
		mView.repaint();
	}
}
