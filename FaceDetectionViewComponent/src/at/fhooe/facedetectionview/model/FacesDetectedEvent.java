package at.fhooe.facedetectionview.model;

import java.util.EventObject;
import java.util.HashMap;

import android.graphics.Bitmap;
import at.fhooe.facedetectionview.model.FaceDetector.Feature;

import com.googlecode.javacv.cpp.opencv_core.CvSeq;

/**
 * Event thrown after each done face detection, no matter if faces have been
 * detected or not.
 * 
 * @author Rainhard Findling
 * @date 15.02.2012
 * @version 1
 */
public class FacesDetectedEvent extends EventObject {

	// ================================================================================================================
	// MEMBERS

	/** map of facelists found for given opencv cascades. */
	private HashMap<Feature, CvSeq>	mOpenCvFaces		= null;
	/** how much the faces have been made smaller during processing */
	private float					mSubSamplingFactor	= 1;
	/** for debugging purposes */
	private Bitmap					mScreenBitmap		= null;

	// ================================================================================================================
	// METHODS

	public FacesDetectedEvent(Object _source) {
		super(_source);
	}

	public FacesDetectedEvent(Object _source, HashMap<Feature, CvSeq> _openCvFaces, float _subSamplingFactor) {
		super(_source);
		mOpenCvFaces = _openCvFaces;
		mSubSamplingFactor = _subSamplingFactor;
	}

	/**
	 * @return true if at least 1 face has been found.
	 */
	public boolean facesFound() {
		for (CvSeq l : mOpenCvFaces.values()) {
			if (l.total() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the amount of total found faces.
	 */
	public int amountOfFaces() {
		int amount = 0;
		for (CvSeq l : mOpenCvFaces.values()) {
			amount += l.total();
		}
		return amount;
	}

	@Override
	public String toString() {
		return "FacesDetectedEvent [facesFound()=" + facesFound() + ", mSubSamplingFactor=" + mSubSamplingFactor
				+ ", mOpenCvFaces=" + mOpenCvFaces + "]";
	}

	/**
	 * @return {@link #openCvFaces}.
	 */
	public HashMap<Feature, CvSeq> getOpenCvFaces() {
		return mOpenCvFaces;
	}

	/**
	 * @param _openCvFaces
	 *            sets {@link #openCvFaces} to _openCvFaces.
	 */
	public void setOpenCvFaces(HashMap<Feature, CvSeq> _openCvFaces) {
		mOpenCvFaces = _openCvFaces;
	}

	/**
	 * @return {@link #subSamplingFactor}.
	 */
	public float getSubSamplingFactor() {
		return mSubSamplingFactor;
	}

	/**
	 * @param _subSamplingFactor
	 *            sets {@link #subSamplingFactor} to _subSamplingFactor.
	 */
	public void setSubSamplingFactor(float _subSamplingFactor) {
		mSubSamplingFactor = _subSamplingFactor;
	}

	/**
	 * @return {@link #screenBitmap}.
	 */
	public Bitmap getScreenBitmap() {
		return mScreenBitmap;
	}

	/**
	 * @param _screenBitmap
	 *            sets {@link #screenBitmap} to _screenBitmap.
	 */
	public void setScreenBitmap(Bitmap _screenBitmap) {
		mScreenBitmap = _screenBitmap;
	}
}
