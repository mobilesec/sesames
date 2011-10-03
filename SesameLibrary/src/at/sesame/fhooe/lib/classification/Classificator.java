package at.sesame.fhooe.lib.classification;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Environment;
import android.util.Log;
import at.sesame.fhooe.lib.classification.ClassificatorException;
import at.sesame.fhooe.lib.classification.IClassifier;
import at.sesame.fhooe.lib.classification.KNNLocalizer;
import at.sesame.fhooe.lib.classification.KStarLocalizer;
import at.sesame.fhooe.lib.classification.RandomForrestLocalizer;
import at.sesame.fhooe.lib.classification.ClassificatorException.ExceptionType;
import at.sesame.fhooe.lib.util.InstanceHelper;


import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class Classificator
//implements Runnable
{
	private ArrayList<IClassifier> mClassifiers = new ArrayList<IClassifier>();
	private IClassifier mCurrentClassifier = null;
	private static final int UNRECEIVED_BSSID_RSSI = -100;
	private Instances mTrainingData = null;
	private ArrayList<String> mAttributeNames = new ArrayList<String>();
	private ArrayList<Attribute> mAttributes = new ArrayList<Attribute>();
	private static ArrayList<String> mLastReceivedBSSIDs = new ArrayList<String>();
//	private SesameIndoorLocalizerActivity mOwner;
	
	private double mA = 0.0;
	private double mB = 1.0;
	
	private boolean mUseCalibration = false;
	
//	private String mBssidNames = "";
	
	private Instance mLastInstance = null;
	private Context mContext;
	
	public Classificator(Context _c)
	{
		mContext = _c;
//		mOwner = _owner;
		mClassifiers.add(new KNNLocalizer(1, false));
		mClassifiers.add(new KNNLocalizer(3, true));
		mClassifiers.add(new KNNLocalizer(4, true));
		mClassifiers.add(new KStarLocalizer());
//		mClassifiers.add(new RandomCommitteeLocalizer());
		mClassifiers.add(new RandomForrestLocalizer());
//		new Thread(this).start();
	}
	
	public void setCalibrationData(double _a, double _b)
	{
		mA = _a;
		mB = _b;
		mUseCalibration = true;
	}
	
	public void setTrainingData(String _path) throws FileNotFoundException
	{
		
//		File f = new File(_path);
		File root = Environment.getExternalStorageDirectory();
		File f = new File(root, "allDataForWekaTraining.arff");
		if(!f.exists())
		{
			Log.e("Classificator", _path+" does not exist!!!!!!!!");
			throw new FileNotFoundException(_path+"was not found");
		}
		long start = System.currentTimeMillis();
		Instances inst = InstanceHelper.getInstancesFromFile(f);
		Log.e("Classificator", "time for reading instances from assets:"+(System.currentTimeMillis()-start)+" ms");
		start = System.currentTimeMillis();
		setTrainingData(inst);
		Log.e("Classificator", "time for setting instances:"+(System.currentTimeMillis()-start)+" ms");
	}
	
	public void setTrainingData(Instances _trainingData)
	{
		
		mTrainingData = _trainingData;
		for(IClassifier c:mClassifiers)
		{
			long start = System.currentTimeMillis();
			c.setTrainingData(mTrainingData);
			Log.e("Classificator", "time for setting trainingdata for "+c.getName()+(System.currentTimeMillis()-start)+" ms");
		}
		parseAttributeNames();
//		mOwner.runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				mOwner.notifySettingTrainingDataFinished();
//				
//			}
//		});
	}
	
//	private void evaluateKNN(Instances _training, String _trainingName, Instances _test, String _testName)
//	{
//
//		Log.e("Classificator", 	"TrainingSet:"+_trainingName+"\n"+
//								"TestSet:"+_testName);
//
//		IBk scheme = new IBk();
//		scheme.setDistanceWeighting(new SelectedTag(IBk.WEIGHT_INVERSE, IBk.TAGS_WEIGHTING));
//		
//		Evaluation evaluation;
//		try 
//		{
//			scheme.buildClassifier(_training);
//			evaluation = new Evaluation(_training);
//			for(int i = 1;i<20;i++)
//			{
//				scheme.setKNN(i);
//				long start = System.currentTimeMillis();
//				evaluation.evaluateModel(scheme, _test);
//				Log.e("Classificator", "K="+i);
//				logEvaluationResults(evaluation,start);
//			}
//			
//		}
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//		}
////		try {
////			Thread.sleep(5000);
////		} catch (InterruptedException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		
//
//	}
	
//	private void logEvaluationResults(Evaluation _e, long _start)
//	{
//		double correct = _e.pctCorrect();
//		long duration =  System.currentTimeMillis() - _start;
//		
//		Log.e("Classificator", "correct classifications:"+correct+"(Duration = "+duration/1000+" s)");
//	}
	
	
	public String[] getClassifierNames()
	{
		String[] res = new String[mClassifiers.size()];
		for(int i = 0;i<mClassifiers.size();i++)
		{
			res[i]=mClassifiers.get(i).getName();
		}
		return res;
	}
	
	public Instance getLastInstance()
	{
		return mLastInstance;
	}
	
	@SuppressWarnings("unchecked")
	public String classify(ArrayList<ScanResult> _results) throws ClassificatorException
	{
		Log.e("Classificator", "classifying");
		if(null==mTrainingData)
		{
			throw new ClassificatorException(mContext, ExceptionType.TRAINING_DATA_NOT_SET);
		}
		if(null==mCurrentClassifier)
		{
			throw new ClassificatorException(mContext, ExceptionType.CLASSIFIER_NOT_SET);
		}
		Instance i = getInstanceFromScanResults(_results);
		if(null==i)
		{
			throw new ClassificatorException(mContext, ExceptionType.NO_BSSID_RECOGNIZED, (ArrayList<String>)mLastReceivedBSSIDs.clone());
		}
		mLastInstance = i;
//		SesameIndoorLocalizerActivity.visualize(i.toString());
		return mCurrentClassifier.classify(i);
	}
	
	public void setClassifier(String _classifierName)
	{
		for(IClassifier c:mClassifiers)
		{
			if(_classifierName.equals(c.getName()))
			{
				mCurrentClassifier = c;
				return;
			}
		}
	}
	
	private void parseAttributeNames()
	{
		if(null==mTrainingData)
		{
			Log.e("Classificator", "trainingdata not set yet");
			return;
		}
//		mBssidNames = "";
		@SuppressWarnings("unchecked")
		Enumeration<Attribute> attributesEnum = mTrainingData.enumerateAttributes();
		while(attributesEnum.hasMoreElements())
		{
			Attribute a = attributesEnum.nextElement();
			mAttributeNames .add(a.name());
//			mBssidNames+=a.name()+"--";
			mAttributes .add(a);
			Log.e("Classificator", a.name());
		}
	}
	
	private Instance getInstanceFromScanResults(ArrayList<ScanResult> _results)
	{
		if(null==_results)
		{
			Log.e("Classificator", "empty result passed");
			return null;
		}
		if(	null==mAttributeNames ||mAttributeNames.isEmpty()||
			null==mAttributes || mAttributes.isEmpty())
		{
			return null;
		}
		
		DenseInstance d = new DenseInstance(mAttributeNames.size()+1);
		boolean anyBssidRecognized = false;
		mLastReceivedBSSIDs = new ArrayList<String>();
		for(ScanResult sr:_results)
		{
			mLastReceivedBSSIDs.add(sr.BSSID+"="+sr.level);
		}
		for(Attribute a:mAttributes)
		{
			double currentValue = UNRECEIVED_BSSID_RSSI;
			for(ScanResult sr:_results)
			{
				
//				Log.e("Classificator", "checking:"+sr.BSSID+" and:"+a.name());
				if(sr.BSSID.equals(a.name()))
				{
					anyBssidRecognized = true;
					if(mUseCalibration)
					{
						currentValue = mA+mB*sr.level;
					}
					else
					{
						currentValue = sr.level;
					}
					break;
				}
				
			}
			d.setValue(a, currentValue);
		}
		if(!anyBssidRecognized)
		{
//			SesameIndoorLocalizerActivity.visualize("no BSSID recognized");
			return null;
		}
		d.setDataset(mTrainingData);
		Log.e("classificator", d.toStringNoWeight());
		return d;
	}
	
//	@Override
//	public void run()
//	{
//
//		mOwner.runOnUiThread(new Runnable() 
//		{	
//			@Override
//			public void run() 
//			{
//				mOwner.showDialog(SesameIndoorLocalizerActivity.EVALUATION_PROGRESS_DIALOG);
//			}
//		});
//
//		Instances all = SesameIndoorLocalizerActivity.getInstancesFromAssets("all/allDataForWekaTraining_Room.arff");
//		Instances desire = SesameIndoorLocalizerActivity.getInstancesFromAssets("desire/desireData_20_07_rooms.arff");
//		Instances nexus = SesameIndoorLocalizerActivity.getInstancesFromAssets("nexus/nexusData_20_07_rooms.arff");
//		Instances g1 = SesameIndoorLocalizerActivity.getInstancesFromAssets("g1/g1Data_21_07_rooms.arff");
//
//		Instances[] training = new Instances[]{	all, all, all, all,
//												desire, desire, desire, desire,
//												nexus, nexus, nexus, nexus,
//												g1, g1, g1, g1};
//		
//		Instances[]test = new Instances[]{	all, desire, nexus, g1,
//											all, desire, nexus, g1,
//											all, desire, nexus, g1,
//											all, desire, nexus, g1,};
//		
//		String[] trainingNames = new String[]{	"all","all","all","all",
//												"desire","desire","desire","desire",
//												"nexus","nexus","nexus","nexus",
//												"g1","g1","g1","g1"};
//		
//		String[] testNames = new String[]{	"all", "desire", "nexus", "g1",
//											"all", "desire", "nexus", "g1",
//											"all", "desire", "nexus", "g1",
//											"all", "desire", "nexus", "g1"};
//				
//		
//
//		for(int i = 0;i<training.length;i++)
//		{
//			evaluateKNN(training[i], trainingNames[i], test[i], testNames[i]);
//		}
//		mOwner.dismissDialog(SesameIndoorLocalizerActivity.EVALUATION_PROGRESS_DIALOG);
//	}
}
