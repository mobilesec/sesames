package at.sesame.fhooe.lib2.ui;

import android.graphics.Color;

public class EnergyMeterRenderer {

	// Parameters for scale
	private float minValue = 0;
	private float maxValue = 100;
	private float fullAngle = 80;

	// Parameter for unit
	private boolean drawUnit = false;
	private String unit = "W";

	// Parameters for current value
	private boolean drawCurrentValue = true;
	private float currentValueX = 0.5f;
	private float currentValueY = 0.25f;

	// Parameters for ticks & labels
	private int tickColor = Color.BLACK;
	private boolean drawTickLabelOnPath = true;
	private int minorTickSpacing = 5;
	private float minorTickLength = 20;
	private int majorTickSpacing = 25;
	private float majorTickLength = 30;
	private float tickTextSize = 16.0f;

	// Parameters for pointer
	private int pointerBaseWidth = 15;

	// Parameter for displaying relative to max radius
	private float relativePointerLength = 1.00f;
	private float relativeTickRadius = 1.10f;
	private float relativePointerBaseY = 0.34f;

	// Parameters for drawing color labels
	private boolean drawColorLabes = true;
	private float[] colorLabelRange = { 0.6f, 0.8f, 1.0f };
	private int[] colorLabels = { 0xff40c200, 0xffffae00, 0xff9e0e0e };
	private float colorLabelWidth = 10.0f;
	
	public float getMinValue() {
		return minValue;
	}
	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}
	public float getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}
	public float getFullAngle() {
		return fullAngle;
	}
	public void setFullAngle(float fullAngle) {
		this.fullAngle = fullAngle;
	}
	public boolean isDrawUnit() {
		return drawUnit;
	}
	public void setDrawUnit(boolean drawUnit) {
		this.drawUnit = drawUnit;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public boolean isDrawCurrentValue() {
		return drawCurrentValue;
	}
	public void setDrawCurrentValue(boolean drawCurrentValue) {
		this.drawCurrentValue = drawCurrentValue;
	}
	public float getCurrentValueX() {
		return currentValueX;
	}
	public void setCurrentValueX(float currentValueX) {
		this.currentValueX = currentValueX;
	}
	public float getCurrentValueY() {
		return currentValueY;
	}
	public void setCurrentValueY(float currentValueY) {
		this.currentValueY = currentValueY;
	}
	public int getTickColor() {
		return tickColor;
	}
	public void setTickColor(int tickColor) {
		this.tickColor = tickColor;
	}
	public boolean isDrawTickLabelOnPath() {
		return drawTickLabelOnPath;
	}
	public void setDrawTickLabelOnPath(boolean drawTickLabelOnPath) {
		this.drawTickLabelOnPath = drawTickLabelOnPath;
	}
	public int getMinorTickSpacing() {
		return minorTickSpacing;
	}
	public void setMinorTickSpacing(int minorTickSpacing) {
		this.minorTickSpacing = minorTickSpacing;
	}
	public float getMinorTickLength() {
		return minorTickLength;
	}
	public void setMinorTickLength(float minorTickLength) {
		this.minorTickLength = minorTickLength;
	}
	public int getMajorTickSpacing() {
		return majorTickSpacing;
	}
	public void setMajorTickSpacing(int majorTickSpacing) {
		this.majorTickSpacing = majorTickSpacing;
	}
	public float getMajorTickLength() {
		return majorTickLength;
	}
	public void setMajorTickLength(float majorTickLength) {
		this.majorTickLength = majorTickLength;
	}
	public float getTickTextSize() {
		return tickTextSize;
	}
	public void setTickTextSize(float tickTextSize) {
		this.tickTextSize = tickTextSize;
	}
	public int getPointerBaseWidth() {
		return pointerBaseWidth;
	}
	public void setPointerBaseWidth(int pointerBaseWidth) {
		this.pointerBaseWidth = pointerBaseWidth;
	}
	public float getRelativePointerLength() {
		return relativePointerLength;
	}
	public void setRelativePointerLength(float relativePointerLength) {
		this.relativePointerLength = relativePointerLength;
	}
	public float getRelativeTickRadius() {
		return relativeTickRadius;
	}
	public void setRelativeTickRadius(float relativeTickRadius) {
		this.relativeTickRadius = relativeTickRadius;
	}
	public float getRelativePointerBaseY() {
		return relativePointerBaseY;
	}
	public void setRelativePointerBaseY(float relativePointerBaseY) {
		this.relativePointerBaseY = relativePointerBaseY;
	}
	public boolean isDrawColorLabes() {
		return drawColorLabes;
	}
	public void setDrawColorLabes(boolean drawColorLabes) {
		this.drawColorLabes = drawColorLabes;
	}
	public float[] getColorLabelRange() {
		return colorLabelRange;
	}
	public void setColorLabelRange(float[] colorLabelRange) {
		this.colorLabelRange = colorLabelRange;
	}
	public int[] getColorLabels() {
		return colorLabels;
	}
	public void setColorLabels(int[] colorLabels) {
		this.colorLabels = colorLabels;
	}
	public float getColorLabelWidth() {
		return colorLabelWidth;
	}
	public void setColorLabelWidth(float colorLabelWidth) {
		this.colorLabelWidth = colorLabelWidth;
	}
}
