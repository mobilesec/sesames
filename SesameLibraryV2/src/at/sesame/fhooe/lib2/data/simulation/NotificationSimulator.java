package at.sesame.fhooe.lib2.data.simulation;

import java.util.ArrayList;
import java.util.Random;

import at.sesame.fhooe.lib2.data.INotificationSource;
import at.sesame.fhooe.lib2.data.SesameNotification;
import at.sesame.fhooe.lib2.util.DateHelper;

public class NotificationSimulator
implements INotificationSource
{

	private Random mRandom = new Random();
	
//	private final String[] mRoomNumbers = new String[]{"1","3","6"};
	private final String mNotificationPart1 = "Computer 'EDV1-Client-";
	private final String mNotificationPart2 = "' in Raum EDV1 seit ";
	private final String mNotificationPart3 = " inaktiv";
	
	private static final double MIN_NOTIFICATION_IDLE_TIME = 3600000;
	private static final double MAX_NOTIFICATION_IDLE_TIME = 18000000;
	
	@Override
	public ArrayList<SesameNotification> getNotifications()
	{
		double idleTime = MIN_NOTIFICATION_IDLE_TIME+mRandom.nextDouble()*(MAX_NOTIFICATION_IDLE_TIME-MIN_NOTIFICATION_IDLE_TIME);
		
		StringBuilder res = new StringBuilder();
		res.append(mNotificationPart1);
		res.append(mRandom.nextInt(100));
		res.append(mNotificationPart2);
		res.append(DateHelper.convertMStoShortReadableString(idleTime));
		res.append(mNotificationPart3);
		
		return new ArrayList<SesameNotification>();
	}


	
//	public void startNotifying()
//	{
//		mRunning = true;
//		this.start();
//	}
//	
//	public void stopNotifying()
//	{
//		mRunning = false;
//	}
	
//	@Override
//	public void run()
//	{
//		int cnt = 0;
//		while(mRunning)
//		{
//			updateNotificationReceivers("Notification "+cnt);
//			cnt++;
//			try 
//			{
//				Thread.sleep(mTimeout);
//			}
//			catch (InterruptedException e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}

}
