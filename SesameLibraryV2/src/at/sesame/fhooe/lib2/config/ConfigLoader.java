package at.sesame.fhooe.lib2.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.os.Environment;
import android.util.Log;

public class ConfigLoader 
{
	private static final String TAG = "ConfigLoader";
	private static final String CONFIG_FILE_NAME = "sesame_config.xml";
	
	public static SesameConfigData loadConfig()
	{
		String externalStoragePath = Environment.getExternalStoragePublicDirectory("").getAbsolutePath()+"/";
		File configFile = new File(externalStoragePath+CONFIG_FILE_NAME);
//		if(!configFile.exists())
//		{
//			return null;			
//		}
		
		try 
		{
			FileInputStream fis = new FileInputStream(configFile);
			ConfigParser cp = new ConfigParser();
			return cp.parse(fis);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			Log.e(TAG, "config file not found");
			e.printStackTrace();
		}
		
		return null;
	}

}
