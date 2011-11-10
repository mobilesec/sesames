package at.sesame.fhooe.lib.location.osm.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.codegist.crest.handler.ResponseHandler;
import org.codegist.crest.io.Response;

import at.sesame.fhooe.lib.location.osm.model.IndoorLocOSMNode;
import at.sesame.fhooe.lib.location.osm.model.OSMResponseParser;

public class OSMResponseHandler 
implements ResponseHandler 
{
	private static final String TAG = "OSMResponseHandler";
	
	@Override
	public Object handle(Response arg0) throws Exception 
	{
		ArrayList<IndoorLocOSMNode> res = OSMResponseParser.parse(arg0.asStream());
//		if(res.isEmpty())
//		{
//			return "empty";
//		}
//		Log.e(TAG, );
		return res;
	}
	
	private String readStringFromStream(InputStream _is)
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(_is));
		StringBuilder total = new StringBuilder();
		String line;
		try {
			while ((line = r.readLine()) != null) {
			    total.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total.toString();
	}

}
