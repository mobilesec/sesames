package at.sesame.fhooe.lib.ui.kiosk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class StatusBarRemover 
{
	private static final String TAG = "StatusBarRemover";
	public static boolean hideStatusBar()
	{
		try
		{
			Process p;
			p = Runtime.getRuntime().exec("su"); 

			// Attempt to write a file to a root-only
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("mount -o remount,rw /dev/block/stl6 /system\n");
			os.writeBytes("mv /system/app/SystemUI.odex /system/app/SystemUI_Old.odex\n");
			os.writeBytes("mv /system/app/SystemUI.apk /system/app/SystemUI_Old.apk\n");
			os.writeBytes("mount -o remount,ro /dev/block/stl6 /system\n");

			// Close the terminal
			os.writeBytes("exit\n");
			os.flush();
			p.waitFor();

			Log.e(TAG, "status bar removed successfully");
			return true;
//			new AlertDialog.Builder(this)
//			.setIconAttribute(android.R.attr.alertDialogIcon)
//			.setMessage("Android Honeycomb StatusBar removed successfully!")
//			.show();

		}
		catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage());
			return false;
		}
	}
	
	public static void showStatusBar()
	{
		 try
	        {
	            Process p;
	            p = Runtime.getRuntime().exec("su"); 

	            // Attempt to write a file to a root-only
	            DataOutputStream os = new DataOutputStream(p.getOutputStream());
	            os.writeBytes("mount -o remount,rw /dev/block/stl6 /system\n");
	            os.writeBytes("mv /system/app/SystemUI_Old.odex /system/app/SystemUI.odex\n");
	            os.writeBytes("mv /system/app/SystemUI_Old.apk /system/app/SystemUI.apk\n");
	            os.writeBytes("mount -o remount,ro /dev/block/stl6 /system\n");
	            String systemServerPID = GetSystemServerPID();
	            if (systemServerPID != null)
	                os.writeBytes("kill " + systemServerPID + "\n");
	            // else ... manual reboot is required if systemServerPID fail.

	            // Close the terminal
	            os.writeBytes("exit\n");
	            os.flush();
	            p.waitFor();
	        }
	        catch (Exception e)
	        {
	        	Log.e(TAG, e.getLocalizedMessage());
	        }
	}
	
	private static String GetSystemServerPID()
    {
        try
        {
            Process p = Runtime.getRuntime().exec("ps -n system_server"); 
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            reader.readLine(); // Skip header.
            return reader.readLine().substring(10, 16).trim();
        }
        catch (Exception e)
        {
            return null;
        }
    }


}
