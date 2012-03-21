package at.sesame.fhooe.lib.data;

import java.util.ArrayList;
import java.util.Date;

public interface ITemperatureDataSource 
{
	public ArrayList<SesameMeasurementPlace> getTemperatureMeasurementPlaces();

	
	public SesameDataContainer getTemperatureData(Date _from, Date _to);
}