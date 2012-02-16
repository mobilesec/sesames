package at.sesame.fhooe.midsd.md;


import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import at.sesame.fhooe.midsd.R;


public class MD_chartFragment 
extends Fragment
{
	private static final String TAG = "MD_chartFragment";
	private String mTitle;
	

	private XYMultipleSeriesDataset mDataset;
	private XYMultipleSeriesRenderer mRenderer;

//	public MD_chartFragment(Context _ctx, LayoutInflater _inf, String _title, XYMultipleSeriesDataset _dataset, XYMultipleSeriesRenderer _renderer)
//	{
//		mTitle = _title;
//		mLi = _inf;
//		mDataset = _dataset;
//		mRenderer = _renderer;
//	}
	
	public MD_chartFragment(String _title)
	{
		mTitle = _title;

	}
	
	public void setChart(XYMultipleSeriesDataset _dataset, XYMultipleSeriesRenderer _renderer)
	{
		mDataset = _dataset;
		mRenderer = _renderer;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return buildView(inflater);
	}

	private View buildView(LayoutInflater _li)
	{
		View v = _li.inflate(R.layout.md_chart_layout, null);
		TextView header = (TextView)v.findViewById(R.id.md_chart_layout_header_textview);
		header.setText(mTitle);
		if(null!=mDataset&&null!=mRenderer)
		{
			try
			{
				FrameLayout fl = (FrameLayout)v.findViewById(R.id.md_chart_layout_chart_container);
				if(null!=fl)
				{
					fl.addView(ChartFactory.getTimeChartView(getActivity(), mDataset, mRenderer, mTitle));
				}
			}
			catch(Exception _e)
			{
				_e.printStackTrace();

			}
		}
		return v;
	}

	public String getTitle() {
		return mTitle;
	}
}
