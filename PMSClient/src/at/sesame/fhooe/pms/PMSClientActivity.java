package at.sesame.fhooe.pms;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import at.sesame.fhooe.lib.pms.PMSProvider;
import at.sesame.fhooe.lib.pms.errorhandling.ErrorForwarder;
import at.sesame.fhooe.lib.pms.errorhandling.IErrorReceiver;
import at.sesame.fhooe.lib.pms.model.ControllableDevice;
import at.sesame.fhooe.lib.pms.model.ExtendedPMSStatus;
import at.sesame.fhooe.lib.pms.model.PMSStatus;
import at.sesame.fhooe.lib.pms.model.ControllableDevice.PowerOffState;
import at.sesame.fhooe.lib.pms.proxy.ProxyHelper;

public class PMSClientActivity 
extends Activity
implements OnItemSelectedListener, OnClickListener, IErrorReceiver
{
	private static final String TAG = "PMSClientActivity";
	private static final int NETWORKING_IN_PROGRESS_DIALOG_ID = 0;
	private Spinner mDeviceSelection;
	private ArrayAdapter<String> mSpinnerAdapter;

	private Button mShutDownButt;
	private Button mWakeupButt;
	private Button mSleepButt;
	private Button mStatusButt;
	private Button mExtendedStatusButt;

	private ControllableDevice mCurrentDevice;
	private ArrayList<ControllableDevice> mDevices = new ArrayList<ControllableDevice>();

	private Context mContext;
	
	ProgressDialog mNetworkingDialog;

	HttpClient mClient = ProxyHelper.getProxiedAllAcceptingHttpsClient();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ErrorForwarder.getInstance().register(this);
		
		setupNetworkingDialog();
		
		loadDevices();

		

		mSleepButt = (Button)findViewById(R.id.main_xml_sleepButt);
		mSleepButt.setOnClickListener(this);

		mShutDownButt = (Button)findViewById(R.id.main_xml_shutDownButt);
		mShutDownButt.setOnClickListener(this);

		mStatusButt = (Button)findViewById(R.id.main_xml_statusButt);
		mStatusButt.setOnClickListener(this);

		mExtendedStatusButt = (Button)findViewById(R.id.main_xml_extendedStatusButt);
		mExtendedStatusButt.setOnClickListener(this);

		mWakeupButt = (Button)findViewById(R.id.main_xml_wakeupButt);
		mWakeupButt.setOnClickListener(this);
//		mContext = getApplicationContext();

		
		
//		mNetworkingDialog.dismiss();
		//		Button rawPoweroffTestButt = (Button)findViewById(R.id.main_xml_rawTestPoweroffButt);
		//		rawPoweroffTestButt.setOnClickListener(this);
		//		
		//		Button rawWakeupTestButt = (Button)findViewById(R.id.main_xml_rawTestWakeupButt);
		//		rawWakeupTestButt.setOnClickListener(this);
		//		
		//		Button rawExtendedStatusTestButt = (Button)findViewById(R.id.main_xml_rawExtendedTestButton);
		//		rawExtendedStatusTestButt.setOnClickListener(this);
		//		testRawClient();
		//		String clients = PMSProvider.getPMS().getClients();
		//		Log.e(TAG, clients);

		//		String encodedURL;
		//		try {
		//			encodedURL = URLEncoder.encode("00:21:5A:17:40:CE", "UTF-8");
		//			PMSProvider.getPMS().reportIdle(encodedURL, 10);
		//		} catch (UnsupportedEncodingException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		testReportIdleRaw();
		//		try {
		//			Thread.sleep(10000);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		PMSProvider.getPMS().reportIdle("00:21:5A:17:40:CE", 10);


	}
	
	private void setupNetworkingDialog()
	{
		mNetworkingDialog = new ProgressDialog(PMSClientActivity.this);
		mNetworkingDialog.setMessage("Networking in progress, please wait...");
		mNetworkingDialog.setCancelable(false);
		mNetworkingDialog.setCanceledOnTouchOutside(false);
		mNetworkingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
	
	private void showNetworkingDialog()
	{
		mNetworkingDialog.show();
	}
	
	private void dismissNetworkingDialog()
	{
		if(mNetworkingDialog.isShowing())
		{
			mNetworkingDialog.dismiss();
		}
	}
	
	private void initSpinner()
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() 
			{
				mSpinnerAdapter= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, getControllableHostNames());
				mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				mDeviceSelection = (Spinner)findViewById(R.id.main_xml_device_spinner);
				mDeviceSelection.setAdapter(mSpinnerAdapter);
				mDeviceSelection.setOnItemSelectedListener(PMSClientActivity.this);
				
				setCurrentDevice(mDeviceSelection.getSelectedItem());
				
			}
		});
		
	}
	
//	@Override
//	public Dialog onCreateDialog(int _id)
//	{
//		Dialog d = null;
//		switch(_id)
//		{
//		case NETWORKING_IN_PROGRESS_DIALOG_ID:
//			Log.e(TAG, "creating progress dialog");
//			ProgressDialog progressDialog;
//			progressDialog = new ProgressDialog(PMSClientActivity.this);
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			progressDialog.setMessage("Networking in progress, please wait...");
//			progressDialog.setCancelable(false);
//			d = progressDialog;
////			progressDialog.show();
//			
//			
//		break;
//		}
//		d.show();
//		return d;
//		
//	}

	private void loadDevices() 
	{
		showNetworkingDialog();
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				Looper.prepare();
				ArrayList<String> macs = new ArrayList<String>();
				macs = PMSProvider.getDeviceList();
				for(int i = 0;i<macs.size();i++)
				{
					ControllableDevice dev = new ControllableDevice(macs.get(i), "admin", "pwd", true);
					mDevices.add(dev);
//					mNetworkingDialog.setProgress((int)(((double)i/macs.size())*100));
					Log.e(TAG, dev.toString());
				}
				dismissNetworkingDialog();
				initSpinner();
				Looper.loop();
			}
		}).start();
		
	}
	//	private void testPoweroffRaw()//Post with json
	//	{
	//		new Thread(new Runnable() 
	//		{
	//			@Override
	//			public void run() 
	//			{
	//				
	//				String powerOffUrl = "http://80.120.3.4:8080/pms/00:21:5A:17:40:CE/poweroff";
	//				
	//				HttpPost powerOffRequest = new HttpPost(powerOffUrl);
	//				powerOffRequest.addHeader("Content-Type", "application/json");
	//				JSONObject jsonForPoweroff = new JSONObject();
	//				try 
	//				{
	//					jsonForPoweroff.put("target-state", "shutdown");
	//					jsonForPoweroff.put("os", "windows");
	//					jsonForPoweroff.put("username", "admin");
	//					jsonForPoweroff.put("password", "pwd");
	//
	//					powerOffRequest.setEntity(new ByteArrayEntity(jsonForPoweroff.toString().getBytes()));
	//					HttpResponse resp = mClient.execute(powerOffRequest);
	//					
	//
	//					Log.e(TAG, resp.getStatusLine().toString());
	//				} catch (JSONException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				} catch (ClientProtocolException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				} catch (IOException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
	//			}
	//		}).start();
	//	}

	//	private void testReportIdleRaw()//Post with json
	//	{
	//		new Thread(new Runnable() 
	//		{
	//			@Override
	//			public void run() 
	//			{
	//				String mac ="";
	//				try {
	//					mac = URLEncoder.encode("00:21:5A:17:40:CE", "UTF-8");
	//				} catch (UnsupportedEncodingException e1) {
	//					// TODO Auto-generated catch block
	//					e1.printStackTrace();
	//				}
	//				String idleSinceUrl = "http://80.120.3.4:8080/pms/"+mac+"/report-idle";
	////				String idleSinceUrl = "http://80.120.3.4:8080/pms/00:21:5A:17:40:CE/report-idle";
	//				HttpPost idleSinceRequest = new HttpPost(idleSinceUrl);
	//				idleSinceRequest.setHeader("Content-Type", "application/json");
	//
	//				JSONObject jsonForReportIdle = new JSONObject();
	//				try 
	//				{
	//					jsonForReportIdle.put("idle-since", "10");
	//					idleSinceRequest.setEntity(new ByteArrayEntity(jsonForReportIdle.toString().getBytes(/*"UTF-8"*/)));
	//					HttpResponse resp = mClient.execute(idleSinceRequest);
	//
	//					
	//
	//					Log.e(TAG, resp.getStatusLine().toString());
	//				} catch (JSONException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				} catch (ClientProtocolException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				} catch (IOException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
	//			}
	//		}).start();
	//	}

	//	private void testWakeupRaw()//Get
	//	{
	//		new Thread(new Runnable() 
	//		{	
	//			@Override
	//			public void run() 
	//			{
	//				String wakeupUrl = "http://80.120.3.4:8080/pms/00:21:5A:17:40:CE/wakeup";
	//				HttpGet wakeupRequest = new HttpGet(wakeupUrl);
	//
	//				try {
	//					HttpResponse resp = mClient.execute(wakeupRequest);
	//					Log.e(TAG, resp.getStatusLine().toString());
	//				} catch (ClientProtocolException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				} catch (IOException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
	//
	//			}
	//		}).start();
	//	}

	//	private void testExtendedStatusRaw()//Post
	//	{
	//		new Thread(new Runnable() 
	//		{
	//			@Override
	//			public void run() 
	//			{
	//				String extendedStatusUrl = "http://80.120.3.4:8080/pms/"+mCurrentDevice.getMac()+"/extended-status";
	//				HttpPost extendedStatusRequest = new HttpPost(extendedStatusUrl);
	//				extendedStatusRequest.addHeader("Content-Type", "application/json");
	//				JSONObject jsonForExtendedStatus = new JSONObject();
	//
	//				try {
	//					jsonForExtendedStatus.put("username", "admin");
	//					jsonForExtendedStatus.put("password", "pwd");
	//					String empty = "{}";
	//					extendedStatusRequest.setEntity(new ByteArrayEntity(empty.getBytes()));
	//
	//					HttpResponse resp = mClient.execute(extendedStatusRequest);
	//					Log.e(TAG, resp.getStatusLine().toString());
	//				} catch (JSONException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				} catch (ClientProtocolException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				} catch (IOException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
	//
	//
	//			}
	//		}).start();
	//	}

	private ArrayList<String> getControllableHostNames()
	{
		ArrayList<String> hostnames = new ArrayList<String>();
		for(ControllableDevice cd:mDevices)
		{
			hostnames.add(cd.getHostname());
		}
		return hostnames;
	}

	private void setCurrentDevice(Object _item)
	{
		ControllableDevice curDev = getControllableDeviceFromSelectedItem(_item);
		if(null!=curDev)
		{
			mCurrentDevice = curDev;
			if(mCurrentDevice.isAlive())
			{
//				mWakeupButt.setEnabled(false);
//				mSleepButt.setEnabled(true);
//				mShutDownButt.setEnabled(true);
			}
			else
			{
//				mSleepButt.setEnabled(false);
//				mShutDownButt.setEnabled(false);
//				mWakeupButt.setEnabled(true);
			}
		}
		else
		{
			Log.e(TAG,"selected device was not found");
		}
	}

	private ControllableDevice getControllableDeviceFromSelectedItem(Object _item)
	{
		for(ControllableDevice cd:mDevices)
		{
			if(cd.getHostname().equals(_item))
			{
				return cd;
			}
		}
		return null;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		setCurrentDevice(mDeviceSelection.getSelectedItem());
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) 
	{
//		showDialog(NETWORKING_IN_PROGRESS_DIALOG_ID);
//		ProgressDialog pd = ProgressDialog.show(PMSClientActivity.this, "networking", "Networking in progess, please wait...", true, false);
		Log.e(TAG, "before dialogshow");
		final ProgressDialog progressDialog = ProgressDialog.show(PMSClientActivity.this, "", "Networking in progress");
		final int id = v.getId();
		Log.e(TAG, "after dialogshow");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				switch(id)
				{
				case R.id.main_xml_sleepButt:
					if(mCurrentDevice.powerOff(PowerOffState.sleep))
					{
						toastOnUiThread("sleep of "+mCurrentDevice.getHostname()+" successful");
					}
					break;
				case R.id.main_xml_shutDownButt:
					if(mCurrentDevice.powerOff(PowerOffState.shutdown))
					{
						toastOnUiThread("shutdown of "+mCurrentDevice.getHostname()+" successful");
					}
					else
					{
						toastOnUiThread("shutdown of "+mCurrentDevice.getHostname()+" failed");
					}

					break;
				case R.id.main_xml_statusButt:
					PMSStatus status = mCurrentDevice.getStatus();

					toastOnUiThread(status.toString());
					break;
				case R.id.main_xml_extendedStatusButt:
					ExtendedPMSStatus extStatus = mCurrentDevice.getExtendedStatus();
					if(null==extStatus)
					{
						//				Log.e(TAG, "extended status was null");
						break;
					}
					toastOnUiThread(extStatus.toString());
					break;
				case R.id.main_xml_wakeupButt:
					if(mCurrentDevice.wakeUp())
					{
						toastOnUiThread("wakeup of "+mCurrentDevice.getHostname()+" successful");
					}
					else
					{
						toastOnUiThread("wakeup of "+mCurrentDevice.getHostname()+" failed");
					}

					break;
					//		case R.id.main_xml_rawTestPoweroffButt:
					//			testPoweroffRaw();
					//			break;
					//		case R.id.main_xml_rawExtendedTestButton:
					//			testExtendedStatusRaw();
					//			break;
					//		case R.id.main_xml_rawTestWakeupButt:
					//			testWakeupRaw();
					//			break;
				}
//				dismissDialog(NETWORKING_IN_PROGRESS_DIALOG_ID);
				progressDialog.dismiss();
				
			}
		}).start();
		
	}
	
	private void toastOnUiThread(final String _msg)
	{
		runOnUiThread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				Toast.makeText(getApplicationContext(), _msg, Toast.LENGTH_LONG).show();
				
			}
		});
	}

	@Override
	public void notifyError(final String _msg) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), _msg, Toast.LENGTH_LONG).show();
				
			}
		});
		

	}

}