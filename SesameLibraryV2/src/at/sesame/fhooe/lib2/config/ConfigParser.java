package at.sesame.fhooe.lib2.config;

import java.util.ArrayList;

import at.sesame.fhooe.lib2.util.GenericXMLParser;

public class ConfigParser 
extends GenericXMLParser<SesameConfigData>
{
	private static final String USER_TAG = "user";
	private static final String PASS_TAG = "pass";
	private static final String MAIL_TAG = "mails";
	private static final String ADDRESS_TAG = "address";
	
	private String mUser;
	private String mPass;
	private ArrayList<String> mMailAddresses;
	@Override
	protected void onStartDocument() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onStartTag() 
	{
		if(mParser.getName().equals(USER_TAG))
		{
			mUser = getNextText();
		}
		else if(mParser.getName().equals(PASS_TAG))
		{
			mPass = getNextText();
		}
		else if(mParser.getName().equals(MAIL_TAG))
		{
			mMailAddresses = new ArrayList<String>();
		}
		else if(mParser.getName().equals(ADDRESS_TAG))
		{
			mMailAddresses.add(getNextText());
		}
		
	}

	@Override
	protected void onEndTag() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onEndDocument() 
	{
		mResult = new SesameConfigData(mUser, mPass, mMailAddresses);
	}

}
