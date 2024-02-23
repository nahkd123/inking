package io.github.nahkd123.inking.otd.sample;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import io.github.nahkd123.inking.api.TabletDriver;
import io.github.nahkd123.inking.api.tablet.TabletSpec;
import io.github.nahkd123.inking.api.util.Vector2;
import io.github.nahkd123.inking.otd.OpenTabletDriver;
import io.github.nahkd123.inking.otd.netnative.OtdNative;

/**
 * <p>
 * This example attempts to detect the tablet's packets per second (as known as
 * "pooling rate"). The packet emitted from the driver is the direct input from
 * the device.
 * </p>
 */
public class TabletPoolingRateMain {
	public static void main(String[] args) throws Throwable {
		Path nativeLibPath = Files.createTempDirectory("inking-sample-otd");
		Linker linker = Linker.nativeLinker();
		Arena arena = Arena.ofAuto();
		TabletDriver driver = new OpenTabletDriver(OtdNative.findNative(nativeLibPath, linker, arena));
		Map<String, Long> counter = new HashMap<>();

		driver.getTabletDiscoverEmitter().listen(tablet -> {
			TabletSpec spec = tablet.getSpec();
			Vector2 physicalSize = spec.getPhysicalSize();

			System.out.println("New tablet discovered: " + tablet.getTabletId());
			System.out.println("  Name:          " + spec.getTabletName());
			System.out.println("  Physical size: " + physicalSize.x() + "mm x " + physicalSize.y() + "mm");

			tablet.getPacketsEmitter().listen(packet -> {
				counter.put(tablet.getTabletId(), counter.getOrDefault(tablet.getTabletId(), 0L) + 1L);
			});
		});

		System.out.println("Ready!");

		while (true) {
			Thread.sleep(5000);

			for (String key : counter.keySet()) {
				long packetsPer5Sec = counter.get(key);
				counter.put(key, 0L);
				System.out.println(key + ": Pooling rate: " + (packetsPer5Sec / 5d) + "Hz");
			}
		}
	}
}
