package at.sesame.fhooe.lib.classification;

import weka.core.Instance;
import weka.core.Instances;

public interface IClassifier 
{
	
	/**
	 * returns a human readable name of the localization algorithm.
	 * @return a human readable name of the localization algorithm.
	 */
	public String getName();
	
	/**
	 * sets the training data for the localization algorithm.
	 * @param _inst the instances to set as training data
	 */
	public void setTrainingData(Instances _inst);
	
	/**
	 * classifies the passed instance and returns the location
	 * @param _inst the instance to classify
	 * @return the result of the classification
	 */
	public String classify(Instance _inst);
	
}
