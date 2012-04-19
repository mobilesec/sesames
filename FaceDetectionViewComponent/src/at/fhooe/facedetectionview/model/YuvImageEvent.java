package at.fhooe.facedetectionview.model;

import java.util.Arrays;
import java.util.EventObject;

public class YuvImageEvent extends EventObject {

	// ================================================================================================================
	// MEMBERS

	private byte[]	data;
	private int		width;
	private int		height;

	// ================================================================================================================
	// METHODS

	public YuvImageEvent(Object _source, byte[] _data, int _width, int _height) {
		super(_source);
		data = _data;
		width = _width;
		height = _height;
	}

	@Override
	public String toString() {
		return "YuvImageEvent [width=" + width + ", height=" + height + ", data=" + Arrays.toString(data) + "]";
	}

	/**
	 * @return {@link #data}.
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param _data
	 *            sets {@link #data} to _data.
	 */
	public void setData(byte[] _data) {
		data = _data;
	}

	/**
	 * @return {@link #width}.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param _width
	 *            sets {@link #width} to _width.
	 */
	public void setWidth(int _width) {
		width = _width;
	}

	/**
	 * @return {@link #height}.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param _height
	 *            sets {@link #height} to _height.
	 */
	public void setHeight(int _height) {
		height = _height;
	}

}
