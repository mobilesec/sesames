package at.sesame.fhooe.midsd.demo;

import java.util.Random;

import at.sesame.fhooe.lib.util.DateHelper;
import at.sesame.fhooe.midsd.data.INotificationSource;

public class NotificationSimulator
implements INotificationSource
{

	private Random mRandom = new Random();
	
	private final String[] mRoomNumbers = new String[]{"1","3","6"};
	private final String mNotificationPart1 = "Computer in Raum EDV";
	private final String mNotificationPart2 = " seit ";
	private final String mNotificationPart3 = " Minuten inaktiv";
	
	private static final double MIN_NOTIFICATION_IDLE_TIME = 3600000;
	private static final double MAX_NOTIFICATION_IDLE_TIME = 18000000;
	
	@Override
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
