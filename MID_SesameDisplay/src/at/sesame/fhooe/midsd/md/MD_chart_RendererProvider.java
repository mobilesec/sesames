package at.sesame.fhooe.midsd.md;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import at.sesame.fhooe.lib.ui.charts.AbstractRendererProvider;



public class MD_chart_RendererProvider
extends AbstractRendererProvider
{

	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		// TODO Auto-generated method stub
		return new XYSeriesRenderer();
	}



}
