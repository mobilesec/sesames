package at.sesame.fhooe.localizationserviceclient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import weka.core.Instances;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;
import android.widget.ToggleButton;
import at.sesame.fhooe.localizationservice.ILocalizationService;

public class SesameIndoorLocalizationServiceClientActivity 
extends Activity
{
	private static final String TAG = "SesameIndoorLocalizationServiceClientActivity";
	private ILocalizationService mLocalizationService;
	private boolean mUpdateRunning = true;
	private ProgressDialog mProgressDialog;
	private EditText mTrainingBSSIDsField;
	private EditText mReceivedBSSIDsField;
	private ToggleButton mUpdateReceivedBssidButt;
	private static final String mTrainingBSSIDS = 	"00:0c:42:05:4b:5f\n" +
													"00:12:a9:17:00:46\n" +
													"00:12:a9:17:00:48\n" +
													"00:12:a9:17:00:c6\n" +
													"00:12:a9:17:00:c8\n" +
													"00:12:a9:17:11:06\n" +
													"00:12:a9:17:11:08\n" +
													"00:12:a9:17:24:c6\n" +
													"00:12:a9:17:24:c8\n" +
													"00:12:a9:17:4a:c6\n" +
													"00:12:a9:17:4a:c8\n" +
													"00:12:a9:17:50:c8\n" +
													"00:12:a9:17:6b:06\n" +
													"00:12:a9:17:6b:08\n" +
													"00:12:a9:17:7b:46\n" +
													"00:12:a9:17:7b:48\n" +
													"00:12:a9:17:90:06\n" +
													"00:12:a9:17:90:08\n" +
													"00:13:80:94:14:80\n" +
													"00:24:b2:8d:28:66";
	
	
	private Thread mLocationUpdateThread = new Thread(new Runnable() 
	{	
		@Override
		public void run() 
		{
			while(mUpdateRunning)
			{
				try 
				{
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() 
						{
							// TODO Auto-generated method stub
							try {
								setLocationString(mLocalizationService.getLocation());
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					
					Thread.sleep(5000);
				}catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	});
	
//	private Thread mConnectionWaitingThread = new Thread(new Runnable() {
//		
//		@Override
//		public void run() {
//			showProgressDialog("waiting for service to attach...",true);
//			while(null==mLocalizationService)
//			{
//				try {
////					Log.e("SesameIndoorLocalizerServiceActivity","waiting for service");
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			dismissProgressDialog();
//			mLocationUpdateThread.start();
//		}
//	});
	
	private void dismissProgressDialog()
	{
		mProgressDialog.dismiss();
	}
	
	private ServiceConnection mSvc = new ServiceConnection() 
	{	
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) 
		{
			Log.e("silca", "service connected");
			mLocalizationService = ILocalizationService.Stub.asInterface(arg1);
			try 
			{
				
				if(null==mLocalizationService)
				{
					Log.e(TAG, "error connecting to service (null)");
					return;
				}
				boolean res = false;
//				mLocalizationService.setTrainingSetPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/allDataForWekaTrainig.arff");
				if(null!=mLocalizationService)
				{
					mLocalizationService.setClassifier("K* (b=20)");
					res = mLocalizationService.queryAvailableLocalizationDataSources();
				}
				else
				{
					Log.e(TAG, "service cant be null, impossible. :-(");
				}
				
				if(res)
				{
					Log.e(TAG, "query was successful");
				}
				else
				{
					Log.e(TAG, "query was not successful");
				}
				
//				testDocTo();
			} catch (RemoteException e) {
				Log.e("slica","##########################ERROR IN ON SERVICE CONNECTED");
				e.printStackTrace();
			}
			dismissProgressDialog();
			mLocationUpdateThread.start();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) 
		{
			Log.e(TAG, "service disconnected");
		}
	};
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mReceivedBSSIDsField = (EditText)findViewById(R.id.receivedBssids);
        mTrainingBSSIDsField = (EditText)findViewById(R.id.trainingBSSIDs);
        mTrainingBSSIDsField.setText(mTrainingBSSIDS);
        mUpdateReceivedBssidButt = (ToggleButton)findViewById(R.id.toggleButton1);
//        copyArffToSD();
        mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		mProgressDialog.setCancelable(false);
        
        
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	Intent i = new Intent("at.sesame.fhooe.localizationservice.LocalizationService");
        bindService(i, mSvc, BIND_AUTO_CREATE);
//        mConnectionWaitingThread.start();
    }
    
    public void onStop()
    {
    	super.onStop();
    	unbindService(mSvc);
		mSvc = null;
		mUpdateRunning = false;
    }
    
//    public void onDestroy()
//	{
//		super.onDestroy();
//		
//	}
    

	public void setLocationString(String _location) throws RemoteException 
	{
		if(mUpdateReceivedBssidButt.isChecked())
		{
			mReceivedBSSIDsField.setText(_location);
		}
		else
		{
//			Toast.makeText(this, "result of localization service:"+_location, Toast.LENGTH_LONG).show();
		}
		
	}

	
	@SuppressWarnings("unused")
	private void copyArffToSD()
	{
		Log.e("slica", "copying file");
		AssetManager am = getAssets();
		try {
			InputStream is = am.open("all/allDataForWekaTraining.arff");
			File root = Environment.getExternalStorageDirectory();
			File f = new File(root, "allDataForWekaTraining.arff");
            FileOutputStream fos = new FileOutputStream(f);
            
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len1);
                Log.e("silca", "writing file to:"+f.getAbsolutePath());
            }
            fos.flush();
            fos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void testDocTo()
	{
//		HttpHost proxy = new HttpHost("80.120.3.4", 3128, "http");

//        DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpClient httpclient = ProxyHelper.getProxiedAllAcceptingHttpsClient();
        try {
//            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        	
            HttpHost target = new HttpHost("doc.to", 443, "https");
            HttpGet req = new HttpGet("/~sesamedata/all/allDataForWekaTraining.arff");

//            System.out.println("ההההה  executing request to " + target + " via " + proxy);
//            HttpResponse rsp = httpclient.execute(target, req);
            HttpUriRequest request = new HttpGet("https://doc.to/~sesamedata/all/allDataForWekaTraining.arff");
            HttpResponse rsp = httpclient.execute(request);
            Log.e(TAG, "request executed");
            HttpEntity entity = rsp.getEntity();
            InputStream entityStream = entity.getContent();
            Instances inst = InstanceHelper.getInstancesFromInputStream(entityStream);
            Log.e(TAG, "number of instances ="+inst.numInstances());
//            System.out.println("----------------------------------------");
//            System.out.println(rsp.getStatusLine());
//            Header[] headers = rsp.getAllHeaders();
//            for (int i = 0; i<headers.length; i++) {
//                System.out.println(headers[i]);
//            }
//            System.out.println("----------------------------------------");
//
//            if (entity != null) {
//                System.out.println(EntityUtils.toString(entity));
//            }

        } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
	}
	
	private void showProgressDialog(final String _title, boolean _onUiThread)
	{
		if(_onUiThread)
		{
			runOnUiThread(new Runnable() 
			{	
				@Override
				public void run() 
				{		
					
					mProgressDialog.setMessage(_title);
					mProgressDialog.show();	
				}
			});
		}
		else
		{
			mProgressDialog.setMessage(_title);
			mProgressDialog.show();
		}
		
	}
}