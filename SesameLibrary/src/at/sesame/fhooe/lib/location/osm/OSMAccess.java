package at.sesame.fhooe.lib.location.osm;

import java.util.ArrayList;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;

import at.sesame.fhooe.lib.location.osm.rest.IOsmApi;

public class OSMAccess 
{
	private static IOsmApi mOSM;
	
	static
	{
		CRest crest = new CRestBuilder().build();
		mOSM = crest.build(IOsmApi.class);
	};
	public static ArrayList<String> getClosestIndoorLocationDataUrl()
	{
		double lat = 48.367;
		double lon = 14.517;
		double left = lon-0.01;
		double right = lon+0.01;

		double top = lat+0.01;
		double bottom = lat-0.01;
		return null;
	}

}
