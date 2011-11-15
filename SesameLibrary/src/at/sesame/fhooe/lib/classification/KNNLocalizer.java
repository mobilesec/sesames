package at.sesame.fhooe.lib.classification;

import android.util.Log;
import at.sesame.fhooe.lib.classification.IClassifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class KNNLocalizer 
implements IClassifier 
{
	private IBk mKNN = null;
	private Instances mTraining;
	private String mName = "KNN";
	private boolean mWeighted = false;
	private int mK = -1;
	
	public KNNLocalizer(int _k, boolean _weighted)
	{
		mWeighted = _weighted;
		mK = _k;
		mKNN = new IBk();
		mKNN.setKNN(mK);
		if(mWeighted)
		{
			mKNN.setDistanceWeighting(new SelectedTag(IBk.WEIGHT_INVERSE, IBk.TAGS_WEIGHTING));
		}
	}

	@Override
	public String getName() 
	{
		StringBuffer sb = new StringBuffer();
		if(mWeighted)
		{
			sb.append("W");
		}
		sb.append(mName);
		sb.append("(");
		sb.append(mK);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public void setTrainingData(Instances _inst) 
	{
		if(null==_inst)
		{
			Log.e("KNN", "passed instances were null");
		}
		try 
		{
			if(null==mKNN)
			{
				Log.e("KNN", "KNN was null");
			}
			mKNN.buildClassifier(_inst);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

	@Override
	public String classify(Instance _inst) 
	{
		try 
		{
			double res = mKNN.classifyInstance(_inst);
			return mTraining.classAttribute().value((int) res);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}

}
