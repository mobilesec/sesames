package at.sesame.fhooe.lib2.ui.charts;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class BasicLineChartWrapper
{
	private static final String TAG = "BasicLineChartWrapper";
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private GraphicalView mView;
	private Context mCtx;
	
	public BasicLineChartWrapper(Context _ctx)
	{
		mCtx = _ctx;
	}
	
	public BasicLineChartWrapper(Context _ctx, XYMultipleSeriesDataset _dataset, XYMultipleSeriesRenderer _renderer)
	{
		mDataset = _dataset;
		mRenderer = _renderer;
	}
	
	public void setDataset(XYMultipleSeriesDataset _dataset)
	{
		mDataset = _dataset;
	}
	
	public void setRenderer(XYMultipleSeriesRenderer _renderer)
	{
		mRenderer = _renderer;
	}
	
	

	public XYMultipleSeriesDataset getDataset() {
		return mDataset;
	}

	public XYMultipleSeriesRenderer getRenderer() {
		return mRenderer;
	}

	public View getChartView()
	{
		Log.e(TAG, "number of series:"+mDataset.getSeriesCount());
		for(XYSeries series:mDataset.getSeries())
		{
			Log.e(TAG, "title="+series.getTitle()+" ("+series.getItemCount()+" items)");
		}
		mView = ChartFactory.getLineChartView(mCtx, mDataset, mRenderer);
		Log.e(TAG, "getChartView called");
		return mView;
	}
	
	public void refresh()
	{
		Log.e(TAG, "refreshing...");
		mView.repaint();
//		mView
	}
}
