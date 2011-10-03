package at.sesame.fhooe.localizationservice;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import at.sesame.fhooe.classification.Classificator;
import at.sesame.fhooe.classification.ClassificatorException;
import at.sesame.fhooe.wifi.IWifiScanReceiver;
import at.sesame.fhooe.wifi.WifiAccess;

public class LocalizationService 
extends Service
implements IWifiScanReceiver
{
	private static final String TAG = "LocalizationService";
	private Classificator mClassificator = null;
	private WifiAccess mWifiAccess = null;
	private ArrayList<ILocationReceiver> mReceiver = new ArrayList<ILocationReceiver>();
	
	/**
	 * this handler publishes the result of the classification on the thread
	 * that called the service. this behavior is not needed for functionality,
	 * just for convenience so the calling activity does not have to call
	 * runOnUiThread for updating the UI.
	 */
	private Handler mWifiHandler = new Handler()
	{
		@Override
		public void handleMessage(Message _msg)
		{
			@SuppressWarnings("unchecked")
			ArrayList<ScanResult> results = (ArrayList<ScanResult>) _msg.obj;
			notifyReceivers(results);
		}
	};
	
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
		public void triggerLocationUpdate()throws RemoteException 
		{
			try 
			{
				mWifiAccess.startSingleWifiScan();
			} 
			catch (Exception e) 
			{
				Log.e(TAG, "error getting location", e);
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
		public void setClassifier(String _classifierName)throws RemoteException 
		{
			mClassificator.setClassifier(_classifierName);
		}


		@Override
		public void registerLocationReceiver(ILocationReceiver _recv)throws RemoteException 
		{
			mReceiver.add(_recv);
		}


		@Override
		public void unregisterLocationReceiver(ILocationReceiver _recv)throws RemoteException 
		{
			mReceiver.remove(_recv);
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) 
	{
		return mLocationService;
	}
	
	public void onCreate()
	{
		super.onCreate();
		Log.e(TAG, "created");
		mClassificator = new Classificator(this);
		mWifiAccess = WifiAccess.getInstance(this);
		mWifiAccess.addWifiScanReceiver(this);
	}
	
	private void notifyReceivers(ArrayList<ScanResult> _results)
	{
		String result = "";
		try 
		{
			result = mClassificator.classify(_results);
		} 
		catch (ClassificatorException e1) 
		{
			result = e1.getMessage();
		}
		for(ILocationReceiver recv:mReceiver)
		{
			try {
				recv.setLocation(result);
			} 
			catch (RemoteException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public synchronized void setWifiScanResults(ArrayList<ScanResult> _results) 
	{
		Message msg = new Message();
		msg.obj = _results;
		mWifiHandler.sendMessage(msg);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mWifiAccess.cleanUp();
	}
}
