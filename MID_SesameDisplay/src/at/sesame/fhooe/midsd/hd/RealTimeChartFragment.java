package at.sesame.fhooe.midsd.hd;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.achartengine.ChartFactory;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.XYMultipleSeriesDataset;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import at.sesame.fhooe.lib.ui.charts.exceptions.RendererInitializationException;
import at.sesame.fhooe.midsd.R;
import at.sesame.fhooe.midsd.demo.DataSimulator;
import at.sesame.fhooe.midsd.md.MD_chart_RendererProvider;

public class RealTimeChartFragment 
extends Fragment
implements OnCheckedChangeListener
{
	private static final String TAG = "RealTimeChartFragment";
	
//	private final Context mCtx;
	
	private boolean mShowEdv1 = false;
	private boolean mShowEdv3 = false;
	private boolean mShowEdv6 = false;
	
	private FrameLayout mChartFrame;
	
	
	private HD_chart_RendererProvider mRendererProvider = new HD_chart_RendererProvider();
//	private MD_chart_RendererProvider mRendererProvider = new MD_chart_RendererProvider();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.hd_realtime_layout, null);
		CheckBox edv1Box = (CheckBox)v.findViewById(R.id.hd_realtime_layout_edv1Box);
		edv1Box.setOnCheckedChangeListener(this);
		
		CheckBox edv3Box = (CheckBox)v.findViewById(R.id.hd_realtime_layout_edv3Box);
		edv3Box.setOnCheckedChangeListener(this);
		
		CheckBox edv6Box = (CheckBox)v.findViewById(R.id.hd_realtime_layout_edv6Box);
		edv6Box.setOnCheckedChangeListener(this);
		
		mChartFrame = (FrameLayout)v.findViewById(R.id.hd_realtime_layout_chartFrame);
		return v;
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
	
	private void updateChart()
	{		
//		GregorianCalendar now = new GregorianCalendar();
//		GregorianCalendar tomorrow = new GregorianCalendar();
//		tomorrow.roll(Calendar.DAY_OF_MONTH, true);
//		DataSimulator.createTimeSeries("asdf", now.getTime(), tomorrow.getTime());
		GregorianCalendar yesterday = new GregorianCalendar();
		yesterday.roll(Calendar.DAY_OF_MONTH, false);
		
		XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
		if(mShowEdv1)
		{
			data.addSeries(DataSimulator.createTimeSeries("EDV 1", yesterday.getTime(), 100));
		}
		if(mShowEdv3)
		{
			data.addSeries(DataSimulator.createTimeSeries("EDV 3", yesterday.getTime(), 100));
		}
		if(mShowEdv6)
		{
			data.addSeries(DataSimulator.createTimeSeries("EDV 6", yesterday.getTime(), 100));
		}
		
		try {
			mRendererProvider.createMultipleSeriesRenderer(data);
			mChartFrame.removeAllViews();
			mChartFrame.addView(ChartFactory.getTimeChartView(getActivity(), data, mRendererProvider.getRenderer(), ""));
			mChartFrame.invalidate();
		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
