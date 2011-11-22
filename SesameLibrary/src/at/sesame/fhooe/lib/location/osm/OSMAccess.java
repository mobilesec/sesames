package at.sesame.fhooe.lib.location.osm;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.io.http.HttpClientHttpChannelFactory;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import at.sesame.fhooe.lib.location.LocationAccess;
import at.sesame.fhooe.lib.location.osm.model.IndoorLocOSMNode;
import at.sesame.fhooe.lib.location.osm.model.OSMResponseParser;
import at.sesame.fhooe.lib.location.osm.rest.IOsmApi;
import at.sesame.fhooe.lib.pms.proxy.ProxyHelper;
import at.sesame.fhooe.lib.util.DownloadHelper;

public class OSMAccess 
{
	private static final String TAG = "OSMAccess";
	private static IOsmApi mOsmApi = null;
	private static ArrayList<IndoorLocOSMNode> mNodes;
//	public static IOsmApi getOsmService()
//	{
//		if(null==mOsmApi)
//		{
//			CRest crest = new CRestBuilder().setHttpChannelFactory(new HttpClientHttpChannelFactory(ProxyHelper.getProxiedAllAcceptingHttpsClient()))
//											.build();
//			mOsmApi = crest.build(IOsmApi.class);
//		}
//		return mOsmApi;
//	}
	private static boolean mQueried = false;
	
	Thread NodeQueryThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			double lat = 48.368;
			double lon = 14.513;
			double left = lon-0.01;
			double right = lon+0.01;
			//14.516,48.366,14.518,48.368?node[indoor_loc_url=*][bbox=14.512,48.367,14.514,48.369]
			double top = lat+0.01;
			double bottom = lat-0.01;
			new OSMResponseParser();
			OSMAccess.setNodes( OSMResponseParser.parse(DownloadHelper.getStreamFromUrl(generateNodeUrl(left, bottom, right, top))));
		}

		
	});
	//	private
	private static URL generateNodeUrl(double _left, double _bottom, double _right, double _top) 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("http://www.overpass-api.de/api/xapi?node[indoor_loc_url=*][bbox=");
		sb.append(_left);
		sb.append(",");
		sb.append(_bottom);
		sb.append(",");
		sb.append(_right);
		sb.append(",");
		sb.append(_top);
		sb.append("]");
		URL res = null;
		try {
			res = new URL(sb.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	public static synchronized void setNodes(ArrayList<IndoorLocOSMNode> _nodes)
	{
		mNodes = _nodes;
		mQueried =true;
	}
	

	public String getClosestIndoorLocationDataUrl()
	{
		
		// TODO Auto-generated method stub
//		try {
//			Looper.getMainLooper().prepare();
//			mNodes = getOsmService().getMap(getBBoxString(left, bottom, right, top));
		NodeQueryThread.start();
		while(!mQueried)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//			mNodes = new OsmMapTask().execute(getBBoxString(left, bottom, right, top)).get();
//			Looper.getMainLooper().loop();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


		//			nodes = getOsmService().getMap(getBBoxString(left, bottom, right, top));
		Log.e(TAG, "nodes read from osm");


		if(mNodes.size()>0)
		{
			Log.e(TAG, "returned not null");
			return mNodes.get(0).getValueForTag(IndoorLocOSMNode.INDOOR_LOCATION_URL_KEY);
		}
		else
		{
			Log.e(TAG, "returned null");
			return null;
		}

	//			catch (InterruptedException e) {
	//			// TODO Auto-generated catch block
	////			Looper.loop();
	//			e.printStackTrace();
	//		} catch (ExecutionException e) {
	//			// TODO Auto-generated catch block
	////			Looper.loop();
	//			e.printStackTrace();
	//		}

	//		return null;
	//		ArrayList<String> res = new ArrayList<String>();
	//		for(IndoorLocOSMNode iloNode:nodes)
	//		{
	//			res.add(iloNode.getValueForTag(IndoorLocOSMNode.INDOOR_LOCATION_URL_KEY));
	//		}
	//		return res;

	//		Location l = Location.
}

private static String getBBoxString(double _left, double _bottom, double _right, double _top)
{
	StringBuilder sb = new StringBuilder();
	sb.append("?node[indoor_loc_url=*][bbox=");
	sb.append(_left);
	sb.append(",");
	sb.append(_bottom);
	sb.append(",");
	sb.append(_right);
	sb.append(",");
	sb.append(_top);
	sb.append("]");
	return sb.toString();

}

}
