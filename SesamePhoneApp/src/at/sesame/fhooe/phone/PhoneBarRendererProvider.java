package at.sesame.fhooe.phone;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import at.sesame.fhooe.lib2.Constants;
import at.sesame.fhooe.lib2.data.simulation.DataSimulator;
import at.sesame.fhooe.lib2.ui.charts.AbstractRendererProvider;

public class PhoneBarRendererProvider 
extends AbstractRendererProvider
{
	//private int mFillColorAlpha = 180;
	
	public PhoneBarRendererProvider(Context _ctx) 
	{
		super(_ctx);
	}
	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		xysr.setColor(getColorForSeries(arg0));
		return xysr;
	}
	
	@Override
	protected void setupRenderer() {
		super.setupRenderer();
		mRenderer.setApplyBackgroundColor(false);
		mRenderer.setAxesColor(0xffffffff);
		mRenderer.setLabelsColor(0xffffffff);
		mRenderer.setMarginsColor(0x00ffffff);
		mRenderer.setClickEnabled(false);
		mRenderer.setPanEnabled(false, false);
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setZoomEnabled(false, false);
		mRenderer.setYTitle(mCtx.getString(R.string.energy_bar_y_title));
		mRenderer.setBarSpacing(1.5f);
		mRenderer.setXAxisMin(0.5);
		mRenderer.setXAxisMax(7.5);
		mRenderer.setYAxisMin(0);
		//mRenderer.setYAxisMax(2500);
		mRenderer.clearXTextLabels();
		for(int i = 0 ;i<DataSimulator.BAR_TITLES.length;i++)
		{
			mRenderer.addXTextLabel(i+1, DataSimulator.BAR_TITLES[i]);
		}
		
	}
	
	private int getColorForSeries(XYSeries _series)
	{
		String title = _series.getTitle();
		int color = getColorForRoom(title);
		if (title.contains(mCtx.getString(R.string.global_current)))
			return color;
		else if (title.contains(mCtx.getString(R.string.hd_comparison_week_cb1_text)))
			return getHistoricalColor(color, 1);
		else if (title.contains(mCtx.getString(R.string.hd_comparison_week_cb2_text)))
			return getHistoricalColor(color, 2);
		else if (title.contains(mCtx.getString(R.string.hd_comparison_week_cb3_text)))
			return getHistoricalColor(color, 3);
		else if (title.contains(mCtx.getString(R.string.hd_comparison_week_cb4_text)))
			return getHistoricalColor(color, 4);
		else
			return Color.GRAY;
	}
}
