package at.sesame.fhooe.lib2.data;

import java.util.ArrayList;
import java.util.Date;

public interface IHumidityDataSource 
{
	public ArrayList<SesameMeasurementPlace> getHumidityMeasurementPlaces();
	
	public SesameDataContainer getHumidityData(Date _from, Date _to);
}
