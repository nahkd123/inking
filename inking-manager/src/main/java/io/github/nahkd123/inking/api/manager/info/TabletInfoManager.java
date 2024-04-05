package io.github.nahkd123.inking.api.manager.info;

import java.util.Map;

import io.github.nahkd123.inking.api.TabletDriver;
import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.tablet.TabletInfo;

public interface TabletInfoManager {
	/**
	 * <p>
	 * Save the information of the tablet if such information is currently absent in
	 * this manager. This method should be called right after
	 * {@link TabletDriver#getTabletDiscoverEmitter()} is emitted.
	 * </p>
	 * 
	 * @param tablet The tablet to save.
	 */
	public void saveIfAbsent(Tablet tablet);

	/**
	 * <p>
	 * Get the tablet info (either saved or currently active from {@link Tablet})
	 * from tablet ID.
	 * </p>
	 * 
	 * @param tabletId The ID of the tablet.
	 * @return The tablet info, or {@code null} if info for provided ID is not saved
	 *         in this manager (which is not likely).
	 */
	public TabletInfo get(String tabletId);

	/**
	 * <p>
	 * Get all tablet info. The returned map is not modifiable.
	 * </p>
	 * 
	 * @return Mapping of tablet ID to {@link TabletInfo}.
	 */
	public Map<String, TabletInfo> getAll();
}
