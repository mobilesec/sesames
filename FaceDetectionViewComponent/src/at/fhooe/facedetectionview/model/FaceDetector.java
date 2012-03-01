package at.fhooe.facedetectionview.model;

import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

/**
 * Detects faces using OpenCV haarcascades.
 * 
 * @author Rainhard Findling
 * @date 13.01.2012
 * @version 1
 */
public class FaceDetector {
	private static final Logger	LOGGER	= LoggerFactory.getLogger(FaceDetector.class);

	/**
	 * All possible opencv haarcascades.
	 * 
	 * @author Rainhard Findling
	 * @date 04.01.2012
	 * @version 1
	 */
	public static enum Feature {
		FRONTALFACE_ALT, FRONTALFACE_ALT2, FRONTALFACE_ALT_TREE, FRONTALFACE_DEFAULT, PROFILEFACE
	}

	// ================================================================================================================
	// STATIC REFERENCE TO HAARCASCADE CLASSIFIER FILES

	private static String								mClassifierPrefix	= "/at/fhooe/facedetectionview/cascades/";
	/** contains the haarcascade files for each haarcascade */
	private static HashMap<Feature, String>				mClassifierFiles	= new HashMap<Feature, String>();
	static {
		mClassifierFiles.put(Feature.FRONTALFACE_DEFAULT, mClassifierPrefix + "haarcascade_frontalface_default.xml");
		mClassifierFiles.put(Feature.FRONTALFACE_ALT, mClassifierPrefix + "haarcascade_frontalface_alt.xml");
		mClassifierFiles.put(Feature.FRONTALFACE_ALT2, mClassifierPrefix + "haarcascade_frontalface_alt2.xml");
		mClassifierFiles.put(Feature.FRONTALFACE_ALT_TREE, mClassifierPrefix + "haarcascade_frontalface_alt_tree.xml");
		mClassifierFiles.put(Feature.PROFILEFACE, mClassifierPrefix + "haarcascade_profileface.xml");
	}

	// ================================================================================================================
	// MEMBERS

	/** needed for doing facedetection */
	private HashMap<Feature, CvMemStorage>				mStorages			= new HashMap<Feature, CvMemStorage>();
	/** needed for doing facedetection */
	private HashMap<Feature, CvHaarClassifierCascade>	mClassifiers		= new HashMap<Feature, CvHaarClassifierCascade>();

	// ================================================================================================================
	// METHODS

	public FaceDetector(Context _context, Feature[] _haarcascadesToUse) throws IOException {
		// Preload the opencv_objdetect module to work around a known bug.
		Loader.load(opencv_objdetect.class);

		for (Feature f : _haarcascadesToUse) {
			File classifierFile = Loader.extractResource(getClass(), mClassifierFiles.get(f), _context.getCacheDir(),
					"classifier", ".xml");
			if (classifierFile == null || classifierFile.length() <= 0) {
				throw new IOException("Could not extract the classifier file from Java resource.");
			}
			mClassifiers.put(f, new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath())));
			classifierFile.delete();
			if (mClassifiers.get(f).isNull()) {
				throw new IOException("Could not load the classifier file.");
			}
			mStorages.put(f, CvMemStorage.create());
			LOGGER.debug("loaded classifier " + f);
		}
	}

	/**
	 * Detect faces in an given image.
	 * 
	 * @param _grayImage
	 * @param _camBitmap
	 * @param _subsamplingFactor
	 * @param _view
	 * @return
	 */
	public HashMap<FaceDetector.Feature, CvSeq> detectFaces(IplImage _grayImage, float _subsamplingFactor) {
		HashMap<FaceDetector.Feature, CvSeq> facesMap = new HashMap<FaceDetector.Feature, CvSeq>();
		// for each haarcascade...
		for (Feature feat : mClassifiers.keySet()) {
			// do facedetection
			CvSeq faces = cvHaarDetectObjects(_grayImage, mClassifiers.get(feat), mStorages.get(feat), 1.1, 3,
					CV_HAAR_DO_CANNY_PRUNING);
			facesMap.put(feat, faces);
			opencv_core.cvClearMemStorage(mStorages.get(feat));
		}
		return facesMap;
	}

	@Override
	public String toString() {
		return "FaceDetector [mClassifiers=" + mClassifiers + "]";
	}
}
