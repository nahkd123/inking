package io.github.nahkd123.inking.otd.tablet;

import io.github.nahkd123.inking.api.TabletDriver;
import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.tablet.TabletSpec;
import io.github.nahkd123.inking.api.util.Emitter;
import io.github.nahkd123.inking.api.util.EmitterSource;
import io.github.nahkd123.inking.otd.OpenTabletDriver;

public class OtdTablet implements Tablet {
	private OpenTabletDriver driver;
	private String serial;
	private TabletSpec spec;

	public boolean connected = false;
	public EmitterSource<Tablet> stateChanges = new EmitterSource<>();
	public EmitterSource<Packet> packets = new EmitterSource<>();

	public OtdTablet(OpenTabletDriver driver, String serial, TabletSpec spec) {
		this.driver = driver;
		this.serial = serial;
		this.spec = spec;
	}

	@Override
	public TabletDriver getDriver() { return driver; }

	@Override
	public String getTabletId() { return "OpenTabletDriver:" + serial; }

	@Override
	public boolean isConnected() { return connected; }

	@Override
	public TabletSpec getSpec() { return spec; }

	@Override
	public Emitter<Tablet> getStateChangesEmitter() { return stateChanges; }

	@Override
	public Emitter<Packet> getPacketsEmitter() { return packets; }
}
