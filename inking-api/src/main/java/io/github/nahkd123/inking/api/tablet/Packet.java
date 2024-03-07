package io.github.nahkd123.inking.api.tablet;

import io.github.nahkd123.inking.api.util.Flag;
import io.github.nahkd123.inking.api.util.Flags;
import io.github.nahkd123.inking.api.util.MeasurementUnit;
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
	 * Get the pen's nib/eraser position in the tablet area.
	 * </p>
	 * <p>
	 * If the unit is {@link MeasurementUnit#UNITLESS}, the pen's position is
	 * located inside the tablet's input area, which is reported from
	 * {@link TabletInfo#getInputSize()}, which also means the driver must be able
	 * to read the absolute position of the pen.
	 * </p>
	 * <p>
	 * If the unit have {@link MeasurementUnit#getBaseUnit()} of
	 * {@link MeasurementUnit#METER}, the pen's position is located inside the
	 * tablet's physical area, which is reported from
	 * {@link TabletInfo#getPhysicalSize()}.
	 * </p>
	 * <p>
	 * If the unit is {@link MeasurementUnit#PIXEL}, the pen's position is relative
	 * to the current window (depending on how the tablet object was created).
	 * </p>
	 * 
	 * @return The pen position.
	 */
	public Vector2 getPenPosition();

	/**
	 * <p>
	 * Get the pen's raw pressure reported from tablet. The maximum value is defined
	 * in {@link TabletInfo#getMaxPressure()}.
	 * </p>
	 * 
	 * @return The current raw pressure of the pen reported from this packet.
	 */
	public int getRawPressure();

	/**
	 * <p>
	 * Get pen states as bit flags. Use {@link Flags#is(Flag)} with {@link PenState}
	 * to check the pen state from this packet.
	 * </p>
	 * 
	 * @return The pen states.
	 */
	public Flags getPenStates();

	default boolean isPenDown() { return getPenStates().is(PenState.PEN_DOWN); }

	default boolean isEraser() { return getPenStates().is(PenState.ERASER); }

	/**
	 * <p>
	 * Get the timestamp reported from this packet. Can be used for stroke
	 * smoothing.
	 * </p>
	 * 
	 * @return The timestamp of this packet.
	 */
	public long getTimestamp();

	/**
	 * <p>
	 * Get the raw hovering distance from this packet. Some tablets may not report
	 * hovering distance. As such, Inking will tries to emulate the hovering
	 * distance by returning 0 if {@link #isPenDown()} is true and 1 otherwise.
	 * </p>
	 * 
	 * @return The raw hovering distance.
	 */
	default int getRawHoverDistance() { return isPenDown() ? 0 : 1; }

	/**
	 * <p>
	 * Get the tilting angles of the pen from this packet. The measurement unit can
	 * be either {@link MeasurementUnit#DEGREE} or {@link MeasurementUnit#RADIAN}.
	 * </p>
	 * 
	 * @return The tilting angle.
	 */
	default Vector2 getTilt() { return Vector2.ANGLE_ZERO; }

	default boolean isButtonDown(ButtonType type, int index) {
		return false;
	}
}
