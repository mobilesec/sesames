/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.location;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import at.sesame.fhooe.R;
import at.sesame.fhooe.lib.location.ILocationUpdateReceiver;
import at.sesame.fhooe.lib.location.LocationAccess;
import at.sesame.fhooe.lib.location.geocoder.GeoCoder;
import at.sesame.fhooe.lib.location.geocoder.GeoCoderResult;

/**
 * this class provides a view for all information obtained by the LocationAccess
 * and the Yahoo reverse geocoder. for detailed information about all fields see
 * @see http://developer.yahoo.com/geo/placefinder/guide/
 * @author Peter Riedl
 *
 */
public class LocationView 
extends Activity implements ILocationUpdateReceiver 
{
	/**
	 * the field to display the current latitude
	 */
	private EditText mLatitudeField;
	
	/**
	 * the field to display the current longitude
	 */
	private EditText mLongitudeField;
	
	/**
	 * the field to display the current offset latitude
	 */
	private EditText mOffsetLatField;
	
	/**
	 * the field to display the current offset longitude
	 */
	private EditText mOffsetLonField;
	
	/**
	 * the field to display the current quality
	 */
	private EditText mQualityField;
	
	/**
	 * the field to display the current radius
	 */
	private EditText mRadiusField;
	
	/**
	 * the field to display the current name
	 */
	private EditText mNameField;
	
	/**
	 * the field to display the current line1 information
	 */
	private EditText mLine1Field;
	
	/**
	 * the field to display the current line2 information
	 */
	private EditText mLine2Field;
	
	/**
	 * the field to display the current line3 information
	 */
	private EditText mLine3Field;
	
	/**
	 * the field to display the current line4 information
	 */
	private EditText mLine4Field;
	
	/**
	 * the field to display the current house
	 */
	private EditText mHouseField;
	
	/**
	 * the field to display the current street
	 */
	private EditText mStreetField;
	
	/**
	 * the field to display the current xstreet
	 */
	private EditText mXStreetField;
	
	/**
	 * the field to display the current unittype
	 */
	private EditText mUnitTypeField;
	
	/**
	 * the field to display the current unit
	 */
	private EditText mUnitField;
	
	/**
	 * the field to display the current postal code
	 */
	private EditText mPostalField;
	
	/**
	 * the field to display the current neighborhood
	 */
	private EditText mNeighborhoodField;
	
	/**
	 * the field to display the current city
	 */
	private EditText mCityField;
	
	/**
	 * the field to display the current county
	 */
	private EditText mCountyField;
	
	/**
	 * the field to display the current state
	 */
	private EditText mStateField;
	
	/**
	 * the field to display the current country
	 */
	private EditText mCountryField;
	
	/**
	 * the field to display the current country code
	 */
	private EditText mCountryCodeField;
	
	/**
	 * the field to display the current state code
	 */
	private EditText mStateCodeField;
	
	/**
	 * the field to display the current county code
	 */
	private EditText mCountyCodeField;
	
	/**
	 * the field to display the current hash value
	 */
	private EditText mHashField;
	
	/**
	 * the field to display the current woeid
	 */
	private EditText mWoeidField;
	
	/**
	 * the field to display the current woe type
	 */
	private EditText mWoeTypeField;
	
	/**
	 * the field to display the current uzip
	 */
	private EditText mUzipField;
	
	/**
	 * the button to start the geocoder manually
	 */
	private Button mUpdateButton;
	
	/**
	 * the checkbox to enable/disable automatic geocoding
	 */
	private CheckBox mAutoUpdateCheckbox;
	
	/**
	 * the geocoder to be used
	 */
	private GeoCoder mCoder = new GeoCoder();
	
	/**
	 * the last location 
	 */
	private Location mLastLocation;
	
	/**
	 * id for the geocoder dialog
	 */
	private static final int GEO_CODER_DIALOG = 1;
	
	/**
	 * id for the location update dialog
	 */
	private static final int LOCATION_UPDATE_DIALOG = 2;
	
	/**
	 * boolean flag showing if the location update dialog is shown
	 */
	private boolean mLocationUpdateDialogShowing = false;
	
	/**
	 * boolean flag to indicate if automatic geocoding is enabled or disabled
	 */
	private boolean mAutoUpdateGeoCoder = false;
	
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "LocationView";
	
	@Override
	public void onCreate( Bundle _savedInstance)
	{
		//super.onCreate(_savedInstance);
		super.onCreate(_savedInstance);
//		_container.removeAllViews();
		setContentView(R.layout.location);
//		View v = _inflater.inflate(R.layout.location, _container,false);

		
		
		mUpdateButton = (Button)findViewById(R.id.location_updateButton);
		if(null==mUpdateButton)
		{
			Log.e(TAG, "updateButton was null");
		}
		mUpdateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateGeoCoderInfo();
			}
		});
		
		mAutoUpdateCheckbox = (CheckBox)findViewById(R.id.location_autoUpdateCheckBox);
		mAutoUpdateCheckbox.setChecked(false);
		mAutoUpdateCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mUpdateButton.setEnabled(!isChecked);
				mAutoUpdateGeoCoder = isChecked;
			}
		});
		
		mLatitudeField = (EditText)findViewById(R.id.latitudeField);
		mLongitudeField  = (EditText)findViewById(R.id.longitudeField);
		mOffsetLatField  = (EditText)findViewById(R.id.offsetLatField);
		mOffsetLonField  = (EditText)findViewById(R.id.offsetLonField);
		mQualityField  = (EditText)findViewById(R.id.qualityField);
		mRadiusField  = (EditText)findViewById(R.id.radiusField);
		mNameField  = (EditText)findViewById(R.id.nameField);
		mLine1Field  = (EditText)findViewById(R.id.line1Field);
		mLine2Field  = (EditText)findViewById(R.id.line2Field);
		mLine3Field  = (EditText)findViewById(R.id.line3Field);
		mLine4Field  = (EditText)findViewById(R.id.line4Field);
		mHouseField  = (EditText)findViewById(R.id.houseField);
		mStreetField  = (EditText)findViewById(R.id.streetField);
		mXStreetField  = (EditText)findViewById(R.id.xstreetField);
		mUnitTypeField  = (EditText)findViewById(R.id.unitTypeField);
		mUnitField  = (EditText)findViewById(R.id.unitField);
		mPostalField  = (EditText)findViewById(R.id.postalField);
		mNeighborhoodField  = (EditText)findViewById(R.id.neighborhoodField);
		mCityField  = (EditText)findViewById(R.id.cityField);
		mCountyField  = (EditText)findViewById(R.id.countyField);
		mStateField  = (EditText)findViewById(R.id.stateField);
		mCountryField  = (EditText)findViewById(R.id.countryField);
		mCountryCodeField  = (EditText)findViewById(R.id.countryCodeField);
		mStateCodeField  = (EditText)findViewById(R.id.stateCodeField);
		mCountyCodeField  = (EditText)findViewById(R.id.countyCodeField);
		mHashField  = (EditText)findViewById(R.id.hashField);
		mWoeidField  = (EditText)findViewById(R.id.woeidField);
		mWoeTypeField  = (EditText)findViewById(R.id.woeTypeField);
		mUzipField  = (EditText)findViewById(R.id.uzipField);
		
		LocationAccess la = new LocationAccess(this);
		la.registerLocationUpdateReceiver(this);
		//FIXME
//		getActivity().showDialog(LOCATION_UPDATE_DIALOG);

	}
	
//	@Override
//	public Dialog onCreateDialog(int id)
//	{
//		ProgressDialog d = null;
//		switch(id)
//		{
//		case GEO_CODER_DIALOG:
//			d = new ProgressDialog(this);
//			d.setCancelable(false);
//			d.setMessage(getString(R.string.locationView_geoCoderWaitingMsg));
//			d.show();
//			return d;
//		case LOCATION_UPDATE_DIALOG:
//			d = new ProgressDialog(this);
//			d.setCancelable(true);
//			d.setButton("cancel", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//					
//				}
//			});
//			d.setMessage(getString(R.string.locationView_locationUpdateMsg));
//			d.show();
//			mLocationUpdateDialogShowing = true;
//			return d;
//		default:
//			return null;
//		}
//	}
	
	/**
	 * called by the LocationAccess whenever a location update is available
	 * @param l the new location to be displayed
	 */
	@Override
	public void updateLocation(Location l)
	{
		if(mLocationUpdateDialogShowing)
		{
			//FIXME
//			getActivity().dismissDialog(LOCATION_UPDATE_DIALOG);
			mLocationUpdateDialogShowing = false;
		}

		mLongitudeField.setText(""+l.getLongitude());
		mLatitudeField.setText(""+l.getLatitude());
		
	
		mLastLocation = l;
		if(mAutoUpdateGeoCoder)
		{
			updateGeoCoderInfo();
		}
	}
	
	/**
	 * sets the information for all fields related with the
	 * geocoding information
	 */
	private void updateGeoCoderInfo()
	{
		final String LOCAL_TAG ="updateLocation()";
		if(null==mLastLocation)
		{
			return;
		}
		try 
		{
//			getActivity().runOnUiThread(new Runnable() {
//				
//				@Override
//				public void run() {
//					//FIXME
////					getActivity().showDialog(GEO_CODER_DIALOG);
//					
//				}
//			});
			
			GeoCoderResult gcr = mCoder.reverseGeoCode(mLastLocation.getLatitude(), mLastLocation.getLongitude());
			mQualityField.setText(""+gcr.getQuality());
			mOffsetLatField.setText(""+gcr.getOffsetLat());
			mOffsetLonField.setText(""+gcr.getOffsetLon());
			mRadiusField.setText(""+gcr.getRadius());
			mNameField.setText(""+gcr.getName());
			mLine1Field.setText(gcr.getLine1());
			mLine2Field.setText(gcr.getLine2());
			mLine3Field.setText(gcr.getLine3());
			mLine4Field.setText(gcr.getLine4());
			mHouseField.setText(gcr.getHouse());
			mStreetField.setText(gcr.getStreet());
			mXStreetField.setText(gcr.getXstreet());
			mUnitTypeField.setText(gcr.getUnittype());
			mUnitField.setText(gcr.getUnit());
			mPostalField.setText(gcr.getPostal());
			mNeighborhoodField.setText(gcr.getNeighborhood());
			mCityField.setText(gcr.getCity());
			mCountyField.setText(gcr.getCounty());
			mStateField.setText(gcr.getState());
			mCountryField.setText(gcr.getCountry());
			mCountryCodeField.setText(gcr.getCountryCode());
			mStateCodeField.setText(gcr.getStateCode());
			mCountyCodeField.setText(gcr.getCountyCode());
			mHashField.setText(gcr.getHash());
			mWoeidField.setText(gcr.getWoeid());
			mWoeTypeField.setText(gcr.getWoetype());
			mUzipField.setText(gcr.getUzip());
			Log.e(TAG, gcr.toString());
		} catch (XmlPullParserException e) {
			Log.e(TAG, LOCAL_TAG, e);
		} catch (IOException e) {
			Log.e(TAG, LOCAL_TAG, e);
		}
		finally
		{
			//FIXME
//			getActivity().dismissDialog(GEO_CODER_DIALOG);
		}
		
	}
}
