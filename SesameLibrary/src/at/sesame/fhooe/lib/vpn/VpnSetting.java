/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.vpn;

/**
 * this class represents a container for certificate and PSK based
 * VPN connection settings stored in an XML file
 * @author Peter Riedl
 *
 */
public class VpnSetting 
{
	/**
	 * all possible types of VPN settings
	 */
	public enum Type{
		CRT,
		PSK,
		CRT_PSK,
		nA
	}
	
	/**
	 * the actual type of this VPN setting
	 */
	private Type mType;
	
	/**
	 * the id of the VPN connection
	 */
	private String mId;
	
	/**
	 * the name of the VPN connection
	 */
	private String mName;
	
	/**
	 * the username of the VPN connection
	 */
	private String mUser;
	
	/**
	 * the password of the VPN connection
	 */
	private String mUserPass;
	
	/**
	 * the server for the VPN connection
	 */
	private String mServer;
	
	/**
	 * the pre-shared key of the VPN connection
	 */
	private String mPSK;
	
	/**
	 * the name of the CA certificate of the VPN connection
	 */
	private String mCaCertName;
	
	/**
	 * the name of the user certificate of the VPN connection
	 */
	private String mUserCertName;
	
	/**
	 * creates a new, empty VPN setting
	 */
	public VpnSetting()
	{	
	}
	
	/**
	 * creates a new VpnSetting with all available information
	 * @param _type the type of the connection
	 * @param _id the id of the connection
	 * @param _name the name of the connection
	 * @param _user the username for the connection
	 * @param _userPass the password for the connection
	 * @param _server the server for the connection
	 * @param _PSK the pre-shared key for the connection
	 * @param _caCertName the name of the CA certificate
	 * @param _userCertName the name of the user certificate
	 */
	public VpnSetting(Type _type, String _id, String _name, String _user,
			String _userPass, String _server, String _PSK, String _caCertName,
			String _userCertName) 
	{
		this.mType = _type;
		this.mId = _id;
		this.mName = _name;
		this.mUser = _user;
		this.mUserPass = _userPass;
		this.mServer = _server;
		this.mPSK = _PSK;
		this.mCaCertName = _caCertName;
		this.mUserCertName = _userCertName;
	}

	public Type getType() {
		return mType;
	}

	public void setType(Type _type) {
		this.mType = _type;
	}

	public String getId() {
		return mId;
	}

	public void setId(String _id) {
		this.mId = _id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String _name) {
		this.mName = _name;
	}

	public String getUser() {
		return mUser;
	}

	public void setUser(String _user) {
		this.mUser = _user;
	}

	public String getUserPass() {
		return mUserPass;
	}

	public void setUserPass(String _userPass) {
		this.mUserPass = _userPass;
	}

	public String getServer() {
		return mServer;
	}

	public void setServer(String mServer) {
		this.mServer = mServer;
	}

	public String getPSK() {
		return mPSK;
	}

	public void setPSK(String _psk) {
		this.mPSK = _psk;
	}

	public String getCaCertName() {
		return mCaCertName;
	}

	public void setCaCertName(String _caCertName) {
		this.mCaCertName = _caCertName;
	}

	public String getUserCertName() {
		return mUserCertName;
	}

	public void setUserCertName(String _userCertName) {
		this.mUserCertName = _userCertName;
	}
	
	/**
	 * returns whether all necessary fields for the specified type are
	 * not null and not empty
	 * @return true if the VpnSetting is valid, false otherwise
	 */
	public boolean isValid()
	{
		if(getType() == Type.nA)
		{
			return false;
		}
		
		if(	null==getId() || ""==getId()||
			null==getName() || ""==getName()||
			null==getUser() || ""==getUser()||
			null==getUserPass() || ""==getUserPass()||
			null==getServer() || ""==getServer())
		{
			return false;
		}
		
		if(mType == Type.CRT)
		{
			if( null==getCaCertName() || ""==getCaCertName()||
				null==getUserCertName() || ""==getUserCertName())
			{
				return false;
			}
		}
		else if(mType == Type.PSK)
		{
			if(null==getPSK()||""==getPSK())
			{
				return false;
			}
		}
		else if(mType == Type.CRT_PSK)
		{
			if( null==getCaCertName() || ""==getCaCertName()||
				null==getUserCertName() || ""==getUserCertName()||
				null==getPSK()||""==getPSK())
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "VpnSetting [valid="+isValid()+", mType=" + mType + ", mId=" + mId + ", mName="
				+ mName + ", mUser=" + mUser + ", mUserPass=" + mUserPass
				+ ", mServer=" + mServer + ", mPSK=" + mPSK + ", mCaCertName="
				+ mCaCertName + ", mUserCertName=" + mUserCertName + "]";
	}
	
	
}
