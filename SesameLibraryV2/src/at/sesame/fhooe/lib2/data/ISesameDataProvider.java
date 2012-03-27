package at.sesame.fhooe.lib2.data;

import java.util.ArrayList;

public interface ISesameDataProvider
{
	public void registerEnergyDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp);
	public void unregisterEnergyDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp);
	public ArrayList<SesameMeasurementPlace> getEnergyMeasurementPlaces();
	
	public void registerHumidityDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp);
	public void unregisterHumidityDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp);
	public ArrayList<SesameMeasurementPlace> getHumidityMeasurementPlaces();
	
	public void registerTemperatureDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp);
	public void unregisterTemperatureDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp);
	public ArrayList<SesameMeasurementPlace> getTemperatureMeasurementPlaces();
	
	public void registerLightDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp);
	public void unregisterLightDataListener(ISesameDataListener _listener, SesameMeasurementPlace _smp);
	public ArrayList<SesameMeasurementPlace> getLightMeasurementPlaces();
	
	public void registerNotificationListener(INotificationListener _listener);
	public void unregisterNotificationListener(INotificationListener _listener);
}
