package at.sesame.fhooe.localizationservice.xml.meta.model;

import java.net.URL;
import java.util.ArrayList;


public class MetaFileInformation 
{
	

	private String mName;
	private String mEmail;
	
	private Building mBuilding;
	
	private ArrayList<Level> mLevels;
	
	public MetaFileInformation(String _name, String _eMail)
	{
		mName = _name;
		mEmail = _eMail;
	}

	public Building getBuilding() {
		return mBuilding;
	}

	public void setBuilding(Building _building) {
		this.mBuilding = _building;
	}

	public ArrayList<Level> getLevels() {
		return mLevels;
	}

	public void setLevels(ArrayList<Level> _levels) {
		this.mLevels = _levels;
	}

	public String getName() {
		return mName;
	}

	public String getEmail() {
		return mEmail;
	}
	
	public URL getSuitableLocalizationMechanismMetaURL()
	{
		if(mBuilding.getLocalizationMechanisms().size()>0)
		{
			return mBuilding.getLocalizationMechanisms().get(0).getURL();
		}
		else
		{
			for(Level l:mLevels)
			{
				if(l.getLocalizationMechanisms().size()>0)
				{
					return l.getLocalizationMechanisms().get(0).getURL();
				}
			}
		}
		return null;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MetaFileInformation [mName=");
		builder.append(mName);
		builder.append(", mEmail=");
		builder.append(mEmail);
		builder.append(", mBuilding=");
		builder.append(mBuilding);
		builder.append(", mLevels=");
		builder.append(mLevels);
		builder.append("]");
		return builder.toString();
	}
	
}
