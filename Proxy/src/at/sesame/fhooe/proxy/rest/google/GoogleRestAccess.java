package at.sesame.fhooe.proxy.rest.google;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.HttpClientRestService;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import at.sesame.fhooe.proxy.ProxyActivity;
import at.sesame.fhooe.proxy.R;
import at.sesame.fhooe.proxy.rest.google.model.Address;
import at.sesame.fhooe.proxy.rest.google.model.SearchResult;

public class GoogleRestAccess 
extends Activity 
{
	private static final String TAG = "GoogleRestAccess";
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.main);
		testGoogleRest();
	}
	
	public void testGoogleRest() {
        /* Get CRest */
        CRest crest = new CRestBuilder().setRestService(new HttpClientRestService(ProxyActivity.getProxiedAllAcceptingHttpsClient())).expectsJson().build();

        /* Build services instances */
        IGoogleSearch searchService = crest.build(IGoogleSearch.class);
        Log.e(TAG,"search service created");
//        LanguageService languageService = crest.build(LanguageService.class);

        /* Use them! */
//        LanguageGuess searchLanguageGuess = languageService.detectLanguage("Guess it!");
//        Translation searchTranslation = languageService.translate("Translate me if you can!", new LangPair("en", "fr"));
        SearchResult<Address> searchResult = searchService.search("this is a google search");
        
        for(Address a:searchResult.getResults())
        {
        	Log.e(TAG,"search=" + a);
        }
        //,url=https://chrome.google.com/webstore/detail/dajedkncpodkggklbegccjpmnglmnflm,visibleUrl=chrome.google.com,cacheUrl=http://www.google.com/search?q=cache:JOONoq9WaF4J:chrome.google.com,title=<b>Search</b> by Image (by <b>Google</b>) - Chrome Web Store,titleNoFormatting=Search by Image (by Google) - Chrome Web Store,content=Jun 15, 2011 <b>...</b> This extension allows you to initiate a <b>Google search</b> using <b>...</b>]

    }

}
