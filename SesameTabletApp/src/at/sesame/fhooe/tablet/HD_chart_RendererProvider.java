package at.sesame.fhooe.tablet;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import at.sesame.fhooe.lib2.ui.charts.AbstractRendererProvider;

public class HD_chart_RendererProvider 
extends AbstractRendererProvider 
{
	private int mFillColorAlpha = 60;
	
	public HD_chart_RendererProvider(Context _ctx) 
	{
		super(_ctx);
	}
	public HD_chart_RendererProvider(Context _ctx, boolean _createFixedLabels) 
	{
		super(_ctx, _createFixedLabels);
	}
	
	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		//int color = getColorForSeries(arg0);
		int color = getColorForRoom(arg0.getTitle());
		xysr.setColor(color);
		xysr.setLineWidth(3.0f);
		xysr.setFillBelowLine(true);
		xysr.setFillBelowLineColor(Color.argb(mFillColorAlpha,
				Color.red(color), Color.green(color), Color.blue(color)));
		xysr.setPointStyle(getPointStyleForRoom(arg0.getTitle()));
		return xysr;
	}

	@Override
	protected void setupRenderer() {
		super.setupRenderer();
		mRenderer.setApplyBackgroundColor(false);
		mRenderer.setMarginsColor(0x00ffffff);
		mRenderer.setAxesColor(0xffffffff);
		mRenderer.setLabelsColor(0xffffffff);
		mRenderer.setClickEnabled(false);
		mRenderer.setPanEnabled(false, false);
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setZoomEnabled(false, false);
		mRenderer.setYAxisMin(0);
		//mRenderer.setYAxisMax(2500);
		mRenderer.setYTitle(mCtx.getString(R.string.energy_graph_y_title));
//		setTimeLabels();
	}
	
//	private void setTimeLabels() {
//		mRenderer.setXLabels(13);
//		mRenderer.clearXTextLabels();
//		mRenderer.addXTextLabel(0, "7:00");
//		mRenderer.addXTextLabel(1, "8:00");
//		mRenderer.addXTextLabel(2, "9:00");
//		mRenderer.addXTextLabel(3, "10:00");
//		mRenderer.addXTextLabel(4, "11:00");
//		mRenderer.addXTextLabel(5, "12:00");
//		mRenderer.addXTextLabel(6, "13:00");
//		mRenderer.addXTextLabel(7, "14:00");
//		mRenderer.addXTextLabel(8, "15:00");
//		mRenderer.addXTextLabel(9, "16:00");
//		mRenderer.addXTextLabel(10, "17:00");
//		mRenderer.addXTextLabel(11, "18:00");
//		mRenderer.addXTextLabel(12, "19:00");
//	}

}
