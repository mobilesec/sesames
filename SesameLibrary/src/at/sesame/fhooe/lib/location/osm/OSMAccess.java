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
		
		return null;
	}

}
