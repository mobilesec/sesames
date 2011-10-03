package at.sesame.fhooe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.sesame.fhooe.localizationservice.ILocalizationService;
import at.sesame.fhooe.localizationservice.ILocationReceiver;
import at.sesame.fhooe.util.InstanceHelper;

public class SesameIndoorLocalizerServiceActivity
extends Activity
implements Runnable, ILocationReceiver
{
	private static final String TAG = "SesameIndoorLocalizerServiceActivity";
//	private static final int PROGRESS_DIALOG = 0;
	private static final int TRAININGSET_SELECTION_DIALOG = 1;
	private static final int CLASSIFIER_SELECTION_DIALOG = 2;
	private static ActivityManager mAm;
	private ArrayList<String> mAvailableDataSets = new ArrayList<String>();
	
	private ProgressDialog mProgressDialog;
	
	private static String mProgressDialogText = "";
//	private LocalizerServiceConnection mSvc = null;
	
	private EditText mInfoField;
	private Button mLocalizeButt;
	private ILocalizationService mLocalizationService = null;
	
	private ServiceConnection mSvc = new ServiceConnection() 
	{	
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) 
		{
			mLocalizationService = ILocalizationService.Stub.asInterface(arg1);
			try {
				mLocalizationService.registerLocationReceiver(SesameIndoorLocalizerServiceActivity.this);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
	};
	
	@Override
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.localization);
		mAm = (ActivityManager)getSystemService(Activity.ACTIVITY_SERVICE);
		mAvailableDataSets = InstanceHelper.getDataSetListFromAssets(getAssets());
		mInfoField = (EditText)findViewById(R.id.localizerInfoField);
		mLocalizeButt = (Button)findViewById(R.id.localizeButt);
		mLocalizeButt.setOnClickListener(new View.OnClickListener()
		{	
			@Override
			public void onClick(View v) 
			{
				
				localize();		
			}
		});
		mProgressDialog = new ProgressDialog(SesameIndoorLocalizerServiceActivity.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		mProgressDialog.setCancelable(false);
		
//		printRunningServices();
//		Intent i = new Intent();
//		i.setClassName("at.sesame.fhooe.localizationservice", "at.sesame.fhooe.localizationservice.LocalizationService");
		Intent i = new Intent("at.sesame.fhooe.localizationservice.LocalizationService");
//		startService(i);
		getParent().bindService(i, mSvc, BIND_AUTO_CREATE);
//		Log.e("SesameIndoorLocalizerServiceActivity", "bound");
//		printRunningServices();
		new Thread(this).start();
//		showDialog(TRAININGSET_SELECTION_DIALOG);
	}
	public static void printRunningServices()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("------------------------------\n");
		for(RunningServiceInfo rsi:mAm.getRunningServices(Integer.MAX_VALUE))
		{
			sb.append(rsi.service.getShortClassName()+"\n");
		}
		sb.append("------------------------------\n");
		Log.e("SesameIndoorLocalizerServiceActivity", sb.toString());
	}
	
	private void localize()
	{
		try 
		{
			Log.e(TAG, "localization started");
			long start = System.currentTimeMillis();
			showProgressDialog("localization in progress", false);
			mLocalizationService.triggerLocationUpdate();
//			Log.e(TAG, "localization took "+(System.currentTimeMillis()-start)+" ms");
//			
		} 
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Dialog onCreateDialog(int _id)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch(_id)
		{
//		case PROGRESS_DIALOG:
//			ProgressDialog progressDialog;
//    		progressDialog = new ProgressDialog(this);
//    		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//    		progressDialog.setMessage(mProgressDialogText);
//    		progressDialog.setCancelable(false);
//    		progressDialog.show();
//			break;
		case TRAININGSET_SELECTION_DIALOG:
    		String[] dataSetPaths = new String[mAvailableDataSets.size()];
    		dataSetPaths = mAvailableDataSets.toArray(dataSetPaths);
    		String[] dataSetNames = new String[dataSetPaths.length];
    		
    		for(int i = 0;i<dataSetPaths.length;i++)
    		{
    			dataSetNames[i] = dataSetPaths[i].substring(dataSetPaths[i].lastIndexOf("/")+1);
    		}
    		builder.setTitle(getString(R.string.SesameIndoorLocalizer_trainingsetSelectionTitle));
    		builder.setItems(dataSetNames, new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int item) 
    		    {
//    		    	mProgressDialogText = "building training data, please wait...";
//    		    	showDialog(PROGRESS_DIALOG);
    		    	Log.e(TAG, "setting trainigset.");
    		    	
    		    	final int idx = item;
    		    	dialog.cancel();
//    		    	dismissDialog(TRAININGSET_SELECTION_DIALOG);
    		    	new Thread(new Runnable() {
						
						@Override
						public void run() {
							showProgressDialog("building trainingset", true);
							try {
								mLocalizationService.setTrainingSetPath(mAvailableDataSets.get(idx));
								Log.e(TAG, "finished setting trainingset");
//								dismissDialogOnUIThread(PROGRESS_DIALOG);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							dismissProgressDialog();
//							dismissDialog(TRAINING_PROGRESS_DIALOG);
						}
					}).start();
    		        
    		    }
    		});
//    		showDialog(TRAINING_PROGRESS_DIALOG);
//    		showDialog(CLASSIFIER_SELECTION_DIALOG);
    		break;
    	case CLASSIFIER_SELECTION_DIALOG:
    		List<String> classifiers = null;
			try {
				classifiers = mLocalizationService.getAvailableClassifiers();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		final String[] classifierNames = new String[classifiers.size()];
    		for(int i = 0;i<classifiers.size();i++)
    		{
    			classifierNames[i]=classifiers.get(i);
    		}
    		
    		builder.setTitle(getString(R.string.SesameIndoorLocalizer_classifierSelectionTitle));
    		builder.setItems(classifierNames, new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int item) {
    		        try {
						mLocalizationService.setClassifier(classifierNames[item]);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    		    }
    		});
    		showDialog(TRAININGSET_SELECTION_DIALOG);
    		break;
		}
		AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	@Override
	public void run() 
	{
//		mProgressDialogText = "waiting for service to attach...";
		showProgressDialog("waiting for service to attach...",true);
		while(null==mLocalizationService)
		{
			try {
				Log.e("SesameIndoorLocalizerServiceActivity","waiting for service");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dismissProgressDialog();
		showDialogOnUIThread(CLASSIFIER_SELECTION_DIALOG);
	}
	
	private void showDialogOnUIThread(final int _id)
	{
		runOnUiThread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				showDialog(_id);
				
			}
		});
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
	
	private void dismissProgressDialog()
	{
		mProgressDialog.dismiss();
	}
	
	private void dismissDialogOnUIThread(final int _id)
	{
		runOnUiThread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				dismissDialog(_id);
				
			}
		});
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		getParent().unbindService(mSvc);
		mSvc = null;
	}
	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setLocation(final String _location) throws RemoteException 
	{
		dismissProgressDialog();
		if(null!=_location)
		{
			runOnUiThread(new Runnable() 
			{	
				@Override
				public void run() 
				{
					mInfoField.setText(_location);		
				}
			});
		}
		else
		{
			runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{	
					mInfoField.setText(getString(R.string.sesameIndoorLocalizerServiceActivity_noLocationNotificationText));
				}
			});
			
		}
	}
}
