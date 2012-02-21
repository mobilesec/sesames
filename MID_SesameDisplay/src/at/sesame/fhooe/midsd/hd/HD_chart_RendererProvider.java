package at.sesame.fhooe.midsd.hd;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import at.sesame.fhooe.lib.ui.charts.AbstractRendererProvider;

public class HD_chart_RendererProvider
extends AbstractRendererProvider
{
	private static final String EDV1_TITLE = "EDV 1";
	private static final String EDV3_TITLE = "EDV 3";
	private static final String EDV6_TITLE = "EDV 6";
	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		// TODO Auto-generated method stub
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		xysr.setColor(getColorForSeries(arg0));
		return xysr;
	}
	
	private int getColorForSeries(XYSeries _series)
	{
		if(_series.getTitle().equals(EDV1_TITLE))
		{
			return Color.GREEN;
		}
		else if(_series.getTitle().equals(EDV3_TITLE))
		{
			return Color.RED;
		}
		else if(_series.getTitle().equals(EDV6_TITLE))
		{
			return Color.YELLOW;
		}
		else
		{
			return Color.BLUE;
		}
	}

}
