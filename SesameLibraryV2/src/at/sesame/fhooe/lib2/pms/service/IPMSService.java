/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib2.pms.service;

import java.util.ArrayList;
import java.util.List;

import org.codegist.crest.annotate.*;

import at.sesame.fhooe.lib2.pms.entitywriter.PMSEntityWriter;
import at.sesame.fhooe.lib2.pms.errorhandling.handlers.ExtendedStatusErrorHandler;
import at.sesame.fhooe.lib2.pms.errorhandling.handlers.PMSBooleanErrorHandler;
import at.sesame.fhooe.lib2.pms.errorhandling.handlers.PowerOffErrorHandler;
import at.sesame.fhooe.lib2.pms.model.ExtendedPMSStatus;
import at.sesame.fhooe.lib2.pms.model.PMSStatus;
import at.sesame.fhooe.lib2.pms.responsehandling.PMSBooleanResponseHandler;

/**
 * this annotated interface represents all possible PMS calls and all information
 * needed by CRest to successfully execute the calls.
 * annotation explanation:
 * 
 * Endpoint: the base url for all calls
 * Path: method specific extension for the url specified by Endpoint
 * GET / POST: indicate whether the call is a GET- or POST request
 * EntityWriter: specifies a class that writes data to the request body
 * Produces: the value of this annotation is used for the "Content-Type" header field
 * Consumes: the value of this annotation is used for the "Accept" header field
 * ErrorHandler: an instance of this class is called when a response has a status
 * code >=400
 * ResponseHandler: an instance of this class is called when a response has a
 * status code <400
 * PathParam: this parameter is used for a placeholder of the same name in the url (Path)
 * FormParam: this parameter is used for the EntityWriter to specify which values should be
 * written to the request body
 * @author admin
 *
 */
//http://192.168.2.7:8080/pms/
@EndPoint("http://80.120.3.4:8080/pms")
//@EndPoint("http://192.168.2.7:8080/pms")
//@HeaderParam(value="Authorization", defaultValue="Basic {basic.auth.digest}")
public interface IPMSService 
{
	
	
	@GET
	@Path("/{mac}/status")
	@ErrorHandler(PMSBooleanErrorHandler.class)
	public PMSStatus getStatus(@PathParam("mac")String _mac);
	
	@POST
	@Path("/{mac}/poweroff")
	@EntityWriter(PMSEntityWriter.class)
	@Produces("application/json")
//	@Consumes("application/json")
	@ErrorHandler(PowerOffErrorHandler.class)
	@ResponseHandler(PMSBooleanResponseHandler.class)
	public Boolean poweroff(@PathParam("mac") String _mac,
							@FormParam("target-state") String _targetState,
							@FormParam("os") String _os,
							@FormParam("username") String _user,
							@FormParam("password") String _pwd);
	
	@GET
	@Path("/{mac}/wakeup")
	@ErrorHandler(PMSBooleanErrorHandler.class)
	@ResponseHandler(PMSBooleanResponseHandler.class)
	public Boolean wakeup(@PathParam("mac") String _mac);
	
	@GET
	@Path("/known-os")
//	@ErrorHandler(PMSBooleanErrorHandler.class)
//	@ErrorHandler(ExtendedStatusErrorHandler.class)
//	@ResponseHandler(PMSBooleanResponseHandler.class)
	public String knownOs();
	
	
	@GET
	@Path("/clients")
	@ErrorHandler(PMSBooleanErrorHandler.class)
	public String getClients();
	
	@POST
	@Path("/{mac}/extended-status")
	@EntityWriter(PMSEntityWriter.class)
	@Produces("application/json")
	@ErrorHandler(ExtendedStatusErrorHandler.class)
	public ExtendedPMSStatus extendedStatus(@PathParam("mac")String _mac,
											@FormParam("username")String _user, 
											@FormParam("password") String password);
	
	@POST
	@Path("/extended-status-list")
	@EntityWriter(PMSEntityWriter.class)
	@Produces("application/json")
	@ErrorHandler(ExtendedStatusErrorHandler.class)
//	@ResponseHandler(PMSStatusListResponseHandler.class)
	@Consumes("application/json")
//	@ConnectionTimeout(25000)
	@SocketTimeout(60000)
	public ArrayList<ExtendedPMSStatus> extendedStatusList(@FormParam("maclist")ArrayList<String> _mac);
	
}
