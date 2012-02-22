package at.sesame.fhooe.esmart.service;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.io.http.HttpClientHttpChannelFactory;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import at.sesame.fhooe.esmart.model.EsmartData;
import at.sesame.fhooe.esmart.model.EsmartDataRow;
import at.sesame.fhooe.esmart.model.EsmartMeasurementPlace;
import at.sesame.fhooe.esmart.model.EsmartMeasurementPlaces;
import at.sesame.fhooe.esmart.model.EsmartService;
import at.sesame.fhooe.esmart.model.EsmartServices;
import at.sesame.fhooe.lib.pms.proxy.ProxyHelper;

public class EsmartDataAccess 
{
	private static final String TAG = "EsmartDataAccess";
	private static final String mUser = "kirchdorf.administrator";
	private static final String mPass = "kirchdorf55";
	private static IEsmartDataAccess mEda;
	static
	{
//		HttpClient client = new DefaultHttpClient();
		CRest crest = new CRestBuilder().setHttpChannelFactory(new HttpClientHttpChannelFactory(ProxyHelper.getProxiedAllAcceptingHttpsClient())).build();
//        CRest crest = new CRestBuilder().build();
		mEda= crest.build(IEsmartDataAccess.class);
        
	}
	
	public static ArrayList<EsmartService>getServices()
	{
		return getServices(mUser, mPass);
	}

	public static ArrayList<EsmartService> getServices(String _user, String _pass)
	{
		GetServicesTask gst = new GetServicesTask();
		String[] params = new String[]{_user, _pass};
		try {
			return (ArrayList<EsmartService>)gst.execute(params).get().getServices();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<EsmartMeasurementPlace> getMeasurementPlaces()
	{
		return getMeasurementPlaces(mUser);
	}
	
	public static ArrayList<EsmartMeasurementPlace> getMeasurementPlaces(String _user)
	{
		String[] params = new String[]{_user};
		EsmartMeasurementPlaces res = mEda.getMeasurementPlaces(_user);
		if(null==res)
		{
			return null;
		}
		return (ArrayList<EsmartMeasurementPlace>)res.getPlaces();
//		Looper.prepare();
//		GetMeasurementPlacesTask gmpt = new GetMeasurementPlacesTask();
//		gmpt.execute(params);
//		
//		
//		try {
//			EsmartMeasurementPlaces places = gmpt.get();
//			if(null==places)
//			{
//				Log.e(TAG, "failed to load esmart places");
//				return null;
//			}
//			return (ArrayList<EsmartMeasurementPlace>)places.getPlaces();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
	}
	
	public static ArrayList<EsmartDataRow> getLoadProfile(int _mp, String _from, String _to)
	{
		Object[] params = new Object[]{new Integer(_mp), _from, _to};
		GetLoadProfileTask glpt = new GetLoadProfileTask();
		glpt.execute(params);
		
		try {
			
			return (ArrayList<EsmartDataRow>)glpt.get().getRows();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
//			e.printStackTrace();
			Log.e(TAG, "getLoadProfile request failed");
		}
		return null;
	}
	
	private static class GetServicesTask extends AsyncTask<String, Void, EsmartServices>
	{
		@Override
		protected EsmartServices doInBackground(String... params) 
		{
			return mEda.getServices(params[0], params[1]);
		}	
	}
	
	private static class GetMeasurementPlacesTask extends AsyncTask<String, Void, EsmartMeasurementPlaces>
	{
		@Override
		protected EsmartMeasurementPlaces doInBackground(String... params) 
		{
			return mEda.getMeasurementPlaces(params[0]);
		}
	}
	
	private static class GetLoadProfileTask extends AsyncTask<Object, Void, EsmartData>
	{
		@Override
		protected EsmartData doInBackground(Object... params) 
		{
			int id = ((Integer)params[0]).intValue();
			String from = (String)params[1];
			String to = (String)params[2];
			EsmartData d = mEda.getLoadProfile(id, from, to);
			return d;
		}
	}
	
}
