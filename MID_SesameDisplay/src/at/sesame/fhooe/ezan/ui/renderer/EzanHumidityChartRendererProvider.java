package at.sesame.fhooe.ezan.ui.renderer;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import at.sesame.fhooe.lib.ui.charts.AbstractRendererProvider;

public class EzanHumidityChartRendererProvider 
extends AbstractRendererProvider 
{
	
	
	public EzanHumidityChartRendererProvider()
	{
		setupRenderer();
	}
	
	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries _series)
	{
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		xysr.setColor(EzanRendererInfoProvider.getHumiditySeriesColor(_series.getTitle()));
		return xysr;
	}
	
	
	
	@Override
	protected void setupRenderer() 
	{
		super.setupRenderer();
		mRenderer.setShowGrid(false);
	}

	
}
