package at.sesame.fhooe.tablet;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import at.sesame.fhooe.lib2.Constants;
import at.sesame.fhooe.lib2.data.simulation.DataSimulator;
import at.sesame.fhooe.lib2.ui.charts.AbstractRendererProvider;

public class HD_Comparison_Bar_RendererProvider 
extends AbstractRendererProvider
{
	//private int mFillColorAlpha = 180;
	
	public HD_Comparison_Bar_RendererProvider(Context _ctx) 
	{
		super(_ctx);
	}
	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		
		int color = getColorForSeries(arg0);
		
		xysr.setColor(color);
//		xysr.setGradientEnabled(true);
//		xysr.setGradientStart(0.0f, color);
//		xysr.setGradientStop(20.0f, applyAlphaForColor(200, color));
		return xysr;
	}
	
	@Override
	protected void setupRenderer() {
		super.setupRenderer();
		mRenderer.setApplyBackgroundColor(false);
		mRenderer.setAxesColor(0xffffffff);
		mRenderer.setLabelsColor(0xffffffff);
		mRenderer.setMarginsColor(0x00ffffff);
		//mRenderer.setLabelsTextSize(20);
		mRenderer.setClickEnabled(false);
		//mRenderer.setLegendTextSize(50);
		//mRenderer.setLegendHeight(70);
		mRenderer.setPanEnabled(false, false);
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setZoomEnabled(false, false);
		mRenderer.setYTitle("Watt");
//		mRenderer.setXLabels(7);
		mRenderer.setBarSpacing(1.5f);
		mRenderer.setXAxisMin(0.5);
		mRenderer.setXAxisMax(7.5);
//		mRenderer.setYAxisMin(0);
		mRenderer.clearXTextLabels();
//		mRenderer.setXLabels(0);
		for(int i = 0 ;i<DataSimulator.BAR_TITLES.length;i++)
		{
			mRenderer.addXTextLabel(i+1, DataSimulator.BAR_TITLES[i]);
		}
		
	}
	
	private int getColorForSeries(XYSeries _series)
	{
		String title = _series.getTitle();
		
		int rgbColor = getColorForRoom(title);
		
		if (title.contains(mCtx.getString(R.string.global_current)))
			return Constants.COLOR_EDV1;
		else if (title.contains(mCtx.getString(R.string.hd_comparison_week_cb1_text)))
			return getHistoricalColor(rgbColor, 1);
		else if (title.contains(mCtx.getString(R.string.hd_comparison_week_cb2_text)))
			return getHistoricalColor(rgbColor, 2);
		else if (title.contains(mCtx.getString(R.string.hd_comparison_week_cb3_text)))
			return getHistoricalColor(rgbColor, 3);
		else if (title.contains(mCtx.getString(R.string.hd_comparison_week_cb4_text)))
			return getHistoricalColor(rgbColor, 4);
		else
			return Color.GRAY;
	}
}
