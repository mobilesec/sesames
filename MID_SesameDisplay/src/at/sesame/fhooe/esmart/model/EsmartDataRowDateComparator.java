package at.sesame.fhooe.esmart.model;

import java.util.Comparator;
import java.util.Date;

public class EsmartDataRowDateComparator implements Comparator<EsmartDataRow> {

	@Override
	public int compare(EsmartDataRow arg0, EsmartDataRow arg1) 
	{
		Date first = arg0.getDate();
		Date second = arg1.getDate();
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
