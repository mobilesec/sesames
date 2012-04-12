package at.sesame.fhooe.lib2.data;

import java.util.Comparator;

public class SesameMeasurementPlaceComparator 
implements Comparator<SesameMeasurementPlace>
{

	@Override
	public int compare(SesameMeasurementPlace lhs, SesameMeasurementPlace rhs) {
		// TODO Auto-generated method stub
		return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
	}

}
