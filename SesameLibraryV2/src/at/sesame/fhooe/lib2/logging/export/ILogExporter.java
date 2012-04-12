package at.sesame.fhooe.lib2.logging.export;

public interface ILogExporter 
{
	public boolean export(String _log)throws LoggingExportException;

}
