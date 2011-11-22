package at.sesame.fhooe.lib.location.osm.rest;

import java.util.ArrayList;

import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.ErrorHandler;
import org.codegist.crest.annotate.GET;
import org.codegist.crest.annotate.Path;
import org.codegist.crest.annotate.PathParam;
import org.codegist.crest.annotate.QueryParam;
import org.codegist.crest.annotate.ResponseHandler;

import at.sesame.fhooe.lib.location.osm.model.IndoorLocOSMNode;

@EndPoint("http://www.overpass-api.de/")
@ResponseHandler(OSMResponseHandler.class)
@ErrorHandler(OSMErrorHandler.class)
public interface IOsmApi 
{
	@GET
	@Path("/api/capabilities")
	public String getCapabilities();
	
	@GET
	@Path("/api/0.6/node/{id}")
	public String getNode(@PathParam("id")int _id);
	
	@GET
	@Path("/api/xapi{node}")
//	@Consumes("gzip")
	public ArrayList<IndoorLocOSMNode> getIndoorLocalizationNodes(	@PathParam("node")String _node);

}
