package at.sesame.fhooe.energydisplay.ezan.ui.renderer;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import at.sesame.fhooe.lib.ui.charts.AbstractRendererProvider;

public class EzanVoltageChartRendererProvider extends AbstractRendererProvider {

	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries _series) {
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		xysr.setColor(EzanRendererInfoProvider.getVoltageSeriesColor(_series.getTitle()));
		return xysr;
	}

}
