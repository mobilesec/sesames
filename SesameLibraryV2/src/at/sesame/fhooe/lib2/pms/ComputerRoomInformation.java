package at.sesame.fhooe.lib2.pms;

import java.io.Serializable;

import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;

public class ComputerRoomInformation
implements Serializable
{
	private static final long serialVersionUID = -1805971564346397759L;
	private String mRoomName;
	private SesameMeasurementPlace mMeasurementPlace;
	private int mNumIdleComputers;
	private int mNumActiveComputers;
	private int mNumNotifications;
	private boolean mDirty;
	
	public ComputerRoomInformation(String _roomName, SesameMeasurementPlace _mp, int _numIdleComputers,int _numActiveComputers, boolean _dirty) 
	{
		this.mRoomName = _roomName;
		this.mMeasurementPlace = _mp;
		this.mNumIdleComputers = _numIdleComputers;
		this.mNumActiveComputers = _numActiveComputers;
		this.mDirty = _dirty;
	}

	public int getNumIdleComputers() {
		return mNumIdleComputers;
	}

	public void setNumIdleComputers(int _numIdleComputers) {
		this.mNumIdleComputers = _numIdleComputers;
	}

	public int getNumActiveComputers() 
	{
		return mNumActiveComputers;
	}

	public void setNumActiveComputers(int _numActiveComputers) {
		this.mNumActiveComputers = _numActiveComputers;
	}
	
//	public String getRoomName() {
//		return mRoomName;
//	}
	
	public int getNumNotifications() {
		return mNumNotifications;
	}

	public void setNumNotifications(int _numNotifications) {
		this.mNumNotifications = _numNotifications;
	}
	
	public boolean isDirty()
	{
		return mDirty;
	}
	
	public void setDirty(boolean _dirty)
	{
		mDirty = _dirty;
	}
	
	public String getRoomName()
	{
		return mRoomName;
	}

	public SesameMeasurementPlace getMeasurementPlace() {
		return mMeasurementPlace;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ComputerRoomInformation [mName=");
		builder.append(mMeasurementPlace);
		builder.append(", mNumIdleComputers=");
		builder.append(mNumIdleComputers);
		builder.append(", mNumActiveComputers=");
		builder.append(mNumActiveComputers);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	

}
