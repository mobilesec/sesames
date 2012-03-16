package at.sesame.fhooe.tablet;

public interface IComparisonSelectionListener 
{

	/**
	 * called by a ComparisonFragment to indicate selection of a room
	 * @param _room name of the room selected for comparison
	 */
	public void notifyRoomSelection(String _room);
	
	/**
	 * called by a ComparisonFragment to indicate selection of comparison filters
	 * @param _selection a boolean array of size 4 indicating which of the four
	 * possible filters apply
	 */
	public void notifyComparisonSelection(boolean[] _selection);

}
