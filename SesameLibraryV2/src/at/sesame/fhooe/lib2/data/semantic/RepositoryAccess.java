package at.sesame.fhooe.lib2.data.semantic;

import java.util.concurrent.ExecutionException;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.io.http.HttpClientHttpChannelFactory;

import android.os.AsyncTask;

public class RepositoryAccess 
{
	private static CRest mCrest;
	private static IRepositoryService mRepo;
	
	static
	{
		mCrest = new CRestBuilder().setHttpChannelFactory(new HttpClientHttpChannelFactory(RequestInterceptingClientProvider.getInterceptedClient()))
										.build();
		mRepo = mCrest.build(IRepositoryService.class);
	}
	

	public static String executeQuery(String _query)
	{
		String res = null;
		try
		{
		res = mRepo.executeQuery(_query);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return res;
//		try 
//		{
////			if(nul=Mr
//			return new ExecuteQueryTask().execute(new String[]{_query}).get();
//		} 
//		catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
	}
	
	private static class ExecuteQueryTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... params) 
		{
			try
			{
				
				String res = mRepo.executeQuery(params[0]);
				return res;
			}
			catch(Exception _e)
			{
//				_e.printStackTrace();
				return null;
			}
//			return null;
		}

		
	}
}
