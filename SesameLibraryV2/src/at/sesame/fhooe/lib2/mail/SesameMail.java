package at.sesame.fhooe.lib2.mail;

import java.util.Arrays;

import android.util.Log;
import at.sesame.fhooe.lib2.config.SesameConfigData;
import at.sesame.fhooe.lib2.logging.SesameLogger;
import at.sesame.fhooe.lib2.logging.SesameLogger.EntryType;

public class SesameMail 
extends Mail
{
	private static final String TAG = "SesameMail";
	private static final String SESAME_MAIL_ADDRESS = "sesame.s123@gmail.com";
	private static final String SESAME_MAIL_PASSWORD = "open_sesame";
	private static final String SESAME_MAIL_SUBJECT = "Automatic Sesame Notification";
	private static final String PMS_FAIL_MESSAGE = "PMS updates failed 5 or more times";
	private static final String REPO_FAIL_MESSAGE = "PMS updates failed 5 or more times";
	private static final String REPO_OLD_MESSAGE = "Last meter value received more than three hours ago";
	private static final String HEARTBEAT_MESAGE = "knock, knock, everything fine...";
	
	public enum NotificationType
	{
		PMS_FAILED,
		REPO_FAILED,
		REPO_OLD,
		HEARTBEAT
	}
	
	public SesameMail()
	{
		super(SESAME_MAIL_ADDRESS, SESAME_MAIL_PASSWORD);
		setFrom(SESAME_MAIL_ADDRESS);
		setSubject(SESAME_MAIL_SUBJECT);
	}
	
	public boolean send(SesameConfigData _config, NotificationType _type)
	{
		String body = "";
		switch(_type)
		{
		case PMS_FAILED:
			body = PMS_FAIL_MESSAGE;
			break;
		case REPO_FAILED:
			body = REPO_FAIL_MESSAGE;
			break;
		case REPO_OLD:
			body = REPO_OLD_MESSAGE;
			break;
		case HEARTBEAT:
			body = HEARTBEAT_MESAGE;
			break;
		default:
			return false;
		}
		return send(_config, body);
	}
	
	public boolean send(SesameConfigData _config, String _body)
	{
//		Log.e(TAG, "sending to:"+Arrays.toString(_config.getAddressArray()));
		SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "sending mail to: "+Arrays.toString(_config.getAddressArray()));
		setTo(_config.getAddressArray());
		setBody(_config.getUser()+"\n\n"+_body);
		try
		{
			return super.send();
		} 
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	

}
