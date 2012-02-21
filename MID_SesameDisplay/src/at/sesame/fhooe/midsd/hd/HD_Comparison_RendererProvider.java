package at.sesame.fhooe.midsd.hd;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import at.sesame.fhooe.lib.ui.charts.AbstractRendererProvider;

public class HD_Comparison_RendererProvider extends AbstractRendererProvider {

	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		// TODO Auto-generated method stub
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		xysr.setColor(getColorForSeries(arg0));
		return xysr;
	}

	private int getColorForSeries(XYSeries _series)
	{
		String title = _series.getTitle();
		if(title.contains(ComparisonFragment.CURRENT_DATA_NAME))
		{
			return Color.BLUE;
		}
		else if(title.contains(ComparisonFragment.DAY_CB1_TEXT))
		{
			return Color.RED;
		}
		else if(title.contains(ComparisonFragment.DAY_CB2_TEXT))
		{
			return Color.GREEN;
		}
		else if(title.contains(ComparisonFragment.DAY_CB3_TEXT))
		{
			return Color.YELLOW;
		}
		else if(title.contains(ComparisonFragment.DAY_CB4_TEXT))
		{
			return Color.LTGRAY;
		}
		else
		{
			return Color.BLACK;
		}
	}
}
