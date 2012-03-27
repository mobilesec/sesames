package at.sesame.fhooe.lib2.data;

import java.util.ArrayList;
import java.util.Date;

public interface ILightDataSource 
{
	public ArrayList<SesameMeasurementPlace> getLightMeasurementPlaces();
	
	public SesameDataContainer getLightData(Date _from, Date _to);
}
