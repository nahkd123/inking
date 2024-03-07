package io.github.nahkd123.inking.api.tablet;

import io.github.nahkd123.inking.api.util.Flag;

public enum PenState implements Flag {
	/**
	 * <p>
	 * Whether the pen nib/eraser is touching the tablet surface.
	 * </p>
	 * <p>
	 * Some tablets may reports the pen as being held down even though the pen is
	 * hovering on the mid-air. This usually happens when user tries to hold the
	 * nib/eraser down on purpose while the pen is near the tablet surface.
	 * </p>
	 */
	PEN_DOWN(1),
	/**
	 * <p>
	 * Whether the eraser of the pen is near or touching the tablet surface.
	 * </p>
	 */
	ERASER(2);

	private long flagBit;

	private PenState(long flagBit) {
		this.flagBit = flagBit;
	}

	@Override
	public long getFlagBit() { return flagBit; }

}
