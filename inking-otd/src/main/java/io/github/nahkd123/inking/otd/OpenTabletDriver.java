package io.github.nahkd123.inking.otd;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nahkd123.inking.api.TabletDriver;
import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.util.Emitter;
import io.github.nahkd123.inking.api.util.EmitterSource;
import io.github.nahkd123.inking.internal.Platform;
import io.github.nahkd123.inking.internal.PlatformArch;
import io.github.nahkd123.inking.otd.netnative.OtdNative;
import io.github.nahkd123.inking.otd.netnative.OtdTabletSpec;
import io.github.nahkd123.inking.otd.tablet.OtdTablet;

public class OpenTabletDriver implements TabletDriver {
	private static final Logger LOGGER = LoggerFactory.getLogger("Inking/OpenTabletDriver");
	private EmitterSource<Tablet> connectEmitter = new EmitterSource<>();
	private EmitterSource<Tablet> disconnectEmitter = new EmitterSource<>();
	private EmitterSource<Tablet> discoverEmitter = new EmitterSource<>();

	private Map<String, OtdTablet> knownTablets = new ConcurrentHashMap<>();
	private Set<String> undiscoveredSerials = ConcurrentHashMap.newKeySet();
	private Set<Tablet> connected = ConcurrentHashMap.newKeySet();
	private OtdNative nativeAccess;
	private Thread driverThread;

	public OpenTabletDriver(OtdNative nativeAccess) {
		this.nativeAccess = nativeAccess;

		if (nativeAccess != null) {
			driverThread = Thread.ofVirtual().start(() -> {
				try {
					nativeAccess.initializeDriver((serial, connected) -> {
						OtdTablet tablet = knownTablets.get(serial);

						if (tablet != null) {
							tablet.connected = connected;
							(connected ? connectEmitter : disconnectEmitter).push(tablet);
							tablet.stateChanges.push(tablet);

							if (connected) this.connected.add(tablet);
							else this.connected.remove(tablet);
						} else if (connected && !undiscoveredSerials.contains(serial))
							undiscoveredSerials.add(serial);
					}, (serial, packet) -> {
						OtdTablet tablet = knownTablets.get(serial);
						if (tablet == null) return;
						tablet.packets.push(packet);
					});

					while (!Thread.currentThread().isInterrupted()) {
						if (undiscoveredSerials.size() > 0) {
							Iterator<String> iter = undiscoveredSerials.iterator();

							while (iter.hasNext()) {
								String serial = iter.next();
								iter.remove();
								OtdTabletSpec info = nativeAccess.getTabletInfo(serial);
								OtdTablet tablet = new OtdTablet(this, serial, info);
								knownTablets.put(serial, tablet);
								tablet.connected = true;
								connected.add(tablet);
								discoverEmitter.push(tablet);
								connectEmitter.push(tablet);
							}
						}

						Thread.sleep(1);
					}
				} catch (Exception e) {
					LOGGER.error("Error occured from driver thread: {}", e);
					LOGGER.error("OpenTabletDriver Inking driver will stops.");
					knownTablets.values().forEach(tablet -> {
						if (tablet.isConnected()) {
							tablet.connected = false;
							disconnectEmitter.push(tablet);
							tablet.stateChanges.push(tablet);
							connected.remove(tablet);
						}
					});
				}
			});
			driverThread.setName("Inking/OpenTabletDriver");
		} else {
			LOGGER.warn("Couldn't find natives for {} (architecture is {}). Inking OpenTabletDriver bridge will "
				+ "do nothing.",
				Platform.getCurrent(), PlatformArch.getCurrent());
		}
	}

	@Override
	public String getDriverName() { return "OpenTabletDriver"; }

	@Override
	public Emitter<Tablet> getTabletConnectEmitter() { return connectEmitter; }

	@Override
	public Emitter<Tablet> getTabletDisconnectEmitter() { return disconnectEmitter; }

	@Override
	public Emitter<Tablet> getTabletDiscoverEmitter() { return discoverEmitter; }

	@Override
	public Collection<Tablet> getConnectedTablets() { return Collections.unmodifiableCollection(connected); }

	public OtdNative getNativeAccess() { return nativeAccess; }

	public Thread getDriverThread() { return driverThread; }
}
