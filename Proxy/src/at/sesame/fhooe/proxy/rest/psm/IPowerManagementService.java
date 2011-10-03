package at.sesame.fhooe.proxy.rest.psm;

import org.codegist.crest.annotate.ContextPath;
import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.Path;

@EndPoint("http://ajax.googleapis.com")
@ContextPath("/ajax/services/search")
public interface IPowerManagementService 
{
	@Path("/{0}/status")
	String getStatus(String _mac);
	
	@Path("/{0}/poweroff")
	void doPowerOff(String _mac, String _json);
	
	@Path("/{0}/wakeup")
	void wakeUp(String _mac);
	
	@Path("/known-os")
	String getKnownOS();
	
	@Path("/{0}/extended-status")
	String getExtendedStatus(String _mac, String _json);
}
