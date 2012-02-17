package at.sesame.fhooe.midsd.hd;

public class ComputerRoomInformation 
{
	
	private String mName;
	private int mNumIdleComputers;
	private int mNumActiveComputers;
	
	public ComputerRoomInformation(String _name, int _numIdleComputers,int _numActiveComputers) 
	{
		this.mName = _name;
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
