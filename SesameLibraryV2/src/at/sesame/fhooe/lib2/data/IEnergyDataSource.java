package at.sesame.fhooe.lib2.data;

import java.util.ArrayList;
import java.util.Date;

public interface IEnergyDataSource 
{
	public ArrayList<SesameMeasurementPlace> getEnergyMeasurementPlaces();
	
	public SesameDataContainer getEnergyData(SesameMeasurementPlace _smp, Date _from, Date _to);
}
