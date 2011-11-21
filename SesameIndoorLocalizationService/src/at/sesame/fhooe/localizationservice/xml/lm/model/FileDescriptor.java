package at.sesame.fhooe.localizationservice.xml.lm.model;

import java.net.MalformedURLException;
import java.net.URL;

public class FileDescriptor 
{
	private String mName;
	private String mFileType;
	private URL mUrl;
	private String mDescription;
	
	public String getName() {
		return mName;
	}
	public void setName(String _name) {
		this.mName = _name;
	}
	public String getFileType() {
		return mFileType;
	}
	public void setFileType(String _fileType) {
		this.mFileType = _fileType;
	}
	public URL getUrl() {
		return mUrl;
	}
	public void setUrl(URL _url) {
		this.mUrl = _url;
	}
	
	public void setUrl(String _url)
	{
		try {
			mUrl = new URL(_url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getDescription() {
		return mDescription;
	}
	public void setDescription(String _description) {
		this.mDescription = _description;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileDescriptor [mName=");
		builder.append(mName);
		builder.append(", mFileType=");
		builder.append(mFileType);
		builder.append(", mUrl=");
		builder.append(mUrl);
		builder.append(", mDescription=");
		builder.append(mDescription);
		builder.append("]");
		return builder.toString();
	}
	
}
