package at.fhooe.facedetectionview.model;

/**
 * Checks if the next image should be processed.
 * 
 * @author Rainhard Findling
 * @date 15.02.2012
 * @version 1
 */
public interface ProcessImageTrigger {
	/**
	 * @return true if the next image should be processed
	 */
	public abstract boolean processNextImage();
}