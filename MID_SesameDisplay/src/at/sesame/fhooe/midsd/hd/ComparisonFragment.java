package at.sesame.fhooe.midsd.hd;

import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import at.sesame.fhooe.lib.ui.charts.exceptions.RendererInitializationException;
import at.sesame.fhooe.midsd.R;
import at.sesame.fhooe.midsd.demo.DataSimulator;

@SuppressWarnings("unused")
public class ComparisonFragment 
extends Fragment
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
	
	private RadioGroup mDayWeekGroup;
	
	private boolean[] mSelectedFilters = new boolean[]{false, false, false, false};
	
	private HD_Comparison_Line_RendererProvider mChartRendererProvider;
	private HD_Comparison_Bar_RendererProvider mBarRendererProvider;
	
	private String mRoomName;
	
	private View mLastView;
	
	@Override
	public void onAttach(Activity activity) 
	{
		super.onAttach(activity);
		mRoomName = activity.getString(R.string.global_Room1_name);
		mChartRendererProvider = new HD_Comparison_Line_RendererProvider(activity);
		mBarRendererProvider = new HD_Comparison_Bar_RendererProvider(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.hd_comparison_layout, null);
		mChartFrame = (FrameLayout)v.findViewById(R.id.hd_comparison_layout_chartFrame);
		mDayWeekGroup = (RadioGroup)v.findViewById(R.id.hd_comparison_layout_day_week_group);
		mDayWeekGroup.setOnCheckedChangeListener(this);
		addCorrectComparisonSelectionFragment();
		updateChart();
		return v;
	}
	
	private void addCorrectComparisonSelectionFragment()
	{
		ComparisonSelectionFragment csf = null;
		switch(mCurMode)
		{
		case day:
			csf = createDayCSF();
			break;
		case week:
			csf = createWeekCSF();
		}
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.hd_comparison_layout_comparisonFrame, csf);
		
		ft.commit();
	}
	
	private ComparisonSelectionFragment createDayCSF()
	{
		return new ComparisonSelectionFragment(getActivity(), this, DisplayMode.day);
	}
	
	private ComparisonSelectionFragment createWeekCSF()
	{
		return new ComparisonSelectionFragment(getActivity(), this, DisplayMode.week);
	}
	
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
		data.addSeries(DataSimulator.createTimeSeries(mRoomName+getActivity().getString(R.string.global_current), new Date(), 100));
		if(mSelectedFilters[0])
		{
			data.addSeries(DataSimulator.createTimeSeries(mRoomName + getActivity().getString(R.string.hd_comparison_day_cb1_text), new Date(), 100));
		}
		if(mSelectedFilters[1])
		{
			data.addSeries(DataSimulator.createTimeSeries(mRoomName + getActivity().getString(R.string.hd_comparison_day_cb2_text), new Date(), 100));
		}
		if(mSelectedFilters[2])
		{
			data.addSeries(DataSimulator.createTimeSeries(mRoomName + getActivity().getString(R.string.hd_comparison_day_cb3_text), new Date(), 100));
		}
		if(mSelectedFilters[3])
		{
			data.addSeries(DataSimulator.createTimeSeries(mRoomName + getActivity().getString(R.string.hd_comparison_day_cb4_text), new Date(), 100));
		}
		try {
			mChartRendererProvider.createMultipleSeriesRenderer(data);
			setChartView(ChartFactory.getTimeChartView(getActivity(), data, mChartRendererProvider.getRenderer(), ""));
		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void updateWeekChart()
	{
		ArrayList<String> titles = new ArrayList<String>();
		titles.add(mRoomName + getActivity().getString(R.string.global_current));
		if(mSelectedFilters[0])
		{
			titles.add(mRoomName+getActivity().getString(R.string.hd_comparison_week_cb1_text));
		}
		if(mSelectedFilters[1])
		{
			titles.add(mRoomName+getActivity().getString(R.string.hd_comparison_week_cb2_text));
		}
		if(mSelectedFilters[2])
		{
			titles.add(mRoomName+getActivity().getString(R.string.hd_comparison_week_cb3_text));
		}
		if(mSelectedFilters[3])
		{
			titles.add(mRoomName+getActivity().getString(R.string.hd_comparison_week_cb4_text));
		}
		XYMultipleSeriesDataset dataset = DataSimulator.createBarSeries(titles);
		try {
			mBarRendererProvider.createMultipleSeriesRenderer(dataset);
			View chartView = ChartFactory.getBarChartView(getActivity(), dataset, mBarRendererProvider.getRenderer(), Type.DEFAULT);
			setChartView(chartView);
		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void setChartView(View _v)
	{
		if(null==mChartFrame)
		{
			return;
		}
		
		
		if(null!=mLastView)
		{
			mChartFrame.removeView(mLastView);
		}
		
		if(null!=_v)
		{
			mChartFrame.addView(_v);
			mLastView = _v;
			mChartFrame.getRootView().invalidate();
//			_v.invalidate();
//			mChartFrame.invalidate();			
		}
		
	}

	@Override
	public void notifyRoomSelection(String _room) 
	{
		mRoomName = _room;
		updateChart();	
	}

	@Override
	public void notifyComparisonSelection(boolean[] _selection) 
	{
		mSelectedFilters = _selection;
		updateChart();
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
		addCorrectComparisonSelectionFragment();
		updateChart();
	}
}
