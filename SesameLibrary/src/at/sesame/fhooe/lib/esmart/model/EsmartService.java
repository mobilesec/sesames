package at.sesame.fhooe.lib.esmart.model;

public class EsmartService 
{
	private String mName;
	private String mMethodName;
	
	public String getName() {
		return mName;
	}
	public void setName(String _name) {
		this.mName = _name;
	}
	public String getMethodName() {
		return mMethodName;
	}
	public void setMethodName(String _methodName) {
		this.mMethodName = _methodName;
	}
	
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Service [mName=");
		builder.append(mName);
		builder.append(", mMethodName=");
		builder.append(mMethodName);
		builder.append("]");
		return builder.toString();
	}
	
	

}
