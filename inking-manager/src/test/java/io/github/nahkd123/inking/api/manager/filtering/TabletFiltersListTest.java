package io.github.nahkd123.inking.api.manager.filtering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import io.github.nahkd123.inking.api.TabletDriver;
import io.github.nahkd123.inking.api.manager.filtering.host.HostHasSize;
import io.github.nahkd123.inking.api.manager.info.ImmutableTabletInfo;
import io.github.nahkd123.inking.api.tablet.ImmutablePacket;
import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.tablet.PenState;
import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.tablet.TabletInfo;
import io.github.nahkd123.inking.api.util.Emitter;
import io.github.nahkd123.inking.api.util.EmitterSource;
import io.github.nahkd123.inking.api.util.Flags;
import io.github.nahkd123.inking.api.util.MeasurementUnit;
import io.github.nahkd123.inking.api.util.Vector2;

class TabletFiltersListTest {
	@Test
	void testFilteringChain() {
		List<Tablet> tablets = new ArrayList<>();

		TabletDriver driver = new TabletDriver() {
			Emitter<Tablet> emitter = new EmitterSource<>();

			@Override
			public Emitter<Tablet> getTabletDiscoverEmitter() { return emitter; }

			@Override
			public Emitter<Tablet> getTabletDisconnectEmitter() { return emitter; }

			@Override
			public Emitter<Tablet> getTabletConnectEmitter() { return emitter; }

			@Override
			public String getDriverName() { return "Test case"; }

			@Override
			public Collection<Tablet> getConnectedTablets() { return tablets; }
		};

		// @formatter:off
		ImmutableTabletInfo info = new ImmutableTabletInfo(
			"test",
			"Fake tablet",
			1024,
			Optional.of(new Vector2(192, 108, MeasurementUnit.MILLIMETER)),
			Optional.of(new Vector2(1920, 1080)),
			1,
			0);
		// @formatter:on

		Tablet tablet = new Tablet() {
			@Override
			public boolean isConnected() { return true; }

			@Override
			public String getTabletId() { return info.tabletId(); }

			@Override
			public Emitter<Tablet> getStateChangesEmitter() { return null; }

			@Override
			public Emitter<Packet> getPacketsEmitter() { return null; }

			@Override
			public TabletInfo getInfo() { return info; }

			@Override
			public TabletDriver getDriver() { return driver; }
		};
		tablets.add(tablet);

		TabletFiltersList list = new TabletFiltersList();
		List<Packet> packetDump = new ArrayList<>();
		AtomicBoolean mark = new AtomicBoolean(false);

		list.add(new PressureMappingFilter(info));
		list.add(new PressureMappingFilter(info) {
			@Override
			public void onPacket(Packet incoming) {
				super.onPacket(incoming);
				mark.set(true);
			}
		});
		list.initialize(new HostHasSize() {
			@Override
			public Tablet getTablet() { return tablet; }

			@Override
			public Vector2 getHostSize() { return new Vector2(1920, 1080, MeasurementUnit.PIXEL); }
		}, packetDump::add);

		// @formatter:off
		list.pushPackets(new ImmutablePacket(
			new Vector2(512, 512),
			new Vector2(0, 0, MeasurementUnit.DEGREE),
			0,
			0,
			new Flags(PenState.PEN_DOWN.getFlagBit()),
			0,
			0, 
			0));
		// @formatter:on

		assertEquals(2, list.size());
		assertTrue(mark.get());
	}
}
