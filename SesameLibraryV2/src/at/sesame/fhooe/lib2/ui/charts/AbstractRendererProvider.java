package at.sesame.fhooe.lib2.ui.charts;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import at.sesame.fhooe.lib2.Constants;
import at.sesame.fhooe.lib2.R;
import at.sesame.fhooe.lib2.ui.charts.exceptions.RendererInitializationException;
import at.sesame.fhooe.lib2.util.DateHelper;

public abstract class AbstractRendererProvider 
implements IRendererProvider 
{
	private static final String TAG = "AbstractRendererProvider";
//	private static final String[] X_LABLES = new String[]{"8:00","10:00","12:00","14:00","16:00","18:00"};
	private static final SimpleDateFormat CHART_X_AXIS_LABEL_FORMAT = new SimpleDateFormat("HH:mm");
	private static final int LABEL_DATE_INCREMENTATION = 1;
	private static final int LABEL_DATE_FIELD_TO_INCREMENT = Calendar.HOUR_OF_DAY;
	private boolean mCreateFixedLabels = false;
	protected XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	protected Context mCtx;
//	private static final double X_PADDING = 100000;
	
	protected AbstractRendererProvider(Context _ctx)
	{
		this(_ctx, false);
	}
	
	protected AbstractRendererProvider(Context _ctx, boolean _createFixedLabels)
	{
		mCtx = _ctx;
		mCreateFixedLabels = _createFixedLabels;
	}
	
	protected void setupRenderer()
	{
		mRenderer.setExternalZoomEnabled(true);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setZoomEnabled(true, false);
		mRenderer.setZoomRate(10);
		mRenderer.setFitLegend(true);
		mRenderer.setPanEnabled(false, false);
		mRenderer.setAntialiasing(true);
		mRenderer.setShowGrid(true);
		mRenderer.setGridColor(0x50ffffff);
		mRenderer.setYLabelsAlign(Align.RIGHT);
		mRenderer.setXLabels(0);
		mRenderer.setShowLabels(true);
//		mRenderer.setMarginsColor(0x00ffffff);
		mRenderer.setMargins(new int[]  { 20, 50, 10, 20 });
	}

	@Override
	public void createMultipleSeriesRenderer(Object... _data) throws RendererInitializationException
	{
		mRenderer = new XYMultipleSeriesRenderer();
		
		XYMultipleSeriesDataset data = null;
		try
		{
			data = (XYMultipleSeriesDataset)_data[0];
		}
		catch(ClassCastException e)
		{
			throw new RendererInitializationException("passed parameter had wrong type (XYMultipleSeriesDataset expected)");
		}
		
		for(XYSeries series:data.getSeries())
		{
			if(mRenderer.getXAxisMin()>series.getMinX())
			{
				mRenderer.setXAxisMin(series.getMinX());
			}
			if(mRenderer.getXAxisMax()<series.getMaxX())
			{
				mRenderer.setXAxisMax(series.getMaxX());
			}
			mRenderer.addSeriesRenderer(setupSeriesRenderer(series));
		}
		setupRenderer();
		if(mCreateFixedLabels)
		{
			createFixedLabels();
		}
		
	}
	
	private void createFixedLabels() {
//		Log.e(TAG, "creating fixed labels");
		// TODO Auto-generated method stub
//		mRenderer.clearXTextLabels();
//		mRenderer.setXLabels(0);
		Date begin = DateHelper.getSchoolStartXDaysAgo(0);
		Date end = DateHelper.getSchoolEndXDaysAgo(0);
//		Log.e(TAG, "begin: "+begin.toString());
//		Log.e(TAG, "end: "+end.toString());
		
		mRenderer.setXAxisMin(begin.getTime());
		mRenderer.setXAxisMax(end.getTime());
		
		GregorianCalendar labelCal = new GregorianCalendar();
		labelCal.setTime(begin);
		
		while(labelCal.getTimeInMillis()<=end.getTime())
		{
//			Log.e(TAG, labelCal.getTime().toString()+" before: "+end.toString());
			mRenderer.addXTextLabel(labelCal.getTimeInMillis(), CHART_X_AXIS_LABEL_FORMAT.format(labelCal.getTime()));
			labelCal.add(LABEL_DATE_FIELD_TO_INCREMENT, LABEL_DATE_INCREMENTATION);
		}
//		Log.e(TAG, labelCal.getTime().toString()+" not before: "+end.toString());
//		Log.e(TAG, "creating fixed text labels done");
//		double min = mRenderer.getXAxisMin();
//		double max = mRenderer.getXAxisMax();
//		double labelStepWidth = (max-min)/X_LABLES.length;
//		
//		double value = mRenderer.getXAxisMin();
//		for(int i = 0;i<X_LABLES.length;i++)
//		{
//			String label = X_LABLES[i];
//			
//			mRenderer.addXTextLabel(value, label);
//			value+=labelStepWidth;
//		}
		
	}
	
	@Override
	public abstract XYSeriesRenderer setupSeriesRenderer(XYSeries _series);

	@Override
	public XYMultipleSeriesRenderer getRenderer() {
		// TODO Auto-generated method stub
		return mRenderer;
	}
	
	public int applyAlphaForColor(int alpha, int color) {
		return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
	}
	
	/**
	 * Calculates a color for historical visualization
	 * based on a given color. Parameter steps defined
	 * as time-dimension (e.g. weeks)
	 * @param color
	 * @param steps
	 * @return
	 */
	public int getHistoricalColor(int color, int steps) {
		float[] hsvColor = new float[3];
		Color.RGBToHSV(Color.red(color), Color.green(color) , Color.blue(color), hsvColor);
		
		switch (steps) {
		case 0:
			return color;
		case 1:
			hsvColor[1] *= 0.45f;
			return Color.HSVToColor(hsvColor);
		case 2:
			hsvColor[1] *= 0.30f;
			return Color.HSVToColor(hsvColor);
		case 3:
			hsvColor[1] *= 0.15f;
			return Color.HSVToColor(hsvColor);
		case 4:
			hsvColor[1] *= 0.00f;
			return Color.HSVToColor(hsvColor);
		default:
			return color;
		}
	}
	
	/**
	 * Get point style for historical charts.
	 * @param steps
	 * @return PointStyle
	 */
	public PointStyle getHistoricalPointStyle(int steps) {
		switch (steps) {
		case 0:
			return PointStyle.CIRCLE;
		case 1:
			return PointStyle.DIAMOND;
		case 2:
			return PointStyle.SQUARE;
		case 3:
			return PointStyle.TRIANGLE;
		case 4:
			return PointStyle.X;
		default:
			return PointStyle.X;
		}
	}
	
	/**
	 * Get color for a specific room.
	 * @param room
	 * @return Color
	 */
	public int getColorForRoom(String room) {
		if (room.contains(mCtx.getString(R.string.global_Room1_name))) {
			return Constants.COLOR_EDV1;
		} else if (room.contains(mCtx.getString(R.string.global_Room3_name))) {
			return Constants.COLOR_EDV3;
		} else if (room.contains(mCtx.getString(R.string.global_Room6_name))) {
			return Constants.COLOR_EDV6;
		} else {
			return Color.GRAY;
		}
	}
	
	/**
	 * Get point style for a specific room.
	 * @param room
	 * @return PointStyle
	 */
	public PointStyle getPointStyleForRoom(String room) {
		if (room.contains(mCtx.getString(R.string.global_Room1_name))) {
			return PointStyle.CIRCLE;
		} else if (room.contains(mCtx.getString(R.string.global_Room3_name))) {
			return PointStyle.DIAMOND;
		} else if (room.contains(mCtx.getString(R.string.global_Room6_name))) {
			return PointStyle.SQUARE;
		} else {
			return PointStyle.X;
		}
	}

}
