package at.sesame.fhooe.lib.esmart.service.response;

import org.codegist.crest.handler.ResponseHandler;
import org.codegist.crest.io.Response;

import com.thoughtworks.xstream.XStream;

import at.sesame.fhooe.lib.esmart.model.EsmartService;
import at.sesame.fhooe.lib.esmart.model.EsmartServices;

public class GetServicesResponseHandler implements ResponseHandler {

	private static final String TAG = "GetServicesResponseHandler";
	@Override
	public Object handle(Response arg0) throws Exception {
//		Log.e(TAG, DownloadHelper.convertStreamToString(arg0.asStream()));
//		ServicesResponseParser parser = new ServicesResponseParser();
		
//		return parser.parse(arg0.asStream());
		XStream xStream = new XStream();
		
		xStream.alias("Service", EsmartService.class);
		xStream.aliasField("ServiceName", EsmartService.class, "mName");
		xStream.aliasField("MethodName", EsmartService.class, "mMethodName");
		
		xStream.alias("Services", EsmartServices.class);
		xStream.addImplicitCollection(EsmartServices.class, "mServices");
		
		return (EsmartServices)xStream.fromXML(arg0.asStream());
	}

}
