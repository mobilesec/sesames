package at.sesame.fhooe.localizationservice;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import at.sesame.fhooe.lib.classification.Classificator;
import at.sesame.fhooe.lib.classification.ClassificatorException;
import at.sesame.fhooe.lib.wifi.WifiAccess;

public class LocalizationService 
extends Service
{
	/**
	 * the Tag identifying this class in the log output
	 */
	private static final String TAG = "LocalizationService";
	
	/**
	 * the classificator that provides 
	 */
	private Classificator mClassificator = null;
	private WifiAccess mWifiAccess = null;

	private IBinder mLocationService = new ILocalizationService.Stub() 
	{	
		@Override
		public void setLinearCalibrationParameters(double _a, double _b)throws RemoteException 
		{
			synchronized (mClassificator) 
			{
				mClassificator.setCalibrationData(_a, _b);
			}
		}
		
		@Override
		public void setTrainingSetPath(String _path) throws RemoteException 
		{
			try 
			{
				synchronized (mClassificator) 
				{
					Log.e(TAG, "passed path:"+_path);
					mClassificator.setTrainingData(_path);
				}
			} 
			catch (FileNotFoundException e) 
			{
				throw new RemoteException();
			}			
		}

		@Override
		public List<String> getAvailableClassifiers() throws RemoteException 
		{
			ArrayList<String> res = new ArrayList<String>();
			for(String s:mClassificator.getClassifierNames())
			{
				res.add(s);
			}
			return res;
		}

		@Override
		public synchronized void setClassifier(String _classifierName)throws RemoteException 
		{
			Log.e(TAG, "setClassifier called with:"+_classifierName);
			mClassificator.setClassifier(_classifierName);
		}

		@Override
		public String getLocation() throws RemoteException
		{
			String location;
			try 
			{
				ArrayList<ScanResult> results = mWifiAccess.getCachedResults();
				location = mClassificator.classify(results);
			} catch (ClassificatorException e) {
				location = e.getMessage();
				e.printStackTrace();
			}
			return location;
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) 
	{
		return mLocationService;
	}
	@Override
	public void onCreate()
	{
		super.onCreate();

		mClassificator = new Classificator(this);
		mWifiAccess = WifiAccess.getInstance(this, true);
		
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mWifiAccess.cleanUp();
	}
}
