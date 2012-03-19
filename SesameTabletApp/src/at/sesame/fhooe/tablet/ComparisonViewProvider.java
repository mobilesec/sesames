package at.sesame.fhooe.tablet;

import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import at.sesame.fhooe.lib.data.simulation.DataSimulator;
import at.sesame.fhooe.lib.ui.charts.exceptions.RendererInitializationException;


@SuppressWarnings("unused")
public class ComparisonViewProvider 
implements IComparisonSelectionListener, OnCheckedChangeListener
{
	private static final String TAG = "ComparisonFragment";

	public enum DisplayMode
	{
		day,
		week
	}
	
	private DisplayMode mCurMode = DisplayMode.day;
	
	private FrameLayout mChartFrame;
	private FrameLayout mSelectionFrame;
	
	private RadioGroup mDayWeekGroup;
	
	private boolean[] mSelectedFilters = new boolean[]{false, false, false, false};
	
	private HD_Comparison_Line_RendererProvider mChartRendererProvider;
	private HD_Comparison_Bar_RendererProvider mBarRendererProvider;
	
	private String mRoomName;
	
	private View mLastView;
	
	private View mView;
	
	private Context mCtx;
	
	private ComparisonSelectionViewProvider mComparisonSelectionViewProvider;
	
	public ComparisonViewProvider(Context _ctx)
	{
		mCtx = _ctx;
		mRoomName = mCtx.getString(R.string.global_Room1_name);
		mComparisonSelectionViewProvider = new ComparisonSelectionViewProvider(mCtx, this, mCurMode);
		mChartRendererProvider = new HD_Comparison_Line_RendererProvider(mCtx, true);
		mBarRendererProvider = new HD_Comparison_Bar_RendererProvider(mCtx);
		initializeView();
	}
	
//	@Override
//	public void onAttach(Activity activity) 
//	{
//		super.onAttach(activity);
//		
//	}

	public void initializeView() 
	{
		LayoutInflater inflater = LayoutInflater.from(mCtx);
		mView = inflater.inflate(R.layout.hd_comparison_layout, null);
		if(null==mChartFrame)
		{
			mChartFrame = (FrameLayout)mView.findViewById(R.id.hd_comparison_layout_chartFrame);
		}
		if(null==mSelectionFrame)
		{
			mSelectionFrame = (FrameLayout)mView.findViewById(R.id.hd_comparison_layout_comparisonFrame);
			mSelectionFrame.removeAllViews();
			mSelectionFrame.addView(mComparisonSelectionViewProvider.getComparisonSelectionView());
		}
		
		mDayWeekGroup = (RadioGroup)mView.findViewById(R.id.hd_comparison_layout_day_week_group);
		mDayWeekGroup.setOnCheckedChangeListener(this);
		updateChart();
//		mView.invalidate();
	}
	
//	private ComparisonSelectionViewProvider createDayCSF()
//	{
//		return new ComparisonSelectionViewProvider(getActivity(), this, DisplayMode.day);
//	}
//	
//	private ComparisonSelectionViewProvider createWeekCSF()
//	{
//		return new ComparisonSelectionViewProvider(getActivity(), this, DisplayMode.week);
//	}
	
	private void updateChart()
	{
		switch(mCurMode)
		{
		case day:
			updateDayChart();
			break;
		case week:
			updateWeekChart();
			break;
		}
	}
	
	private void updateDayChart()
	{
		XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
		data.addSeries(DataSimulator.createTimeSeries(mRoomName+mCtx.getString(R.string.global_current), new Date(), 100));
		if(mSelectedFilters[0])
		{
			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb1_text), new Date(), 100));
		}
		if(mSelectedFilters[1])
		{
			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb2_text), new Date(), 100));
		}
		if(mSelectedFilters[2])
		{
			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb3_text), new Date(), 100));
		}
		if(mSelectedFilters[3])
		{
			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb4_text), new Date(), 100));
		}
		try {
			mChartRendererProvider.createMultipleSeriesRenderer(data);
			setChartView(ChartFactory.getTimeChartView(mCtx, data, mChartRendererProvider.getRenderer(), ""));
		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void updateWeekChart()
	{
		ArrayList<String> titles = new ArrayList<String>();
		titles.add(mRoomName + mCtx.getString(R.string.global_current));
		if(mSelectedFilters[0])
		{
			titles.add(mRoomName+mCtx.getString(R.string.hd_comparison_week_cb1_text));
		}
		if(mSelectedFilters[1])
		{
			titles.add(mRoomName+mCtx.getString(R.string.hd_comparison_week_cb2_text));
		}
		if(mSelectedFilters[2])
		{
			titles.add(mRoomName+mCtx.getString(R.string.hd_comparison_week_cb3_text));
		}
		if(mSelectedFilters[3])
		{
			titles.add(mRoomName+mCtx.getString(R.string.hd_comparison_week_cb4_text));
		}
		XYMultipleSeriesDataset dataset = DataSimulator.createBarSeries(titles);
		try {
			mBarRendererProvider.createMultipleSeriesRenderer(dataset);
			setChartView(ChartFactory.getBarChartView(mCtx, dataset, mBarRendererProvider.getRenderer(), Type.DEFAULT));
			
		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void setChartView(View _v)
	{
//		if(null==mChartFrame)
//		{
//			return;
//		}
		
		mChartFrame.removeAllViews();
		mChartFrame.addView(_v);
//		_v.invalidate();
//		mChartFrame.invalidate();
//		mView.invalidate();
//		if(null!=mLastView)
//		{
//			mChartFrame.removeView(mLastView);
//		}
		
//		if(null!=_v)
//		{
//			mChartFrame.addView(_v);
//			mLastView = _v;
//			mChartFrame.getRootView().invalidate();
////			_v.invalidate();
////			mChartFrame.invalidate();			
//		}
		
	}

	@Override
	public void notifyRoomSelection(String _room) 
	{
		mRoomName = _room;
//		updateChart();
		initializeView();
	}

	@Override
	public void notifyComparisonSelection(boolean[] _selection) 
	{
		mSelectedFilters = _selection;
//		updateChart();
		initializeView();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) 
	{
		switch(mDayWeekGroup.getCheckedRadioButtonId())
		{
		case R.id.hd_comparison_layout_dayRadioButt:
			mCurMode = DisplayMode.day;
			break;
		case R.id.hd_comparison_layout_weekRadioButt:
			mCurMode = DisplayMode.week;
			break;
		}
		mComparisonSelectionViewProvider.setDisplayMode(mCurMode);
		initializeView();
	}

	public View getComparisonView()
	{
		return mView;
	}
	
}
