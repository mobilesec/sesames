package at.sesame.fhooe.lib.data.semantic;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

public class RequestInterceptingClientProvider 
{
	private static final String TAG = "RequestInterceptingClientProvider";
	public static HttpClient getInterceptedClient()
	{
		DefaultHttpClient client = new DefaultHttpClient();
		HttpRequestInterceptor headerAdder = new HttpRequestInterceptor() 
		{
			@Override
			public void process(HttpRequest request, HttpContext context)throws HttpException, IOException 
			{
//				Log.e(TAG, request.getRequestLine().toString());
				request.setHeader("Accept", "application/sparql-results+json");
//				request.setHeader("Accept","application/rdf+xml");
//				request.setHeader("Content-Type", "application/x-www-form-urlencoded");
//				request.setHeader("Accept", "application/sparql-results+xml, */*;q=0.5");
//				request.setHeader("Accept","text/xml,application/xml,application/xhtml+xml,*/*;q=0.1");
			}    
		};
		client.addRequestInterceptor(headerAdder);
		
		return client;
	}

}
