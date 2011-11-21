package at.sesame.fhooe.lib.location.osm;

import java.util.ArrayList;

import android.os.AsyncTask;
import at.sesame.fhooe.lib.location.osm.model.IndoorLocOSMNode;

public class OsmMapTask extends AsyncTask<String, Void, ArrayList<IndoorLocOSMNode>> {

	@Override
	protected ArrayList<IndoorLocOSMNode> doInBackground(String... arg0) {
		String param = arg0[0];
//		return OSMAccess.getOsmService().getIndoorLocalizationNodes(param);
		return null;
	}

}
