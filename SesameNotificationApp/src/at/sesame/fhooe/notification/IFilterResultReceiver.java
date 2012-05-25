package at.sesame.fhooe.notification;

import java.util.Date;

public interface IFilterResultReceiver 
{
	public void notifyFilterSet(Date _from, Date _to);
}
