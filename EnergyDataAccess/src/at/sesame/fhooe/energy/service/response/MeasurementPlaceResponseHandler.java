package at.sesame.fhooe.energy.service.response;

import org.codegist.crest.handler.ResponseHandler;
import org.codegist.crest.io.Response;

import com.thoughtworks.xstream.XStream;

import at.sesame.fhooe.energy.model.MeasurementPlace;
import at.sesame.fhooe.energy.model.MeasurementPlaces;

public class MeasurementPlaceResponseHandler implements ResponseHandler {

	@Override
	public Object handle(Response arg0) throws Exception 
	{
		
//		return new MeasurementPlacesParser().parse(arg0.asStream());
		XStream xStream  = new XStream();
		
		xStream.alias("MeasurementPlace", MeasurementPlace.class);
		xStream.aliasField("ID_MP", MeasurementPlace.class, "mId");
		xStream.aliasField("Name", MeasurementPlace.class, "mName");
		
		xStream.alias("MeasurementPlaces", MeasurementPlaces.class);
		xStream.addImplicitCollection(MeasurementPlaces.class, "mPlaces", MeasurementPlace.class);
		
		return (MeasurementPlaces)xStream.fromXML(arg0.asStream());
	}

}
