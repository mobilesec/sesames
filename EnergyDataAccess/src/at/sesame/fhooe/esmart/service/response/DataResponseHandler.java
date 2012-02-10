package at.sesame.fhooe.esmart.service.response;


import org.codegist.crest.handler.ResponseHandler;
import org.codegist.crest.io.Response;

import com.thoughtworks.xstream.XStream;

import at.sesame.fhooe.esmart.model.Data;
import at.sesame.fhooe.esmart.model.DataRow;

public class DataResponseHandler implements ResponseHandler {

	@Override
	public Object handle(Response arg0) throws Exception {
		// TODO Auto-generated method stub
		XStream xStream = new XStream();
		
		xStream.alias("DataRow", DataRow.class);
		xStream.aliasField("DataValue", DataRow.class, "mDataValue");
		xStream.aliasField("TimeStamp", DataRow.class, "mTimeStamp");
//		xStream.
		
		xStream.alias("Data", Data.class);
		xStream.addImplicitCollection(Data.class, "mRows");
		
		return (Data)xStream.fromXML(arg0.asStream());
//		return new DataParser().parse(arg0.asStream());
	}

}
