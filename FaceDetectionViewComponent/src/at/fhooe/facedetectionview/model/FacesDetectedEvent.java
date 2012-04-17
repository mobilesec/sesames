package at.fhooe.facedetectionview.model;

import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;

import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
	private static final long				serialVersionUID				= 1L;

	/** faces nearer than this treshold are counted as "near", other as "far". */
	private static final float				NEAR_FACES_DEFAULT_BORDER_CM	= 60.0f;
	// ================================================================================================================
	// MEMBERS

	/** map of facelists found for given opencv cascades. */
	private volatile Map<Feature, CvSeq>	mOpenCvFaces					= null;
	/** how much the faces have been made smaller during processing */
	private float							mSubSamplingFactor				= 1;
	/** for debugging purposes. can be null. */
	private Bitmap							mScreenBitmap					= null;
	/**
	 * the size of the original camera image the faces have been extracted from.
	 */
	private Point							mCameraPictureSize				= null;
	/**
	 * true: data accessed via getter/setters is cached data. false: data
	 * accessed via getter/setters will be calculated each time new. resolves
	 * javacv-multithreading problem (access to opencv-memory is denied when
	 * calling from wrong thread).
	 */
	private boolean							mUseCachedData					= false;
	/** cached distance metric list from {@link #getDistanceMetricList()}. */
	private List<Float>						mCachedDistanceMetricList		= null;

	// ================================================================================================================
	// METHODS

	public FacesDetectedEvent(Object _source, Map<Feature, CvSeq> _openCvFaces, float _subSamplingFactor, Bitmap _screenBitmap,
			Point _cameraPictureSize) {
		super(_source);
		mOpenCvFaces = _openCvFaces;
		mSubSamplingFactor = _subSamplingFactor;
		mScreenBitmap = _screenBitmap;
		mCameraPictureSize = _cameraPictureSize;
	}

	/**
	 * Caches data from opencv memory to the event now.
	 */
	public void cacheData() {
		mCachedDistanceMetricList = getDistanceMetricList();
	}

	/**
	 * @return true if at least 1 face has been found.
	 */
	public boolean areFacesFound() {
		return getAmountOfFaces() != 0;
	}

	/**
	 * @return the amount of total found faces.
	 */
	public int getAmountOfFaces() {
		return getDistanceMetricList().size();
	}

	/**
	 * @return a list roughly estimated face-distances. each distance represents
	 *         the distance to a detected face in cm. <b>THIS IS A ROUGH
	 *         ESTIMATION AND NOT AN EXACT VALUE!</b> Specially for very near
	 *         faces (<20cm) the estimated distances might be slightly too big.
	 *         if the list is empty, not faces have been detected.
	 */
	public List<Float> getDistanceMetricList() {
		List<Float> list = new Vector<Float>();
		if (mUseCachedData) {
			if (mCachedDistanceMetricList == null) {
				throw new RuntimeException("mCachedDistanceMetricList not initialized via cacheData().");
			}
			return mCachedDistanceMetricList;
		}
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
	public Map<Feature, CvSeq> getOpenCvFaces() {
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

	/**
	 * @return {@link #cachedDistanceMetricList}.
	 */
	public List<Float> getCachedDistanceMetricList() {
		return mCachedDistanceMetricList;
	}

	/**
	 * @param _cachedDistanceMetricList
	 *            sets {@link #cachedDistanceMetricList} to
	 *            _cachedDistanceMetricList.
	 */
	public void setCachedDistanceMetricList(List<Float> _cachedDistanceMetricList) {
		mCachedDistanceMetricList = _cachedDistanceMetricList;
	}

	/**
	 * @return {@link #useCachedData}.
	 */
	public boolean isUseCachedData() {
		return mUseCachedData;
	}

	/**
	 * Before setting to true, you should call {@link #cacheData()} - otherwise
	 * you are going to work with uninitialized cached data.
	 * 
	 * @param _useCachedData
	 *            sets {@link #useCachedData} to _useCachedData.
	 */
	public void setUseCachedData(boolean _useCachedData) {
		mUseCachedData = _useCachedData;
	}

}
