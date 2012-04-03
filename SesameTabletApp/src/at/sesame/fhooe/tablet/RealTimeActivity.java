package at.sesame.fhooe.tablet;

import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.data.SesameDataContainer;
import at.sesame.fhooe.lib2.data.SesameMeasurement;
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;
import at.sesame.fhooe.lib2.ui.charts.DefaultDatasetProvider;
import at.sesame.fhooe.lib2.ui.charts.exceptions.DatasetCreationException;
import at.sesame.fhooe.lib2.ui.charts.exceptions.RendererInitializationException;
import at.sesame.fhooe.lib2.util.DateHelper;

public class RealTimeActivity 
extends Activity implements OnCheckedChangeListener 
{
	private static final String TAG = "RealTimeActivity";
	private SesameDataCache mDataCache = SesameDataCache.getInstance();
	
	private boolean mShowEdv1 = true;
	private boolean mShowEdv3 = true;
	private boolean mShowEdv6 = true;
	
	private HD_chart_RendererProvider mRendererProvider;
	
	private FrameLayout mChartFrame;
	private SesameMeasurementPlace mEdv1Place;
	private SesameMeasurementPlace mEdv3Place;
	private SesameMeasurementPlace mEdv6Place;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		ArrayList<SesameMeasurementPlace> places = mDataCache.getEnergyMeasurementPlaces();
		mEdv1Place = places.get(0);
		mEdv3Place = places.get(1);
		mEdv6Place = places.get(2);
		initializeView();
	}

	private void initializeView() 
	{
		
		mRendererProvider = new HD_chart_RendererProvider(getApplicationContext(),true);
//		LayoutInflater inflater = LayoutInflater.from(mCtx);
		setContentView(R.layout.hd_realtime_layout);
		CheckBox edv1Box = (CheckBox)findViewById(R.id.hd_realtime_layout_edv1Box);
		edv1Box.setChecked(true);
		edv1Box.setOnCheckedChangeListener(this);
		
		CheckBox edv3Box = (CheckBox)findViewById(R.id.hd_realtime_layout_edv3Box);
		edv3Box.setChecked(true);
		edv3Box.setOnCheckedChangeListener(this);
		
		CheckBox edv6Box = (CheckBox)findViewById(R.id.hd_realtime_layout_edv6Box);
		edv6Box.setChecked(true);
		edv6Box.setOnCheckedChangeListener(this);
		
		mChartFrame = (FrameLayout)findViewById(R.id.hd_realtime_layout_chartFrame);
		updateChart();
	}

	private void updateChart()
	{		
//		GregorianCalendar now = new GregorianCalendar();
//		GregorianCalendar tomorrow = new GregorianCalendar();
//		tomorrow.roll(Calendar.DAY_OF_MONTH, true);
//		DataSimulator.createTimeSeries("asdf", now.getTime(), tomorrow.getTime());
//		GregorianCalendar yesterday = new GregorianCalendar();
//		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		
//		XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<Date[]> dates = new ArrayList<Date[]>();
		ArrayList<double[]> values = new ArrayList<double[]>();
		
		if(mShowEdv1)
		{
//			data.addSeries(DataSimulator.createTimeSeries(mCtx.getString(R.string.global_Room1_name), yesterday.getTime(), 100));
			titles.add(getString(R.string.global_Room1_name));
			SesameDataContainer edv1Raw = mDataCache.getAllEnergyReadings(mEdv1Place);
			if(null==edv1Raw)
			{
				Log.e(TAG, "edv1Raw was null");
			}
			ArrayList<SesameMeasurement> edv1 = SesameDataContainer.filterByDate(edv1Raw.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(14), DateHelper.getSchoolEndXDaysAgo(14));
//			Log.d(TAG, "four weeks ago:"+Arrays.toString((SesameMeasurement[]) edv1.toArray(new SesameMeasurement[edv1.size()])));
			dates.add(SesameDataContainer.getTimeStampArray(edv1));
			values.add(SesameDataContainer.getValueArray(edv1));
		}
		if(mShowEdv3)
		{
//			data.addSeries(DataSimulator.createTimeSeries(mCtx.getString(R.string.global_Room3_name), yesterday.getTime(), 100));
			titles.add(getString(R.string.global_Room3_name));
			SesameDataContainer edv3Raw = mDataCache.getAllEnergyReadings(mEdv3Place);
			ArrayList<SesameMeasurement> edv3 = SesameDataContainer.filterByDate(edv3Raw.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(14), DateHelper.getSchoolEndXDaysAgo(14));
//			Log.d(TAG, "four weeks ago:"+Arrays.toString((SesameMeasurement[]) edv1.toArray(new SesameMeasurement[edv1.size()])));
			dates.add(SesameDataContainer.getTimeStampArray(edv3));
			values.add(SesameDataContainer.getValueArray(edv3));
		}
		if(mShowEdv6)
		{
//			data.addSeries(DataSimulator.createTimeSeries(mCtx.getString(R.string.global_Room6_name), yesterday.getTime(), 100));
			titles.add(getString(R.string.global_Room6_name));
			SesameDataContainer edv6Raw = mDataCache.getAllEnergyReadings(mEdv6Place);
			ArrayList<SesameMeasurement> edv6 = SesameDataContainer.filterByDate(edv6Raw.getMeasurements(), DateHelper.getSchoolStartXDaysAgo(14), DateHelper.getSchoolEndXDaysAgo(14));
//			Log.d(TAG, "four weeks ago:"+Arrays.toString((SesameMeasurement[]) edv1.toArray(new SesameMeasurement[edv1.size()])));
			dates.add(SesameDataContainer.getTimeStampArray(edv6));
			values.add(SesameDataContainer.getValueArray(edv6));
		}
		
		try {
			XYMultipleSeriesDataset data = new DefaultDatasetProvider().buildDateDataset((String[]) titles.toArray(new String[titles.size()]), dates, values);
			mRendererProvider.createMultipleSeriesRenderer(data);
			mChartFrame.removeAllViews();
			mChartFrame.addView(ChartFactory.getTimeChartView(getApplicationContext(), data, mRendererProvider.getRenderer(), ""));
//			mChartFrame.invalidate();
		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatasetCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		switch(buttonView.getId())
		{
		case R.id.hd_realtime_layout_edv1Box:
			mShowEdv1 = isChecked;
			break;
		case R.id.hd_realtime_layout_edv3Box:
			mShowEdv3 = isChecked;
			break;
		case R.id.hd_realtime_layout_edv6Box:
			mShowEdv6 = isChecked;
			break;
		}
		updateChart();
		
	}
	

}
