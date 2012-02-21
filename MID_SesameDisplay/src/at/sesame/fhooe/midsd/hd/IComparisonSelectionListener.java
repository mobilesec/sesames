package at.sesame.fhooe.midsd.hd;

public interface IComparisonSelectionListener 
{
	public enum ComparisonRoom
	{
		edv1,
		edv3,
		edv6
	}
	/**
	 * called by a ComparisonFragment to indicate selection of a room
	 * @param _room the room selected for comparison
	 */
	public void notifyRoomSelection(ComparisonRoom _room);
	
	/**
	 * called by a ComparisonFragment to indicate selection of comparison filters
	 * @param _selection a boolean array of size 4 indicating which of the four
	 * possible filters apply
	 */
	public void notifyComparisonSelection(boolean[] _selection);

}
