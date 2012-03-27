package at.sesame.fhooe.lib2.data.semantic;

import org.codegist.crest.annotate.ConnectionTimeout;
import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.GET;
import org.codegist.crest.annotate.Path;
import org.codegist.crest.annotate.QueryParam;

@EndPoint("http://193.170.124.176/openrdf-sesame")
public interface IRepositoryService 
{
	@GET
//	@Path("/repositories/NONE/server/namespaces")
//	@Consumes("application/xml")
//	@Consumes("application/sparql-results+json")
	@Path("/repositories/smartbuilding")
//	@ResponseHandler(RepositoryResponseHandler.class)
	@ConnectionTimeout(30000)
	String executeQuery(@QueryParam("query")String query);
}
