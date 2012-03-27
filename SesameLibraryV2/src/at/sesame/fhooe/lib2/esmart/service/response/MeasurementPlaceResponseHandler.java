package at.sesame.fhooe.lib2.esmart.service.response;

import org.codegist.crest.handler.ResponseHandler;
import org.codegist.crest.io.Response;

import com.thoughtworks.xstream.XStream;

import at.sesame.fhooe.lib2.esmart.model.EsmartMeasurementPlace;
import at.sesame.fhooe.lib2.esmart.model.EsmartMeasurementPlaces;

public class MeasurementPlaceResponseHandler implements ResponseHandler {

	@Override
	public Object handle(Response arg0) throws Exception 
	{
		
//		return new MeasurementPlacesParser().parse(arg0.asStream());
		XStream xStream  = new XStream();
		
		xStream.alias("MeasurementPlace", EsmartMeasurementPlace.class);
		xStream.aliasField("ID_MP", EsmartMeasurementPlace.class, "mId");
		xStream.aliasField("Name", EsmartMeasurementPlace.class, "mName");
		
		xStream.alias("MeasurementPlaces", EsmartMeasurementPlaces.class);
		xStream.addImplicitCollection(EsmartMeasurementPlaces.class, "mPlaces", EsmartMeasurementPlace.class);
		
		return (EsmartMeasurementPlaces)xStream.fromXML(arg0.asStream());
	}

}
