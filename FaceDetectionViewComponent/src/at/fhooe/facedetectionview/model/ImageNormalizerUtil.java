package at.fhooe.facedetectionview.model;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Point;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Normalizes images for given rotations/orientations/mirrorings.
 * 
 * @author Rainhard Findling
 * @date 02.03.2012
 * @version 1
 */
public class ImageNormalizerUtil {
	private static final Logger	LOGGER	= LoggerFactory.getLogger(ImageNormalizerUtil.class);

	// ================================================================================================================
	// MEMBERS

	/**
	 * How the device - and therefore the pix taken by its camera - are
	 * oriented. Necessary for face detection in
	 * {@link FaceDetectionComponent#detectFaces(List, float, boolean, Point)}.
	 * 
	 * @author Rainhard Findling
	 * @date 02.03.2012
	 * @version 1
	 */
	public static enum Orientation {
		Landscape, LandscapeTopDown, Portrait, PortraitTopDown
	}

	// ================================================================================================================
	// METHODS

	/**
	 * Normalize a given iplimage from the device orientation and knowledge if
	 * the image was mirrored by the camera.
	 * 
	 * @param _i
	 * @param _o
	 * @param _imageMirrored
	 * @return
	 */
	public static IplImage normalizeImageFromOrientation(IplImage _i, Orientation _o, boolean _imageMirrored) {
		IplImage i = _i;
		if (_imageMirrored) {
			i = ImageUtil.createHorizontallyFlippedIplImage(i);
		}
		switch (_o) {
			case Landscape:
				// nothing to do
				break;
			case LandscapeTopDown:
				// TODO not supported right now
			case PortraitTopDown:
				// TODO not supported right now
				break;
			case Portrait:
				LOGGER.debug("rotating image");
				i = ImageUtil.createRotated90DegreeRightImage(i);
				break;
			default:
				throw new RuntimeException("unkown orientation");
		}
		return i;
	}
}
