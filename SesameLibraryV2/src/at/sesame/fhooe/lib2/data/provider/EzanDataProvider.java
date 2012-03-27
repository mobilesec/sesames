package at.sesame.fhooe.lib2.data.provider;

import java.util.ArrayList;
import java.util.Date;

import at.sesame.fhooe.lib2.data.IHumidityDataSource;
import at.sesame.fhooe.lib2.data.ILightDataSource;
import at.sesame.fhooe.lib2.data.ITemperatureDataSource;
import at.sesame.fhooe.lib2.data.SesameDataContainer;
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;


public class EzanDataProvider implements IHumidityDataSource,
		ITemperatureDataSource, ILightDataSource {

	@Override
	public ArrayList<SesameMeasurementPlace> getLightMeasurementPlaces() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public SesameDataContainer getLightData(Date _from, Date _to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SesameMeasurementPlace> getTemperatureMeasurementPlaces() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public SesameDataContainer getTemperatureData(Date _from, Date _to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SesameMeasurementPlace> getHumidityMeasurementPlaces() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public SesameDataContainer getHumidityData(Date _from, Date _to) {
		// TODO Auto-generated method stub
		return null;
	}

}
