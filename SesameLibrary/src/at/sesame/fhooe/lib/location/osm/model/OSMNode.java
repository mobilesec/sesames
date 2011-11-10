package at.sesame.fhooe.lib.location.osm.model;

import java.util.HashMap;

public class OSMNode 
{
	private long mId;
	private double mLat;
	private double mLon;
	private int mVersion;
	private long mChangeSet;
	private String mUser;
	private long mUid;
	private boolean mVisible;
	private String mTimeStamp;
	
	private HashMap<String, String> mTags;

	public OSMNode(long _id, double _lat, double _lon, int _version,
			long _changeSet, String _user, long _uid, boolean _visible, String _timeStamp) 
	{
		super();
		this.mId = _id;
		this.mLat = _lat;
		this.mLon = _lon;
		this.mVersion = _version;
		this.mChangeSet = _changeSet;
		this.mUser = _user;
		this.mUid = _uid;
		this.mVisible = _visible;
		this.mTimeStamp = _timeStamp;
		mTags = new HashMap<String, String>();

	}
	
	public void addTag(String _key, String _value)
	{
		mTags.put(_key, _value);
	}

	public long getId() {
		return mId;
	}

	public double getLatitude() {
		return mLat;
	}

	public double getLongitude() {
		return mLon;
	}

	public int getVersion() {
		return mVersion;
	}

	public long getChangeSet() {
		return mChangeSet;
	}

	public String getUser() {
		return mUser;
	}

	public long getUid() {
		return mUid;
	}

	public boolean isVisible() {
		return mVisible;
	}

	public String getTimeStamp() {
		return mTimeStamp;
	}

	public String getValueForTag(String _key) 
	{
		return mTags.get(_key);
	}
	
	

}
