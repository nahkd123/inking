package io.github.nahkd123.inking.api.tablet;

import io.github.nahkd123.inking.api.util.Vector2;

public interface TabletSpec {
	public String getTabletName();

	public int getMaxPressure();

	public Vector2 getPhysicalSize();

	public Vector2 getInputSize();

	public int getButtonsCount(ButtonType type);
}
