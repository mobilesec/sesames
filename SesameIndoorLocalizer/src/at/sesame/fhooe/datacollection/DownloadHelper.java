package at.sesame.fhooe.datacollection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class DownloadHelper 
{
	public static File downloadFile(String _url, String _path) {
		try 
		{
			URL u = new URL(_url);
			URLConnection conn = u.openConnection();
			int contentLength = conn.getContentLength();

			DataInputStream stream = new DataInputStream(u.openStream());

			byte[] buffer = new byte[contentLength];
			stream.readFully(buffer);
			stream.close();
			File f = new File(_path, "data.arff");
			DataOutputStream fos = new DataOutputStream(new FileOutputStream(f));
			fos.write(buffer);
			fos.flush();
			fos.close();
			return f;
		} catch(FileNotFoundException e) {
			Log.e("DownloadHelper", "exception", e);
			return null; // swallow a 404
		} catch (IOException e) {
			Log.e("DownloadHelper", "exception", e);
			return null; // swallow a 404
		}
	}

}
