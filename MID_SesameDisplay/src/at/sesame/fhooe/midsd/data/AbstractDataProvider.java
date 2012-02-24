package at.sesame.fhooe.midsd.data;

import java.util.ArrayList;

public class AbstractDataProvider 
{
	protected ArrayList<ISesameDataListener> mListener = new ArrayList<ISesameDataListener>();
	
	protected void registerListener(ISesameDataListener _listener)
	{
		mListener.add(_listener);
	}
	
	protected void unregisterListener(ISesameDataListener _listener)
	{
		mListener.remove(_listener);
	}

}
