package at.sesame.fhooe.proxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;


import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
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



import android.app.Activity;

import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.widget.Button;


public class ProxyActivity extends Activity 
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button b = (Button)findViewById(R.id.button1);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("ProxyActivity", "button clicked");
				try {
					connectToDocTo();
				} catch (KeyManagementException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				Log.e("ProxyActivity123","!!!DONE!!!");
			}
		});
//		new Thread(new Runnable() 
//		{
//			@Override
//			public void run() {
				
				
//			}
//		}).start();
	}

	private void connectToDocTo() throws NoSuchAlgorithmException, KeyManagementException, InstantiationException, IllegalAccessException
	{
		HttpClient defClient = getProxiedAllAcceptingHttpsClient();

		HttpGet req = new HttpGet("https://doc.to/~sesamedata/all/allDataForWekaTraining.arff");
		try {
			HttpResponse resp = defClient.execute( req);
			InputStream is = resp.getEntity().getContent();
//			DataInputStream dis = new DataInputStream(is);
//			int contentLength = (int) resp.getEntity().getContentLength();
//			byte[] content = new byte[contentLength];
//			dis.readFully(content);
			byte buf[]=new byte[1024];
			File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/downloaded.arff");
			FileOutputStream fos = new FileOutputStream(f);

	        int len;

	        while((len=is.read(buf))>0)
	        {
	        	fos.write(buf,0,len);

	        }
	        fos.flush();
	        fos.close();
//			BufferedReader br = new BufferedReader(new InputStreamReader(is));
//			String line = "";
//			StringBuffer sb = new StringBuffer();
//			while(null!=(line=br.readLine()))
//			{
//				sb.append(line);
//				sb.append("\n");
//			}
//			String msg = Arrays.toString(content);
//			Log.e("ProxyActivityStream1",msg);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static HttpClient getProxiedAllAcceptingHttpsClient() 
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


			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			DefaultHttpClient defClient = new DefaultHttpClient(ccm, params);
			HttpHost proxy = new HttpHost("80.120.3.4", 80);
//			BasicHttpParams hp = (BasicHttpParams) defClient.getParams();
//
//			Log.e("ProxyActivity",hp.toString());

			CredentialsProvider cp = defClient.getCredentialsProvider();
			cp.setCredentials(new AuthScope(proxy.getHostName(),proxy.getPort()), new UsernamePasswordCredentials("test01", "testme!#"));
			defClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			return defClient;	
		} 
		catch (Exception e) 
		{
			return new DefaultHttpClient();
		}
	}
}