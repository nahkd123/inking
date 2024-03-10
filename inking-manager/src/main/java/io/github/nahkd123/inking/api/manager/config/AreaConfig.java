package io.github.nahkd123.inking.api.manager.config;

import java.util.Optional;

import io.github.nahkd123.inking.api.manager.utils.Rectangle;
import io.github.nahkd123.inking.api.manager.utils.XYConsumer;
import io.github.nahkd123.inking.api.tablet.TabletInfo;
import io.github.nahkd123.inking.api.util.MeasurementUnit;

/**
 * <p>
 * Area mapping configuration. If {@link TabletInfo#getInputSize()} is empty
 * ({@link Optional#empty()}), the tablet can't use area configuration, because
 * the input rectangle is unknown.
 * </p>
 */
public class AreaConfig {
	private Rectangle mappedArea;
	private boolean letterboxing;

	public AreaConfig(Rectangle mappedArea, boolean letterboxing) {
		if (mappedArea.unit() != MeasurementUnit.UNITLESS)
			throw new IllegalArgumentException("The mapped area must use unitless measurement unit");
		this.mappedArea = mappedArea;
		this.letterboxing = letterboxing;
	}

	/**
	 * <p>
	 * Get the mapped are on the tablet's input rectangle whose size is defined in
	 * {@link TabletInfo#getInputSize()}. The measurement unit is always
	 * {@link MeasurementUnit#UNITLESS}.
	 * </p>
	 * 
	 * @return The mapped area.
	 */
	public Rectangle getMappedArea() { return mappedArea; }

	/**
	 * <p>
	 * Getter for {@link #getMappedArea()}.
	 * </p>
	 * 
	 * @param mappedArea The area to set. The measurement unit must be
	 *                   {@link MeasurementUnit#UNITLESS}.
	 */
	public void setMappedArea(Rectangle mappedArea) {
		if (mappedArea.unit() != MeasurementUnit.UNITLESS)
			throw new IllegalArgumentException("The mapped area must use unitless measurement unit");
		this.mappedArea = mappedArea;
	}

	/**
	 * <p>
	 * Check mapping are letterboxing mode. Letterboxing mode fits the output
	 * rectangle inside mapped input area without distortion, similar to "Force
	 * proportion" mode on official Wacom drivers.
	 * </p>
	 * 
	 * @return Letterboxing mode.
	 */
	public boolean isLetterboxing() { return letterboxing; }

	public void setLetterboxing(boolean letterboxing) { this.letterboxing = letterboxing; }

	/**
	 * <p>
	 * Map input XY to location in defined output rectangle.
	 * </p>
	 * 
	 * @param inputX       X input coordinate on 2D plane.
	 * @param inputY       Y input coordinate on 2D plane.
	 * @param outputWidth  The width of output rectangle.
	 * @param outputHeight The height of output rectangle.
	 * @param setter       A 2D coordinates consumer.
	 */
	public void map(double inputX, double inputY, double outputWidth, double outputHeight, XYConsumer setter) {
		double tabletX = mappedArea.x();
		double tabletY = mappedArea.y();
		double tabletWidth = mappedArea.width();
		double tabletHeight = mappedArea.height();

		if (letterboxing) {
			double areaRatio = mappedArea.width() / mappedArea.height();
			double screenRatio = outputWidth / outputHeight;

			if (areaRatio > screenRatio) {
				tabletWidth = tabletHeight / screenRatio;
				tabletX += (mappedArea.width() - tabletWidth) / 2;
			} else {
				tabletHeight = tabletWidth / screenRatio;
				tabletY += (mappedArea.height() - tabletHeight) / 2;
			}
		}

		double outputX = (inputX - tabletX) * outputWidth / tabletWidth;
		double screenY = (inputY - tabletY) * outputHeight / tabletHeight;
		setter.accept(outputX, screenY);
	}
}
