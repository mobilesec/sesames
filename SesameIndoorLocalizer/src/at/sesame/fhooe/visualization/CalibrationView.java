package at.sesame.fhooe.visualization;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.FirstOrder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.sesame.fhooe.R;
import at.sesame.fhooe.calibration.Calibrator;
import at.sesame.fhooe.util.ARFFGenerator;
import at.sesame.fhooe.util.ArrayHelper;
import at.sesame.fhooe.util.InstanceHelper;

public class CalibrationView 
extends Activity 
{
	private static final String TAG = "CalibrationView";
	private static final int REFERENCESET_SELECTION_DIALOG = 0;
	private static final int CALIBRATIONSET_SELECTION_DIALOG = 1;
	
	private Instances mReferenceInstances = null;
	private Instances mCalibrationInstances = null;
	
	@Override
	public void onCreate(final Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.calibration);
		final Calibrator c = new Calibrator();

		Button showChartButt = (Button)findViewById(R.id.calibrationViewShowChartButton);
		showChartButt.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(REFERENCESET_SELECTION_DIALOG);
			}
		});
		
		Button calAndExButt = (Button)findViewById(R.id.calAndExButt);
		calAndExButt.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				new Thread(new Runnable() 
				{	
					@Override
					public void run() 
					{
//						calibrateAndExportAllMP();
						calibrateAndExportAllRoom();
					}
				}).start();
			}
		});
		
//		if(null==_savedInstance)
//		{
//			showDialog(REFERENCESET_SELECTION_DIALOG);
//		}
	}
	
	private void generateAndShowCalibrationResults()
	{
		Intent intent = new Intent(getApplicationContext(), ChartView.class);
		Bundle b = new Bundle();
//		while(null==mCalibrationInstances)
//		{
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		//b.putParcelableArrayList("data", c.generateAttributeValueList(null));
//		Instance firstInst = mCalibrationInstances.firstInstance();
//		String attribute2check = firstInst.stringValue(firstInst.classAttribute());
		String attribute2check = Calibrator.getFirstCommonAttribute(mReferenceInstances, mCalibrationInstances);
		ArrayList<double[]> referenceData = InstanceHelper.generateAttributeValueList(InstanceHelper.extractInstances(mReferenceInstances, attribute2check));
		ArrayList<double[]> calibrationData = InstanceHelper.generateAttributeValueList(InstanceHelper.extractInstances(mCalibrationInstances, attribute2check));
//		ArrayList<double[]> referenceData = InstanceHelper.generateAttributeValueList(mReferenceInstances);
//		ArrayList<double[]> calibrationData = InstanceHelper.generateAttributeValueList(mCalibrationInstances);
		
		referenceData = ArrayHelper.removeValues(referenceData, -100);
		calibrationData = ArrayHelper.removeValues(calibrationData, -100);
		
		ArrayList<Double>x = new ArrayList<Double>();
		ArrayList<Double>y = new ArrayList<Double>();
		
		for(int i = 0;i<referenceData.size();i++)
		{
			double[]refArr = referenceData.get(i);
			double[]calArr = calibrationData.get(i);
			if(refArr.length>0&&calArr.length>0)
			{
				x.add(ArrayHelper.mean(refArr));
				y.add(ArrayHelper.mean(calArr));
			}
//			x[i] = ArrayHelper.mean(extractedRefList.get(i));
//			y[i] = ArrayHelper.mean(extractedCalList.get(i));
		}
//		double[]x = new double[referenceData.size()];
//		double[]y = new double[calibrationData.size()];
//		for(int i = 0;i<x.length;i++)
//		{
//			x[i] = ArrayHelper.mean(referenceData.get(i));
//			y[i] = ArrayHelper.mean(calibrationData.get(i));
//		}
//		int referenceDataLen = referenceData.get(0).length;
//		int calibrationDataLen = calibrationData.get(0).length;
//		
//		int dataLen = referenceDataLen<calibrationDataLen?referenceDataLen:calibrationDataLen;
//		
//		for(int i = 0;i<referenceData.size();i++)
//		{
//			double[] arrLong = referenceData.get(i);
//			double[] arrShort = new double[dataLen];
//			for(int j = 0 ;j<dataLen;j++)
//			{
//				arrShort[j]=arrLong[i];
//			}
//			b.putDoubleArray("reference"+i, arrShort);
//		}
//		for(int i = 0;i<calibrationData.size();i++)
//		{
//			double[] arrLong = referenceData.get(i);
//			double[] arrShort = new double[dataLen];
//			for(int j = 0 ;j<dataLen;j++)
//			{
//				arrShort[j]=arrLong[i];
//			}
//			b.putDoubleArray("calibration"+i, arrShort);
//		}
//		String[] titles = new String[mReferenceInstances.numAttributes()];
//		for(int i = 1;i<mReferenceInstances.numAttributes();i++)
//		{
//			Attribute att = mReferenceInstances.attribute(i);
//			
//			titles[i-1] = att.name();
//		}
//		titles[titles.length-1] = "lineare regression";
		String[] titles = new String[]{attribute2check, "linear regression"/*, "logarithmic regression"*/};
		
		b.putStringArray("titles", titles);
		
		Calibrator c = new Calibrator();
		double[] ab = c.performLinearCalibration(mReferenceInstances, mCalibrationInstances);
		Log.e(TAG, "!!!!!a="+ab[0]+", b="+ab[1]);
		double[]xLinReg = new double[20];
		double[]yLinReg = new double[20];
		for(int i = 0;i<xLinReg.length;i++)
		{
			double xVal = -100+2*i;
			xLinReg[i] = xVal;
			yLinReg[i]= ab[0]+ab[1]*xVal;
		}
		
//		double[] ar = c.performLogarithmicCalibration(mReferenceInstances, mCalibrationInstances);
//		double[] xLogReg = new double[50];
//		double[] yLogReg = new double[50];
//		
//		for(int i = 0;i<xLogReg.length;i++)
//		{
//			double xVal = -110+2*i;
//			xLogReg[i] = xVal;
//			yLogReg[i] = -1*ar[0]*Math.pow(-1*ar[1], xVal);
//		}
		
		b.putDoubleArray("x", ArrayHelper.getDoubleArrayFromArrayList(x));
		b.putDoubleArray("y", ArrayHelper.getDoubleArrayFromArrayList(y));
		b.putDoubleArray("linRegX", xLinReg);
		b.putDoubleArray("linRegY", yLinReg);
//		b.putDoubleArray("logRegX", xLogReg);
//		b.putDoubleArray("logRegY", yLogReg);
		intent.putExtras(b);
		startActivity(intent);
	}
	
	private void calibrateAndExportAllMP()
	{
		
		Calibrator cal = new Calibrator();
		
		Instances all = InstanceHelper.getInstancesFromAssets(getAssets(), "all/allDataForWekaTraining_MP.arff");
		Instances desire = InstanceHelper.getInstancesFromAssets(getAssets(), "desire/desireData_20_07_mp.arff");
		Instances nexus = InstanceHelper.getInstancesFromAssets(getAssets(), "nexus/nexusData_20_07_mp.arff");
		Instances g1 = InstanceHelper.getInstancesFromAssets(getAssets(), "g1/g1Data_21_07_mp.arff");
		
		Instances[] reference = new Instances[]{	all, 	all, 	all,	desire, desire, g1, 	g1, 	nexus, 	nexus};
		Instances[] calibration = new Instances[]{	desire, g1, 	nexus, 	g1, 	nexus, 	desire, nexus, 	desire, g1};

		
		String[]fileNames = new String[]{	"desireCalibratedForAll.arff", "g1CalibratedForAll.arff","nexusCalibratedForAll.arff",
											"g1CalibratedForDesire.arff", "nexusCalibratedForDesire.arff","desireCalibratedForG1.arff",
											"nexusCalibratedForG1.arff", "desireCalibratedForNexus.arff","g1CalibratedForNexus.arff"};
		
		for(int i = 0;i<reference.length;i++)
		{
			long start = System.currentTimeMillis();
			double[] ab = cal.performLinearCalibration(reference[i], calibration[i]);
			Instances calibrated = Calibrator.performLinearTransformation(calibration[i], ab[0], ab[1]);
			ARFFGenerator.writeInstancesToArff(calibrated, getExternalFilesDir(null).getAbsolutePath()+"/normalizedSingleMP/"+fileNames[i]);
			Log.e(TAG, "calibration and export for "+fileNames[i]+" took "+(System.currentTimeMillis()-start)+" ms");
		}
	}
	
	private void calibrateAndExportAllRoom()
	{
		Calibrator cal = new Calibrator();
		
		Instances all = InstanceHelper.getInstancesFromAssets(getAssets(), "all/allDataForWekaTraining_Room.arff");
		Instances desire = InstanceHelper.getInstancesFromAssets(getAssets(), "desire/desireData_20_07_rooms.arff");
		Instances nexus = InstanceHelper.getInstancesFromAssets(getAssets(), "nexus/nexusData_20_07_rooms.arff");
		Instances g1 = InstanceHelper.getInstancesFromAssets(getAssets(), "g1/g1Data_21_07_rooms.arff");
		
		Instances[] reference = new Instances[]{	all, 	all, 	all,	desire, desire, g1, 	g1, 	nexus, 	nexus};
		Instances[] calibration = new Instances[]{	desire, g1, 	nexus, 	g1, 	nexus, 	desire, nexus, 	desire, g1};

		
		String[]fileNames = new String[]{	"desireCalibratedForAll.arff", "g1CalibratedForAll.arff","nexusCalibratedForAll.arff",
											"g1CalibratedForDesire.arff", "nexusCalibratedForDesire.arff","desireCalibratedForG1.arff",
											"nexusCalibratedForG1.arff", "desireCalibratedForNexus.arff","g1CalibratedForNexus.arff"};
		
		for(int i = 0;i<reference.length;i++)
		{
			long start = System.currentTimeMillis();
			double[] ab = cal.performLinearCalibration(reference[i], calibration[i]);
			Instances calibrated = Calibrator.performLinearTransformation(calibration[i], ab[0], ab[1]);
			ARFFGenerator.writeInstancesToArff(calibrated, getExternalFilesDir(null).getAbsolutePath()+"/normalizedRoomV2/"+fileNames[i]);
			Log.e(TAG, "calibration and export for "+fileNames[i]+" took "+(System.currentTimeMillis()-start)+" ms");
			Log.e(TAG, "intercept="+ab[0]+", slope="+ab[1]);
		}
	}
	
	@Override
	public Dialog onCreateDialog(int _id)
	{
		final String[] availableDataSets = InstanceHelper.getDataSetListFromAssets(getAssets()).toArray(new String[]{});

		String[] dataSetNames = new String[availableDataSets.length];
		
		for(int i = 0;i<dataSetNames.length;i++)
		{
			dataSetNames[i] = availableDataSets[i].substring(availableDataSets[i].lastIndexOf("/")+1);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch(_id)
		{
		case REFERENCESET_SELECTION_DIALOG:
			builder.setTitle(R.string.calibrationView_referenceSelectionDialogTitle);
    		builder.setItems(dataSetNames, new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int item) 
    		    {
    		    	final int idx = item;
//    		    	dismissDialog(TRAININGSET_SELECTION_DIALOG);
    		    	new Thread(new Runnable() {
						
						@Override
						public void run() {

							mReferenceInstances = InstanceHelper.getInstancesFromAssets(getAssets(), availableDataSets[idx]);
							
						}
					}).start();    
    		    }
    		});
    		showDialog(CALIBRATIONSET_SELECTION_DIALOG);
			break;
		case CALIBRATIONSET_SELECTION_DIALOG:
			builder.setTitle(R.string.calibrationView_calibrationSelectionDilogTitle);
    		builder.setItems(dataSetNames, new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int item) 
    		    {
    		    	final int idx = item;
//    		    	dismissDialog(TRAININGSET_SELECTION_DIALOG);
    		    	mCalibrationInstances = InstanceHelper.getInstancesFromAssets(getAssets(), availableDataSets[idx]);
    		    	generateAndShowCalibrationResults();
    		    }
    		});
			break;
		}
		AlertDialog ad = builder.create();
		ad.show();
		return ad;
	}
}
