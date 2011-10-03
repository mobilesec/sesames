package at.sesame.fhooe.classification;

import android.util.Log;
import weka.classifiers.lazy.KStar;
import weka.core.Instance;
import weka.core.Instances;

public class KStarLocalizer 
implements IClassifier 
{
	private KStar mKStar = new KStar();
	private Instances mTraining = null;
	
	private static final int DEFAULT_B = 20;
	private int mB;
	
	public KStarLocalizer()
	{
		this(DEFAULT_B);
	}
	
	public KStarLocalizer(int _b)
	{
		mB = _b;
		mKStar.setGlobalBlend(_b);
	}
	
	@Override
	public String getName() 
	{
		return "K* (b="+mB+")";
	}

	@Override
	public void setTrainingData(Instances _inst) 
	{
		mTraining = _inst;
		try 
		{
			mKStar.buildClassifier(mTraining);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public String classify(Instance _inst) {
		try 
		{
			
			double res = mKStar.classifyInstance(_inst);
			String location = mTraining.classAttribute().value((int) res);
			return location;
		} 
		catch (Exception e) 
		{
			Log.e(getName(), "exception during classification", e);
			e.printStackTrace();
		}
		Log.e(getName(), "classification failed, returning null");
		return null;
	}
}
