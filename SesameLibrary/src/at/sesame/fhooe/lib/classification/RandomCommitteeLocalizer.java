package at.sesame.fhooe.lib.classification;

import at.sesame.fhooe.lib.classification.IClassifier;
import weka.classifiers.meta.RandomCommittee;
import weka.core.Instance;
import weka.core.Instances;

public class RandomCommitteeLocalizer 
implements IClassifier 
{
	private RandomCommittee mRC = new RandomCommittee();
	private Instances mTraining;
	
	@Override
	public String getName() 
	{
		return "Random Committee";
	}

	@Override
	public void setTrainingData(Instances _inst) 
	{
		try 
		{
			mRC.buildClassifier(_inst);
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
			double res = mRC.classifyInstance(_inst);
			return mTraining.classAttribute().value((int) res);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}

}
