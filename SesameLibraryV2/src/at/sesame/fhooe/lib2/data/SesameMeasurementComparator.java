package at.sesame.fhooe.lib2.data;

import java.util.Comparator;
import java.util.Date;

public class SesameMeasurementComparator
implements Comparator<SesameMeasurement>
{

	@Override
	public int compare(SesameMeasurement arg0, SesameMeasurement arg1) {
		Date first = arg0.getTimeStamp();
		Date second = arg1.getTimeStamp();
		if(first.equals(second))
		{
			return 0;
		}
		else if(first.before(second))
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}


}
