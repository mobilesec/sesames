package at.sesame.fhooe.lib.location.geocoder;

import android.os.AsyncTask;

public class GeoCoderTask extends AsyncTask<String, Void, String> 
{
	private HttpRetriever retr = new HttpRetriever();
	@Override
	protected String doInBackground(String... arg0) 
	{
		// TODO Auto-generated method stub
		return retr.retrieve(arg0[0]);
	}

}
