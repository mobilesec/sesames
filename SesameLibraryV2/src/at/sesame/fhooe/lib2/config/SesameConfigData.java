package at.sesame.fhooe.lib2.config;

import java.util.ArrayList;

public class SesameConfigData 
{
	private String mUser = "";
	private String mPass = "";
	
	private ArrayList<String> mMailAddresses = new ArrayList<String>();

	public SesameConfigData(String mUser, String mPass,
			ArrayList<String> mMailAddresses) {
		super();
		this.mUser = mUser;
		this.mPass = mPass;
		this.mMailAddresses = mMailAddresses;
	}

	public String getUser() {
		return mUser;
	}

	public String getPass() {
		return mPass;
	}

	public ArrayList<String> getMailAddresses() {
		return mMailAddresses;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SesameConfigData [mUser=");
		builder.append(mUser);
		builder.append(", mPass=");
		builder.append(mPass);
		builder.append(", mMailAddresses=");
		builder.append(mMailAddresses);
		builder.append("]");
		return builder.toString();
	}
	
	

}
