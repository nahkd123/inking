package io.github.nahkd123.inking.otd.sample;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class TabletPollingRateMain {
	public static void main(String[] args) throws Throwable {
		Path nativeLibPath = Files.createTempDirectory("inking-sample-otd");
		Linker linker = Linker.nativeLinker();
		Arena arena = Arena.ofAuto();
		TabletDriver driver = new OpenTabletDriver(OtdNative.findNative(nativeLibPath, linker, arena));
		Map<String, Long> counter = new HashMap<>();
		Map<String, List<Long>> history = new HashMap<>();

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

		// There is no way a normal person can have 1kHz tablet
		// Or you could be a certified osu! player.
		DecimalFormat formatter = new DecimalFormat("#,##0.##");
		System.out.println("Ready!");

		while (true) {
			Thread.sleep(1000);

			for (String key : counter.keySet()) {
				long packetsPer = counter.get(key);
				counter.put(key, 0L);

				List<Long> h = history.get(key);
				if (h == null) history.put(key, h = new ArrayList<>());
				h.add(packetsPer);
				if (h.size() > 10) h.remove(0);

				// Average the samples
				double avg = h.stream().mapToLong(l -> l).collect(
					DoubleHolder::new,
					(d, value) -> d.value += value,
					(a, b) -> a.value += b.value).value / h.size();
				System.out.println(key + ": Polling rate is " + formatter.format(avg));
			}
		}
	}

	private static class DoubleHolder {
		public double value = 0;
	}
}
