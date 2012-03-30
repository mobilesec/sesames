package at.sesame.fhooe.tablet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.data.SesameDataCache.DataSource;
import at.sesame.fhooe.lib2.data.SesameDataContainer;
import at.sesame.fhooe.lib2.data.SesameMeasurement;
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;
import at.sesame.fhooe.lib2.data.simulation.DataSimulator;
import at.sesame.fhooe.lib2.ui.charts.DefaultDatasetProvider;
import at.sesame.fhooe.lib2.ui.charts.exceptions.DatasetCreationException;
import at.sesame.fhooe.lib2.ui.charts.exceptions.RendererInitializationException;
import at.sesame.fhooe.lib2.util.DateHelper;



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

	//	private View mChartView;

	private View mView;

	private Context mCtx;

	private ComparisonSelectionViewProvider mComparisonSelectionViewProvider;

	private SesameDataCache mDataCache = SesameDataCache.getInstance(DataSource.semantic_repo);
	private SesameMeasurementPlace mEdv1Place;
	private SesameMeasurementPlace mEdv3Place;
	private SesameMeasurementPlace mEdv6Place;

	private SesameMeasurementPlace mCurRoom;

	private DefaultDatasetProvider mDatasetProvider = new DefaultDatasetProvider();

	public ComparisonViewProvider(Context _ctx)
	{
		mCtx = _ctx;
		mRoomName = mCtx.getString(R.string.global_Room1_name);
		mComparisonSelectionViewProvider = new ComparisonSelectionViewProvider(mCtx, this, mCurMode);
		mChartRendererProvider = new HD_Comparison_Line_RendererProvider(mCtx, true);
		mBarRendererProvider = new HD_Comparison_Bar_RendererProvider(mCtx);

		ArrayList<SesameMeasurementPlace> places = mDataCache.getEnergyMeasurementPlaces();
		mEdv1Place = places.get(0);
		mEdv3Place = places.get(3);
		mEdv6Place = places.get(2);

		mCurRoom = mEdv1Place;
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
//		if(null==mChartFrame)
		{
			mChartFrame = (FrameLayout)mView.findViewById(R.id.hd_comparison_layout_chartFrame);

		}
//		if(null==mSelectionFrame)
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
		//		SesameDataContainer edv1Readings = mDataCache.getEnergyReadings(mEdv1Place, DateHelper.getFirstDateToday(), new Date());
		//		SesameDataContainer edv3Readings = mDataCache.getEnergyReadings(mEdv3Place, DateHelper.getFirstDateToday(), new Date());
		//		SesameDataContainer edv6Readings = mDataCache.getEnergyReadings(mEdv6Place, DateHelper.getFirstDateToday(), new Date());
		SesameDataContainer readings = mDataCache.getAllEnergyReadings(mCurRoom);
		//		XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
		ArrayList<String> titles = new ArrayList<String>();
		List<Date[]> dates = new ArrayList<Date[]>();
		List<double[]> values = new ArrayList<double[]>();
		titles.add(mCtx.getString(R.string.global_current));

		ArrayList<SesameMeasurement> currentMeasurements = SesameDataContainer.filterByDate(readings.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(0), DateHelper.getSchoolEndXDaysAgo(0));
		Log.d(TAG, "current:"+Arrays.toString((SesameMeasurement[]) currentMeasurements.toArray(new SesameMeasurement[currentMeasurements.size()])));
		Date[] currentDates = SesameDataContainer.getTimeStampArray(currentMeasurements);
		dates.add(currentDates);
		values.add(SesameDataContainer.getValueArray(currentMeasurements));

		//		data.addSeries(DataSimulator.createTimeSeries(mRoomName+mCtx.getString(R.string.global_current), new Date(), 100));
		if(mSelectedFilters[0])
		{
			//			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb1_text), new Date(), 100));
			titles.add(mCtx.getString(R.string.hd_comparison_day_cb1_text));
			ArrayList<SesameMeasurement> oneWeekAgo = SesameDataContainer.filterByDate(readings.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(7), DateHelper.getSchoolEndXDaysAgo(7));
			Log.d(TAG, "one week ago:"+Arrays.toString((SesameMeasurement[]) oneWeekAgo.toArray(new SesameMeasurement[oneWeekAgo.size()])));
			dates.add(currentDates);
			values.add(SesameDataContainer.getValueArray(oneWeekAgo));
		}
		if(mSelectedFilters[1])
		{
			//			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb2_text), new Date(), 100));
			titles.add(mCtx.getString(R.string.hd_comparison_day_cb2_text));
			ArrayList<SesameMeasurement> twoWeeksAgo = SesameDataContainer.filterByDate(readings.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(14), DateHelper.getSchoolEndXDaysAgo(14));
			Log.d(TAG, "two weeks ago:"+Arrays.toString((SesameMeasurement[]) twoWeeksAgo.toArray(new SesameMeasurement[twoWeeksAgo.size()])));
			dates.add(currentDates);
			values.add(SesameDataContainer.getValueArray(twoWeeksAgo));
		}
		if(mSelectedFilters[2])
		{
			//			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb3_text), new Date(), 100));
			titles.add(mCtx.getString(R.string.hd_comparison_day_cb3_text));
			ArrayList<SesameMeasurement> threeWeeksAgo = SesameDataContainer.filterByDate(readings.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(21), DateHelper.getSchoolEndXDaysAgo(21));
			Log.d(TAG, "three weeks ago:"+Arrays.toString((SesameMeasurement[]) threeWeeksAgo.toArray(new SesameMeasurement[threeWeeksAgo.size()])));
			dates.add(currentDates);
			values.add(SesameDataContainer.getValueArray(threeWeeksAgo));
		}
		if(mSelectedFilters[3])
		{
			//			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb4_text), new Date(), 100));
			titles.add(mCtx.getString(R.string.hd_comparison_day_cb4_text));
			ArrayList<SesameMeasurement> fourWeeksAgo = SesameDataContainer.filterByDate(readings.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(28), DateHelper.getSchoolEndXDaysAgo(28));
			Log.d(TAG, "four weeks ago:"+Arrays.toString((SesameMeasurement[]) fourWeeksAgo.toArray(new SesameMeasurement[fourWeeksAgo.size()])));
			dates.add(currentDates);
			values.add(SesameDataContainer.getValueArray(fourWeeksAgo));
		}
		try {
			XYMultipleSeriesDataset data = mDatasetProvider.buildDateDataset((String[]) titles.toArray(new String[titles.size()]), dates, values);
			mChartRendererProvider.createMultipleSeriesRenderer(data);
			setChartView(ChartFactory.getTimeChartView(mCtx, data, mChartRendererProvider.getRenderer(), ""));
		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatasetCreationException e) {
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
			Log.e(TAG, "setting chart view with week/bar chart");
			setChartView(ChartFactory.getBarChartView(mCtx, dataset, mBarRendererProvider.getRenderer(), Type.DEFAULT));

		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setChartView(GraphicalView _v)
	{
		//		if(null==mChartFrame)
		//		{
		//			return;
		//		}
		//		mChartView = _v;
		mChartFrame.removeAllViews();
		//		mChartFrame.invalidate();
		mChartFrame.addView(_v);
		_v.repaint();
		mChartFrame.invalidate();
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
		if(_room.equals(mCtx.getString(R.string.global_Room1_name)))
		{
			mCurRoom = mEdv1Place;
		}
		else if(_room.equals(mCtx.getString(R.string.global_Room3_name)))
		{
			mCurRoom = mEdv3Place;
		}
		else if(_room.equals(mCtx.getString(R.string.global_Room6_name)))
		{
			mCurRoom = mEdv6Place;
		}
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
		//		Log.e(TAG, "checkedRadioButt=")
		switch(mDayWeekGroup.getCheckedRadioButtonId())
		{
		case R.id.hd_comparison_layout_dayRadioButt:
			mCurMode = DisplayMode.day;
			Log.e(TAG, "display mode set to day");
			break;
		case R.id.hd_comparison_layout_weekRadioButt:
			mCurMode = DisplayMode.week;
			Log.e(TAG, "display mode set to week");
			break;
		default:
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
