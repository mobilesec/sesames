package at.sesame.fhooe.midsd.hd;

import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;

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

public class ComparisonFragment 
extends Fragment
implements IComparisonSelectionListener, OnCheckedChangeListener
{
	private static final String TAG = "ComparisonFragment";
	public static final String CURRENT_DATA_NAME = " aktuell";
	private enum DisplayMode
	{
		day,
		week
	}
	
	private DisplayMode mCurMode = DisplayMode.day;
	
	private FrameLayout mChartFrame;
	
	public static final String DAY_CB1_TEXT = " vor 1 Woche";
	public static final String DAY_CB2_TEXT = " vor 2 Wochen";
	public static final String DAY_CB3_TEXT = " vor 3 Wochen";
	public static final String DAY_CB4_TEXT = " vor 4 Wochen";
	
	public static final String WEEK_CB1_TEXT = "1 Woche zuvor";
	public static final String WEEK_CB2_TEXT = "2 Wochen zuvor";
	public static final String WEEK_CB3_TEXT = "3 Wochen zuvor";
	public static final String WEEK_CB4_TEXT = "4 Wochen zuvor";
	
	private RadioGroup mDayWeekGroup;
	
	private boolean[] mSelectedFilters = new boolean[]{false, false, false, false};
	private ComparisonRoom mRoom = ComparisonRoom.edv1;
	
	private HD_Comparison_RendererProvider mChartRendererProvider = new HD_Comparison_RendererProvider();
	private HD_Comparison_Bar_RendererProvider mBarRendererProvider = new HD_Comparison_Bar_RendererProvider();
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
		return new ComparisonSelectionFragment(this, DAY_CB1_TEXT, DAY_CB2_TEXT, DAY_CB3_TEXT, DAY_CB4_TEXT);
	}
	
	private ComparisonSelectionFragment createWeekCSF()
	{
		return new ComparisonSelectionFragment(this, WEEK_CB1_TEXT, WEEK_CB2_TEXT, WEEK_CB3_TEXT, WEEK_CB4_TEXT);
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
		String title  = mRoom.name();
		XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
		data.addSeries(DataSimulator.createTimeSeries(title+CURRENT_DATA_NAME, new Date(), 100));
		if(mSelectedFilters[0])
		{
			data.addSeries(DataSimulator.createTimeSeries(title + DAY_CB1_TEXT, new Date(), 100));
		}
		if(mSelectedFilters[1])
		{
			data.addSeries(DataSimulator.createTimeSeries(title + DAY_CB2_TEXT, new Date(), 100));
		}
		if(mSelectedFilters[2])
		{
			data.addSeries(DataSimulator.createTimeSeries(title + DAY_CB3_TEXT, new Date(), 100));
		}
		if(mSelectedFilters[3])
		{
			data.addSeries(DataSimulator.createTimeSeries(title + DAY_CB4_TEXT, new Date(), 100));
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
		Log.e(TAG, "update week chart");
		int cnt = 1;
		ArrayList<String> titles = new ArrayList<String>();
		titles.add(CURRENT_DATA_NAME+mRoom.name());
		if(mSelectedFilters[0])
		{
			titles.add(mRoom.name()+WEEK_CB1_TEXT);
		}
		if(mSelectedFilters[1])
		{
			titles.add(mRoom.name()+WEEK_CB2_TEXT);
		}
		if(mSelectedFilters[2])
		{
			titles.add(mRoom.name()+WEEK_CB3_TEXT);
		}
		if(mSelectedFilters[3])
		{
			titles.add(mRoom.name()+WEEK_CB4_TEXT);
		}
		XYMultipleSeriesDataset dataset = DataSimulator.createBarSeries(titles);
		try {
			mBarRendererProvider.createMultipleSeriesRenderer(dataset);
			setChartView(ChartFactory.getBarChartView(getActivity(), dataset, mBarRendererProvider.getRenderer(), Type.DEFAULT));
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
		
		mChartFrame.removeAllViews();
		
		if(null!=_v)
		{
			mChartFrame.addView(_v);
//			_v.invalidate();
			mChartFrame.invalidate();			
		}
		
	}

	@Override
	public void notifyRoomSelection(ComparisonRoom _room) 
	{
		mRoom = _room;
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
