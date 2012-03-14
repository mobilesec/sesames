package at.sesame.fhooe.lib.ezan;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.io.http.HttpClientHttpChannelFactory;

import android.os.AsyncTask;
import android.os.Looper;
import at.sesame.fhooe.lib.ezan.model.EzanMeasurement;
import at.sesame.fhooe.lib.ezan.model.EzanMeasurementPlace;
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
//			Looper.prepare();
			return new EzanPlacesTask().execute(new Void[]{}).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
//		return mEzanAccess.getEzanPlaces();
	}
	
	public static ArrayList<EzanMeasurement> getEzanMeasurements(String _id, int _numMeasurements)
	{
		try {
			return new EzanMeasurementTask().execute(new String[]{_id, ""+_numMeasurements}).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
//		return mEzanAccess.getEzanMeasurements(_id);
	}
	
	private static class EzanPlacesTask extends AsyncTask<Void, Void, ArrayList<EzanMeasurementPlace>>
	{
		@Override
		protected ArrayList<EzanMeasurementPlace> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return mEzanAccess.getEzanPlaces();
		}
	}
	
	private static class EzanMeasurementTask extends AsyncTask<String, Void, ArrayList<EzanMeasurement>>
	{
		@Override
		protected ArrayList<EzanMeasurement> doInBackground(String... params) {
			
			return mEzanAccess.getEzanMeasurements(params[0], Integer.parseInt(params[1]));
		}
		
	}
}
