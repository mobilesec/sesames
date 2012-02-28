package at.sesame.fhooe.midsd.md;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import at.sesame.fhooe.lib.ui.charts.AbstractRendererProvider;

public class MD_chart_RendererProvider 
extends AbstractRendererProvider 
{
	private static final String TAG = "~~~~~~~~~~~~~~~~~~";
	private int mFillColorAlpha = 180;
	private static final String[] X_LABLES = new String[]{"8:00","10:00","12:00","14:00","16:00","18:00"};
	
	public MD_chart_RendererProvider(Context _ctx) 
	{
		super(_ctx);
	}
	
	@Override
	public XYSeriesRenderer setupSeriesRenderer(XYSeries arg0) {
		XYSeriesRenderer xysr = new XYSeriesRenderer();

		Log.e(TAG, "series min="+arg0.getMinX()+", series max = "+arg0.getMaxX());
		if(mRenderer.getXAxisMin()>arg0.getMinX())
		{
			mRenderer.setXAxisMin(arg0.getMinX());
		}
		if(mRenderer.getXAxisMax()<arg0.getMaxX())
		{
			mRenderer.setXAxisMax(arg0.getMaxX());
		}
		int color;
		if(arg0.getTitle().contains("aktuell"))
		{
			color = Color.RED;
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
		Log.e(TAG, "setupRenderer");
		mRenderer.setShowGrid(false);
		mRenderer.setApplyBackgroundColor(false);
		mRenderer.setMarginsColor(0x00ffffff);
		mRenderer.setMargins(new int[] { 0, 100, 70, 100 });
		mRenderer.setAxesColor(0xffffffff);
		mRenderer.setLabelsColor(0xffffffff);
		mRenderer.setLabelsTextSize(20);
		mRenderer.setClickEnabled(false);
		mRenderer.setLegendTextSize(50);
		mRenderer.setLegendHeight(70);
		mRenderer.setPanEnabled(false, false);
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setZoomEnabled(false, false);
		mRenderer.setYTitle("kW");
		
		createLabels();
		

		
		
	}
	
	private void createLabels() {
		// TODO Auto-generated method stub
		mRenderer.clearXTextLabels();
		mRenderer.setXLabels(0);
//		mRenderer.clearTextLabels();
		mRenderer.setXLabels(X_LABLES.length);
		double min = mRenderer.getXAxisMin();
		double max = mRenderer.getXAxisMax();
		Log.e(TAG, "min="+min+", max="+max);
		double labelStepWidth = (max-min)/X_LABLES.length;
		Log.e(TAG, "stepWidth="+labelStepWidth);
		
		double value = mRenderer.getXAxisMin();
		for(int i = 0;i<X_LABLES.length;i++)
		{
			String label = X_LABLES[i];
			
//			mRenderer.addXTextLabel(value, label);
			Log.e(TAG, "adding x label:"+label);
			value+=labelStepWidth;
		}
	}
}
