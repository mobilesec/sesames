package at.sesame.fhooe.lib.data.provider;

import java.util.ArrayList;
import java.util.Date;

import at.sesame.fhooe.lib.data.IEnergyDataSource;
import at.sesame.fhooe.lib.data.SesameDataContainer;
import at.sesame.fhooe.lib.data.SesameMeasurementPlace;
import at.sesame.fhooe.lib.esmart.model.EsmartDataRow;
import at.sesame.fhooe.lib.esmart.model.EsmartMeasurementPlace;
import at.sesame.fhooe.lib.esmart.service.EsmartDataAccess;


public class EsmartDataProvider
implements IEnergyDataSource 
{
	private ArrayList<SesameMeasurementPlace> mMeasurementPlaces;
	
	public EsmartDataProvider()
	{
		loadMeasurementPlaces();
	}
	
	private void loadMeasurementPlaces()
	{
		mMeasurementPlaces = convertEsmartToSesameMeasurementPlaces(EsmartDataAccess.getMeasurementPlaces());
	}

	@Override
	public ArrayList<SesameMeasurementPlace> getEnergyMeasurementPlaces() 
	{
		return mMeasurementPlaces;
	}


	@Override
	public SesameDataContainer getEnergyData(SesameMeasurementPlace _smp, Date _from, Date _to) 
	{
		return convertEsmartDataRowsToSesameDataContainer(_smp, EsmartDataAccess.getLoadProfile(_smp.getId(), EsmartDateHelper.getUrlTimeString(_from), EsmartDateHelper.getUrlTimeString(_to)));
	}
	
	private SesameMeasurementPlace convertEsmartToSesameMeasurementPlace(EsmartMeasurementPlace _emp)
	{
		return new SesameMeasurementPlace(_emp.getId(), _emp.getName());
	}
	
	private ArrayList<SesameMeasurementPlace> convertEsmartToSesameMeasurementPlaces(ArrayList<EsmartMeasurementPlace> _emps)
	{
		if(null==_emps)
		{
			return null;
		}
		ArrayList<SesameMeasurementPlace> res = new ArrayList<SesameMeasurementPlace>(_emps.size());
		for(EsmartMeasurementPlace emp:_emps)
		{
			res.add(convertEsmartToSesameMeasurementPlace(emp));
		}
		return res;
	}

	
	private SesameDataContainer convertEsmartDataRowsToSesameDataContainer(SesameMeasurementPlace _smp, ArrayList<EsmartDataRow> _edrs)
	{
		ArrayList<Date> dates = new ArrayList<Date>(_edrs.size());
		ArrayList<Double> values = new ArrayList<Double>(_edrs.size());
		for(int i = 0 ;i<_edrs.size();i++)
		{
			EsmartDataRow edr = _edrs.get(i);
			dates.add(edr.getDate());
			values.add(edr.getDataValue());
		}
		return new SesameDataContainer(_smp, ""+_smp.getId(), dates, values);
	}
}
