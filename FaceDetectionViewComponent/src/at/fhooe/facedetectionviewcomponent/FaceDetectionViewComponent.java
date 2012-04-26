package at.fhooe.facedetectionviewcomponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.view.ViewGroup;
import at.fhooe.facedetectionview.model.FaceDetector;
import at.fhooe.facedetectionview.model.FaceDetector.Feature;
import at.fhooe.facedetectionview.model.FacesDetectedEvent;
import at.fhooe.facedetectionview.model.ImageNormalizerUtil.Orientation;
import at.fhooe.facedetectionview.model.ProcessImageTrigger;
import at.fhooe.facedetectionview.model.VideoRecordUtil;
import at.fhooe.facedetectionview.view.CameraPreview;
import at.fhooe.facedetectionview.view.FaceView;
import at.fhooe.mc.genericobserver.GenericObservable;
import at.fhooe.mc.genericobserver.GenericObserver;

/**
 * Shows the camera image and detects faces in it periodically.
 * 
 * @author Rainhard Findling
 * @date 17.02.2012
 * @version 1
 */
public class FaceDetectionViewComponent {
	private static final Logger							LOGGER					= LoggerFactory
																						.getLogger(FaceDetectionViewComponent.class);

	// ================================================================================================================
	// MEMBERS

	/** android hw camera */
	private Camera										mCamera;
	/** where faces get marked */
	private FaceView									mFaceview;
	/** where android automatically projects the current cam image onto */
	private CameraPreview								mPreview;
	/**
	 * facedetection observers of the facedetectioncomponent. get added to the
	 * FaceView each time {@link #resume(Context, ViewGroup)} is called.
	 */
	private List<GenericObserver<FacesDetectedEvent>>	mFaceDetectionListeners	= new ArrayList<GenericObserver<FacesDetectedEvent>>();
	// /**
	// * yuv-image observers of the facedetectioncomponent. get added to the
	// * FaceView each time {@link #resume(Context, ViewGroup)} is called.
	// */
	// private List<GenericObserver<YuvImageEvent>> mYuvImageListeners = new
	// ArrayList<GenericObserver<YuvImageEvent>>();
	/**
	 * viewgroup the view has to be removed from in {@link #onPause(ViewGroup)}.
	 */
	private ViewGroup									mViewGroup				= null;
	/**
	 * the last created face detection event. if null, no face detection has
	 * been done yet.
	 */
	private volatile FacesDetectedEvent					mLastFaceDetectedEvent	= null;

	// /**
	// * the last created yuv-image event from {@link #mFaceview}. if null, no
	// * yuv-image has been broadcastet yet.
	// */
	// private volatile YuvImageEvent mLastYuvImageEvent = null;

	// ================================================================================================================
	// METHODS

	/**
	 * Call this method in your Activity's {@link Activity#onResume()} method.
	 * Only call {@link #addFaceDetectionObserver(Observer)} after calling this
	 * method, and before calling {@link #onPause(ViewGroup)} if you want to
	 * receive updates.
	 * 
	 * @param _context
	 *            the calling activity
	 * @param _viewGroup
	 *            the view object this {@link FaceDetectionViewComponent} should
	 *            use to show the camera images + the detected faces.
	 * @param _subsamplingFactor
	 *            how much the camera image should be made smaller before
	 *            processing it (a higher number increases the speed but lowers
	 *            the quality).
	 * @param _haarcascadeFeatures
	 *            an array of all haarcascadeFeatures that should be used. The
	 *            more are stated here, the slower the complete face detection
	 *            gets, as more face detections are done serially. For
	 *            detections from the front
	 *            <code>Feature.FRONTALFACE_ALT2</code> has proved to work well.
	 *            For detections from profile <code>Feature.PROFILEFACE</code>
	 *            is the only choice.
	 * @param _trigger
	 *            gets used to determine when to process the next camera image,
	 *            provided by Android, to search for faces. Within the
	 *            {@link ProcessImageTrigger#processNextImage()} method you can
	 *            specify when exactly to do that (e.g. you can implement a
	 *            power-saving-function there).
	 * @param _markFaces
	 *            if true, found faces get marked with rectangles.
	 * @return true if the face detection component could be initialized
	 *         correclty, false otherwise.
	 */
	public boolean resume(Context _context, ViewGroup _viewGroup, int _subsamplingFactor, Feature[] _haarcascadeFeatures,
			ProcessImageTrigger _trigger, boolean _doFaceDetection, boolean _markFaces) {
		// remember viewgroup
		mViewGroup = _viewGroup;

		// Create an instance of Camera.
		// mCamera =
		// CVideoRecordUtil.getCameraInstance(CameraInfo.CAMERA_FACING_BACK);
		mCamera = VideoRecordUtil.getCameraInstance(CameraInfo.CAMERA_FACING_FRONT);
		if (mCamera == null) {
			LOGGER.error("FaceDetectionViewComponent: unable to obtain camera, aborting");
			return false;
		}
		// set camera orientation to 90°
		// mCamera.setDisplayOrientation(90);
		VideoRecordUtil.setCameraDisplayOrientation((Activity) _context, CameraInfo.CAMERA_FACING_FRONT, mCamera);

		// Create preview view and set it as the content of our activity.
		try {
			mFaceview = new FaceView(_context, _subsamplingFactor, _haarcascadeFeatures, _trigger, _doFaceDetection, _markFaces);
		} catch (IOException e) {
			LOGGER.error("cannot work without a faceview, terminating.");
			throw new RuntimeException("cannot work without a faceview, terminating.");
		}
		mPreview = new CameraPreview(_context, mCamera, mFaceview);

		// add view-components to viewgroup
		_viewGroup.addView(mPreview);
		_viewGroup.addView(mFaceview);

		// add listeners
		for (GenericObserver<FacesDetectedEvent> o : mFaceDetectionListeners) {
			LOGGER.error("adding observer to faceview...");
			mFaceview.addFaceDetectionObserver(o);
		}
		mFaceview.addFaceDetectionObserver(new GenericObserver<FacesDetectedEvent>() {
			@Override
			public void update(GenericObservable<FacesDetectedEvent> _o, FacesDetectedEvent _arg) {
				// cache the facedetectedevent
				// LOGGER.debug("caching event, #faces=" +
				// _arg.getAmountOfNearFaces() + ", faces=" + _arg);
				mLastFaceDetectedEvent = _arg;
			}
		});
		// for (GenericObserver<YuvImageEvent> o : mYuvImageListeners) {
		// LOGGER.error("adding observer to faceview...");
		// mFaceview.addYuvImageObserver(o);
		// }
		// mFaceview.addYuvImageObserver(new GenericObserver<YuvImageEvent>() {
		// @Override
		// public void update(GenericObservable<YuvImageEvent> _o, YuvImageEvent
		// _arg) {
		// // can't be more than once directly in this class: would cause
		// // method signature duplication as java doesn't differentiate
		// // between methods with different template types.
		// mLastYuvImageEvent = _arg;
		// }
		// });
		return true;
	}

	/**
	 * See
	 * {@link #resume(Context, ViewGroup, int, Feature[], ProcessImageTrigger, boolean)}
	 * 
	 * 
	 * @param _context
	 * @param _viewGroup
	 * @param _markFaces
	 */
	public boolean resume(Context _context, ViewGroup _viewGroup, boolean _markFacesInUi) {
		return resume(_context, _viewGroup, 1, new FaceDetector.Feature[] { Feature.FRONTALFACE_ALT2 },
				new ProcessImageTrigger() {
					@Override
					public boolean processNextImage() {
						return true;
					}
				}, true, _markFacesInUi);
	}

	/**
	 * See
	 * {@link #resume(Context, ViewGroup, int, Feature[], ProcessImageTrigger, boolean)}
	 * 
	 * 
	 * @param _context
	 * @param _viewGroup
	 * @param _markFaces
	 * @param _trigger
	 */
	public void resume(Context _context, ViewGroup _viewGroup, int _subSamplingFactor, boolean _doFaceDetection,
			boolean _markFaces, Feature... _features) {
		resume(_context, _viewGroup, _subSamplingFactor, _features, new ProcessImageTrigger() {
			@Override
			public boolean processNextImage() {
				return true;
			}
		}, _doFaceDetection, _markFaces);
	}

	/**
	 * Call this in your Activitie's {@link Activity#onPause()} method. All
	 * listeners to the internal {@link FaceView} get forgotten - so you have to
	 * add them again after calling
	 * {@link #resume(Context, ViewGroup, int, Feature[], ProcessImageTrigger, boolean)}
	 * if you want to receive updates again.
	 * 
	 * @param _viewGroup
	 *            the view object this {@link FaceDetectionViewComponent} uses
	 *            to show the camera images + the detected faces.
	 */
	public void pause() {
		VideoRecordUtil.releaseCamera(mCamera);
		mCamera = null;

		// remove items from view
		if (mViewGroup != null) {
			mViewGroup.removeView(mPreview);
			mViewGroup.removeView(mFaceview);
		} else {
			LOGGER.error("mViewGroup=null");
		}
	}

	/**
	 * {@link Observable#addObserver(Observer)}.
	 */
	public void addFaceDetectionObserver(GenericObserver<FacesDetectedEvent> _o) {
		mFaceDetectionListeners.add(_o);
	}

	/**
	 * {@link Observable#deleteObserver(Observer)}.
	 */
	public void deleteFaceDetectionObserver(GenericObserver<FacesDetectedEvent> _o) {
		mFaceDetectionListeners.remove(_o);
	}

	public void setOrientation(Orientation _orientation) {
		mFaceview.setOrientation(_orientation);
	}

	/**
	 * See {@link #mLastFaceDetectedEvent}.
	 * 
	 * @return the current (=last cached) {@link FacesDetectedEvent} event. This
	 *         event was saved the last time a face detection was finished. if
	 *         the returned value is null, no face detection has been finished
	 *         yet.
	 */
	public FacesDetectedEvent getLastFaceDetectedEvent() {
		return mLastFaceDetectedEvent;
	}
}
