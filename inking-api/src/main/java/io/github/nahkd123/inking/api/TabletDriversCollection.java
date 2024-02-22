package io.github.nahkd123.inking.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.util.Emitter;
import io.github.nahkd123.inking.api.util.EmitterSource;

/**
 * <p>
 * A collection of tablet drivers, which can be used just like regular
 * {@link TabletDriver}. Mainly used for combining multiple drivers into a
 * single collection.
 * </p>
 */
public class TabletDriversCollection implements TabletDriver {
	private Set<TabletDriver> drivers = new HashSet<>();
	private EmitterSource<Tablet> connectEmitter = new EmitterSource<>();
	private EmitterSource<Tablet> disconnectEmitter = new EmitterSource<>();
	private EmitterSource<Tablet> discoverEmitter = new EmitterSource<>();
	private Consumer<Tablet> connectHandler;
	private Consumer<Tablet> disconnectHandler;
	private Consumer<Tablet> discoverHandler;

	public TabletDriversCollection() {
		connectHandler = connectEmitter::push;
		disconnectHandler = disconnectEmitter::push;
		discoverHandler = discoverEmitter::push;
	}

	@Override
	public String getDriverName() { return "Drivers Collection"; }

	public Set<TabletDriver> getAllDrivers() { return Collections.unmodifiableSet(drivers); }

	@Override
	public Emitter<Tablet> getTabletConnectEmitter() { return connectEmitter; }

	@Override
	public Emitter<Tablet> getTabletDisconnectEmitter() { return disconnectEmitter; }

	@Override
	public Emitter<Tablet> getTabletDiscoverEmitter() { return discoverEmitter; }

	@Override
	public Collection<Tablet> getConnectedTablets() {
		return drivers.stream().flatMap(driver -> driver.getConnectedTablets().stream()).toList();
	}

	public boolean addDriver(TabletDriver driver) {
		if (drivers.add(driver)) {
			driver.getTabletConnectEmitter().listen(connectHandler);
			driver.getTabletDisconnectEmitter().listen(disconnectHandler);
			driver.getTabletDiscoverEmitter().listen(discoverHandler);
			return true;
		}

		return false;
	}

	public boolean removeDriver(TabletDriver driver) {
		if (drivers.remove(driver)) {
			driver.getTabletConnectEmitter().stopListening(connectHandler);
			driver.getTabletDisconnectEmitter().stopListening(disconnectHandler);
			driver.getTabletDiscoverEmitter().stopListening(discoverHandler);
			return true;
		}

		return false;
	}
}
