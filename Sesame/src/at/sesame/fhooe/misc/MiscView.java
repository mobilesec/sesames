/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.misc;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Date;

import to.doc.android.ipv6config.Command;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import at.sesame.fhooe.R;

/**
 * this class offers a GUI for some miscellaneous functions
 * @author Peter Riedl
 *
 */
public class MiscView 
extends Activity
implements OnClickListener
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "MiscView";
	
	/**
	 * the ActivityManager to retrieve all running services
	 */
	private static ActivityManager mAm;
	
	/**
	 * the context of the MiscView
	 */
	private static Context mContext;
	
	/**
	 * all possible paths where the "cp" command could be located
	 */
	private static final String[] mPossibleCopyCommands = new String[]{	"/system/bin/cp ",
																		"/system/xbin/cp ", 
																		"/system/bin/busybox cp ",
																		"/system/xbin/busybox cp " };
	
	/**
	 * the path to the directory where the application can store data
	 */
	private String mFilesDir ="";
	
	/**
	 * the destination where the keystore has to be copied to
	 */
	private String mKeystoreDest = "/system/bin/";
	
	/**
	 * the name the keystore file has to have
	 */
	private static final String mFileName = "keystore";
	
	/**
	 * the name the client certificate has to have
	 */
	private static final String CERT_FILENAME = "client.p12";
	
	@Override
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		setContentView(R.layout.misc);
		
		mAm = (ActivityManager)getSystemService(Activity.ACTIVITY_SERVICE);
		mContext = getApplicationContext();
		mFilesDir = getFilesDir().getAbsolutePath();
		
		Button replaceKSButt = (Button)findViewById(R.id.replaceKeyStoreButton);
		replaceKSButt.setOnClickListener(this);
		
		Button certStoreButt = (Button)findViewById(R.id.certStoreButton);
		certStoreButt.setOnClickListener(this);
		
		Button certImportButt = (Button)findViewById(R.id.certImportButton);
		certImportButt.setOnClickListener(this);
		
		Button toastDirectoriesButt = (Button)findViewById(R.id.toastDirectoriesButton);
		toastDirectoriesButt.setOnClickListener(this);
		
		Button toastRunningServicesButt = (Button)findViewById(R.id.toastRunningServicesButton);
		toastRunningServicesButt.setOnClickListener(this);
		
//		Intent intent = new Intent(Credentials.INSTALL_ACTION);
//        intent.setClassName("com.android.certinstaller",
//                "com.android.certinstaller.CertInstallerMain");
//        startActivity(intent);
		KeyStore ks;
		try {
			ks = KeyStore.getInstance("");
			
			boolean contains = ks.containsAlias("Gibraltar client IPSec certificate");
			Toast.makeText(this, "contains:"+contains, Toast.LENGTH_LONG);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * reads the keystore file from the assets folder and copies it
	 * to the correct location.
	 */
	private void replaceKeystore()
    {
    	try 
    	{
    		AssetManager am = getAssets();
    		InputStream is = am.open(mFileName);
    		DataInputStream dis = new DataInputStream(is);

    		FileOutputStream fos = getApplicationContext().openFileOutput(mFileName, MODE_WORLD_WRITEABLE);
    		

	        byte buf[]=new byte[1024];

	        int len;
	        int cnt = 0;
	        while((len=dis.read(buf))>0)
	        {
	        	fos.write(buf,0,len);
	        	cnt+=len;
	        }
	        fos.flush();
	        dis.close();
	        is.close();
	        fos.close();
	        Log.e(TAG,"wrote "+cnt+" bytes to "+mFilesDir+"/"+mFileName);
    	        
	       
	        executeCommand("mount -o remount,rw /system /system");
	        
	        String params = mFilesDir + "/" + mFileName + " " + mKeystoreDest + mFileName;
	        
	        int result = Integer.MAX_VALUE;
	        
	        for(String prefix:mPossibleCopyCommands)
	        {
	        	String cmd = prefix+params;
	        	result = executeCommand(cmd);
	        	
	        	if(result==0)
	        	{
	        		Log.e(TAG, "correct command:"+cmd);
	        		break;
	        	}
	        }
	        
	        if(result!=0)
	        {
	        	toastAndLog(getString(R.string.misc_negativeKeystoreToastMsg));
	        }
	        else
	        {
	        	toastAndLog(getString(R.string.misc_positiveKeystoreToastMsg));
	        }
	        
	       
	        File source = new File(mFilesDir+"/"+mFileName);
	        Date sourceModified = new Date(source.lastModified());
	        File dest = new File(mKeystoreDest+mFileName);
	        Date destModified = new Date(dest.lastModified());
	        
	        Log.e(TAG, source.getAbsolutePath()+" exists:"+source.exists()+"("+sourceModified.toLocaleString()+")");
	        Log.e(TAG,dest.getAbsolutePath()+" exists:"+dest.exists()+"("+destModified.toLocaleString()+")");

	        executeCommand("mount -o remount,ro /system /system");
    	} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Error in doCopy()",e2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.wtf(TAG, "Error in doCopy()", e);
//			Log.e("MAIN", "io exception:"+e.getMessage());
		}

    }
	
	/**
	 * executes the passed command in a shell on android
	 * @param _cmd the command to execute
	 * @return the returncode of the command
	 */
    private int executeCommand(String _cmd)
    {
    	try {
			return Command.executeCommand("sh", true, _cmd, null, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
    }
	
    /**
     * opens the android activity for importing x509 certificates
     */
	private void openCertImport()
	{
		Intent i = new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS);
		i = new Intent("android.credentials.INSTALL");
		startActivity(i);
	}
	
	/**
	 * retrieves the paths for the data, root, external and files directories, 
	 * prints and toast them
	 */
	private void printDirectories()
	{
		File dataDir = Environment.getDataDirectory();
		File rootDir = Environment.getRootDirectory();
		File extDir = Environment.getExternalStorageDirectory();
		File fileDir = getApplicationContext().getFilesDir();
		
		String msg = 	"data:"+dataDir.getAbsolutePath()+
						"\nroot:"+rootDir.getAbsolutePath()+
						"\nexternal:"+extDir.getAbsolutePath()+
						"\nfile:"+fileDir.getAbsolutePath();
		
		toastAndLog(msg);
	}
	
	
	/**
	 * prints and toasts all currently running services
	 */
	public static void printRunningServices()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("------------------------------\n");
		for(RunningServiceInfo rsi:mAm.getRunningServices(Integer.MAX_VALUE))
		{
			sb.append(rsi.service.getShortClassName()+"\n");
		}
		sb.append("------------------------------\n");
		
		toastAndLog(sb.toString());
	}
	
	/**
	 * reads the certificate from the assets and copies it to the
	 * root of the sdcard
	 */
	private void copyCertificate()
    {
    	File sdDir = Environment.getExternalStorageDirectory();
    	File cert = new File(sdDir.getAbsolutePath()+"/"+CERT_FILENAME);
    	Log.e("MAIN", "checking if file \""+cert.getAbsolutePath()+"\" exists...");
    	if(cert.exists())
    	{
    		Log.e("MAIN", "certificate exists...");
    	}
    	else
    	{
    		Log.e("MAIN", "certificate doesn't exist...");

    		try 
    		{
    			AssetManager am = getAssets();
    			String[] assets = am.list("");
    			for(String s:assets)
    			{
    				Log.e(TAG, s);
    			}
    			InputStream is = am.open(CERT_FILENAME);
    			DataInputStream dis = new DataInputStream(is);
    			FileOutputStream fos = new FileOutputStream(cert);
    			byte buf[]=new byte[1024];

    			int len;

    			while((len=dis.read(buf))>0)
    			{
    				fos.write(buf,0,len);
    			}
    			fos.flush();
    			dis.close();
    			is.close();
    			fos.close();
    		} 
    		catch (IOException e) 
    		{
    			e.printStackTrace();
    		}
    	}
    }
	
	/**
	 * creates a toast and logs the passed message
	 * @param msg the message to be toasted and logged
	 */
	private static void toastAndLog(String msg)
	{
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
		
		Log.e(TAG,msg);
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
		case R.id.replaceKeyStoreButton:
			replaceKeystore();
			break;
		case R.id.certStoreButton:
			copyCertificate();
			break;
		case R.id.certImportButton:
			openCertImport();
			break;
		case R.id.toastDirectoriesButton:
			printDirectories();
			break;
		case R.id.toastRunningServicesButton:
			printRunningServices();
			break;
		default:
			Log.e(TAG, "default in onClick()");	
		}
	}
}
