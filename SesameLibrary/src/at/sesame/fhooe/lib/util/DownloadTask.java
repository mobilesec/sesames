package at.sesame.fhooe.lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.os.AsyncTask;
import at.sesame.fhooe.lib.pms.proxy.ProxyHelper;

public class DownloadTask extends AsyncTask<String, Void, InputStream> {

	@Override
	protected InputStream doInBackground(String... params) {
		HttpClient httpclient = ProxyHelper.getProxiedAllAcceptingHttpsClient();
		HttpUriRequest request = new HttpGet(params[0]);
        HttpResponse rsp;
		try {
			rsp = httpclient.execute(request);
			 HttpEntity entity = rsp.getEntity();
		        InputStream entityStream = entity.getContent();
				return entityStream;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       return null;
	}
	
	

}
