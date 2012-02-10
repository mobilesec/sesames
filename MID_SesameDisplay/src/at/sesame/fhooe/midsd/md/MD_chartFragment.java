package at.sesame.fhooe.midsd.md;

import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.sesame.fhooe.esmart.model.EsmartDataRow;
import at.sesame.fhooe.esmart.model.EsmartMeasurementPlace;
import at.sesame.fhooe.esmart.service.EsmartDataAccess;
import at.sesame.fhooe.lib.ui.ViewFragment;
import at.sesame.fhooe.lib.ui.charts.DefaultDatasetProvider;
import at.sesame.fhooe.lib.ui.charts.IDatasetProvider;
import at.sesame.fhooe.lib.ui.charts.IRendererProvider;
import at.sesame.fhooe.lib.ui.charts.exceptions.DatasetCreationException;
import at.sesame.fhooe.lib.ui.charts.exceptions.RendererInitializationException;
import at.sesame.fhooe.midsd.R;

public class MD_chartFragment 
extends Fragment 
{
	private static final String TAG = "MD_chartFragment";
	
	private EsmartMeasurementPlace mEmp;
	
	private static final long DATA_UPDATE_TIMEOUT = 2000000;
	
	private IDatasetProvider mDatasetProvider = new DefaultDatasetProvider();
	private IRendererProvider mRendererProvider = new MD_chart_RendererProvider();
	
	private DataUpdateThread mUpdateThread;
	
	private XYMultipleSeriesDataset mDataset;
	private XYMultipleSeriesRenderer mRenderer;
	
	private Handler mUiHandler = new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if(null!=mViewFrag)
			{
				ft.remove(mViewFrag);
			}
			mViewFrag = new ViewFragment(ChartFactory.getTimeChartView(getActivity(), mDataset, mRenderer, null));
			
			ft.add(R.id.md_chart_layout_chart_container, mViewFrag);
			ft.commit();
			return false;
		}
	});
	
	private LayoutInflater mLi;
	
	private ViewFragment mViewFrag;
	
	public MD_chartFragment(Context _ctx, EsmartMeasurementPlace _emp)
	{
		mEmp = _emp;
		
		mLi = LayoutInflater.from(_ctx);
	}
	
	

	@Override
	public void onAttach(Activity activity) 
	{
		super.onAttach(activity);
		buildView();
		if(null!=mUpdateThread)
		{
			mUpdateThread.stop();
		}
		
		mUpdateThread = new DataUpdateThread(mEmp, DATA_UPDATE_TIMEOUT);
		mUpdateThread.startUpdating();
	}



	@Override
	public void onDestroyView() {
		if(null!=mUpdateThread)
		{
			mUpdateThread.stopUpdating();
		}
		super.onDestroyView();
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return buildView();
	}
	
	private View buildView()
	{
		View v = mLi.inflate(R.layout.md_chart_layout, null, false);
		TextView header = (TextView)v.findViewById(R.id.md_chart_layout_header_textview);
		header.setText(mEmp.getName());
		return v;
	}
	
	public void updateData(ArrayList<EsmartDataRow> _data)
	{
		Log.e(TAG, "updated");
		if(null==_data)
		{
			return;
		}
		int size = _data.size();
		String[] titles = new String[]{mEmp.getName()};
		Date[] datesArr = new Date[size];
		double[] valuesArr = new double[size];
		
		for(int i = 0;i<size;i++)
		{
			EsmartDataRow edr = _data.get(i);
			datesArr[i] = edr.getDate();
			valuesArr[i] = edr.getDataValue();
		}
		
		ArrayList<Date[]>dates = new ArrayList<Date[]>(1);
		dates.add(datesArr);
		
		ArrayList<double[]>values = new ArrayList<double[]>(1);
		values.add(valuesArr);
		
		try 
		{
			mDataset = new DefaultDatasetProvider().buildDateDataset(titles, dates, values);
			
			mRendererProvider.createMultipleSeriesRenderer(mDataset);
			
			mRenderer = mRendererProvider.getRenderer();
			
			
			mUiHandler.sendEmptyMessage(0);
			
		} catch (DatasetCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		mUiHandler.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//			}
//		});
	}
	
	private class DataUpdateThread extends Thread
	{
		private long mTimeout;
		private boolean mRunning;
		private EsmartMeasurementPlace mEmp;
		
		public DataUpdateThread(EsmartMeasurementPlace _emp, long _timeout)
		{
			mEmp = _emp;
			mTimeout = _timeout;
		}
		
		public void startUpdating()
		{
			mRunning = true;
			start();
		}
		
		public void stopUpdating()
		{
			mRunning = false;
		}
		
		@Override
		public void run()
		{
			while(mRunning)
			{
				ArrayList<EsmartDataRow> data = EsmartDataAccess.getLoadProfile(mEmp.getId(),EsmartDataRow.getUrlTimeString(2011, 11, 25),
						EsmartDataRow.getUrlTimeString());
				updateData(data);
				try {
					Thread.sleep(mTimeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
