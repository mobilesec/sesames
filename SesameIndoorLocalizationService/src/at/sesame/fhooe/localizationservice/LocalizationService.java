package at.sesame.fhooe.localizationservice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import at.sesame.fhooe.lib.classification.Classificator;
import at.sesame.fhooe.lib.classification.ClassificatorException;
import at.sesame.fhooe.lib.location.osm.OSMAccess;
import at.sesame.fhooe.lib.util.DownloadHelper;
import at.sesame.fhooe.lib.util.InstanceHelper;
import at.sesame.fhooe.lib.wifi.WifiAccess;
import at.sesame.fhooe.localizationservice.xml.lm.LocalizationMechanismMetaFileParser;
import at.sesame.fhooe.localizationservice.xml.lm.model.LocalizationMechanismMetaInformation;
import at.sesame.fhooe.localizationservice.xml.meta.MetaFileParser;
import at.sesame.fhooe.localizationservice.xml.meta.model.MetaFileInformation;

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
	private Handler mHandler = new Handler();

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

		@Override
		public boolean queryAvailableLocalizationDataSources()throws RemoteException 
		{
//			Looper.prepare();
			Log.e(TAG, Thread.currentThread().getName());
//			mHandler.post(new Runnable() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					
//				}
//			});
			String url =  new OSMAccess().getClosestIndoorLocationDataUrl();
//			Looper.loop();
			Log.e(TAG, "queryAvailableLocalizationDataSources() called");
			if(null!=url)
			{
//				Looper.prepare();
//				String test = DownloadHelper.getStringFromUrl(url);
				InputStream is = DownloadHelper.getStreamFromUrl(url+"Meta.xml");
//				Looper.loop();
//				Log.e(TAG, "------result:"+test);
//				InputStream is = DownloadHelper.getStreamFromUrl(url+"Meta.xml");
//				InputStream is = new StringBufferInputStream(test);
				if(null!=is)
				{
					MetaFileInformation mfi = new MetaFileParser().parse(is);
					Log.e(TAG, "parsed meta file:"+mfi.toString());
					URL lmMetaUrl = mfi.getSuitableLocalizationMechanismMetaURL();
					Log.e(TAG, "lmMetaURL = "+lmMetaUrl);
					InputStream metaLmStream = DownloadHelper.getStreamFromUrl(lmMetaUrl);
					if(null!=metaLmStream)
					{
						LocalizationMechanismMetaInformation lmmi= LocalizationMechanismMetaFileParser.parse(metaLmStream);
						URL trainingDataUrl = lmmi.getFingerprintDatabases().get(0).getDbInfo().getDatabaseFile().getUrl();
						InputStream trainingDataStream = DownloadHelper.getStreamFromUrl(trainingDataUrl);
						Instances inst = InstanceHelper.getInstancesFromInputStream(trainingDataStream);
						Log.e(TAG, "number of instances parsed:"+inst.numInstances());
						mClassificator.setTrainingData(inst);
						return true;
//						Log.e(TAG, convertStreamToString(metaLmStream));
					}
					else 
					{
						return false;
					}
//					Instances inst = InstanceHelper.getInstancesFromInputStream(is);
//					if(null!=inst)
//					{
//						mClassificator.setTrainingData(inst);
//						return true;
//					}
//					Log.e(TAG, "Instances were null");
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
			
//			return false;
		}
	};
	
	@SuppressWarnings("unused")
	private static String convertStreamToString(InputStream _is)
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(_is));
		StringBuilder total = new StringBuilder();
		String line;
		try {
			while ((line = r.readLine()) != null) {
			    total.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total.toString();
	}
	
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
