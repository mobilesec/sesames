package at.sesame.fhooe.localizationservice.xml.lm.model;

public class FingerPrintDatabaseInformation 
{
	
	private DbInfo mDbInfo;
	private FileDescriptor mMpFile;
	
	public DbInfo getDbInfo() {
		return mDbInfo;
	}
	public void setDbInfo(DbInfo _dbInfo) {
		this.mDbInfo = _dbInfo;
	}
	public FileDescriptor getMpFile() {
		return mMpFile;
	}
	public void setMpFile(FileDescriptor _mpFile) {
		this.mMpFile = _mpFile;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FingerPrintDatabaseInformation [mDbInfo=");
		builder.append(mDbInfo);
		builder.append(", mMpFile=");
		builder.append(mMpFile);
		builder.append("]");
		return builder.toString();
	}
}
