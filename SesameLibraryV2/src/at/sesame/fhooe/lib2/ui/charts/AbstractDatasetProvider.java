package at.sesame.fhooe.lib2.ui.charts;

import org.achartengine.model.XYMultipleSeriesDataset;

import at.sesame.fhooe.lib2.ui.charts.exceptions.DatasetCreationException;

public abstract class AbstractDatasetProvider 
implements IDatasetProvider 
{

	protected XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	
	@Override
	public abstract int createDataset(Object... _data) throws DatasetCreationException;

	@Override
	public XYMultipleSeriesDataset getDataset() 
	{
		return mDataset;
	}

}
