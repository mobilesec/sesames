package at.sesame.fhooe.lib.data.semantic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import at.sesame.fhooe.lib.data.IEnergyDataSource;
import at.sesame.fhooe.lib.data.IHumidityDataSource;
import at.sesame.fhooe.lib.data.ILightDataSource;
import at.sesame.fhooe.lib.data.ITemperatureDataSource;
import at.sesame.fhooe.lib.data.SesameDataContainer;
import at.sesame.fhooe.lib.data.SesameMeasurementPlace;
import at.sesame.fhooe.lib.data.SesameSensor;
import at.sesame.fhooe.lib.data.SesameSensor.SensorType;
import at.sesame.fhooe.lib.data.semantic.parsing.SemanticQueryResultParser;
import at.sesame.fhooe.lib.data.semantic.parsing.SemanticRepoHelper;

public class SemanticSesameDataSource 
implements IEnergyDataSource, IHumidityDataSource, ILightDataSource, ITemperatureDataSource 
{

	private ArrayList<SesameMeasurementPlace> mEnergyPlaces = new ArrayList<SesameMeasurementPlace>();

	public SemanticSesameDataSource()
	{
		queryEnergyMeasurementPlaces();
	}

	private void queryEnergyMeasurementPlaces()
	{
		queryMeasurementPlaces(SensorType.energy, mEnergyPlaces);
	}

	private void queryMeasurementPlaces(SensorType _st, ArrayList<SesameMeasurementPlace> _placeList)
	{
		HashMap<String, String> placeSensorMap = SemanticQueryResultParser
				.parseSensorsQueryResult(RepositoryAccess
						.executeQuery(SemanticRepoHelper
								.getSensorsQuery(_st)));
		Iterator<String> placeIt = placeSensorMap.keySet().iterator();
		while(placeIt.hasNext())
		{
			String mp = placeIt.next();
			SesameMeasurementPlace smp = new SesameMeasurementPlace(mp);
			smp.addSensor(new SesameSensor(placeSensorMap.get(mp), _st));
			_placeList.add(smp);
//			mEnergyPlaceSensorMap.put(new SesameMeasurementPlace(_id, _name), value)
		}
//		placeStrings = makeUnique(placeStrings);
////		ArrayList<SesameMeasurementPlace> places = new ArrayList<SesameMeasurementPlace>(placeStrings.size());
//		for(int i = 0;i<placeStrings.size();i++)
//		{
////			places.add(new SesameMeasurementPlace(i, placeStrings.get(i)));
//			mEnergyPlaceSensorMap.put(new SesameMeasurementPlace(i, placeStrings.get(i)), null);
//		}
//		return places;
	}
	
	private ArrayList<String> makeUnique(ArrayList<String> _list)
	{
		ArrayList<String> res = new ArrayList<String>();
		
		for(String entry:_list)
		{
			if(!res.contains(entry))
			{
				res.add(entry);
			}
		}
		return res;
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
	public ArrayList<SesameMeasurementPlace> getHumidityMeasurementPlaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SesameDataContainer getHumidityData(Date _from, Date _to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SesameMeasurementPlace> getEnergyMeasurementPlaces() 
	{
		if(null==mEnergyPlaces || mEnergyPlaces.isEmpty())
		{
			queryEnergyMeasurementPlaces();
		}
//		ArrayList<SesameMeasurementPlace> res = new ArrayList<SesameMeasurementPlace>(mEnergyPlaceSensorMap.size());
//		Iterator<SesameMeasurementPlace> placeIt = mEnergyPlaceSensorMap.keySet().iterator();
//		while(placeIt.hasNext())
//		{
//			res.add(placeIt.next());
//		}
		return mEnergyPlaces;
	}

	@Override
	public SesameDataContainer getEnergyData(SesameMeasurementPlace _smp, Date _from, Date _to) 
	{
		if(null==mEnergyPlaces || mEnergyPlaces.isEmpty())
		{
			queryEnergyMeasurementPlaces();
		}
		String sensorId = _smp.getEnergySensors().get(0).getId();
		String query = SemanticRepoHelper.getSensorValuesQuery(sensorId, _from, _to);
		String result = RepositoryAccess.executeQuery(query);
		
		HashMap<Date, Double> dateValueMap = SemanticQueryResultParser.parseValues(result);
		
		Iterator<Date> dateIt = dateValueMap.keySet().iterator();
		ArrayList<Date> dates = new ArrayList<Date>();
		
		while(dateIt.hasNext())
		{
			dates.add(dateIt.next());
		}
		
		Iterator<Double> valIt = dateValueMap.values().iterator();
		ArrayList<Double> values = new ArrayList<Double>();
		
		while(valIt.hasNext())
		{
			values.add(valIt.next());
		}
		
		return new SesameDataContainer(_smp, dates, values);
	}

}
