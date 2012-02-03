package at.sesame.fhooe.lib.ui.charts;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import at.sesame.fhooe.lib.ui.charts.exceptions.RendererInitializationException;

public abstract class AbstractRendererProvider 
implements IRendererProvider 
{
	protected XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	
	protected void setupRenderer()
	{
		mRenderer.setExternalZoomEnabled(true);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setZoomEnabled(true, false);
		mRenderer.setZoomRate(10);
		mRenderer.setFitLegend(true);
		mRenderer.setPanEnabled(true, false);
		mRenderer.setAntialiasing(true);
		mRenderer.setShowGrid(true);
	}

	@Override
	public void createMultipleSeriesRenderer(Object... _data) throws RendererInitializationException
	{
		XYMultipleSeriesDataset data = null;
		try
		{
			data = (XYMultipleSeriesDataset)_data[0];
		}
		catch(ClassCastException e)
		{
			throw new RendererInitializationException("passed parameter had wrong type (XYMultipleSeriesDataset expected)");
		}
		
		mRenderer.setXLabels(10);
		
		for(XYSeries series:data.getSeries())
		{
			mRenderer.addSeriesRenderer(setupSeriesRenderer(series));
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
