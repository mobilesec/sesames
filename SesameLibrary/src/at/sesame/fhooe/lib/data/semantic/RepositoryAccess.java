package at.sesame.fhooe.lib.data.semantic;

import java.util.concurrent.ExecutionException;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.io.http.HttpClientHttpChannelFactory;

import android.os.AsyncTask;

public class RepositoryAccess 
{
	private static IRepositoryService mRepo;
	
	static
	{
		CRest crest = new CRestBuilder().setHttpChannelFactory(new HttpClientHttpChannelFactory(RequestInterceptingClientProvider.getInterceptedClient()))
										.build();
		mRepo = crest.build(IRepositoryService.class);
	}

	public static String executeQuery(String _query)
	{
		try 
		{
			return new ExecuteQueryTask().execute(new String[]{_query}).get();
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static class ExecuteQueryTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... params) 
		{
			return mRepo.executeQuery(params[0]);
//			return null;
		}

		
	}
}
