package at.fhooe.mc.survey.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import at.fhooe.mc.R;

/**
 * The FileDialog View Class
 * 
 * @author bernhard_stemmer
 * 
 */
public class FileDialogView extends ListActivity {

	/** LOG TAG **/
	private static final String LOG_TAG = "FileDialogView";
	/** the item list **/
	private List<String> item = null;
	/** the path list **/
	private List<String> path = null;
	/** the root string **/
	private String root = "/";
	/** the act Path **/
	private TextView m_actPath;
	/** the ok button **/
	private Button m_okButton;
	/** the act path string **/
	private String m_actPathString;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_dialog_main);
		m_actPath = (TextView) findViewById(R.id.file_dialog_main_path);

		m_okButton = (Button) findViewById(R.id.file_dialog_main_okButton);
		// m_okButton.setEnabled(true);
		m_okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setActPath();
			}
		});

		getDir(root);
	}

	/**
	 * The get Dir methods
	 * 
	 * @param dirPath
	 *            sets the path
	 */
	private void getDir(String dirPath) {
		m_actPathString = dirPath;
		m_actPath.setText(R.string.FileDialogView_path + dirPath);

		item = new ArrayList<String>();
		path = new ArrayList<String>();

		File f = new File(dirPath);
		File[] files = f.listFiles();

		if (!dirPath.equals(root)) {

			item.add(root);
			path.add(root);

			item.add("../");
			path.add(f.getParent());

		}

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			path.add(file.getPath());
			if (file.isDirectory())
				item.add(file.getName() + "/");
			else
				item.add(file.getName());
		}

		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
				R.layout.file_dialog_row, item);
		setListAdapter(fileList);
	}

	/**
	 * Called when button is clicked
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		File file = new File(path.get(position));

		if (file.isDirectory()) {
			if (file.canRead())
				getDir(path.get(position));
			else {
				new AlertDialog.Builder(this)
						.setIcon(R.drawable.ic_launcher)
						.setTitle(R.string.FileDialogView_unable_to_open_folder)
						.setPositiveButton(R.string.FileDialogView_OK_Field,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								}).show();
			}
		} else {
			new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher)
					// .setTitle("[" + file.getName() + "]")
					.setTitle(R.string.FileDialogView_no_folder)
					.setPositiveButton(R.string.FileDialogView_OK_Field,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}
							}).show();
		}
	}

	/**
	 * Sets the act path
	 */
	private void setActPath() {
		String path = m_actPathString;
		Log.e(LOG_TAG, "act Path: " + path);
		Intent data = new Intent();
		data.putExtra(SaveInformationView.RESULT_BUNDLE_DIRECTORY_KEY, path);
		setResult(RESULT_OK, data);
		finish();

	}

}
