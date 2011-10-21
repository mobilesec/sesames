/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms.proxy;

import java.io.IOException;
import java.security.KeyStore;
import java.security.Principal;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultProxyAuthenticationHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

/**
 * this class creates a proxied http client
 *
 */
public class ProxyHelper 
{
	/**
	 * creates an http client that uses a proxy server with authentication 
	 * and accepts all SSL certificates
	 * @return the HttpClient to use for all Rest calls
	 */
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
			HttpClientParams.setAuthenticating(params, true);
			
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			DefaultHttpClient defClient = new DefaultHttpClient(ccm, params);
			defClient.getParams().setParameter(ClientPNames.HANDLE_AUTHENTICATION, true);
			defClient.getParams().setParameter(AuthPNames.CREDENTIAL_CHARSET, "UTF-8");
	
			HttpHost proxy = new HttpHost("80.120.3.4", 80);

			CredentialsProvider cp = defClient.getCredentialsProvider();
			cp.setCredentials(new AuthScope(proxy.getHostName(),proxy.getPort()), new UsernamePasswordCredentials("test01", "testme!#"));
			defClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				
			HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() 
			{
				@Override
				public void process(HttpRequest request, HttpContext context)throws HttpException, IOException 
				{
					request.addHeader(BasicScheme.authenticate(getCredentials(),"UTF-8",true));
				}    
			};

			defClient.addRequestInterceptor(preemptiveAuth, 0);
			
			defClient.setProxyAuthenticationHandler(new DefaultProxyAuthenticationHandler());
			
			return defClient;	
		} 
		catch (Exception e) 
		{
			return new DefaultHttpClient();
		}
	}
	
	/**
	 * provides the credentials used for proxy authentication
	 * @return credendtials for proxy authentication
	 */
	public static Credentials getCredentials()
	{
		return new Credentials() 
		{	
			@Override
			public Principal getUserPrincipal() 
			{
				return new Principal() 
				{	
					@Override
					public String getName() 
					{
						return "test01";
					}
				};
			}	
			@Override
			public String getPassword()
			{
				return "testme!#";
			}
		};
	}
}
