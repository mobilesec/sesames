package at.sesame.fhooe.lib.ezan.ui.renderer;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import at.sesame.fhooe.lib.ui.charts.AbstractRendererProvider;

public class EzanLightChartRendererProvider 
extends AbstractRendererProvider 
{

	public EzanLightChartRendererProvider(Context _ctx) 
	{
		super(_ctx);
	}

	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries _series) {
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		xysr.setColor(EzanRendererInfoProvider.getLightSeriesColor(_series.getTitle()));
		return xysr;
	}

}
