package at.sesame.fhooe.lib.ui.charts;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import at.sesame.fhooe.lib.ui.charts.exceptions.RendererInitializationException;

public interface IRendererProvider 
{
	public void createMultipleSeriesRenderer(Object... _data)throws RendererInitializationException;
	public XYSeriesRenderer setupSeriesRenderer(XYSeries _series);
	public XYMultipleSeriesRenderer getRenderer();
}
