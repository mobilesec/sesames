package at.sesame.fhooe.lib2.data.simulation;

import java.util.ArrayList;
import java.util.Random;

import at.sesame.fhooe.lib2.data.INotificationListener;
import at.sesame.fhooe.lib2.data.INotificationProvider;
import at.sesame.fhooe.lib2.data.SesameNotification;
import at.sesame.fhooe.lib2.data.SesameNotification.NotificationType;
import at.sesame.fhooe.lib2.util.DateHelper;

public class EventSimulator
extends Thread
implements INotificationProvider
{
	private ArrayList<INotificationListener> mRecvs = new ArrayList<INotificationListener>();
	private boolean mRunning = true;
	private long mTimeout = 5000;
	private Random mRandom = new Random();
	
	private final String[] mRoomNumbers = new String[]{"1","3","6"};
	private final String mNotificationPart1 = "Computer 'EDV1-CLIENT-02' in Raum EDV";
	private final String mNotificationPart2 = " seit ";
	private final String mNotificationPart3 = " Minuten inaktiv";
	
	private static final double MIN_NOTIFICATION_IDLE_TIME = 3600000;
	private static final double MAX_NOTIFICATION_IDLE_TIME = 18000000;
	public String getNotification()
	{
		int roomIdx = mRandom.nextInt(mRoomNumbers.length);
		double idleTime = MIN_NOTIFICATION_IDLE_TIME+mRandom.nextDouble()*(MAX_NOTIFICATION_IDLE_TIME-MIN_NOTIFICATION_IDLE_TIME);
		
		StringBuilder res = new StringBuilder();
		res.append(mNotificationPart1);
		res.append(mRoomNumbers[roomIdx]);
		res.append(mNotificationPart2);
		res.append(DateHelper.convertMStoReadableString(idleTime, false));
		res.append(mNotificationPart3);
		
		return res.toString();
	}

	@Override
	public void registerNotificationreceiver(INotificationListener _recv) {
		mRecvs.add(_recv);
		
	}

	@Override
	public void unregisterNotificationreceiver(INotificationListener _recv) {
		mRecvs.remove(_recv);
		
	}

	@Override
	public void updateNotificationReceivers(ArrayList<SesameNotification> _notifications) 
	{
		for(INotificationListener recv:mRecvs)
		{
			recv.notifyAboutNotification(_notifications);
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
		ArrayList<SesameNotification> notifications = new ArrayList<SesameNotification>();
		notifications.add(new SesameNotification(NotificationType.Type40, "00:00:00:00:00"));
		while(mRunning)
		{
			updateNotificationReceivers(notifications);
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
