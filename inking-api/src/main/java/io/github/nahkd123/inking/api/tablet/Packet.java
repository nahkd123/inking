package io.github.nahkd123.inking.api.tablet;

import io.github.nahkd123.inking.api.util.ConstantVector2;
import io.github.nahkd123.inking.api.util.Vector2;

/**
 * <p>
 * The interface for interacting with packets emitted from tablet driver. To
 * keep things simple, all common graphics tablet inputs are included in this
 * one packet interface.
 * </p>
 */
public interface Packet {
	/**
	 * <p>
	 * Get the pen position that is within the tablet input rectangle. If
	 * {@link #isPenNear()} returns {@code false}, the value returned from this
	 * method is the last position.
	 * </p>
	 * <p>
	 * <b>Windows Ink</b>: The pen position is the pixel position on the window,
	 * with its origin from the top-left of the window.
	 * </p>
	 * 
	 * @return The pen position.
	 */
	public Vector2 getPenPosition();

	/**
	 * <p>
	 * Get the raw pen pressure that is clamped under the tablet's maximum pressure
	 * value. If {@link #isPenNear()} returns {@code false}, the value returned from
	 * this method will be {@code 0}.
	 * </p>
	 * 
	 * @return Raw pressure value.
	 */
	public int getRawPressure();

	/**
	 * <p>
	 * Check if the pen's nib is being pressed against the tablet surface. The
	 * returned value is always {@code false} if {@link #isPenNear()} returns
	 * {@code false}.
	 * </p>
	 * 
	 * @return Pen down state.
	 */
	public boolean isPenDown();

	/**
	 * <p>
	 * Check if the nib is the eraser. Returns {@code false} if the tablet does not
	 * support erasers or the pen is not near the tablet.
	 * </p>
	 * 
	 * @return Eraser state.
	 */
	default boolean isEraser() { return false; }

	/**
	 * <p>
	 * Get the pen hovering distance. If the tablet does not support checking
	 * hovering distance, it will returns {@code 0} if the pen is touching the
	 * surface, {@code 1} otherwise.
	 * </p>
	 * <p>
	 * This method shouldn't be used for checking if the pen is touching the
	 * surface; use {@link #isPenDown()} instead. This is because the value returned
	 * from this method might be not {@code 0}, even though the pen is, in fact,
	 * being held down.
	 * </p>
	 * <p>
	 * If {@link #isPenNear()} is {@code false}, the returned value from this method
	 * will be the last value.
	 * </p>
	 * 
	 * @return The pen hovering distance.
	 */
	default int getRawHoverDistance() { return isPenDown() ? 0 : 1; }

	/**
	 * <p>
	 * Get the pen tilt angles. If the tablet does not support tilting, it will
	 * returns {@link ConstantVector2#ZERO}. Please note that the object
	 * implementing {@link Vector2} returned from this method can be something other
	 * than {@link ConstantVector2} (it read directly from raw packet data for
	 * example).
	 * </p>
	 * 
	 * @return The tilting angles.
	 */
	default Vector2 getTilt() { return ConstantVector2.ZERO; }

	/**
	 * <p>
	 * Check if the pen or auxiliary button is being held down. The default return
	 * value is always {@code false}.
	 * </p>
	 * 
	 * @param type  The button type.
	 * @param index The button index. If the index is out of bounds, the method will
	 *              returns {@code false}.
	 * @return The button held down state.
	 */
	default boolean isButtonDown(ButtonType type, int index) {
		return false;
	}
}
