package at.sesame.fhooe.ezan.service;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.io.http.HttpClientHttpChannelFactory;

import android.os.AsyncTask;
import at.sesame.fhooe.ezan.model.EzanMeasurement;
import at.sesame.fhooe.ezan.model.EzanMeasurementPlace;
import at.sesame.fhooe.lib.pms.proxy.ProxyHelper;

public class EzanDataAccess 
{
	private static final String TAG = "EzanDataAccess";
	private static IEzanDataAccess mEzanAccess;
	
	static
	{
		CRest crest = new CRestBuilder().setHttpChannelFactory(new HttpClientHttpChannelFactory(ProxyHelper.getProxiedAllAcceptingHttpsClient())).build();
		mEzanAccess = crest.build(IEzanDataAccess.class);
	}
	
	public static ArrayList<EzanMeasurementPlace> getEzanPlaces()
	{
		try {
			return new EzanPlacesTask().execute(new Void[]{}).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<EzanMeasurement> getEzanMeasurements()
	{
		try {
			return new EzanMeasurementTask().execute(new Void[]{}).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static class EzanPlacesTask extends AsyncTask<Void, Void, ArrayList<EzanMeasurementPlace>>
	{
		@Override
		protected ArrayList<EzanMeasurementPlace> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return mEzanAccess.getEzanPlaces();
		}
	}
	
	private static class EzanMeasurementTask extends AsyncTask<Void, Void, ArrayList<EzanMeasurement>>
	{
		@Override
		protected ArrayList<EzanMeasurement> doInBackground(Void... params) {
			return mEzanAccess.getEzanMeasurements();
		}
		
	}
}
