package at.sesame.fhooe.lib2.pms.dialogs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import at.sesame.fhooe.lib2.logging.SesameLogger;
import at.sesame.fhooe.lib2.logging.SesameLogger.EntryType;
import at.sesame.fhooe.lib2.pms.dialogs.PMSActionDialogFragment.PMSActionDialogType;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;

public class PMSDialogFactory 
{
	@SuppressWarnings("unused")
	private static final String TAG = "PMSDialogFactory";

	private static DialogFragment mShownDialog = null;
//	private static FragmentManager mFragMan;
	public enum DialogType
	{
		NETWORKING_IN_PROGRESS,
		ACTION_IN_PROGRESS,
		ACTIVE_DEVICE_ACTION_DIALOG,
		INACTIVE_DEVICE_ACTION_DIALOG,
		NO_NETWORK_DIALOG,
		CANT_SHUTDONW_DIALOG,
		CANT_WAKEUP_DIALOG,
		ACTION_IN_PROGRESS_DIALOG
	}


	public static DialogFragment showDialog(DialogType _dt, final FragmentManager _fm, IPMSDialogActionHandler _handler, Object... _params)
	{
//		mFragMan = _fm;
		if(null!=mShownDialog&&mShownDialog.isVisible())
		{
			mShownDialog.dismiss();
		}
		DialogFragment dialog2Show = null;

		switch (_dt) 
		{
		case NETWORKING_IN_PROGRESS:
			//			Log.e(TAG, "--------------------------");
			//			Log.e(TAG, _params[0].getClass().getCanonicalName());
			//			Log.e(TAG, "--------------------------");
			if(	!checkParams(_params, new Class[]{Context.class}))
			{
				throw new IllegalArgumentException("passed parameter for networking in progress dialog was not ok");
			}
			//			Looper.prepare();
			dialog2Show = new PMSNetworkingInProgressDialogFragment();
			//			Looper.loop();
			break;
		case ACTIVE_DEVICE_ACTION_DIALOG:
			if(!checkParams(_params, new Class[]{Context.class, ControllableDevice.class}))
			{
				throw new IllegalArgumentException("passed parameter for active device action dialog was not ok");
			}
			dialog2Show = new PMSActionDialogFragment((Context)_params[0], _handler, (ControllableDevice)_params[1], PMSActionDialogType.ActiveDeviceActionDialog);
			break;
		case INACTIVE_DEVICE_ACTION_DIALOG:
			if(!checkParams(_params, new Class[]{Context.class, ControllableDevice.class}))
			{
				throw new IllegalArgumentException("passed parameter for inactive device action dialog was not ok");
			}
			dialog2Show = new PMSActionDialogFragment((Context)_params[0], _handler, (ControllableDevice)_params[1], PMSActionDialogType.InactiveDeviceActionDialog);
			break;
		case NO_NETWORK_DIALOG:
			dialog2Show = new PMSNoNetworkDialogFragment();
			break;

		case CANT_SHUTDONW_DIALOG:
			if(!checkParams(_params, new Class[]{String.class}))
			{
				throw new IllegalArgumentException("passed parameters for cant shutdown dialog were not ok");
			}
			dialog2Show = new PMSCantShutdownDialogFragment((String)_params[0]);
			break;
		case CANT_WAKEUP_DIALOG:
			if(!checkParams(_params, new Class[]{String.class}))
			{
				throw new IllegalArgumentException("passed parameters for cant wakeup dialog were not ok");
			}
			dialog2Show = new PMSCantWakeUpDialogFragment((String)_params[0]);
			break;

		case ACTION_IN_PROGRESS_DIALOG:
			if(!checkParams(_params, new Class[]{Context.class, String.class, Integer.class}))
			{
				throw new IllegalArgumentException("passed parameters for action in progress dialog were not ok");
			}
			//			Bundle args = new Bundle();
			//			args.putString(PMSActionInProgressDialogFragment.TITLE_BUNDLE_KEY, (String)_params[0]);
			//			args.putInt(PMSActionInProgressDialogFragment.MAX_BUNDLE_KEY, (Integer)_params[1]);

			dialog2Show = new PMSActionInProgressDialogFragment((Context)_params[0], (String)_params[1], (Integer)_params[2]);
			break;

		default:
			break;
		}
		
		mShownDialog = dialog2Show;


		if(null!=mShownDialog&& null!=_fm)
		{
			try
			{
				mShownDialog.show(_fm, null);
				Log.i(TAG, "showing dialog:"+mShownDialog.toString());				
			}
			catch(Exception e)
			{
				Log.e(TAG, "could not show dialog");
				SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "failed to show dialog");
			}

		}
		else
		{
			Log.e(TAG, "could not show dialog");
		}
		//		Log.i(TAG, "returning:"+mShownDialog.toString());
		return dialog2Show;
	}

	@SuppressWarnings("rawtypes")
	private static boolean checkParams(Object[] _params, Class[] _expectedTypes)
	{
		if(	null==_params || null==_expectedTypes ||
				_params.length !=_expectedTypes.length)
		{
			return false;
		}
//		System.out.println("size of params:"+_params.length);
//		System.out.println("size of types:"+_expectedTypes.length);
//		System.out.println("params null:"+(null==_params));
//		System.out.println("types null:"+(null==_expectedTypes));
		//		System.out.println("size of params:"+_params.length);
		for(int i = 0;i<_params.length; i++)
		{
			try
			{
				Class expectedType = _expectedTypes[i];
				Class param = _params[i].getClass();
//				System.out.println("checking:"+expectedType.getSimpleName()+" and "+param.getSimpleName());
				if(!expectedType.isAssignableFrom(param))
				{
					return false;
				}
			}
			catch(NullPointerException _npe)
			{
				return false;
			}


		}
		return true;
	}

	public static void dismissCurrentDialog()
	{
		if(null!=mShownDialog&&!mShownDialog.isVisible())
		{
			try
			{
				mShownDialog.dismiss();	
			}
			catch(NullPointerException _npe)
			{

			}
			//			FragmentTransaction ft = mFragMan.beginTransaction();
			//			ft.remove(mShownDialog);
			//			ft.commit();
		}
	}
}
