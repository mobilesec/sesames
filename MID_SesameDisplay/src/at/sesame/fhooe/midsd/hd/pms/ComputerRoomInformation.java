package at.sesame.fhooe.midsd.hd.pms;

public class ComputerRoomInformation 
{
	private RoomName mRoomName;
	private String mName;
	private int mNumIdleComputers;
	private int mNumActiveComputers;
	
	public enum RoomName
	{
		EDV_1,
		EDV_3,
		EDV_6
	}
	
	public ComputerRoomInformation(RoomName _name, int _numIdleComputers,int _numActiveComputers) 
	{
		this.mRoomName = _name;
		this.mName = _name.name();
		this.mNumIdleComputers = _numIdleComputers;
		this.mNumActiveComputers = _numActiveComputers;
	}

	public String getName() {
		return mName;
	}

	public void setName(String _name) {
		this.mName = _name;
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
	
	public RoomName getRoomName() {
		return mRoomName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ComputerRoomInformation [mName=");
		builder.append(mName);
		builder.append(", mNumIdleComputers=");
		builder.append(mNumIdleComputers);
		builder.append(", mNumActiveComputers=");
		builder.append(mNumActiveComputers);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	

}
