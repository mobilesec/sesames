package at.sesame.fhooe.midsd.demo;

import java.util.ArrayList;

import at.sesame.fhooe.midsd.ld.INotificationProvider;
import at.sesame.fhooe.midsd.ld.INotificationReceiver;

public class EventSimulator
extends Thread
implements INotificationProvider
{
	private ArrayList<INotificationReceiver> mRecvs = new ArrayList<INotificationReceiver>();
	private boolean mRunning = true;
	private long mTimeout = 5000;

	@Override
	public void registerNotificationreceiver(INotificationReceiver _recv) {
		mRecvs.add(_recv);
		
	}

	@Override
	public void unregisterNotificationreceiver(INotificationReceiver _recv) {
		mRecvs.remove(_recv);
		
	}

	@Override
	public void update(String _msg) 
	{
		for(INotificationReceiver recv:mRecvs)
		{
			recv.notifyAboutEvent(_msg);
		}
	}
	
	public void startNotifying()
	{
		mRunning = true;
		this.start();
	}
	
	public void stopNotifying()
	{
		mRunning = false;
	}
	
	@Override
	public void run()
	{
		int cnt = 0;
		while(mRunning)
		{
			update("Notification "+cnt);
			cnt++;
			try 
			{
				Thread.sleep(mTimeout);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
