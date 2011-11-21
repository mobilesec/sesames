package at.sesame.fhooe.localizationservice.xml.lm.model;

import java.util.ArrayList;

public class LocalizationMechanismMetaInformation 
{
	private String mName;
	private ArrayList<FingerPrintDatabaseInformation> mFpDbs = new ArrayList<FingerPrintDatabaseInformation>();
	private FileDescriptor mMapFile;
	
	
	public LocalizationMechanismMetaInformation(String _name)
	{
		mName =_name;
	}
	
	public String getName()
	{
		return mName;
	}
	
	public ArrayList<FingerPrintDatabaseInformation> getFingerprintDatabases() {
		return mFpDbs;
	}
	public void setFingerprintDatabases(ArrayList<FingerPrintDatabaseInformation> _fpDbs) {
		this.mFpDbs = _fpDbs;
	}
	
	public void addFingerPrintDataBase(FingerPrintDatabaseInformation _fpDb)
	{
		mFpDbs.add(_fpDb);
	}
	public FileDescriptor getMapFile() {
		return mMapFile;
	}
	public void setMapFile(FileDescriptor _mapFile) {
		this.mMapFile = _mapFile;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocalizationMechanismMetaInformation [mFpDbs=");
		builder.append(mFpDbs);
		builder.append(", mMapFile=");
		builder.append(mMapFile);
		builder.append("]");
		return builder.toString();
	}
}
