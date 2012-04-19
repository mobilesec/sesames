package at.sesame.fhooe.tablet;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import at.sesame.fhooe.lib2.ui.charts.AbstractRendererProvider;

public class HD_Comparison_Line_RendererProvider 
extends AbstractRendererProvider 
{
	private int mFillColorAlpha = 60;

	public HD_Comparison_Line_RendererProvider(Context _ctx) 
	{
		super(_ctx);
	}
	public HD_Comparison_Line_RendererProvider(Context _ctx, boolean _createFixedLabels) 
	{
		super(_ctx, _createFixedLabels);
	}
	
	private int getIndexForTitle(String title) {
		if (title.contains(mCtx.getString(R.string.global_current))) {
			return 0;
		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb1_text))) {
			return 1;
		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb2_text))) {
			return 2;
		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb3_text))) {
			return 3;
		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb4_text))) {
			return 4;
		} else {
			return -1;
		}	
	}
	
	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		int color = getColorForSeries(arg0);
		xysr.setColor(color);
		xysr.setLineWidth(3.0f);
		xysr.setFillBelowLine(true);
		xysr.setFillBelowLineColor(applyAlphaForColor(mFillColorAlpha, color));
		xysr.setPointStyle(getHistoricalPointStyle(getIndexForTitle(arg0.getTitle())));
		return xysr;
	}

	private int getColorForSeries(XYSeries _series) {
		String title = _series.getTitle();
		int color = getColorForRoom(title);
		return getHistoricalColor(color, getIndexForTitle(title));
	}

	@Override
	protected void setupRenderer() {
		super.setupRenderer();
		mRenderer.setAxesColor(0xffffffff);
		mRenderer.setLabelsColor(0xffffffff);
		mRenderer.setMarginsColor(0x00ffffff);
		mRenderer.setClickEnabled(false);
		mRenderer.setPanEnabled(false, false);
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setZoomEnabled(false, false);
		mRenderer.setYAxisMin(0);
		//mRenderer.setYAxisMax(2500);
		mRenderer.setYTitle(mCtx.getString(R.string.energy_graph_y_title));
	}
}
