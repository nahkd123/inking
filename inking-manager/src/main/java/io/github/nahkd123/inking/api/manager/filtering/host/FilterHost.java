package io.github.nahkd123.inking.api.manager.filtering.host;

import io.github.nahkd123.inking.api.tablet.Tablet;

/**
 * <p>
 * Filter host provides information about the application that is interacting
 * with the tablet filters. By default, the host only provides the tablet that
 * created the original packet. Additional information can be obtained by
 * checking if the object is implementing interface that is a subinterface of
 * {@link FilterHost}, like {@link HostHasSize} for example.
 * </p>
 * 
 * @see HostHasSize
 */
public interface FilterHost {
	public Tablet getTablet();
}
