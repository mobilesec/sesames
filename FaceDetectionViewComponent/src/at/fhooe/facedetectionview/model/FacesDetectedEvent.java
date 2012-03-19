package at.fhooe.facedetectionview.model;

import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Point;
import at.fhooe.facedetectionview.model.FaceDetector.Feature;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;

/**
 * Gets thrown after each done face detection, no matter if faces have been
 * detected or not. Contains the detected faces as OpenCv objects, metadata used
 * during the detection and
 * 
 * @author Rainhard Findling
 * @date 15.02.2012
 * @version 1
 */
public class FacesDetectedEvent extends EventObject {

	/** faces nearer than this treshold are counted as "near", other as "far". */
	private static final float		NEAR_FACES_DEFAULT_BORDER_CM	= 60.0f;
	// ================================================================================================================
	// MEMBERS

	/** map of facelists found for given opencv cascades. */
	private HashMap<Feature, CvSeq>	mOpenCvFaces					= null;
	/** how much the faces have been made smaller during processing */
	private float					mSubSamplingFactor				= 1;
	/** for debugging purposes. can be null. */
	private Bitmap					mScreenBitmap					= null;
	/**
	 * the size of the original camera image the faces have been extracted from.
	 */
	private Point					mCameraPictureSize				= null;

	// ================================================================================================================
	// METHODS

	/**
	 * @return true if at least 1 face has been found.
	 */
	public boolean areFacesFound() {
		for (CvSeq l : mOpenCvFaces.values()) {
			if (l.total() > 0) {
				return true;
			}
		}
		return false;
	}

	public FacesDetectedEvent(Object _source, HashMap<Feature, CvSeq> _openCvFaces, float _subSamplingFactor,
			Bitmap _screenBitmap, Point _cameraPictureSize) {
		super(_source);
		mOpenCvFaces = _openCvFaces;
		mSubSamplingFactor = _subSamplingFactor;
		mScreenBitmap = _screenBitmap;
		mCameraPictureSize = _cameraPictureSize;
	}

	/**
	 * @return the amount of total found faces.
	 */
	public int getAmountOfFaces() {
		int amount = 0;
		for (CvSeq l : mOpenCvFaces.values()) {
			amount += l.total();
		}
		return amount;
	}

	/**
	 * @return a list roughly estimated face-distances. each distance represents
	 *         the distance to a detected face in cm. <b>THIS IS A ROUGH
	 *         ESTIMATION AND NOT AN EXACT VALUE!</b> Specially for very near
	 *         faces (<20cm) the estimated distances might be slightly too big.
	 *         if the list is empty, not faces have been detected.
	 */
	public List<Float> getDistanceMetricList() {
		List<Float> list = new ArrayList<Float>();
		for (CvSeq seq : mOpenCvFaces.values()) {
			int total = seq.total();
			for (int i = 0; i < total; i++) {
				CvRect r = new CvRect(cvGetSeqElem(seq, i));
				list.add(DistanceMetricUtil.calculateDistanceMetric(new Point(r.width(), r.height()), mCameraPictureSize,
						mSubSamplingFactor));
			}
		}
		return list;
	}

	/**
	 * See {@link #getDistanceMetricList()}. This list contains only the
	 * distance metrices for faces for which the distance metric returned a
	 * smaller value than _nearBorderCm.
	 * 
	 * @param _nearBorderCm
	 *            treshold in cm.
	 * @return
	 */
	public List<Float> getNearFacesDistanceMetricList(float _nearBorderCm) {
		List<Float> list = getDistanceMetricList();
		Float[] vals = list.toArray(new Float[list.size()]);
		for (Float v : vals) {
			if (v > _nearBorderCm) {
				list.remove(v);
			}
		}
		return list;
	}

	/**
	 * The amount of faces for which the distance metric indicates that the face
	 * is nearer than _nearBorderCm. Is equal to
	 * {@link #getNearFacesDistanceMetricList(float)}{@code .size()}.
	 * 
	 * @param _nearBorderCm
	 *            treshold in cm.
	 * @return
	 */
	public int getAmountOfNearFaces(float _nearBorderCm) {
		return getNearFacesDistanceMetricList(_nearBorderCm).size();
	}

	/**
	 * Equal to {@link #getAmountOfNearFaces(float)} with
	 * {@link #NEAR_FACES_DEFAULT_BORDER_CM}.
	 * 
	 * @return
	 */
	public int getAmountOfNearFaces() {
		return getAmountOfNearFaces(NEAR_FACES_DEFAULT_BORDER_CM);
	}

	@Override
	public String toString() {
		return "FacesDetectedEvent [mCameraPictureSize=" + mCameraPictureSize + ", mOpenCvFaces=" + mOpenCvFaces
				+ ", mScreenBitmap=" + mScreenBitmap + ", mSubSamplingFactor=" + mSubSamplingFactor + "]";
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
