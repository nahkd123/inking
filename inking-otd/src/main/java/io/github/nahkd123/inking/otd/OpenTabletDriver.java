package io.github.nahkd123.inking.otd;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
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

	private OpenTabletDriver(OtdNative nativeAccess) {
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

	private static OtdNative findNative(Linker linker, Arena arena) {
		ClassLoader clsLoader = OtdNative.class.getClassLoader();
		String os = System.getProperty("os.name");
		String osId = os.startsWith("Windows") ? "win"
			: os.toLowerCase().equals("linux") ? "linux"
			: os.startsWith("Mac OS X") ? "osx"
			: "unknown";
		String arch = System.getProperty("os.arch");
		String archId = arch.equals("amd64") ? "x64" : arch;
		String libExt = os.startsWith("Windows") ? "dll"
			: os.toLowerCase().equals("linux") ? "so"
			: os.startsWith("Mac OS X") ? "dylib"
			: "so";
		URL resUrl = clsLoader.getResource("natives/" + osId + "-" + archId + "/Inking.Otd." + libExt);
		if (resUrl == null) return null;

		try {
			Path path = Path.of(resUrl.toURI());
			return new OtdNative(linker, SymbolLookup.libraryLookup(path, arena), arena);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static OpenTabletDriver driver;

	/**
	 * <p>
	 * Get the driver. This method will handles the native libraries loading and it
	 * will do nothing if the native library doesn't exists (a.k.a the platform is
	 * not supported).
	 * </p>
	 * 
	 * @return The driver.
	 */
	public static OpenTabletDriver getDriver() {
		if (driver != null) return driver;
		driver = new OpenTabletDriver(findNative(Linker.nativeLinker(), Arena.ofAuto()));
		return driver;
	}
}
