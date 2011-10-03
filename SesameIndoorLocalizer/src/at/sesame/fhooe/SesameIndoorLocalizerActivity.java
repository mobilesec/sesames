package at.sesame.fhooe;

import java.io.File;
import java.util.ArrayList;

import weka.core.Instance;
import weka.core.Instances;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import at.sesame.fhooe.calibration.Calibrator;
import at.sesame.fhooe.classification.Classificator;
import at.sesame.fhooe.classification.ClassificatorException;
import at.sesame.fhooe.datacollection.DownloadHelper;
import at.sesame.fhooe.evaluation.LiveEvaluationResults;
import at.sesame.fhooe.fingerprintInformation.FingerPrintItem;
import at.sesame.fhooe.util.ARFFGenerator;
import at.sesame.fhooe.util.InstanceHelper;
import at.sesame.fhooe.wifi.IWifiScanReceiver;
import at.sesame.fhooe.wifi.WifiAccess;
import at.sesame.fhooe.xml.CalibrationResultParser;
import at.sesame.fhooe.xml.CalibrationResultWriter;

public class SesameIndoorLocalizerActivity 
extends Activity implements IWifiScanReceiver
{
	private static final String TAG = "SesameIndoorLocalizerActivity";
	
	private static final int TRAININGSET_SELECTION_DIALOG = 0;
	private static final int CLASSIFIER_SELECTION_DIALOG = 1;
	public static final int TRAINING_PROGRESS_DIALOG = 2;
	private static final int EVALUATION_DIALOG = 3;
	
	private static final String MP_PREFIX = "MP";
	
//	private static AssetManager mAssets = null;
	
	private ArrayList<String> mAvailableDataSets = new ArrayList<String>();
	private ArrayList<FingerPrintItem> mFPIs = new ArrayList<FingerPrintItem>();
	
	private Classificator mClassificator = null;
	private WifiAccess mWifiAccess = null;
	
	private static EditText mInfoField = null;
	
	private String mLastLocation = "";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        if(null==savedInstanceState)
        {
        	Log.e(TAG, "bundle was null");
        }
        else
        {
        	Log.e(TAG, "bundle was not null");
        }
        Log.e(TAG, "onCreate");
//        mAssets = getAssets();
        setContentView(R.layout.localization);
        
        mInfoField = (EditText)findViewById(R.id.localizerInfoField);
        
        mClassificator = new Classificator(getApplicationContext());
        
        CalibrationResultParser.setContext(this);
        double[] calibrationVals = CalibrationResultParser.parseCalibrationFile();
        if(null==calibrationVals)
        {
        	Toast.makeText(this, "parsed calibration results were null", Toast.LENGTH_SHORT).show();
        	Log.e(TAG, "parsed calibration results were null");
        }
        else
        {
        	Toast.makeText(this, "a="+calibrationVals[0]+", b="+calibrationVals[1], Toast.LENGTH_SHORT).show();
        	Log.e(TAG, "a="+calibrationVals[0]+", b="+calibrationVals[1]);
        }
        mClassificator.setCalibrationData(calibrationVals[0], calibrationVals[1]);
        
//        getDataSetListFromAssets();
        mAvailableDataSets = InstanceHelper.getDataSetListFromAssets(getAssets());
        
        //TODO: uncomment
//        mWifiAccess = WifiAccess.getInstance(this,false);
        
        
        mWifiAccess.addWifiScanReceiver(this);
//        retrieveArff();
//        testCalibrator();
//        testArffGenerator();
        if(null==savedInstanceState)
        {
        	//showDialog(TRAININGSET_SELECTION_DIALOG);
        }
    }
    

    
    
    @SuppressWarnings("unused")
	private void testArffGenerator()
    {
    	double[] calibrationVals = CalibrationResultParser.parseCalibrationFile();
    	
    	Instances i = InstanceHelper.getInstancesFromAssets(getAssets(),"desire/desireData_20_07_mp.arff");
    	Log.e(TAG, "read: num attributes:"+i.numAttributes()+", num instances:"+i.numInstances());
    	ARFFGenerator.writeInstancesToArff(	Calibrator.performLinearTransformation(i, calibrationVals[0], calibrationVals[1]), 
    										getExternalFilesDir(null).getAbsolutePath()+"/desireNormalizedForNexus.arff");
    	
    	
    	Instances iStored = InstanceHelper.getInstancesFromFile(new File(getExternalFilesDir(null).getAbsolutePath()+"/desireNormalizedForNexus.arff"));
    	Log.e(TAG, "stored: num attributes:"+iStored.numAttributes()+", num instances:"+iStored.numInstances());
    }
    
    @SuppressWarnings("unused")
    private void testCalibrator()
    {
//    	Instances reference = getInstancesFromAssets("desire/desireData_20_07_mp.arff");
    	Instances reference = InstanceHelper.getInstancesFromAssets(getAssets(),"nexus/nexusData_20_07_mp.arff");

    	
//    	Instances calibration = getInstancesFromAssets("nexus/nexusData_20_07_mp.arff");
    	Instances calibration = InstanceHelper.getInstancesFromAssets(getAssets(),"desire/desireData_20_07_mp.arff");
//    	Instances calibration = getInstancesFromAssets("g1/g1Data_21_07_mp.arff");
    	Calibrator c = new Calibrator();
    	long offsetStart = System.currentTimeMillis();
    	double offset = c.performOffsetCalibration(reference, calibration);
    	Log.e(TAG, "offset = "+offset+" (computation time:"+(System.currentTimeMillis()-offsetStart)+" ms)");
    	
//    	double[] x = new double[]{55,74,77,85,110,150};
//    	double[] y = new double[]{6.4,7.6,6.8,7.9,9.3,10.8};

    	long linRegStart = System.currentTimeMillis();
    	double[] ab = c.performLinearCalibration(reference, calibration);
    	Log.e(TAG, "a="+ab[0]+", b="+ab[1]+" (computation time:"+(System.currentTimeMillis()-linRegStart)+" ms)");
    	Log.e(TAG, "datadir="+getFilesDir()+"calibration.xml");
    	boolean res = CalibrationResultWriter.exportLinearRegressionResults(getFilesDir().getAbsolutePath()+"/calibration.xml", ab[0], ab[1], "allData");
    	if(res)
    	{
    		Toast.makeText(this, "Export successful", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show();
    	}
    }
    
    @SuppressWarnings("unused")
	private void retrieveArff()
    {
    	//http://pwr-apw.googlecode.com/files/iris.arff
//    	HttpRetriever retr = new HttpRetriever();
//    	String res = retr.retrieve("http://pwr-apw.googlecode.com/files/iris.arff");
//    	if(null==res)
//    	{
//    		return;
//    	}
//    	System.out.println(res);
    	String url = "http://pwr-apw.googlecode.com/files/iris.arff";
//    	String url = "ftp://Test01:test01@127.0.0.1/allDataForWekaTraining_Room.arff";
//    	String url = "http://lyle.smu.edu/~yuhangw/hykgene/testdata/amlall/AMLALL.arff";
//    	String url = "http://www.google.com";
//    	String url = "ftp://Test01:test01@193.170.124.186/allDataForWekaTraining_Room.arff";
    	Instances i = InstanceHelper.getInstancesFromFile(DownloadHelper.downloadFile(url, getFilesDir().getAbsolutePath()));
    	if(null==i)
    	{
    		Log.e(TAG, "instances was null....");
    		return;
    	}
    	Log.e(TAG, "number of attributes in downloaded file="+i.numAttributes());
    	
    	
    }
    
    public void onDestroy()
    {
    	super.onDestroy();
    	Log.e(TAG, "onDestroy");
    	mWifiAccess.cleanUp();
    }
    
    public void onPause()
    {
    	super.onPause();
    	mWifiAccess.cleanUp();
    }
    
    public void onResume()
    {
    	super.onResume();
    	//mWifiAccess.startContinuousScanning();
    }
    
    public Dialog onCreateDialog(int _id)
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		
    	switch(_id)
    	{
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
    		    	final int idx = item;
    		    	dialog.cancel();
//    		    	dismissDialog(TRAININGSET_SELECTION_DIALOG);
    		    	new Thread(new Runnable() {
						
						@Override
						public void run() {

							mClassificator.setTrainingData(InstanceHelper.getInstancesFromAssets(getAssets(),mAvailableDataSets.get(idx)));
//							dismissDialog(TRAINING_PROGRESS_DIALOG);
						}
					}).start();
    		        
    		    }
    		});
//    		showDialog(TRAINING_PROGRESS_DIALOG);
    		showDialog(CLASSIFIER_SELECTION_DIALOG);
    		break;
    	case CLASSIFIER_SELECTION_DIALOG:
    		final String[] classifierNames = mClassificator.getClassifierNames();
    		builder.setTitle(getString(R.string.SesameIndoorLocalizer_classifierSelectionTitle));
    		builder.setItems(classifierNames, new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int item) {
    		        mClassificator.setClassifier(classifierNames[item]);
    		    }
    		});
    		break;
    	case TRAINING_PROGRESS_DIALOG:
    		ProgressDialog progressDialog;
    		progressDialog = new ProgressDialog(this);
    		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		progressDialog.setMessage("Building training data, please wait...");
    		progressDialog.setCancelable(false);
    		progressDialog.show();
    		
    		return progressDialog;
    	case EVALUATION_DIALOG:
    		builder.setMessage(getString(R.string.SesameIndoorLocalizerActivity_evaluationDialogMessagePart1)+mLastLocation+"\n"+
    				getString(R.string.SesameIndoorLocalizerActivity_evaluationDialogMessagePart2));
    		builder.setPositiveButton(R.string.SesameIndoorLocalizerActivity_evaluationDialogPositiveButtonText, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{	
					LiveEvaluationResults.addResult(mClassificator.getLastInstance(), mLastLocation, true);
					mWifiAccess.startSingleWifiScan();
				}
			});
    		builder.setNegativeButton(R.string.SesameIndoorLocalizerActivity_evaluationDialogNegativeButtonText, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{	
					LiveEvaluationResults.addResult(mClassificator.getLastInstance(), mLastLocation, false);
					mWifiAccess.startSingleWifiScan();
				}
			});
    		
    		builder.setNeutralButton(R.string.SesameIndoorLocalizerActivity_evaluationDialogNeutralButtonText, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					double correct = LiveEvaluationResults.getCorrectPercentage();
					double incorrect = LiveEvaluationResults.getIncorrectPercentage();
					Toast.makeText(SesameIndoorLocalizerActivity.this, "correct = "+correct+", incorrect = "+incorrect, Toast.LENGTH_LONG).show();
					LiveEvaluationResults.printAllResults();				}
			});
    		
    		break;
    	}
    	
    	AlertDialog alert = builder.create();
		alert.show();
    	return alert;
    }
    
    public void notifySettingTrainingDataFinished()
    {
    	runOnUiThread(new Runnable() 
    	{	
			@Override
			public void run() 
			{
				dismissDialog(TRAINING_PROGRESS_DIALOG);
		    	mWifiAccess.startSingleWifiScan();	
			}
		});
    	
    }
    
    

	@Override
	public void setWifiScanResults(ArrayList<ScanResult> _results) 
	{
		Log.e(TAG, "scan result received");
		if(null==mClassificator)
		{
			Log.e(TAG, "classificator was null");
			return;
		}
		String location ="";
		try {
			location = mClassificator.classify(_results);
		} catch (ClassificatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null==location)
		{
			Log.e(TAG, "location was null");
//			return;
		}
		if(null!=location && location.startsWith(MP_PREFIX))
		{
			Log.e(TAG, location+" will be mapped to a room.");
			location = getFPIByName(location).getRoom();
		}
		mLastLocation = location;
		visualizeLocation(location);
		showDialog(EVALUATION_DIALOG);
	}
	
	/**
	 * searches for a certain FingerPrintItem specified by it's name and returns it
	 * @param _name name of the FingerPrintItem
	 * @return the FingerPrintItem with the passed name, or null if no FingerPrintItem was found
	 */
	private FingerPrintItem getFPIByName(String _name)
	{
		for(FingerPrintItem fpi:mFPIs)
		{
			if(fpi.getName().equals(_name))
			{
				return fpi;
			}
		}
		return null;
	}
	
	public static void visualize(String _s)
	{
		mInfoField.append(_s+"\n");
	}
	
	private void visualizeLocation(String _location)
	{
		Log.e(TAG, "location:"+_location);
		Toast.makeText(this, "current location:"+_location, Toast.LENGTH_SHORT).show();
		SesameIndoorLocalizerActivity.visualize(_location);
	}
}