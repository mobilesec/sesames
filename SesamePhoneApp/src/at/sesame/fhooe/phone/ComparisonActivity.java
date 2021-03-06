package at.sesame.fhooe.phone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.data.SesameDataContainer;
import at.sesame.fhooe.lib2.data.SesameMeasurement;
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;
import at.sesame.fhooe.lib2.pms.IComparisonSelectionListener;
import at.sesame.fhooe.lib2.ui.charts.DefaultDatasetProvider;
import at.sesame.fhooe.lib2.ui.charts.exceptions.DatasetCreationException;
import at.sesame.fhooe.lib2.ui.charts.exceptions.RendererInitializationException;
import at.sesame.fhooe.lib2.util.ArrayHelper;
import at.sesame.fhooe.lib2.util.DateHelper;
import at.sesame.fhooe.phone.ComparisonSelectionActivity.DisplayMode;


public class ComparisonActivity 
extends Activity 
implements OnCheckedChangeListener, IComparisonSelectionListener, OnClickListener
{
	private static final String TAG = "ComparisonActivity";

	private static final int UPDATE_PERIOD = 15000;
	private Timer mUpdateTimer;

	private DisplayMode mCurMode = DisplayMode.day;

	private FrameLayout mChartFrame;

	private RadioGroup mDayWeekGroup;
	
	private ImageButton mFilterButt;

	private boolean[] mSelectedFilters = new boolean[]{false, false, false, false};

	private PhoneChartRendererProvider mChartRendererProvider;
	private PhoneBarRendererProvider mBarRendererProvider;

	private String mRoomName;


	private SesameMeasurementPlace mCurRoom;

	private DefaultDatasetProvider mDatasetProvider = new DefaultDatasetProvider();
	
	
//	private SesameMeasurementPlace mEdv1Place;
//	private SesameMeasurementPlace mEdv3Place;
//	private SesameMeasurementPlace mEdv6Place;

//	private ComparisonSelectionFragment mCsf;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		mRoomName = getString(R.string.global_Room1_name);
		//		mComparisonSelectionViewProvider = new ComparisonSelectionFragment(mCtx, this, mCurMode);
		mChartRendererProvider = new PhoneChartRendererProvider(getApplicationContext(), true);
		mBarRendererProvider = new PhoneBarRendererProvider(getApplicationContext());

//		ArrayList<SesameMeasurementPlace> places = mDataCache.getEnergyMeasurementPlaces();
//		mEdv1Place = places.get(4);
//		mEdv3Place = places.get(3);
//		mEdv6Place = places.get(5);
		mCurRoom = SesameDataCache.EDV1_PLACE;
		
		if(null!=savedInstanceState)
		{
			mRoomName = savedInstanceState.getString(ComparisonSelectionActivity.BUNDLE_ROOM_NAME_KEY);
			mSelectedFilters = savedInstanceState.getBooleanArray(ComparisonSelectionActivity.BUNDLE_TIMES_KEY);
			mCurMode = DisplayMode.valueOf(savedInstanceState.getString(ComparisonSelectionActivity.BUNDLE_MODE_KEY));
		}
		initializeView();
	}
	
	private class UpdateTask extends TimerTask
	{

		@Override
		public void run() 
		{
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() 
				{
					updateChart();
				}
			});
			
		}
		
	}
	
	private void stopUpdates()
	{
		if(null != mUpdateTimer)
		{
			mUpdateTimer.cancel();
			mUpdateTimer.purge();
		}
	}
	
	private void startUpdates()
	{
		stopUpdates();
		mUpdateTimer = new Timer();
		mUpdateTimer.schedule(new UpdateTask(), 0, UPDATE_PERIOD);
	}

	public void initializeView() 
	{
		//		LayoutInflater inflater = LayoutInflater.from(mCtx);
		setContentView(R.layout.hd_comparison_layout);
		//		if(null==mChartFrame)
		{
			mChartFrame = (FrameLayout)findViewById(R.id.hd_comparison_layout_chartFrame);

		}
		//		if(null==mSelectionFrame)
		//		{
		////			mSelectionFrame = (FrameLayout)mView.findViewById(R.id.hd_comparison_layout_comparisonFrame);
		////						mSelectionFrame.removeAllViews();
		////			mSelectionFrame.addView(mComparisonSelectionViewProvider.getComparisonSelectionView());
		//		}

//		mCsf = (ComparisonSelectionFragment)getSupportFragmentManager().findFragmentById(R.id.comparisonSelectionFragment1);
//		mCsf.setContext(getApplicationContext());
//		mCsf.setComparisonSelectionListener(this);
//		mCsf.setDisplayMode(mCurMode);
		mDayWeekGroup = (RadioGroup)findViewById(R.id.hd_comparison_layout_day_week_group);
		mDayWeekGroup.setOnCheckedChangeListener(this);
		mFilterButt = (ImageButton)findViewById(R.id.filterButton);
		mFilterButt.setOnClickListener(this);
		updateChart();
		//		mView.invalidate();
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
		//		SesameDataContainer edv1Readings = mDataCache.getEnergyReadings(mEdv1Place, DateHelper.getFirstDateToday(), new Date());
		//		SesameDataContainer edv3Readings = mDataCache.getEnergyReadings(mEdv3Place, DateHelper.getFirstDateToday(), new Date());
		//		SesameDataContainer edv6Readings = mDataCache.getEnergyReadings(mEdv6Place, DateHelper.getFirstDateToday(), new Date());
		SesameDataContainer readings = SesameDataCache.getInstance().getAllEnergyReadings(mCurRoom);
//		Log.e(TAG, "data to filter:"+Arrays.toString((SesameMeasurement[]) readings.getMeasurements().toArray(new SesameMeasurement[readings.getMeasurements().size()])));
		//		XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
		Date[] todayDates = DateHelper.getDatesBetweenDates(DateHelper.getSchoolStartXDaysAgo(0), DateHelper.getSchoolEndXDaysAgo(0), Calendar.MINUTE, 15);
		ArrayList<String> titles = new ArrayList<String>();
		List<Date[]> dates = new ArrayList<Date[]>();
		List<double[]> values = new ArrayList<double[]>();
		titles.add(mRoomName + getString(R.string.global_current));

		ArrayList<SesameMeasurement> currentMeasurements = SesameDataContainer.filterByDate(readings.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(0), DateHelper.getSchoolEndXDaysAgo(0), false);
//		Log.d(TAG, "current:"+Arrays.toString((SesameMeasurement[]) currentMeasurements.toArray(new SesameMeasurement[currentMeasurements.size()])));
		Date[] currentDates = SesameDataContainer.getTimeStampArray(currentMeasurements);
		dates.add(currentDates);
		values.add(SesameDataContainer.getValueArray(currentMeasurements));

		//		data.addSeries(DataSimulator.createTimeSeries(mRoomName+mCtx.getString(R.string.global_current), new Date(), 100));
		if(mSelectedFilters[0])
		{
			//			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb1_text), new Date(), 100));
			titles.add(mRoomName + getString(R.string.hd_comparison_day_cb1_text));
			ArrayList<SesameMeasurement> oneWeekAgo = SesameDataContainer.filterByDate(readings.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(7), DateHelper.getSchoolEndXDaysAgo(7), false);
//			Log.d(TAG, "one week ago:"+Arrays.toString((SesameMeasurement[]) oneWeekAgo.toArray(new SesameMeasurement[oneWeekAgo.size()])));
//			dates.add(moveToCurrentDay(SesameDataContainer.getTimeStampArray(oneWeekAgo)));
			dates.add(todayDates);
			values.add(SesameDataContainer.getValueArray(oneWeekAgo));
		}
		if(mSelectedFilters[1])
		{
			//			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb2_text), new Date(), 100));
			titles.add(mRoomName + getString(R.string.hd_comparison_day_cb2_text));
			ArrayList<SesameMeasurement> twoWeeksAgo = SesameDataContainer.filterByDate(readings.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(14), DateHelper.getSchoolEndXDaysAgo(14), false);
//			Log.d(TAG, "two weeks ago:"+Arrays.toString((SesameMeasurement[]) twoWeeksAgo.toArray(new SesameMeasurement[twoWeeksAgo.size()])));
//			dates.add(moveToCurrentDay(SesameDataContainer.getTimeStampArray(twoWeeksAgo)));
			dates.add(todayDates);
			values.add(SesameDataContainer.getValueArray(twoWeeksAgo));
		}
		if(mSelectedFilters[2])
		{
			//			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb3_text), new Date(), 100));
			titles.add(mRoomName + getString(R.string.hd_comparison_day_cb3_text));
			ArrayList<SesameMeasurement> threeWeeksAgo = SesameDataContainer.filterByDate(readings.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(21), DateHelper.getSchoolEndXDaysAgo(21), false);
//			Log.d(TAG, "three weeks ago:"+Arrays.toString((SesameMeasurement[]) threeWeeksAgo.toArray(new SesameMeasurement[threeWeeksAgo.size()])));
//			dates.add(moveToCurrentDay(SesameDataContainer.getTimeStampArray(threeWeeksAgo)));
			dates.add(todayDates);
			values.add(SesameDataContainer.getValueArray(threeWeeksAgo));
		}
		if(mSelectedFilters[3])
		{
			//			data.addSeries(DataSimulator.createTimeSeries(mRoomName + mCtx.getString(R.string.hd_comparison_day_cb4_text), new Date(), 100));
			titles.add(mRoomName + getString(R.string.hd_comparison_day_cb4_text));
			ArrayList<SesameMeasurement> fourWeeksAgo = SesameDataContainer.filterByDate(readings.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(28), DateHelper.getSchoolEndXDaysAgo(28), false);
//			Log.d(TAG, "four weeks ago:"+Arrays.toString((SesameMeasurement[]) fourWeeksAgo.toArray(new SesameMeasurement[fourWeeksAgo.size()])));
//			dates.add(moveToCurrentDay(SesameDataContainer.getTimeStampArray(fourWeeksAgo)));
			dates.add(todayDates);
			values.add(SesameDataContainer.getValueArray(fourWeeksAgo));
		}
		try {
			XYMultipleSeriesDataset data = mDatasetProvider.buildDateDataset((String[]) titles.toArray(new String[titles.size()]), dates, values);
			mChartRendererProvider.createMultipleSeriesRenderer(data);
			setChartView(ChartFactory.getTimeChartView(getApplicationContext(), data, mChartRendererProvider.getRenderer(), ""));
		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatasetCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Date[] moveToCurrentDay(Date[] _dates)
	{
		Date now = new Date();
		int nowDay = now.getDate();
		int nowMonth = now.getMonth();
		int nowYear = now.getYear();
		for(int i = 0;i<_dates.length;i++)
		{
			Date d = _dates[i];
			d.setDate(nowDay);
			d.setMonth(nowMonth);
			d.setYear(nowYear);
		}
		return _dates;
	}

	private void updateWeekChart()
	{
		double multiplicationFactor = 0.00025d;
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<double[]>values = new ArrayList<double[]>();
		SesameDataContainer data = SesameDataCache.getInstance().getAllEnergyReadings(mCurRoom);

		titles.add(mRoomName + getString(R.string.global_current));
		
		double[] currentValues = extractWeekDayValues(data, 0);
		values.add(ArrayHelper.multiply(currentValues, multiplicationFactor));
		
		if(mSelectedFilters[0])
		{
			titles.add(mRoomName+getString(R.string.hd_comparison_week_cb1_text));
			double[] oneWeekAgoValues = extractWeekDayValues(data, 1);
			values.add(ArrayHelper.multiply(oneWeekAgoValues, multiplicationFactor));
		}
		if(mSelectedFilters[1])
		{
			titles.add(mRoomName+getString(R.string.hd_comparison_week_cb2_text));
			double[] twoWeeksAgoValues = extractWeekDayValues(data, 2);
			values.add(ArrayHelper.multiply(twoWeeksAgoValues, multiplicationFactor));
		}
		if(mSelectedFilters[2])
		{
			titles.add(mRoomName+getString(R.string.hd_comparison_week_cb3_text));
			double[] threeWeeksAgoValues = extractWeekDayValues(data, 3);
			values.add(ArrayHelper.multiply(threeWeeksAgoValues, multiplicationFactor));
		}
		if(mSelectedFilters[3])
		{
			titles.add(mRoomName+getString(R.string.hd_comparison_week_cb4_text));
			double[] fourWeeksAgoValues = extractWeekDayValues(data, 4);
			values.add(ArrayHelper.multiply(fourWeeksAgoValues,multiplicationFactor));
		}
//		XYMultipleSeriesDataset dataset = DataSimulator.createBarSeries(titles);
		try {
			mDatasetProvider.createBarDataSet(titles, values);
			XYMultipleSeriesDataset dataset = mDatasetProvider.getDataset();
			mBarRendererProvider.createMultipleSeriesRenderer(dataset);
//			Log.e(TAG, "setting chart view with week/bar chart");
			setChartView(ChartFactory.getBarChartView(getApplicationContext(), dataset, mBarRendererProvider.getRenderer(), Type.DEFAULT));

		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatasetCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private double[] extractWeekDayValues(SesameDataContainer _data, int _numWeeksAgo)
	{
		double[] res = new double[7];
		Date monday = DateHelper.getWeekDayXWeeksAgo(Calendar.MONDAY, _numWeeksAgo);
		Date tuesday = DateHelper.getWeekDayXWeeksAgo(Calendar.TUESDAY, _numWeeksAgo);
		Date wednesday = DateHelper.getWeekDayXWeeksAgo(Calendar.WEDNESDAY, _numWeeksAgo);
		Date thursday = DateHelper.getWeekDayXWeeksAgo(Calendar.THURSDAY, _numWeeksAgo);
		Date friday = DateHelper.getWeekDayXWeeksAgo(Calendar.FRIDAY, _numWeeksAgo);
		Date saturday = DateHelper.getWeekDayXWeeksAgo(Calendar.SATURDAY, _numWeeksAgo);
		Date sunday = DateHelper.getWeekDayXWeeksAgo(Calendar.SUNDAY, _numWeeksAgo);
		
		ArrayList<SesameMeasurement> mondayMeasurements = SesameDataContainer.filterByDate(_data.getMeasurements(), DateHelper.getDayStart(monday), DateHelper.getDayEnd(monday), true);
		res[0] = SesameDataContainer.sumValues(mondayMeasurements);
		
		ArrayList<SesameMeasurement> tuesdayMeasurements = SesameDataContainer.filterByDate(_data.getMeasurements(), DateHelper.getDayStart(tuesday), DateHelper.getDayEnd(tuesday), true);
		res[1] = SesameDataContainer.sumValues(tuesdayMeasurements);
		
		ArrayList<SesameMeasurement> wednesdayMeasurements = SesameDataContainer.filterByDate(_data.getMeasurements(), DateHelper.getDayStart(wednesday), DateHelper.getDayEnd(wednesday), true);
		res[2] = SesameDataContainer.sumValues(wednesdayMeasurements);
		
		ArrayList<SesameMeasurement> thursdayMeasurements = SesameDataContainer.filterByDate(_data.getMeasurements(), DateHelper.getDayStart(thursday), DateHelper.getDayEnd(thursday), true);
		res[3] = SesameDataContainer.sumValues(thursdayMeasurements);
		
		ArrayList<SesameMeasurement> fridayMeasurements = SesameDataContainer.filterByDate(_data.getMeasurements(), DateHelper.getDayStart(friday), DateHelper.getDayEnd(friday), true);
		res[4] = SesameDataContainer.sumValues(fridayMeasurements);
		
		ArrayList<SesameMeasurement> saturdayMeasurements = SesameDataContainer.filterByDate(_data.getMeasurements(), DateHelper.getDayStart(saturday), DateHelper.getDayEnd(saturday), true);
		res[5] = SesameDataContainer.sumValues(saturdayMeasurements);
		
		ArrayList<SesameMeasurement> sundayMeasurements = SesameDataContainer.filterByDate(_data.getMeasurements(), DateHelper.getDayStart(sunday), DateHelper.getDayEnd(sunday), true);
		res[6] = SesameDataContainer.sumValues(sundayMeasurements);
		
		return res;
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
		//		mChartFrame.invalidate();
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
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch(mDayWeekGroup.getCheckedRadioButtonId())
		{
		case R.id.hd_comparison_layout_dayRadioButt:
			mCurMode = DisplayMode.day;
//			Log.e(TAG, "display mode set to day");
			break;
		case R.id.hd_comparison_layout_weekRadioButt:
			mCurMode = DisplayMode.week;
//			Log.e(TAG, "display mode set to week");
			break;
		default:
			break;
		}
//		mComparisonSelectionViewProvider.setDisplayMode(mCurMode);
//		mCsf.setDisplayMode(mCurMode);
//		initializeView();
		updateChart();

	}
	
	


	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		mSelectedFilters = arg2.getBooleanArrayExtra(ComparisonSelectionActivity.BUNDLE_TIMES_KEY);
		mRoomName = arg2.getStringExtra(ComparisonSelectionActivity.BUNDLE_ROOM_NAME_KEY);
		notifyRoomSelection(mRoomName);
		
		
	}

	@Override
	public void notifyRoomSelection(String _room) {
		mRoomName = _room;
		if(_room.equals(getString(R.string.global_Room1_name)))
		{
			mCurRoom = SesameDataCache.EDV1_PLACE;
		}
		else if(_room.equals(getString(R.string.global_Room3_name)))
		{
			mCurRoom = SesameDataCache.EDV3_PLACE;
		}
		else if(_room.equals(getString(R.string.global_Room6_name)))
		{
			mCurRoom = SesameDataCache.EDV6_PLACE;
		}
		updateChart();
		//		initializeView();

	}

	@Override
	public void notifyComparisonSelection(boolean[] _selection) {
		mSelectedFilters = _selection;
//		updateChart();

	}

	@Override
	protected void onDestroy() {
		stopUpdates();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		stopUpdates();
		super.onPause();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		startUpdates();
	}
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item)
//	{
//		Log.e(TAG, "optionsMenuSelected");
//		switch(item.getItemId())
//		{
//		case R.id.pms_item:
//			startActivity(getPmsIntent());
//			finish();
//			return true;
//		case R.id.today_item:
//			startActivity(getTodayIntent());
//			finish();
//			return true;
//		case R.id.week_item:
////			startActivity(getComparisonIntent());
////			finish();
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}

	@Override
	public void onClick(View v) 
	{
		showFilterActivity();
		
	}

	private void showFilterActivity()
	{
		Intent i = new Intent(this, ComparisonSelectionActivity.class);
		i.putExtras(getState());
		startActivityForResult(i, 0);
	}
	
	private Bundle getState()
	{
		Bundle b = new Bundle();
		b.putString(ComparisonSelectionActivity.BUNDLE_MODE_KEY, mCurMode.toString());
		b.putString(ComparisonSelectionActivity.BUNDLE_ROOM_NAME_KEY, mRoomName);
		b.putBooleanArray(ComparisonSelectionActivity.BUNDLE_TIMES_KEY, mSelectedFilters);
		return b;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		outState.putAll(getState());
		super.onSaveInstanceState(outState);
	}
}
