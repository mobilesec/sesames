package at.sesame.fhooe.localizationservice.xml.meta.model;

import java.util.ArrayList;

public class LocalizationMechanismContainer 
{
	private ArrayList<LocalizationMechanism> mLMs = new ArrayList<LocalizationMechanism>();
	
	public ArrayList<LocalizationMechanism> getLocalizationMechanisms() 
	{
		return mLMs;
	}
	
	public void setLocalizationMechanisms(ArrayList<LocalizationMechanism> _lms) 
	{
		this.mLMs = _lms;
	}
	
	public void addLocalizationMechanism(LocalizationMechanism _lm)
	{
		mLMs.add(_lm);
	}

}
