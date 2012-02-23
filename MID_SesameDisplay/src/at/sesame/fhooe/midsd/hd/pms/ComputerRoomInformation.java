package at.sesame.fhooe.midsd.hd.pms;

public class ComputerRoomInformation 
{
	private String mRoomName;
	private int mNumIdleComputers;
	private int mNumActiveComputers;
	
	public ComputerRoomInformation(String _name, int _numIdleComputers,int _numActiveComputers) 
	{
		this.mRoomName = _name;
		this.mNumIdleComputers = _numIdleComputers;
		this.mNumActiveComputers = _numActiveComputers;
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
