package at.fhooe.mc.notification;

import android.content.Context;
import android.widget.Toast;
import at.fhooe.mc.R;

/**
 * Class with all Toasts
 * 
 * @author bernhard_stemmer
 *
 */
public class Toasts {

	/**
	 * Toast for empty MP List
	 * @param _appContext the context
	 */
	public static void emptyMpList(Context _appContext) {
		Toast.makeText(_appContext, R.string.Toasts_no_measurement_points,
				Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Toast for FPI load ok
	 * @param _appContext the context
	 */
	public static void fPIloadSuccess(Context _appContext) {
		Toast.makeText(_appContext, R.string.Toasts_FPI_data_successfully_loaded,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast for  WiFi not enabled
	 * @param _appContext the context
	 */
	public static void wifiNotEnabled(Context _appContext) {
		Toast.makeText(_appContext,
				R.string.Toasts_WLAN_not_active,
				Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Toast for empty FPI load ok
	 * @param _appContext the context
	 */
	public static void mPnotVaild(Context _appContext) {
		Toast.makeText(_appContext,
				R.string.Toasts_no_valid_mp,
				Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Toast for relation name empty
	 * @param _appContext the context
	 */
	public static void relationNameEmpty(Context _appContext) {
		Toast.makeText(_appContext,
				R.string.Toasts_no_arff_name,
				Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Toast for file name empty
	 * @param _appContext the context
	 */
	public static void fileNameEmpty(Context _appContext) {
		Toast.makeText(_appContext,
				R.string.Toasts_no_arff_filename,
				Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Toast for export arff ok
	 * @param _appContext the context
	 */
	public static void exportArffSuccessfull(Context _appContext) {
		Toast.makeText(_appContext,
				R.string.Toasts_arff_file_saved_successfully,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast for export arff nok
	 * @param _appContext the context
	 */
	public static void exportArffFailed(Context _appContext) {
		Toast.makeText(_appContext,
				R.string.Toasts_error_writing_arff,
				Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Toast for export mp nok
	 * @param _appContext the context
	 */
	public static void exportMPFailed(Context _appContext) {
		Toast.makeText(_appContext,
				R.string.Toasts_saving_mp_arff_failed,
				Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Toast for export mp ok
	 * @param _appContext the context
	 */
	public static void exportMPSuccessfull(Context _appContext) {
		Toast.makeText(_appContext,
				R.string.Toasts_saving_mp_arff_successfully,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast for not a valid number
	 * @param _appContext the context
	 */
	public static void noInterger(Context _appContext) {
		Toast.makeText(_appContext,
				R.string.Toasts_input_not_a_number,
				Toast.LENGTH_SHORT).show();
	}	
	
	/**
	 * Toast for wrong setting of mp in rows
	 * @param _appContext the context
	 */
	public static void wrongMpInRow(Context _appContext) {
		Toast.makeText(_appContext,
				R.string.Toasts_number_of_rows_to_big,
				Toast.LENGTH_LONG).show();
	}
}
