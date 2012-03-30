package at.sesame.fhooe.lib2.ui.charts;

import org.achartengine.model.XYMultipleSeriesDataset;

import at.sesame.fhooe.lib2.ui.charts.exceptions.DatasetCreationException;

public interface IDatasetProvider 
{
	/**
	 * creates a new XYMultipleSeriesDataset consisting of the passed data
	 * @param _data data used by implementing class to actually build the dataset
	 * @return the number of series that were added to the dataset
	 * @throws DatasetCreationException when dataset creation fails
	 */
	public int createDataset(Object... _data)throws DatasetCreationException;
	public XYMultipleSeriesDataset getDataset();
}
