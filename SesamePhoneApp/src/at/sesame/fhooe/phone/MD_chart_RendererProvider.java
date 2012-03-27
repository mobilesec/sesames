package at.sesame.fhooe.phone;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import at.sesame.fhooe.lib2.ui.charts.AbstractRendererProvider;

public class MD_chart_RendererProvider 
extends AbstractRendererProvider 
{
	private int mFillColorAlpha = 180;
	
	
	public MD_chart_RendererProvider(Context _ctx) 
	{
		super(_ctx);
	}
	
	public MD_chart_RendererProvider(Context _ctx, boolean _createFixedLabels)
	{
		super(_ctx, _createFixedLabels);
	}
	
	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		XYSeriesRenderer xysr = new XYSeriesRenderer();

		
		int color;
		if(arg0.getTitle().contains("aktuell"))
		{
			color = Color.GREEN;
			mFillColorAlpha = 180;
		}
		else
		{
			color = Color.CYAN;
			mFillColorAlpha = 50;
		}
		xysr.setColor(color);
		xysr.setLineWidth(3.0f);
		xysr.setFillBelowLine(true);
		xysr.setFillBelowLineColor(Color.argb(mFillColorAlpha,
				Color.red(color), Color.green(color), Color.blue(color)));
		return xysr;
	}

	@Override
	protected void setupRenderer() {
		super.setupRenderer();
		mRenderer.setShowGrid(true);
		mRenderer.setGridColor(0x50ffffff);
		mRenderer.setApplyBackgroundColor(false);
		mRenderer.setMarginsColor(0x00ffffff);
		//mRenderer.setMargins(new int[] { 0, 100, 70, 100 });
		mRenderer.setAxesColor(0xffffffff);
		mRenderer.setLabelsColor(0xffffffff);
		//mRenderer.setLabelsTextSize(20);
		mRenderer.setClickEnabled(false);
		//mRenderer.setLegendTextSize(50);
		//mRenderer.setLegendHeight(70);
		mRenderer.setPanEnabled(false, false);
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setZoomEnabled(false, false);
		mRenderer.setYTitle("kW");
		//mRenderer.setXLabelsAlign(Align.CENTER);
		mRenderer.setXLabelsAngle(-45);
		//mRenderer.setAxisTitleTextSize(20);
	}
	
	
}
