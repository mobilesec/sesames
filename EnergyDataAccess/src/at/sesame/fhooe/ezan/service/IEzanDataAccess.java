package at.sesame.fhooe.ezan.service;

import java.util.ArrayList;

import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.GET;
import org.codegist.crest.annotate.Path;

import at.sesame.fhooe.ezan.model.EzanMeasurement;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;

@EndPoint("http://178.79.158.199/")
public interface IEzanDataAccess 
{
	@GET
	@Path("places.json")
	public ArrayList<EzanMeasurementPlace> getEzanPlaces();
	
	@GET
	@Path("/places/4ee9e611e5f0014652000005/measures.json")
	public ArrayList<EzanMeasurement> getEzanMeasurements();

}
