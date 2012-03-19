package at.fhooe.facedetectionview.view;

import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;

import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;
import at.fhooe.facedetectionview.model.FaceDetector;
import at.fhooe.facedetectionview.model.FacesDetectedEvent;
import at.fhooe.facedetectionview.model.ImageNormalizerUtil;
import at.fhooe.facedetectionview.model.ImageUtil;
import at.fhooe.facedetectionview.model.ProcessImageTrigger;
import at.fhooe.facedetectionview.model.FaceDetector.Feature;
import at.fhooe.facedetectionview.model.ImageNormalizerUtil.Orientation;
import at.fhooe.mc.genericobserver.GenericObservable;
import at.fhooe.mc.genericobserver.GenericObserver;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * UI element that shows the marked faces.
 * 
 * @author Rainhard Findling
 * @date 04.01.2012
 * @version 1
 */
public class FaceView extends View implements Camera.PreviewCallback {
	private static final Logger						LOGGER				= LoggerFactory.getLogger(FaceView.class);

	// ================================================================================================================
	// MEMBERS

	/**
	 * factor by which {@link #grayImage} is smaller than the real camera image.
	 */
	private int										mSubsamplingFactor	= 4;
	/**
	 * this image gets used for finding faces in it. it reduces the needed
	 * processing power as it is {@link #mSubsamplingFactor} times smaller than
	 * the real camera image.
	 */
	private IplImage								grayImage;
	/**
	 * gives updates about the least recently found faces. the parameter
	 * <code>_data</code> is an instance of {@link FacesDetectedEvent}.
	 */
	private GenericObservable<FacesDetectedEvent>	mObservable			= new GenericObservable<FacesDetectedEvent>() {
																			@Override
																			public void notifyObservers(FacesDetectedEvent _arg) {
																				setChanged();
																				super.notifyObservers(_arg);
																			}
																		};

	/** the face detector we're using to find faces inside the camview */
	private FaceDetector							mFaceDetector		= null;
	/** a list of currently found faces. */
	private HashMap<Feature, CvSeq>					mFaces				= null;
	/** if true, found faces get marked on the screen with rectangles. */
	private boolean									mMarkFaces			= false;
	/** checks if the next image should already be processed. */
	private ProcessImageTrigger						mTrigger			= null;
	/** the current device orientation */
	private Orientation								mOrientation		= Orientation.Landscape;

	// ================================================================================================================
	// CONSTRUCTORS

	public FaceView(Context context, int _subsamplingFactor, Feature[] haarcascadeFeatures, ProcessImageTrigger _trigger,
			boolean _markFaces) throws IOException {
		super(context);
		mFaceDetector = new FaceDetector(context, haarcascadeFeatures);
		mSubsamplingFactor = _subsamplingFactor;
		mTrigger = _trigger;
		mMarkFaces = _markFaces;
	}

	public FaceView(Context _context, AttributeSet _attrs, int _defStyle) {
		super(_context, _attrs, _defStyle);
	}

	public FaceView(Context _context, AttributeSet _attrs) {
		super(_context, _attrs);
	}

	public FaceView(Context _context) {
		super(_context);
	}

	// ================================================================================================================
	// METHODS

	public void onPreviewFrame(final byte[] data, final Camera camera) {
		// LOGGER.debug("FaceView: got new image");
		try {
			Camera.Size size = camera.getParameters().getPreviewSize();
			processImage(data, size.width, size.height);
			camera.addCallbackBuffer(data);
		} catch (RuntimeException e) {
			// The camera has probably just been released, ignore.
		}
	}

	/**
	 * Find faces in the camera image.
	 * 
	 * @param data
	 * @param width
	 * @param height
	 */
	protected void processImage(byte[] data, int width, int height) {
		if (mTrigger.processNextImage()) {
			// create iplimage out of yuv image
			grayImage = ImageUtil.writeGrayPartOfYuvTo1ChannelIplimageHorizontal(grayImage, data, width, height,
					mSubsamplingFactor);
			// normalize image due to device orientation and cam mirroring
			grayImage = ImageNormalizerUtil.normalizeImageFromOrientation(grayImage, mOrientation, true);

			// LOGGER.debug("cam=" + width + "/" + height + ", resized=" +
			// grayImage.width() + "/" + grayImage.height() + ", view="
			// + getWidth() + "/" + getHeight());

			// // DEBUG
			// if (FaceDetectionViewComponentActivity.WILD_HACK == null) {
			// LOGGER.error("wild hack is null!");
			// return;
			// }
			// // VARIANT 1: FAST BUT WRONG
			// //
			// FaceDetectionViewComponentActivity.WILD_HACK.setBitmapViewBitmap(ImageUtil
			// //
			// .createBitmapOutOf4ChannelIplImages(ImageUtil.create4ChannelIplImageOutOf1ChannelIplImage(grayImage)));
			// // VARIANT 2: SLOW BUT CORRECT
			// FaceDetectionViewComponentActivity.WILD_HACK.setBitmapViewBitmap(ImageUtil
			// .createBitmapOutOf1ChannelIplImage(grayImage));

			// detect and remember faces to mark them in overlay
			mFaces = mFaceDetector.detectFaces(grayImage, mSubsamplingFactor);
			// LOGGER.debug("we have " +
			// mFaces.get(Feature.FRONTALFACE_ALT2).total() + " faces.");
			// inform listener
			FacesDetectedEvent e = new FacesDetectedEvent(this, mFaces, mSubsamplingFactor, null, new Point(width, height));
			// e.setScreenBitmap(ImageUtil.createBitmapOutOf1ChannelIplImage(grayImage));
			mObservable.notifyObservers(e);
			// gui update
			postInvalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		paintFaces(canvas);
	}

	/**
	 * Marks faces known from recent facedetection with rectangles.
	 * 
	 * @param canvas
	 */
	private void paintFaces(Canvas canvas) {
		if (mMarkFaces && mFaces != null) {
			Paint paint = new Paint();
			paint.setTextSize(20);
			// String s = "CameraPreview - This side up.";
			// float textWidth = paint.measureText(s);
			// canvas.drawText(s, (getWidth() - textWidth) / 2, 20, paint);
			paint.setStrokeWidth(2);
			paint.setStyle(Paint.Style.STROKE);
			// resize to correct screen size if the faceview is smaller than the
			// image the camera provides
			float scaleX = (float) getWidth() / (float) grayImage.width();
			float scaleY = (float) getHeight() / (float) grayImage.height();
			// LOGGER.debug("factor=" + scaleX + "/" + scaleY);
			for (Feature f : mFaces.keySet()) {
				if (mFaces.get(f) != null) {
					paint.setColor(featureColor(f));
					int total = mFaces.get(f).total();
					for (int i = 0; i < total; i++) {
						CvRect r = new CvRect(cvGetSeqElem(mFaces.get(f), i));
						int x = r.x(), y = r.y(), w = r.width(), h = r.height();
						LOGGER.debug("face=" + x + "/" + y + ", " + w + "/" + h);
						canvas.drawRect(x * scaleX, y * scaleY, (x + w) * scaleX, (y + h) * scaleY, paint);
					}
				}
			}
		}
	}

	/**
	 * Maps haarcascadetypes to colors for marking faces in the camera preview.
	 * 
	 * @param _f
	 * @return
	 */
	private int featureColor(Feature _f) {
		switch (_f) {
			case FRONTALFACE_ALT:
				return Color.RED;
			case FRONTALFACE_ALT2:
				return Color.GREEN;
			case FRONTALFACE_ALT_TREE:
				return Color.BLUE;
			case FRONTALFACE_DEFAULT:
				return Color.YELLOW;
			case PROFILEFACE:
				return Color.WHITE;
			default:
				throw new NullPointerException("no color defined for this feature type: " + _f);
		}
	}

	/**
	 * {@link Observable#addObserver(Observer)}.
	 */
	public void addObserver(GenericObserver<FacesDetectedEvent> _o) {
		mObservable.addObserver(_o);
	}

	/**
	 * {@link Observable#deleteObserver(Observer)}.
	 */
	public void deleteObserver(GenericObserver<FacesDetectedEvent> _o) {
		mObservable.deleteObserver(_o);
	}

	/**
	 * @return {@link #paintFaces}.
	 */
	public boolean isPaintFaces() {
		return mMarkFaces;
	}

	/**
	 * @param _paintFaces
	 *            sets {@link #paintFaces} to _paintFaces.
	 */
	public void setPaintFaces(boolean _paintFaces) {
		mMarkFaces = _paintFaces;
	}

	/**
	 * @return {@link #orientation}.
	 */
	public Orientation getOrientation() {
		return mOrientation;
	}

	/**
	 * @param _orientation
	 *            sets {@link #orientation} to _orientation.
	 */
	public void setOrientation(Orientation _orientation) {
		mOrientation = _orientation;
	}
}