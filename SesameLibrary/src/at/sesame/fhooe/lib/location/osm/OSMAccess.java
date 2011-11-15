package at.sesame.fhooe.lib.location.osm;

import java.util.ArrayList;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;

import at.sesame.fhooe.lib.location.LocationAccess;
import at.sesame.fhooe.lib.location.osm.model.IndoorLocOSMNode;
import at.sesame.fhooe.lib.location.osm.rest.IOsmApi;

public class OSMAccess 
{

	public static String getClosestIndoorLocationDataUrl()
	{
		CRest crest = new CRestBuilder().build();
		IOsmApi osm = crest.build(IOsmApi.class);
		double lat = 48.367;
		double lon = 14.517;
		double left = lon-0.01;
		double right = lon+0.01;

		double top = lat+0.01;
		double bottom = lat-0.01;
		ArrayList<IndoorLocOSMNode> nodes = osm.getMap(getBBoxString(left, bottom, right, top));
		
		if(nodes.size()>0)
		{
			return nodes.get(0).getValueForTag(IndoorLocOSMNode.INDOOR_LOCATION_URL_KEY);
		}
		else
		{
			return null;
		}
//		ArrayList<String> res = new ArrayList<String>();
//		for(IndoorLocOSMNode iloNode:nodes)
//		{
//			res.add(iloNode.getValueForTag(IndoorLocOSMNode.INDOOR_LOCATION_URL_KEY));
//		}
//		return res;
		
//		Location l = Location.
	}

	private static String getBBoxString(double _left, double _bottom, double _right, double _top)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(_left);
		sb.append(",");
		sb.append(_bottom);
		sb.append(",");
		sb.append(_right);
		sb.append(",");
		sb.append(_top);
		return sb.toString();

	}

}
