package at.sesame.fhooe.localizationservice.xml.lm.model;

import java.util.ArrayList;

public class DbInfo 
{
	private int mNumInstances;
	private ArrayList<AccessPointInformation> mAPs;
	private Anchor mAnchor;
	private FileDescriptor mDBFile;
	
	public int getmNumberOfInstances() {
		return mNumInstances;
	}
	public void setNumberOfInstances(int _numInstances) {
		this.mNumInstances = _numInstances;
	}
	public ArrayList<AccessPointInformation> getAccessPointInfos() {
		return mAPs;
	}
	public void setAccessPointInfos(ArrayList<AccessPointInformation> _APs) {
		this.mAPs = _APs;
	}
	public void addAccessPointInfo(AccessPointInformation _ap)
	{
		mAPs.add(_ap);
	}
	
	public Anchor getAnchor() {
		return mAnchor;
	}
	public void setAnchor(Anchor _anchor) {
		this.mAnchor = _anchor;
	}
	public FileDescriptor getDatabaseFile() {
		return mDBFile;
	}
	public void setDatabaseFile(FileDescriptor _DBFile) {
		this.mDBFile = _DBFile;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DbInfo [mNumInstances=");
		builder.append(mNumInstances);
		builder.append(", mAPs=");
		builder.append(mAPs);
		builder.append(", mAnchor=");
		builder.append(mAnchor);
		builder.append(", mDBFile=");
		builder.append(mDBFile);
		builder.append("]");
		return builder.toString();
	}
}
