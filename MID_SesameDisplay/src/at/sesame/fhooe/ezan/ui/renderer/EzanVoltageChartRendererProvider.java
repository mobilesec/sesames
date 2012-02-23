package at.sesame.fhooe.ezan.ui.renderer;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import at.sesame.fhooe.lib.ui.charts.AbstractRendererProvider;

public class EzanVoltageChartRendererProvider 
extends AbstractRendererProvider 
{

	public EzanVoltageChartRendererProvider(Context _ctx) 
	{
		super(_ctx);
	}

	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries _series) {
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		xysr.setColor(EzanRendererInfoProvider.getVoltageSeriesColor(_series.getTitle()));
		return xysr;
	}

}
