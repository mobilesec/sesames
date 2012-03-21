package at.sesame.fhooe.midsd.hd;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;

import android.app.Activity;
import android.content.res.Resources;
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
import at.sesame.fhooe.lib.data.simulation.DataSimulator;
import at.sesame.fhooe.lib.ui.charts.exceptions.RendererInitializationException;
import at.sesame.fhooe.midsd.R;

@SuppressWarnings("unused")
public class RealTimeChartFragment 
extends Fragment
implements OnCheckedChangeListener
{
	private static final String TAG = "RealTimeChartFragment";
	
//	private final Context mCtx;
	
	private boolean mShowEdv1 = true;
	private boolean mShowEdv3 = true;
	private boolean mShowEdv6 = true;
	
	private FrameLayout mChartFrame;
	
	
	private HD_chart_RendererProvider mRendererProvider;
//	private MD_chart_RendererProvider mRendererProvider = new MD_chart_RendererProvider();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.hd_realtime_layout, null);
		CheckBox edv1Box = (CheckBox)v.findViewById(R.id.hd_realtime_layout_edv1Box);
		edv1Box.setChecked(true);
		edv1Box.setOnCheckedChangeListener(this);
		
		CheckBox edv3Box = (CheckBox)v.findViewById(R.id.hd_realtime_layout_edv3Box);
		edv3Box.setChecked(true);
		edv3Box.setOnCheckedChangeListener(this);
		
		CheckBox edv6Box = (CheckBox)v.findViewById(R.id.hd_realtime_layout_edv6Box);
		edv6Box.setChecked(true);
		edv6Box.setOnCheckedChangeListener(this);
		
		mChartFrame = (FrameLayout)v.findViewById(R.id.hd_realtime_layout_chartFrame);
		updateChart();
		
		return v;
	}
	
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mRendererProvider = new HD_chart_RendererProvider(activity,true);
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
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		
		XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
		if(mShowEdv1)
		{
			data.addSeries(DataSimulator.createTimeSeries(getActivity().getString(R.string.global_Room1_name), yesterday.getTime(), 100));
		}
		if(mShowEdv3)
		{
			data.addSeries(DataSimulator.createTimeSeries(getActivity().getString(R.string.global_Room3_name), yesterday.getTime(), 100));
		}
		if(mShowEdv6)
		{
			data.addSeries(DataSimulator.createTimeSeries(getActivity().getString(R.string.global_Room6_name), yesterday.getTime(), 100));
		}
		
		try {
			mRendererProvider.createMultipleSeriesRenderer(data);
			mChartFrame.removeAllViews();
			mChartFrame.addView(ChartFactory.getTimeChartView(getActivity(), data, mRendererProvider.getRenderer(), ""));
//			mChartFrame.invalidate();
		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
