package at.sesame.fhooe.lib2.data.semantic;

import java.io.IOException;
import java.security.KeyStore;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import at.sesame.fhooe.lib2.pms.proxy.MySSLSocketFactory;

public class RequestInterceptingClientProvider 
{
	private static final String TAG = "RequestInterceptingClientProvider";
	public static HttpClient getInterceptedClient()
	{
		try
		{

			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpClientParams.setAuthenticating(params, true);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));
			DefaultHttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, registry), params);
			client.setReuseStrategy(new ConnectionReuseStrategy() {

				@Override
				public boolean keepAlive(HttpResponse response, HttpContext context) {
					// TODO Auto-generated method stub
					return true;
				}
			});

//			client.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
//
//				@Override
//				public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
//					// TODO Auto-generated method stub
//					return 1;
//				}
//			});
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
		catch(Exception e)
		{
			return new DefaultHttpClient();
		}
	}

}
