package at.sesame.fhooe.energydisplay.ezan;

import java.util.ArrayList;

import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.FormParam;
import org.codegist.crest.annotate.GET;
import org.codegist.crest.annotate.Path;
import org.codegist.crest.annotate.PathParam;
import org.codegist.crest.annotate.QueryParam;

import android.test.UiThreadTest;
import at.sesame.fhooe.energydisplay.ezan.model.EzanMeasurement;
import at.sesame.fhooe.energydisplay.ezan.model.EzanMeasurementPlace;


@EndPoint("http://178.79.158.199/")
public interface IEzanDataAccess 
{
	@GET
	@Path("places.json")
	public ArrayList<EzanMeasurementPlace> getEzanPlaces();
	
	@GET
	@Path("/places/{id}/measures.json")
	public ArrayList<EzanMeasurement> getEzanMeasurements(@PathParam("id") String _id, @QueryParam("limit") int _numMeasurements);

}
