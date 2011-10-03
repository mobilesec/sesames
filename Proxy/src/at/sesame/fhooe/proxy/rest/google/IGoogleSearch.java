package at.sesame.fhooe.proxy.rest.google;

import org.codegist.crest.annotate.ContextPath;
import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.Path;
import org.codegist.crest.annotate.ResponseHandler;


import at.sesame.fhooe.proxy.rest.google.model.Address;
import at.sesame.fhooe.proxy.rest.google.model.SearchResult;

@EndPoint("http://ajax.googleapis.com")
@ContextPath("/ajax/services/search")
@ResponseHandler(GoogleResponseHandler.class)

public interface IGoogleSearch {
	@Path("/web?v=1.0&q={0}")
    SearchResult<Address> search(String text);
}
