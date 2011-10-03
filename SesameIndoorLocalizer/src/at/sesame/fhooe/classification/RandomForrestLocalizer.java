package at.sesame.fhooe.classification;

import weka.classifiers.trees.RandomTree;
import weka.core.Instance;
import weka.core.Instances;

public class RandomForrestLocalizer 
implements IClassifier 
{
	private RandomTree mRT = new RandomTree();
	private Instances mTraining;
	
	@Override
	public String getName() 
	{
		return "RandomTree";
	}

	@Override
	public void setTrainingData(Instances _inst) 
	{
		try 
		{
			mRT.buildClassifier(_inst);
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
			double res = mRT.classifyInstance(_inst);
			return mTraining.classAttribute().value((int) res);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}

}
