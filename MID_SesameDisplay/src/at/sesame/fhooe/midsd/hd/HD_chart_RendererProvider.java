package at.sesame.fhooe.midsd.hd;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import at.sesame.fhooe.lib.ui.charts.AbstractRendererProvider;

public class HD_chart_RendererProvider extends AbstractRendererProvider {
	private static final String EDV1_TITLE = "EDV 1";
	private static final String EDV3_TITLE = "EDV 3";
	private static final String EDV6_TITLE = "EDV 6";

	private int mFillColorAlpha = 180;

	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		int color = getColorForSeries(arg0);
		xysr.setColor(color);
		xysr.setLineWidth(3.0f);
		xysr.setFillBelowLine(true);
		xysr.setFillBelowLineColor(Color.argb(mFillColorAlpha,
				Color.red(color), Color.green(color), Color.blue(color)));
		return xysr;
	}

	private int getColorForSeries(XYSeries _series) {
		if (_series.getTitle().equals(EDV1_TITLE)) {
			return Color.GREEN;
		} else if (_series.getTitle().equals(EDV3_TITLE)) {
			return Color.CYAN;
		} else if (_series.getTitle().equals(EDV6_TITLE)) {
			return Color.BLUE;
		} else {
			return Color.YELLOW;
		}
	}

	@Override
	protected void setupRenderer() {
		super.setupRenderer();
		mRenderer.setShowGrid(false);
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
	}

}
