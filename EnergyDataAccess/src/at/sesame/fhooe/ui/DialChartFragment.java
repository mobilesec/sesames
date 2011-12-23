package at.sesame.fhooe.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.DialChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DialRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.DialRenderer.Type;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DialChartFragment 
extends Fragment 
{
	private static final String TAG = "DialChartFragment";
	private Context mCtx;
	private CategorySeries mCategories;
	private ArrayList<DialRenderer.Type> mTypes;
	private ArrayList<SimpleSeriesRenderer> mSeriesRenderers;
	private DialRenderer mRenderer;
	private GraphicalView mView;
	
	private boolean mRunning = true;
	
	private Runnable mAutoIncrementRunnable = new Runnable() {
		
		@Override
		public void run() 
		{
			while(mRunning)
			{
				Random r = new Random(System.currentTimeMillis());
			
				setValueAt("current", r.nextDouble()*mRenderer.getMaxValue());
				setValueAt("minimum", r.nextDouble()*mRenderer.getMaxValue());
				setValueAt("maximum", r.nextDouble()*mRenderer.getMaxValue());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	public void onDestroy()
	{
		super.onDestroy();
		mRunning = false;
	}
	
	public DialChartFragment(Context _ctx, String _title)
	{
		mCtx = _ctx;
		mCategories = new CategorySeries(_title);
		mTypes = new ArrayList<DialRenderer.Type>();
		mSeriesRenderers = new ArrayList<SimpleSeriesRenderer>();
		mRenderer = new DialRenderer();
		buildSampleData();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		new Thread(mAutoIncrementRunnable).start();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		
		mView =  ChartFactory.getDialChartView(mCtx, mCategories, buildDialRenderer());
		return mView;
	}
	
	public void addDataSeries(String _title, double _val, int _color, DialRenderer.Type _type)
	{
		SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
		ssr.setColor(_color);
		mSeriesRenderers.add(ssr);
		
		mCategories.add(_title, _val);
		mTypes.add(_type);
	}
	
	public void setValueAt(String _title, double _value)
	{
		int idx = getIndexForCategoryName(_title);
		if(-1<idx)
		{
			mCategories.set(idx, mCategories.getCategory(idx), _value);
		}
		mView.repaint();
	}
	
	private int getIndexForCategoryName(String _name)
	{
		for(int i = 0;i<mCategories.getItemCount();i++)
		{
			if(mCategories.getCategory(i).equals(_name))
			{
				return i;
			}
		}
		return -1;
	}
	
	private CategorySeries buildSampleData()
	{
//	    mCategories.add("Current", 75);
//	    mCategories.add("Minimum", 65);
//	    mCategories.add("Maximum", 90);
		addDataSeries("current", 75, Color.GREEN, DialRenderer.Type.ARROW);
		addDataSeries("minimum", 65, Color.RED, DialRenderer.Type.NEEDLE);
		addDataSeries("maximum", 90, Color.BLUE, DialRenderer.Type.NEEDLE);
	    return mCategories;
	}
	
	
	
	private DialRenderer buildDialRenderer()
	{
		
	    mRenderer.setChartTitleTextSize(20);
	    mRenderer.setLabelsTextSize(15);
	    mRenderer.setLegendTextSize(15);
	    mRenderer.setMargins(new int[] {20, 30, 15, 0});
	    mRenderer.setLabelsTextSize(10);
	    mRenderer.setLabelsColor(Color.WHITE);
	    mRenderer.setShowLabels(true);
//	    mRenderer.s
	    
	    for(SimpleSeriesRenderer r:mSeriesRenderers)
	    {
	    	mRenderer.addSeriesRenderer(r);
	    }
	    DialRenderer.Type[] types = mTypes.toArray(new DialRenderer.Type[mTypes.size()]);
//	    Log.e(TAG, "types:"+Arrays.toString(types));
	    mRenderer.setVisualTypes(types);
	    mRenderer.setMinValue(0);
	    mRenderer.setMaxValue(150);
//	    return ChartFactory.getDialChartIntent(context, category, renderer, "Weight indicator");
	    return mRenderer;
	}
	
	

}
