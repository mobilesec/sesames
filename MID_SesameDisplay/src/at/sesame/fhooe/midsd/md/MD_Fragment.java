package at.sesame.fhooe.midsd.md;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.sesame.fhooe.lib.ui.EnergyMeter;
import at.sesame.fhooe.lib.ui.charts.DefaultDatasetProvider;
import at.sesame.fhooe.lib.ui.charts.IRendererProvider;
import at.sesame.fhooe.lib.ui.charts.exceptions.DatasetCreationException;
import at.sesame.fhooe.lib.ui.charts.exceptions.RendererInitializationException;
import at.sesame.fhooe.midsd.MID_SesameDisplayActivity;
import at.sesame.fhooe.midsd.R;
import at.sesame.fhooe.midsd.data.ISesameDataListener;
import at.sesame.fhooe.midsd.data.SesameDataContainer;
import at.sesame.fhooe.midsd.ld.INotificationListener;
import at.sesame.fhooe.midsd.ui.MeterWheelFragment;

public class MD_Fragment 
extends Fragment
implements ISesameDataListener, INotificationListener
{
	private static final String TAG = "MD_Fragment";

	private static final long FLIP_TIMEOUT = 5000;

	private Timer mFlipTimer = null;

	private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

	private int mCurFragIdx = 0;
	private Fragment mCurFrag = null;

	private MD_chartFragment mEsmartRoom1Frag;
	private MD_chartFragment mEsmartRoom3Frag;
	private MD_chartFragment mEsmartRoom6Frag;
	
	private MeterWheelFragment mEnergyMeterRoom1Frag;
	private MeterWheelFragment mEnergyMeterRoom3Frag;
	private MeterWheelFragment mEnergyMeterRoom6Frag;

	private MD_NotificationFragment mNotificationFrag;
	
	private DefaultDatasetProvider mDatasetProvider = new DefaultDatasetProvider();
	private IRendererProvider mRendererProvider;
	
	private Context mCtx;
	
	private static final int WHEEL_TEXT_SIZE = 100;
	
	private Handler mUiHandler;

	public MD_Fragment(FragmentManager _fm, Context _ctx, Handler _uiHandler)
	{
		mCtx = _ctx;
		mUiHandler = _uiHandler;
		mRendererProvider = new MD_chart_RendererProvider(mCtx);
		createFragments(_fm);
	}

	public void startFlipping()
	{
		stopFlipping();
		mFlipTimer = new Timer();
		mFlipTimer.schedule(new ViewFlipperTask(), 0, FLIP_TIMEOUT);
	}

	public void stopFlipping()
	{
		if(null!=mFlipTimer)
		{
			mFlipTimer.cancel();
			mFlipTimer.purge();
		}
	}

	public void showNextFragment()
	{

		FragmentManager fm = getFragmentManager();
		if(null==fm)
		{
			Log.e(TAG, "FragmentManager could not be loaded...");
			return;
		}
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();

		//fade
		//ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

		//slide
		ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);


		if(null!=mCurFrag)
		{
			ft.remove(mCurFrag);
		}

		Fragment temp = mFragments.get(mCurFragIdx);
		if(temp instanceof MD_NotificationFragment)
		{
			MD_NotificationFragment md = (MD_NotificationFragment)temp;
			if(null==md.getNotification() || md.getNotification().isEmpty())
			{
				incrementFragmentIndex();
//				showNextFragment();
			}
		}
		mCurFrag = mFragments.get(mCurFragIdx);
		incrementFragmentIndex();

		ft.add(R.id.md_layout_container, mCurFrag);
		ft.commit();
	}

	private void incrementFragmentIndex() {
		mCurFragIdx++;
		mCurFragIdx%=mFragments.size();
	}

	private List<Fragment> createFragments(FragmentManager _fm)
	{	
		String room1Name = mCtx.getString(R.string.global_Room1_name);
		String room3Name = mCtx.getString(R.string.global_Room3_name);
		String room6Name = mCtx.getString(R.string.global_Room6_name);
		
		mEsmartRoom1Frag = new MD_chartFragment(room1Name);
		mEsmartRoom3Frag = new MD_chartFragment(room3Name);
		mEsmartRoom6Frag = new MD_chartFragment(room6Name);
		
		mEnergyMeterRoom1Frag = new MeterWheelFragment(mCtx, mUiHandler, room1Name, 100.0f, "test1", 30.0f, WHEEL_TEXT_SIZE, 5, 250);
		mEnergyMeterRoom3Frag = new MeterWheelFragment(mCtx, mUiHandler, room3Name, 100.0f, "test1", 30.0f, WHEEL_TEXT_SIZE, 5, 250);
		mEnergyMeterRoom6Frag = new MeterWheelFragment(mCtx, mUiHandler, room6Name, 100.0f, "test1", 30.0f, WHEEL_TEXT_SIZE, 5, 250);
		
		setupEnergyMeter(mEnergyMeterRoom1Frag);
		setupEnergyMeter(mEnergyMeterRoom3Frag);
		setupEnergyMeter(mEnergyMeterRoom6Frag);
		
		mNotificationFrag = new MD_NotificationFragment(mUiHandler);

		mFragments.add(mEsmartRoom1Frag);
		mFragments.add(mEnergyMeterRoom1Frag);
		mFragments.add(mNotificationFrag);
		
		mFragments.add(mEsmartRoom3Frag);
		mFragments.add(mEnergyMeterRoom3Frag);
		mFragments.add(mNotificationFrag);
		
		mFragments.add(mEsmartRoom6Frag);
		mFragments.add(mEnergyMeterRoom6Frag);
		mFragments.add(mNotificationFrag);

		return mFragments;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		startFlipping();
		return inflater.inflate(R.layout.md_layout, null);
	}

	@Override
	public void onDestroyView() 
	{
		stopFlipping();
		super.onDestroyView();
	}
	
	@Override
	public void onDetach() 
	{
		stopFlipping();
		super.onDetach();
	}

	private class ViewFlipperTask extends TimerTask
	{
		@Override
		public void run() 
		{
			showNextFragment();
		}
	}

	@Override
	public void notifyAboutData(ArrayList<SesameDataContainer> _data) 
	{
		//TODO implement multiple series
		SesameDataContainer data = _data.get(0)	;
		int type = Integer.parseInt(data.getId());
		switch(type)
		{
		case MID_SesameDisplayActivity.EDV_1_ID:
			updateChartFragment(mEsmartRoom1Frag, data);
			break;
		case MID_SesameDisplayActivity.EDV_3_ID:
			updateChartFragment(mEsmartRoom3Frag, data);
			break;
		case MID_SesameDisplayActivity.EDV_6_ID:
			updateChartFragment(mEsmartRoom6Frag, data);
			break;
		}
	}
	
	private void setupEnergyMeter(MeterWheelFragment m) {
		if (m != null) {
			m.setColorLabelWidth(50.0f);
			m.setTickTextSize(40.0f);
			m.setMinorTickLength(60);
			m.setMajorTickLength(80);
		}
	}

	private void updateChartFragment(MD_chartFragment _frag, SesameDataContainer _data)
	{
		if(null==_data)
		{
			return;
		}

		String[] titles = new String[]{_frag.getTitle()};

		ArrayList<Date[]>dates = new ArrayList<Date[]>(1);
		dates.add((Date[]) _data.getTimeStamps().toArray(new Date[_data.getTimeStamps().size()]));

		ArrayList<double[]>values = new ArrayList<double[]>(1);
		double[] temp = new double[_data.getValues().size()];
		for(int i = 0;i<_data.getValues().size();i++)
		{
			temp[i]=_data.getValues().get(i);
		}
		values.add(temp);

		try 
		{
			XYMultipleSeriesDataset dataset = mDatasetProvider.buildDateDataset(titles, dates, values);
			mRendererProvider.createMultipleSeriesRenderer(dataset);

			XYMultipleSeriesRenderer renderer = mRendererProvider.getRenderer();
			_frag.setChart(dataset, renderer);

		} 
		catch (DatasetCreationException e) 
		{
			e.printStackTrace();
		} 
		catch (RendererInitializationException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void notifyAboutNotification(String _msg) 
	{
		mNotificationFrag.setNotification(_msg);
	}
}