package at.sesame.fhooe.lib2.pms;

public class ComputerRoomInformation 
{
	private String mRoomName;
	private int mNumIdleComputers;
	private int mNumActiveComputers;
	private int mNumNotifications;
	private boolean mDirty;
	
	public ComputerRoomInformation(String _name, int _numIdleComputers,int _numActiveComputers, boolean _dirty) 
	{
		this.mRoomName = _name;
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
	
	public String getRoomName() {
		return mRoomName;
	}
	
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ComputerRoomInformation [mName=");
		builder.append(mRoomName);
		builder.append(", mNumIdleComputers=");
		builder.append(mNumIdleComputers);
		builder.append(", mNumActiveComputers=");
		builder.append(mNumActiveComputers);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	

}
