package at.sesame.fhooe.lib.location.osm.rest;

import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.ErrorHandler;
import org.codegist.crest.annotate.GET;
import org.codegist.crest.annotate.Path;
import org.codegist.crest.annotate.PathParam;
import org.codegist.crest.annotate.QueryParam;
import org.codegist.crest.annotate.ResponseHandler;

@EndPoint("http://api.openstreetmap.org/")
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
	@Path("/api/0.6/map")
//	@Consumes("gzip")
	public String getMap(	@QueryParam("bbox")String _bbox);

}
