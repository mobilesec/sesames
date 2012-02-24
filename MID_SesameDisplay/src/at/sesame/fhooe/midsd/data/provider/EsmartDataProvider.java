package at.sesame.fhooe.midsd.data.provider;

import java.util.ArrayList;
import java.util.Date;

import at.sesame.fhooe.esmart.model.EsmartDataRow;
import at.sesame.fhooe.esmart.model.EsmartMeasurementPlace;
import at.sesame.fhooe.esmart.service.EsmartDataAccess;
import at.sesame.fhooe.midsd.data.AbstractDataProvider;
import at.sesame.fhooe.midsd.data.IEnergyDataSource;
import at.sesame.fhooe.midsd.data.SesameDataContainer;
import at.sesame.fhooe.midsd.data.SesameMeasurementPlace;

public class EsmartDataProvider
extends AbstractDataProvider
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
	public SesameDataContainer getEnergyData(int _id, String _from, String _to) 
	{
		return convertEsmartDataRowsToSesameDataContainer(""+_id, EsmartDataAccess.getLoadProfile(_id, _from, _to));
	}
	
	private SesameMeasurementPlace convertEsmartToSesameMeasurementPlace(EsmartMeasurementPlace _emp)
	{
		return new SesameMeasurementPlace(_emp.getId(), _emp.getName());
	}
	
	private ArrayList<SesameMeasurementPlace> convertEsmartToSesameMeasurementPlaces(ArrayList<EsmartMeasurementPlace> _emps)
	{
		ArrayList<SesameMeasurementPlace> res = new ArrayList<SesameMeasurementPlace>(_emps.size());
		for(EsmartMeasurementPlace emp:_emps)
		{
			res.add(convertEsmartToSesameMeasurementPlace(emp));
		}
		return res;
	}

	
	private SesameDataContainer convertEsmartDataRowsToSesameDataContainer(String _id, ArrayList<EsmartDataRow> _edrs)
	{
		Date[] dates = new Date[_edrs.size()];
		double[] values = new double[_edrs.size()];
		for(int i = 0 ;i<_edrs.size();i++)
		{
			EsmartDataRow edr = _edrs.get(i);
			dates[i] = edr.getDate();
			values[i] = edr.getDataValue();
		}
		return new SesameDataContainer(_id, dates, values);
	}
}
