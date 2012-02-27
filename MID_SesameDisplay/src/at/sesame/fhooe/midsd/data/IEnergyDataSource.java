package at.sesame.fhooe.midsd.data;

import java.util.ArrayList;
import java.util.Date;

public interface IEnergyDataSource 
{
	public ArrayList<SesameMeasurementPlace> getEnergyMeasurementPlaces();
	
	public SesameDataContainer getEnergyData(int _id, String _from, String _to);
}