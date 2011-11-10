package at.sesame.fhooe.lib.location.osm.model;

public class IndoorLocOSMNode 
extends OSMNode 
{
	public static final String INDOOR_LOCATION_URL_KEY = "indoor_loc_url";
	
	public IndoorLocOSMNode(long _id, double _lat, double _lon, int _version,
			long _changeSet, String _user, long _uid, boolean _visible, String _timeStamp) 
	{
		super(_id, _lat, _lon, _version, _changeSet, _user, _uid, _visible, _timeStamp);
	}
	
	public IndoorLocOSMNode(OSMNode _node)
	{
		super(	_node.getId(), 
				_node.getLatitude(), 
				_node.getLongitude(), 
				_node.getVersion(), 
				_node.getChangeSet(), 
				_node.getUser(), 
				_node.getUid(), 
				_node.isVisible(),
				_node.getTimeStamp());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IndoorLocOSMNode [getId()=");
		builder.append(getId());
		builder.append(", getLatitude()=");
		builder.append(getLatitude());
		builder.append(", getLongitude()=");
		builder.append(getLongitude());
		builder.append(", getVersion()=");
		builder.append(getVersion());
		builder.append(", getChangeSet()=");
		builder.append(getChangeSet());
		builder.append(", getUser()=");
		builder.append(getUser());
		builder.append(", getUid()=");
		builder.append(getUid());
		builder.append(", isVisible()=");
		builder.append(isVisible());
		builder.append(", URL=");
		builder.append(getValueForTag(INDOOR_LOCATION_URL_KEY));
		builder.append("]");
		return builder.toString();
	}	
}
