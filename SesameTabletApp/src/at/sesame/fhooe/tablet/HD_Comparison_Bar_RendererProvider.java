package at.sesame.fhooe.tablet;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import at.sesame.fhooe.lib2.data.simulation.DataSimulator;
import at.sesame.fhooe.lib2.ui.charts.AbstractRendererProvider;

public class HD_Comparison_Bar_RendererProvider 
extends AbstractRendererProvider
{
	private int mFillColorAlpha = 180;
	
	public HD_Comparison_Bar_RendererProvider(Context _ctx) 
	{
		super(_ctx);
	}
	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		// TODO Auto-generated method stub
		XYSeriesRenderer xysr = new XYSeriesRenderer();
		
		xysr.setColor(getColorForSeries(arg0));
//		xysr.setLineWidth(2.0f);
		return xysr;
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
		mRenderer.setXLabels(7);
		mRenderer.setBarSpacing(0.5);
		mRenderer.setXAxisMin(0.5);
		mRenderer.setXAxisMax(7.3);
		mRenderer.setYAxisMin(0);
		mRenderer.clearXTextLabels();
		mRenderer.setXLabels(0);
		for(int i = 0 ;i<DataSimulator.BAR_TITLES.length;i++)
		{
			mRenderer.addXTextLabel(i+1, DataSimulator.BAR_TITLES[i]);
		}
		
	}
	
	private int getColorForSeries(XYSeries _series)
	{
		String title = _series.getTitle();
		
		if(title.contains(mCtx.getString(R.string.global_current)))
		{
			return Color.BLUE;
		}
		else if(title.contains(mCtx.getString(R.string.hd_comparison_week_cb1_text)))
		{
			return Color.GREEN;
		}
		else if(title.contains(mCtx.getString(R.string.hd_comparison_week_cb2_text)))
		{
			return Color.RED;
		}
		else if(title.contains(mCtx.getString(R.string.hd_comparison_week_cb3_text)))
		{
			return Color.YELLOW;
		}
		else if(title.contains(mCtx.getString(R.string.hd_comparison_week_cb4_text)))
		{
			return Color.CYAN;
		}
		else
		{
			return Color.TRANSPARENT;
		}
	}

}
