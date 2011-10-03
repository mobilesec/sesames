package at.sesame.fhooe.datacollection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.util.Log;

public class DownloadHelper 
{
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		
		@Override
		public boolean verify(String hostname, SSLSession session) {
			// TODO Auto-generated method stub
			return true;
		}
	};

	public static File downloadFile(String _url, String _path) {
		try 
		{
			URL u = new URL(_url);
			HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
//			conn.setHostnameVerifier(DO_NOT_VERIFY);
//			trustAllHosts();
			InputStream is = conn.getInputStream();
			Log.e("DOWNLOAD_HELPER", "length:"+is.read());
//			int contentLength = conn.getContentLength();
//
//			DataInputStream stream = new DataInputStream(u.openStream());
//
//			byte[] buffer = new byte[contentLength];
//			stream.readFully(buffer);
//			stream.close();
			File f = new File(_path, "data.arff");
//			DataOutputStream fos = new DataOutputStream(new FileOutputStream(f));
//			fos.write(buffer);
//			fos.flush();
//			fos.close();
			return f;
		} catch(FileNotFoundException e) {
			Log.e("DownloadHelper", "exception", e);
			return null; // swallow a 404
		} catch (IOException e) {
			Log.e("DownloadHelper", "exception", e);
			return null; // swallow a 404
		}
	}
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
			.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
