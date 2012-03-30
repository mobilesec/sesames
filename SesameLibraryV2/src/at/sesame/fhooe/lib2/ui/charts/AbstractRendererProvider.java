package at.sesame.fhooe.lib2.ui.charts;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Paint.Align;
import android.util.Log;
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
		mRenderer.setYLabelsAlign(Align.RIGHT);
		mRenderer.setXLabels(0);
		mRenderer.setShowLabels(true);
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

}
