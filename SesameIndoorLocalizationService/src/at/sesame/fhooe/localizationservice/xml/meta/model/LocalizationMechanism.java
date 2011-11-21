package at.sesame.fhooe.localizationservice.xml.meta.model;

import java.net.URL;

public class LocalizationMechanism 
{
	private String mName;
	
	private URL mURL;

	public String getName() {
		return mName;
	}

	public void setName(String _name) {
		this.mName = _name;
	}

	public URL getURL() {
		return mURL;
	}

	public void setURL(URL _url) {
		this.mURL = _url;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocalizationMechanism [mName=");
		builder.append(mName);
		builder.append(", mURL=");
		builder.append(mURL);
		builder.append("]");
		return builder.toString();
	}
	
	

}
