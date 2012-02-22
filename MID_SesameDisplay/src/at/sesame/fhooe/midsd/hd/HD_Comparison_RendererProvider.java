package at.sesame.fhooe.midsd.hd;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import at.sesame.fhooe.lib.ui.charts.AbstractRendererProvider;

public class HD_Comparison_RendererProvider extends AbstractRendererProvider {

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
		String title = _series.getTitle();
		if (title.contains(ComparisonFragment.CURRENT_DATA_NAME)) {
			return Color.BLUE;
		} else if (title.contains(ComparisonFragment.DAY_CB1_TEXT)) {
			return Color.RED;
		} else if (title.contains(ComparisonFragment.DAY_CB2_TEXT)) {
			return Color.GREEN;
		} else if (title.contains(ComparisonFragment.DAY_CB3_TEXT)) {
			return Color.YELLOW;
		} else if (title.contains(ComparisonFragment.DAY_CB4_TEXT)) {
			return Color.LTGRAY;
		} else {
			return Color.BLACK;
		}
	}

	@Override
	protected void setupRenderer() {
		super.setupRenderer();
		mRenderer.setShowGrid(false);
		mRenderer.setApplyBackgroundColor(false);
		mRenderer.setMarginsColor(0x00ffffff);
		// mRenderer.setMargins(new int[] { 0, 100, 70, 100 });
		mRenderer.setAxesColor(0xffffffff);
		mRenderer.setLabelsColor(0xffffffff);
		// mRenderer.setLabelsTextSize(20);
		mRenderer.setClickEnabled(false);
		// mRenderer.setLegendTextSize(50);
		// mRenderer.setLegendHeight(70);
		mRenderer.setPanEnabled(true, true);
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setZoomEnabled(true, true);
		mRenderer.setYTitle("kW");
	}
}
