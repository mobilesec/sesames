package at.sesame.fhooe.ezan.ui.renderer;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import at.sesame.fhooe.lib.ui.charts.AbstractRendererProvider;

public class EzanTemperatureChartRendererProvider
extends AbstractRendererProvider
{
	public EzanTemperatureChartRendererProvider()
	{
		setupRenderer();
	}

	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries _series)
	{
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		xysr.setColor(EzanRendererInfoProvider.getTemperatureSeriesColor(_series.getTitle()));
		return xysr;
	}
}
