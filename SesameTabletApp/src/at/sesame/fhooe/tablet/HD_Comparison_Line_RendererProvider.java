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
	private int mFillColorAlpha = 180;

	public HD_Comparison_Line_RendererProvider(Context _ctx) 
	{
		super(_ctx);
	}
	public HD_Comparison_Line_RendererProvider(Context _ctx, boolean _createFixedLabels) 
	{
		super(_ctx, _createFixedLabels);
	}
	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		int color = getColorForSeries(arg0);
		xysr.setColor(color);
		xysr.setLineWidth(3.0f);
		xysr.setFillBelowLine(true);
		xysr.setFillBelowLineColor(applyAlphaForColor(mFillColorAlpha, color));
		return xysr;
	}

	private int getColorForSeries(XYSeries _series) {
		String title = _series.getTitle();
//		if (title.contains(mCtx.getString(R.string.global_current))) {
//			return Color.BLUE;
//		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb1_text))) {
//			return Color.RED;
//		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb2_text))) {
//			return Color.GREEN;
//		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb3_text))) {
//			return Color.YELLOW;
//		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb4_text))) {
//			return Color.LTGRAY;
//		} else {
//			return Color.BLACK;
//		}
		
		int color = getColorForRoom(title);
		if (title.contains(mCtx.getString(R.string.global_current))) {
			return color;
		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb1_text))) {
			return getHistoricalColor(color, 1);
		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb2_text))) {
			return getHistoricalColor(color, 2);
		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb3_text))) {
			return getHistoricalColor(color, 3);
		} else if (title.contains(mCtx.getString(R.string.hd_comparison_day_cb4_text))) {
			return getHistoricalColor(color, 4);
		} else {
			return Color.GRAY;
		}	
	}

	@Override
	protected void setupRenderer() {
		super.setupRenderer();
		mRenderer.setAxesColor(0xffffffff);
		mRenderer.setLabelsColor(0xffffffff);
		mRenderer.setMarginsColor(0x00ffffff);
		// mRenderer.setLabelsTextSize(20);
		mRenderer.setClickEnabled(false);
		// mRenderer.setLegendTextSize(50);
		// mRenderer.setLegendHeight(70);
		mRenderer.setPanEnabled(true, true);
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setZoomEnabled(true, true);
		mRenderer.setYTitle("Watt");
		//mRenderer.setXLabels(10);
		setTimeLabels();
	}
	
	private void setTimeLabels() {
		mRenderer.setXLabels(13);
		mRenderer.clearXTextLabels();
		mRenderer.addXTextLabel(0, "7:00");
		mRenderer.addXTextLabel(1, "8:00");
		mRenderer.addXTextLabel(2, "9:00");
		mRenderer.addXTextLabel(3, "10:00");
		mRenderer.addXTextLabel(4, "11:00");
		mRenderer.addXTextLabel(5, "12:00");
		mRenderer.addXTextLabel(6, "13:00");
		mRenderer.addXTextLabel(7, "14:00");
		mRenderer.addXTextLabel(8, "15:00");
		mRenderer.addXTextLabel(9, "16:00");
		mRenderer.addXTextLabel(10, "17:00");
		mRenderer.addXTextLabel(11, "18:00");
		mRenderer.addXTextLabel(12, "19:00");
	}
}
