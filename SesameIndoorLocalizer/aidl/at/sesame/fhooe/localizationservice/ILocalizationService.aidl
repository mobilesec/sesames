package at.sesame.fhooe.localizationservice;

import at.sesame.fhooe.localizationservice.ILocationReceiver;

interface ILocalizationService
{
	/**
	* sets the path to the set of used training data
	*/
	void setTrainingSetPath(String _path);
	
	/**
	* sets the parameters for linear calibration
	* @param _a the intercept for calibration
	* @param _b the slope for calibration
	*/
	void setLinearCalibrationParameters(double _a, double _b);
	
	/**
	* triggers a location update
	* @return a string representation of the current location
	*/
	void triggerLocationUpdate();
	
	/**
	* returns a list of all available classifiers
	* @return a list of all available classifiers
	*/
	List<String> getAvailableClassifiers();
	
	/**
	* sets a the current classifier for the classificator
	* @param _classifierName the name of the classifier to set
	*/
	void setClassifier(String _classifierName);
	
	void registerLocationReceiver(ILocationReceiver _recv);
	
	void unregisterLocationReceiver(ILocationReceiver _recv);
	
}