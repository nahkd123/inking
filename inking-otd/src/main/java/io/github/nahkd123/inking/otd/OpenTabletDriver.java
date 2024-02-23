package io.github.nahkd123.inking.otd;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.github.nahkd123.inking.api.TabletDriver;
import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.util.Emitter;
import io.github.nahkd123.inking.api.util.EmitterSource;
import io.github.nahkd123.inking.otd.netnative.OtdNative;
import io.github.nahkd123.inking.otd.netnative.OtdTabletSpec;
import io.github.nahkd123.inking.otd.tablet.OtdTablet;

public class OpenTabletDriver implements TabletDriver {
	private EmitterSource<Tablet> connectEmitter = new EmitterSource<>();
	private EmitterSource<Tablet> disconnectEmitter = new EmitterSource<>();
	private EmitterSource<Tablet> discoverEmitter = new EmitterSource<>();

	private Map<String, OtdTablet> knownTablets = new HashMap<>();
	private Set<String> undiscoveredSerials = new HashSet<>();
	private Set<Tablet> connected = new HashSet<>();
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
								discoverEmitter.push(tablet);
								connectEmitter.push(tablet);
							}
						}

						Thread.sleep(1);
					}
				} catch (Exception e) {
					// TODO log error using slf4j
					e.printStackTrace();
				}
			});
			driverThread.setName("Inking/OpenTabletDriver");
		} else {
			// TODO log warning that native does not exists
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
