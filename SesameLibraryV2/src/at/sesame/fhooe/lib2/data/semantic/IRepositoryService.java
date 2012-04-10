package at.sesame.fhooe.lib2.data.semantic;

import org.codegist.crest.annotate.ConnectionTimeout;
import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.GET;
import org.codegist.crest.annotate.Path;
import org.codegist.crest.annotate.QueryParam;

@EndPoint("http://193.170.124.176:8080/openrdf-sesame")
public interface IRepositoryService 
{
	@GET
//	@Path("/repositories/NONE/server/namespaces")
//	@Consumes("application/xml")
//	@Consumes("application/sparql-results+json")
//	@Consumes("application/sparql-results+json")
//	@HeaderParam(value="Accept", defaultValue = "application/sparql-results+json")
	@Path("/repositories/smartbuilding")
//	@Path("/repositories/test")
//	@ResponseHandler(RepositoryResponseHandler.class)
	@ConnectionTimeout(30000)
	String executeQuery(@QueryParam("query")String query);
}
