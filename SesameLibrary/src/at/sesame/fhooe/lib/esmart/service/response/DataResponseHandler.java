package at.sesame.fhooe.lib.esmart.service.response;


import org.codegist.crest.handler.ResponseHandler;
import org.codegist.crest.io.Response;

import com.thoughtworks.xstream.XStream;

import at.sesame.fhooe.lib.esmart.model.EsmartData;
import at.sesame.fhooe.lib.esmart.model.EsmartDataRow;

public class DataResponseHandler implements ResponseHandler {

	@Override
	public Object handle(Response arg0) throws Exception {
		// TODO Auto-generated method stub
		XStream xStream = new XStream();
		
		xStream.alias("DataRow", EsmartDataRow.class);
		xStream.aliasField("DataValue", EsmartDataRow.class, "mDataValue");
		xStream.aliasField("TimeStamp", EsmartDataRow.class, "mTimeStamp");
//		xStream.
		
		xStream.alias("Data", EsmartData.class);
		xStream.addImplicitCollection(EsmartData.class, "mRows");
		
		return (EsmartData)xStream.fromXML(arg0.asStream());
//		return new DataParser().parse(arg0.asStream());
	}

}
