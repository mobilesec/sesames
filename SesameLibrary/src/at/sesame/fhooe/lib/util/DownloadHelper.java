package at.sesame.fhooe.lib.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.util.Log;
import at.sesame.fhooe.lib.pms.proxy.ProxyHelper;

public class DownloadHelper 
{
	private static final String TAG = "DownloadHelper";
	public static InputStream getStreamFromUrl(String _url)
	{
//		HttpHost proxy = new HttpHost("80.120.3.4", 3128, "http");

//        DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpClient httpclient = ProxyHelper.getProxiedAllAcceptingHttpsClient();
        try {
//            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        	
//            HttpHost target = new HttpHost("doc.to", 443, "https");
//            HttpGet req = new HttpGet("/~sesamedata/all/allDataForWekaTraining.arff");

//            System.out.println("ההההה  executing request to " + target + " via " + proxy);
//            HttpResponse rsp = httpclient.execute(target, req);
//            HttpUriRequest request = new HttpGet("https://doc.to/~sesamedata/all/allDataForWekaTraining.arff");
        	HttpUriRequest request = new HttpGet(_url);
            HttpResponse rsp = httpclient.execute(request);
            Log.e(TAG, "request executed");
            HttpEntity entity = rsp.getEntity();
            InputStream entityStream = entity.getContent();
            return entityStream;
//            Instances inst = InstanceHelper.getInstancesFromInputStream(entityStream);
//            Log.e(TAG, "number of instances ="+inst.numInstances());
//            System.out.println("----------------------------------------");
//            System.out.println(rsp.getStatusLine());
//            Header[] headers = rsp.getAllHeaders();
//            for (int i = 0; i<headers.length; i++) {
//                System.out.println(headers[i]);
//            }
//            System.out.println("----------------------------------------");
//
//            if (entity != null) {
//                System.out.println(EntityUtils.toString(entity));
//            }

        } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().closeExpiredConnections();
        }
        return null;
	}

}
