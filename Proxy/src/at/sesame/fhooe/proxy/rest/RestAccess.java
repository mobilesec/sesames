package at.sesame.fhooe.proxy.rest;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.HttpClientRestService;

import at.sesame.fhooe.proxy.ProxyActivity;
import at.sesame.fhooe.proxy.rest.psm.IPowerManagementService;

public class RestAccess 
{
	public RestAccess()
	{
		CRest crest = new CRestBuilder().expectsJson().setRestService(new HttpClientRestService(ProxyActivity.getProxiedAllAcceptingHttpsClient())).build();
		IPowerManagementService pms = crest.build(IPowerManagementService.class);
		
	}
}
