package at.sesame.fhooe.lib2.logging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.codegist.common.log.LogCatLogger;

import android.util.Log;
import at.sesame.fhooe.lib2.logging.export.ILogExporter;
import at.sesame.fhooe.lib2.logging.export.LoggingExportException;

public class SesameLogger 
{
	private static final String TAG = "SesameLogger";
	private static final String SEPARATOR = ";";
	private static final SimpleDateFormat LOG_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private static ArrayList<ILogExporter> mExporters = new ArrayList<ILogExporter>();
	private static ArrayList<String> mLog = new ArrayList<String>();
	private static Timer mExportTimer;
	
	public enum EntryType
	{
		FACE_DETECTION,
		PMS, 
		POSITION,
		APPLICATION_INFO
	}
	
	public static void log(EntryType _type, String _source, String _msg)
	{
		String logLine = assembleLogEntry(_type, _source, _msg);
		Log.i(TAG, "logging:"+logLine);
		mLog.add(logLine);
	}
	
	private static String assembleLogEntry(EntryType _type, String _source, String _msg)
	{
		StringBuilder assembler = new StringBuilder(LOG_TIME_FORMAT.format(new Date()));
		assembler.append(SEPARATOR);
		assembler.append(_type.name());
		assembler.append(SEPARATOR);
		assembler.append(_source);
		assembler.append(SEPARATOR);
		assembler.append(_msg);
		return assembler.toString();
	}
	
	public static void export()
	{
		new Timer().schedule(new ExportTask(), 0);
	}
	
	public static void startContinuousExport(long _exportPeriod)
	{
		stopContinuousExporting();
		mExportTimer = new Timer();
		mExportTimer.schedule(new ExportTask(), 0, _exportPeriod);
	}
	
	public static void stopContinuousExporting()
	{
		if(null!=mExportTimer)
		{
			mExportTimer.cancel();
			mExportTimer.purge();
		}
	}
	
	public static void addExporter(ILogExporter _exporter)
	{
		mExporters.add(_exporter);
	}
	
	public static void setExporter(ILogExporter _exporter)
	{
		mExporters.clear();
		mExporters.add(_exporter);
	}
	
	public static void removeExporter(ILogExporter _exporter)
	{
		mExporters.remove(_exporter);
	}
	
	private static String getLogAsString()
	{
		ArrayList<String> logCopy = null;
		synchronized(mLog)
		{
			try
			{
				logCopy = (ArrayList<String>) mLog.clone();	
			}
			catch(ConcurrentModificationException _cme)
			{
				return null;
			}
		}
		StringBuilder sb = new StringBuilder();
		for(String log:logCopy)
		{
			sb.append(log);
			sb.append("\n");
		}			
		return sb.toString();
	}
	
	private static class ExportTask extends TimerTask
	{
		@Override
		public void run() 
		{
			Log.i(TAG, "exporting");
			String log = getLogAsString();
			if(null==log||log.isEmpty())
			{
				Log.e(TAG, "nothing to log");
				return;
			}
			for(int i = 0;i<mExporters.size();i++)
			{
				Log.i(TAG, "exporting to exporter "+(i+1)+" of "+mExporters.size());
				ILogExporter exporter = mExporters.get(i);
				if(null==exporter)
				{
					Log.e(TAG, "exporter was null");
					return;
				}
				try 
				{
					exporter.export(log);
				} 
				catch (LoggingExportException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}
