package at.sesame.fhooe.lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import weka.core.converters.ConverterUtils;

import android.util.Log;
import at.sesame.fhooe.lib.pms.proxy.ProxyHelper;

public class DownloadHelper 
{
	private static final String TAG = "DownloadHelper";
	
	public static InputStream getStreamFromUrl(URL _url)
	{
		return getStreamFromUrl(_url.toString());
	}
	
	
	public static InputStream getStreamFromUrlTask(String _url)
	{
		InputStream is;
		try {
			is = new DownloadTask().execute(_url).get();
//			String res = convertStreamToString(is);
//			Log.e(TAG, res);
			return is;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getStringFromUrl(String _url)
	{
		return convertStreamToString(getStreamFromUrl(_url));
	}
	
	@SuppressWarnings("unused")
	private static String convertStreamToString(InputStream _is)
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(_is));
		StringBuilder total = new StringBuilder();
		String line;
		try 
		{
			while ((line = r.readLine()) != null) 
			{
			    total.append(line);
			}
			_is.close();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return total.toString();
	}
	
	
	public static InputStream getStreamFromUrl(String _url)
	{
//		HttpHost proxy = new HttpHost("80.120.3.4", 3128, "http");

//        DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpClient httpclient = ProxyHelper.getProxiedAllAcceptingHttpsClient();
		HttpUriRequest request = new HttpGet(_url);
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
		
//        try 
//        {
////            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//
//        	
////            HttpHost target = new HttpHost("doc.to", 443, "https");
////            HttpGet req = new HttpGet("/~sesamedata/all/allDataForWekaTraining.arff");
//
////            System.out.println("ההההה  executing request to " + target + " via " + proxy);
////            HttpResponse rsp = httpclient.execute(target, req);
////            HttpUriRequest request = new HttpGet("https://doc.to/~sesamedata/all/allDataForWekaTraining.arff");
//        	
//            return new DownloadTask().execute(_url).get();
////            Instances inst = InstanceHelper.getInstancesFromInputStream(entityStream);
////            Log.e(TAG, "number of instances ="+inst.numInstances());
////            System.out.println("----------------------------------------");
////            System.out.println(rsp.getStatusLine());
////            Header[] headers = rsp.getAllHeaders();
////            for (int i = 0; i<headers.length; i++) {
////                System.out.println(headers[i]);
////            }
////            System.out.println("----------------------------------------");
////
////            if (entity != null) {
////                System.out.println(EntityUtils.toString(entity));
////            }
//
//        } 
////        catch (ClientProtocolException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} 
//        catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//            // When HttpClient instance is no longer needed,
//            // shut down the connection manager to ensure
//            // immediate deallocation of all system resources
////            httpclient.getConnectionManager().closeExpiredConnections();
//        }
//        return null;
	}

}
