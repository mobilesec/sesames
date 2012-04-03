package at.sesame.fhooe.lib2.ui.charts;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;
import at.sesame.fhooe.lib2.Constants;
import at.sesame.fhooe.lib2.R;
import at.sesame.fhooe.lib2.ui.charts.exceptions.RendererInitializationException;

public abstract class AbstractRendererProvider 
implements IRendererProvider 
{
	private static final String[] X_LABLES = new String[]{"8:00","10:00","12:00","14:00","16:00","18:00"};
	private boolean mCreateFixedLabels = false;
	protected XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	protected Context mCtx;
	
	
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
		mRenderer.setMarginsColor(0x00ffffff);
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
		// TODO Auto-generated method stub
		mRenderer.clearXTextLabels();
		mRenderer.setXLabels(0);
		double min = mRenderer.getXAxisMin();
		double max = mRenderer.getXAxisMax();
		double labelStepWidth = (max-min)/X_LABLES.length;
		
		double value = mRenderer.getXAxisMin();
		for(int i = 0;i<X_LABLES.length;i++)
		{
			String label = X_LABLES[i];
			
			mRenderer.addXTextLabel(value, label);
			value+=labelStepWidth;
		}
		
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
	 * Get color for a specific room.
	 * @param room
	 * @return
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

}
