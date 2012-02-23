package at.sesame.fhooe.ezan.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.sesame.fhooe.ezan.model.EzanMeasurement;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;
import at.sesame.fhooe.ezan.model.EzanMeasurement.MeasurementType;
import at.sesame.fhooe.ezan.ui.renderer.EzanHumidityChartRendererProvider;
import at.sesame.fhooe.ezan.ui.renderer.EzanLightChartRendererProvider;
import at.sesame.fhooe.ezan.ui.renderer.EzanTemperatureChartRendererProvider;
import at.sesame.fhooe.ezan.ui.renderer.EzanVoltageChartRendererProvider;
import at.sesame.fhooe.lib.ui.charts.DefaultDatasetProvider;
import at.sesame.fhooe.lib.ui.charts.IRendererProvider;
import at.sesame.fhooe.lib.ui.charts.exceptions.DatasetCreationException;
import at.sesame.fhooe.lib.ui.charts.exceptions.RendererInitializationException;


public class EzanDataViewFragment 
extends Fragment
{
	private static final String TAG = "EzanDataViewFragment";
	
	private DefaultDatasetProvider mDataSetProvider = new DefaultDatasetProvider();
	
	private IRendererProvider mRendererProvider;
	
	private XYMultipleSeriesDataset mDataset;
	private XYMultipleSeriesRenderer mRenderer;
	
	
	public void setData(MeasurementType _type, ArrayList<EzanMeasurementPlace> _selectedPlaces, Date _start, Date _end)
	{
		if(_type.equals(MeasurementType.nA))
		{
			Log.e(TAG, "nA is not a valid type");
			return;
		}
		else
		{
			Log.e(TAG, _type.name());
		}
		Date start = null==_start?new Date(0):_start;
		Date end = null==_end?new Date():_end;
		
//		mDataSetProvider.createDataset(new Object[]{_titles, _data});
		try {
			String[] titles = getTitles(_selectedPlaces);
			List<Date[]> dates = getDates(_selectedPlaces, start, end);
			List<double[]> values = getValues(_type, _selectedPlaces, start, end);
			for(double[] arr:values)
			{
				if(arr.length==0)
				{
					Log.e(TAG, "values contain empty array...");
//					return;
					//FIXME solution for empty (E010) place
				}
			}
//			Log.e(TAG, "titles:"+titles.length);
//			Log.e(TAG, "dates:"+dates.size());
//			Log.e(TAG, "values:"+values.size());
			if(titles.length==0||dates.size()==0||values.size()==0)
			{
				Log.e(TAG, "nothing to display...");
				return;
			}
			
			mDataSetProvider.buildDateDataset(titles, dates, values);
		} catch (DatasetCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}

		
		
//		mChartWrapper.setDataset(dataset);
		
		//TODO: renderer bauen, views anzeigen/updaten, testen
//		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		switch(_type)
		{
		case temperature:
			mRendererProvider = new EzanTemperatureChartRendererProvider(getActivity());
			break;
		case temperature2:
//			break;
		case humidity:
			mRendererProvider = new EzanHumidityChartRendererProvider(getActivity());
			break;
		case light:
			mRendererProvider = new EzanLightChartRendererProvider(getActivity());
			break;
		case voltage:
			mRendererProvider = new EzanVoltageChartRendererProvider(getActivity());
			break;
			default:
			mRendererProvider = new EzanHumidityChartRendererProvider(getActivity());
			break;
			
		}
		XYMultipleSeriesDataset dataset = mDataSetProvider.getDataset();
		try 
		{
			mRendererProvider.createMultipleSeriesRenderer(new Object[]{dataset});
		} 
		catch (RendererInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mDataset = dataset;
		mRenderer = mRendererProvider.getRenderer();
//		mChartWrapper.setDataset(dataset);
//		mChartWrapper.setRenderer(mRendererProvider.getRenderer());
//		mChartWrapper.refresh();
		
		
//		getActivity().runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				mChartWrapper.refresh();
//			}
//		});
		
		//TODO
	}
	
//	private void queryData()
//	{
//		mMeasurements = EzanDataAccess.getEzanMeasurements();
//		Log.e(TAG, "measurements read:"+mMeasurements.size());
//	}
	
//	public void setShownPlaces(ArrayList<EzanMeasurementPlace> _places)
//	{
//		mShownPlaces = _places;
//		for(EzanMeasurementPlace emp:mShownPlaces)
//		{
//			Log.e(TAG, emp.toString());
//		}
//		displayValues();
//	}
	
	private String[] getTitles(ArrayList<EzanMeasurementPlace> _places)
	{
		String[] res = new String[_places.size()];
		for(int i = 0;i<_places.size();i++)
		{
			res[i]=_places.get(i).getTitle();
		}
		return res;
	}
	
	private List<Date[]> getDates(ArrayList<EzanMeasurementPlace> _places, Date _start, Date _end)
	{
		ArrayList<Date[]> res = new ArrayList<Date[]>();
		for(EzanMeasurementPlace emp:_places)
		{
			ArrayList<EzanMeasurement> measurements = emp.getMeasurements();
			Date[] dates = new Date[measurements.size()];
			for(int i = 0;i<measurements.size();i++)
			{
				EzanMeasurement em = measurements.get(i);
				if(em.getTimeStampDate().after(_start)&&em.getTimeStampDate().before(_end))
				{
					dates[i]=measurements.get(i).getTimeStampDate();
				}
			}
			res.add(dates);
		}
			
		return res;
	}
	
	private List<double[]> getValues(MeasurementType _type, ArrayList<EzanMeasurementPlace> _places, Date _start, Date _end)
	{
		ArrayList<double[]> res = new ArrayList<double[]>();
		
		for(EzanMeasurementPlace emp:_places)
		{
//			ArrayList<EzanMeasurement> measurements = emp.getMeasurements();
			res.add(emp.getFilteredMeasurements(_type, _start, _end));
		}
		return res;
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(null==mDataset||null==mRenderer)
		{
			return new View(getActivity());
		}
		Log.e(TAG, "onCreateView called (#series="+mDataset.getSeriesCount()+")");
		
		return ChartFactory.getTimeChartView(getActivity(), mDataset, mRenderer, null);
	}
}
