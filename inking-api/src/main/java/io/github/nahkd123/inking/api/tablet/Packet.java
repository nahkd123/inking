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
 * <p>
 * A packet is a snapshot of the tablet's state, captured at given time
 * ({@link #getTimestamp()}). Tablet's states includes: pen position (on the
 * sensing area), raw pressure, tilt and more.
 * </p>
 * 
 * @see #getPenPosition()
 * @see #getRawPressure()
 * @see #getTilt()
 * @see #isButtonDown(ButtonType, int)
 * @see #getTimestamp()
 * @see MutablePacket
 * @see ImmutablePacket
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
	 * Tablet mapper that maps pen's physical/input position to application's window
	 * must ignore the pen's position if {@link MeasurementUnit#PIXEL} is reported,
	 * or set the pointer at exact pixel in the window.
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

	/**
	 * <p>
	 * Get a set of (up to 64) flags where each flag is a button being held down
	 * (a.k.a being pressed). You can use {@link #isButtonDown(ButtonType, int)} to
	 * check individual button, or use this with a bit of bitwise operations.
	 * </p>
	 * 
	 * @param type The type of button.
	 * @return A set of flags composed in a single 64-bit integer. The least
	 *         significant bit is the first button (index 0).
	 */
	default long getButtonsDown(ButtonType type) {
		return 0L;
	}

	/**
	 * <p>
	 * Check if the button at given index is being held down since this packet is
	 * captured.
	 * </p>
	 * 
	 * @param type  The button type. {@link ButtonType#AUXILIARY} indicates the
	 *              buttons on the tablet, while {@link ButtonType#PEN} indicates
	 *              the buttons on the pen.
	 * @param index The index of the button, starting from {@code 0}. Button #1 is
	 *              index 0, button #2 is index 1 and so on.
	 * @return {@code true} if the button is being held down.
	 */
	default boolean isButtonDown(ButtonType type, int index) {
		return (getButtonsDown(type) & (1L << index)) != 0L;
	}
}
