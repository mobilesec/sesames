package at.sesame.fhooe.esmart.service.response;

import org.codegist.crest.handler.ResponseHandler;
import org.codegist.crest.io.Response;

import com.thoughtworks.xstream.XStream;

import at.sesame.fhooe.esmart.model.Service;
import at.sesame.fhooe.esmart.model.Services;

public class GetServicesResponseHandler implements ResponseHandler {

	private static final String TAG = "GetServicesResponseHandler";
	@Override
	public Object handle(Response arg0) throws Exception {
//		Log.e(TAG, DownloadHelper.convertStreamToString(arg0.asStream()));
//		ServicesResponseParser parser = new ServicesResponseParser();
		
//		return parser.parse(arg0.asStream());
		XStream xStream = new XStream();
		
		xStream.alias("Service", Service.class);
		xStream.aliasField("ServiceName", Service.class, "mName");
		xStream.aliasField("MethodName", Service.class, "mMethodName");
		
		xStream.alias("Services", Services.class);
		xStream.addImplicitCollection(Services.class, "mServices");
		
		return (Services)xStream.fromXML(arg0.asStream());
	}

}
