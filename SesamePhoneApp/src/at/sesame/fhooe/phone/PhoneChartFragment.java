package at.sesame.fhooe.phone;


import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;



public class PhoneChartFragment 
extends Fragment
{
	private static final String TAG = "MD_chartFragment";
	private String mTitle;

	private View mChartView;

	private XYMultipleSeriesDataset mDataset;
	private XYMultipleSeriesRenderer mRenderer;

	private Handler mUiHandler;
	private Context mCtx;

	private FrameLayout mChartContainer;


	//	public MD_chartFragment(Context _ctx, LayoutInflater _inf, String _title, XYMultipleSeriesDataset _dataset, XYMultipleSeriesRenderer _renderer)
	//	{
	//		mTitle = _title;
	//		mLi = _inf;
	//		mDataset = _dataset;
	//		mRenderer = _renderer;
	//	}

	public PhoneChartFragment()
	{

	}
	public PhoneChartFragment(String _title, Context _ctx, Handler _uiHandler)
	{
		setRetainInstance(true);
		mTitle = _title;
		mUiHandler = _uiHandler;
		mCtx = _ctx;
	}

	public void setChart(XYMultipleSeriesDataset _dataset, XYMultipleSeriesRenderer _renderer)
	{
		mDataset = _dataset;
//		XYSeries series = mDataset.getSeries()[0];
//		for(int i = 0;i<series.getItemCount();i++)
//		{
//			Log.d(TAG, "passed value for chart:"+series.getY(i));
//		}
		Log.d(TAG, "num entries in first set:"+_dataset.getSeries()[0].getItemCount());
//		Log.d(TAG, "num entries in second set:"+_dataset.getSeries()[1].getItemCount());
		mRenderer = _renderer;
//		refreshUi(LayoutInflater.from(mCtx));
		refreshChart();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.md_chart_layout, null);
		TextView header = (TextView)v.findViewById(R.id.md_chart_layout_header_textview);
		header.setText(mTitle);
//		if(null==mChartContainer)
		{
			mChartContainer = (FrameLayout)v.findViewById(R.id.md_chart_layout_chart_container);
		}

		if(null!=mDataset&&null!=mRenderer)
		{

//			mUiHandler.post(new Runnable() {
//
//				@Override
//				public void run() {
					mChartView = ChartFactory.getTimeChartView(mCtx, mDataset, mRenderer,null);
					mChartContainer.removeAllViews();
					mChartContainer.invalidate();
					mChartContainer.addView(mChartView);
					mChartContainer.invalidate();
//				}
//			});

		}
		return v;
	}

	private void refreshChart()
	{
		mUiHandler.post(new Runnable() {
			
			@Override
			public void run() {
				mChartView = ChartFactory.getLineChartView(mCtx, mDataset, mRenderer);
//				mChartContainer.invalidate();
				
			}
		});
	}

	public String getTitle() {
		return mTitle;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


}
