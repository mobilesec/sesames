package at.sesame.fhooe.classification;

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
	 * finds optimal parameters for a specific combination of trainingset and testset
	 * @param _trainingSet the trainingset to optimize for
	 * @param _trainingSetName the name of the trainingset
	 * @param _test the testset to optimize for
	 * @param _testSetName the name of the testset
	 * @return a description of the best result and the set of optimal parameters
	 */
	public String findOptimalParameters(Instances _trainingSet, Instances _test);
	
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
	public double[] classify(Instance _inst);
	
}
