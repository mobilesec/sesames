package at.sesame.fhooe.proxy.rest.psm;

public class PSMParam 
{
	public enum TargetState
	{
		shutdown,
		sleep
	}
	
	private String target_state = null;
	private String os = null;
	private String username = null;
	private String password = null;
	
	public PSMParam(TargetState _state)
	{
		target_state = _state.name();
	}
	
	public void setOS(String _os)
	{
		os = _os;
	}
	
	public void setUsernameAndPassword(String _user, String _pwd)
	{
		username = _user;
		password = _pwd;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("target-state:");
		sb.append(target_state);
		sb.append("\n");
		if(null!=os)
		{
			sb.append("os:");
		}
		
		return sb.toString();
	}

}
