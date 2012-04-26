package at.sesame.fhooe.lib2.logging.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class SesameFileLogExporter 
implements ILogExporter 
{
	private static final String TAG = "SesameFileLogExporter";
	private static final String BACKUP_EXTENSION = ".bak";
	private static final String FOLDER_NAME = "sesame";
	private static String mExportPath;
	private String mFileName;
	public static final String MAIL_LOG_FILE_NAME = "sesame_log_mail.csv";

	private FileOutputStream mOutputStream;

	private static Context mCtx;
	public enum ExportLocation
	{ 
		ROOT_DIR,
		DATA_DIR,
		EXT_DIR,
		FILE_DIR,
		USB_DIR,
		EXT_PUB_DIR,
	}

	public SesameFileLogExporter(FileOutputStream _fos)
	{
		mOutputStream = _fos;
	}
	
	public String getMailLogFilePath()
	{
		return mExportPath+MAIL_LOG_FILE_NAME;
	}

	public SesameFileLogExporter(Context _ctx, ExportLocation _exLoc, String _fileName)
	{
		mCtx = _ctx;
//		logPaths(_ctx);
		String exportPath = "";
		switch(_exLoc)
		{
		case ROOT_DIR:
			exportPath = Environment.getRootDirectory().getAbsolutePath()+"/";
			break;
		case DATA_DIR:
			exportPath = Environment.getDataDirectory().getAbsolutePath()+"/";
			break;
		case EXT_DIR:
			exportPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
			break;
		case FILE_DIR:
//			try
//			{
				exportPath = _ctx.getFilesDir().getAbsolutePath()+"/";
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//				setExportPath("/mnt/sdcard/");
//				setFileName(_fileName);
//				return;
//			}
			break;
		case EXT_PUB_DIR:
			exportPath = Environment.getExternalStoragePublicDirectory("").getAbsolutePath()+"/";
			break;
		case USB_DIR:
		default:
			exportPath = "/mnt/usb_storage/sesame_log/";
			break;
		}
		setExportPath(exportPath);
		setFileName(_fileName);
	}

//	private void logPaths(Context _ctx)
//	{
//		Log.e(TAG, Environment.getRootDirectory().getAbsolutePath());
//		Log.e(TAG, Environment.getDataDirectory().getAbsolutePath());
//		Log.e(TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
//		Log.e(TAG, _ctx.getFilesDir().getAbsolutePath());
//	}

	public SesameFileLogExporter(String _path, String _fileName)
	{
		setExportPath(_path);
		setFileName(_fileName);
	}

	private void setExportPath(String _path)
	{
		mExportPath = _path;
		Log.i(TAG, "export path = \""+mExportPath+"\"");
	}

	private void setFileName(String _fileName)
	{
		mFileName = _fileName;
//		mBackupFileName = mFileName+BACKUP_EXTENSION;
	}

	@Override
	public boolean export(String _log) throws LoggingExportException {
		// TODO Auto-generated method stub
//		if(null!=mOutputStream)
//		{
//			return writeStringToFile(mOutputStream, _log);
//		}
//		else
		{
//			boolean res =  writeStringToFile(mExportPath+FOLDER_NAME+mFileName, _log);
			//		writeStringToFile(mExportPath+mBackupFileName, _log);
			writeArffFile(mExportPath+MAIL_LOG_FILE_NAME, _log);
			boolean res = writeArffFile(mExportPath+mFileName, _log);
			return res;			
		}
	}

	private boolean writeStringToFile(FileOutputStream _fos, String _msg)
	{
		logAndToast("writing to file from stream", false);
		try
		{
			PrintWriter pw = new PrintWriter(_fos);
			pw.write(_msg);
			pw.flush();
			pw.close();
		}
		catch(Exception e)
		{
			logAndToast("fail", true);
			return false;
		}
		logAndToast("success", false);
		return true;
	}

	/**
	 * writes the passed content to the passed file
	 * @param _path the path of the file to be created
	 * @param _content the content of the arff file
	 */
	private static boolean writeStringToFile(String _path, String _content)
	{
		logAndToast("exporting to:"+_path, false);

		File f = new File(_path);

		if(!f.exists())
		{
			//			logAndToast("creating file", false);
			//			File parent = f.getParentFile();
			//			Log.i(TAG, parent.getAbsolutePath());
			boolean dirsCreated = f.getParentFile().mkdirs();
			
			logAndToast("dirs created:"+dirsCreated, !dirsCreated);
//			try {
//				f.createNewFile();
//			} 
//			catch (IOException e) 
//			{
//				logAndToast("file creation failed", true);
//				return false;
//			}
//			if(f.exists())
//			{
//				logAndToast("file created successfully", true);
//			}
//			else
//			{
//				logAndToast("file creation failed",true);
//			}
		}
		try 
		{
			PrintWriter pw = new PrintWriter(f);
			pw.write(_content);
			pw.flush();
			pw.close();
		} 
		catch (FileNotFoundException e) 
		{
			logAndToast("file not found", true);
			return false;
		}
		logAndToast("export successful", false);
		return true;
	}
	
	/**
	 * writes the passed content to the passed file
	 * @param _f the file specifying the location for the arff file
	 * @param _content the content of the arff file
	 */
	private static boolean writeArffFile(String _path, String _content)
	{
		File f = new File(_path);
//		logAndToast("exporting to:"+f.getAbsolutePath(), false);
//		if(!f.exists())
//		{
//			boolean created = f.getParentFile().mkdirs();
//			logAndToast("directoryCreated:"+created, !created);
//			try {
//				f.createNewFile();
//			} 
//			catch (IOException e) 
//			{
//				logAndToast("could not create file", true);
//				return false;
//			}
//			if(f.exists())
//			{
//				System.out.println("file created successfully");
//			}
//			else
//			{
//				System.out.println("file creation failed");
//			}
//		}
		try 
		{
			PrintWriter pw = new PrintWriter(f);
			pw.write(_content);
			pw.flush();
			pw.close();
		} 
		catch (FileNotFoundException e) 
		{
			return false;
		}
		return true;
	}

	private static void logAndToast(final String _msg, boolean _error)
	{
		if(_error)
		{
			Log.e(TAG, _msg);
		}
		else
		{
			Log.i(TAG, _msg);			
		}

		if(null!=mCtx)
		{
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() 
				{
					Toast.makeText(mCtx, _msg, Toast.LENGTH_SHORT).show();
				}
			});
			//			Toast t = 
			//			Looper.loop();
			////			Toast.makeText(mCtx, _msg, Toast.LENGTH_LONG).show();
			//			t.show();
		}
	}
}
