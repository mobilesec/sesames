package at.sesame.fhooe.lib.esmart.service;

import java.util.ArrayList;

import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.ErrorHandler;
import org.codegist.crest.annotate.GET;
import org.codegist.crest.annotate.Path;
import org.codegist.crest.annotate.QueryParam;
import org.codegist.crest.annotate.ResponseHandler;

import at.sesame.fhooe.lib.esmart.model.EsmartData;
import at.sesame.fhooe.lib.esmart.model.EsmartDataRow;
import at.sesame.fhooe.lib.esmart.model.EsmartMeasurementPlace;
import at.sesame.fhooe.lib.esmart.model.EsmartMeasurementPlaces;
import at.sesame.fhooe.lib.esmart.model.EsmartService;
import at.sesame.fhooe.lib.esmart.model.EsmartServices;
import at.sesame.fhooe.lib.esmart.service.error.EnergyDataErrorHandler;
import at.sesame.fhooe.lib.esmart.service.response.DataResponseHandler;
import at.sesame.fhooe.lib.esmart.service.response.GetServicesResponseHandler;
import at.sesame.fhooe.lib.esmart.service.response.MeasurementPlaceResponseHandler;
@EndPoint("http://684d21e6181c406f84c3ab968880f960.cloudapp.net/ServicesProxy.svc/")
public interface IEsmartDataAccess 
{
	/**
	 * Getting Services: http://684d21e6181c406f84c3ab968880f960.cloudapp.net/servicesproxy.svc/GetServices?Username=kirchdorf.administrator&Password=kirchdorf55

Getting MeasurementPlaces: http://684d21e6181c406f84c3ab968880f960.cloudapp.net/servicesproxy.svc/GetMeasurementPlaces?Username=kirchdorf.administrator

Getting LoadProfile Data: http://684d21e6181c406f84c3ab968880f960.cloudapp.net/servicesproxy.svc/GetData_LoadProfile?ID_MP=15&From=2011-11-25&To=2011-11-28

Getting DailyConsumption Data: http://684d21e6181c406f84c3ab968880f960.cloudapp.net/servicesproxy.svc/GetData_DailyConsumption?ID_MP=15&From=2011-11-20&To=2011-11-28
	 */
	
	
	
	@GET
	@Path("/GetServices")
	@ResponseHandler(GetServicesResponseHandler.class)
	@ErrorHandler(EnergyDataErrorHandler.class)
	public EsmartServices getServices(@QueryParam("Username")String _user, @QueryParam("Password") String _pass);
	
	@GET
	@Path("/GetMeasurementPlaces")
	@ResponseHandler(MeasurementPlaceResponseHandler.class)
	@ErrorHandler(EnergyDataErrorHandler.class)
	public EsmartMeasurementPlaces getMeasurementPlaces(@QueryParam("Username")String _user);
	
	
	@GET
	@Path("/GetData_LoadProfile")
	@ResponseHandler(DataResponseHandler.class)
	@ErrorHandler(EnergyDataErrorHandler.class)
	public EsmartData getLoadProfile(@QueryParam("ID_MP")int _id, @QueryParam("From") String _from, @QueryParam("To")String _to);
	
	@GET
	@Path("/GetData_DailyConsumption")
	@ResponseHandler(DataResponseHandler.class)
	@ErrorHandler(EnergyDataErrorHandler.class)
	public EsmartData getDailyConsumption(@QueryParam("ID_MP")int _id, @QueryParam("From") String _from, @QueryParam("To")String _to);
}
