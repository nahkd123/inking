package io.github.nahkd123.inking.otd.sample;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.nahkd123.inking.api.TabletDriver;
import io.github.nahkd123.inking.api.tablet.TabletInfo;
import io.github.nahkd123.inking.otd.OpenTabletDriver;
import io.github.nahkd123.inking.otd.netnative.OtdNative;

/**
 * <p>
 * This example just simply log the packet from the device.
 * </p>
 */
public class TabletPacketMain {
	public static void main(String[] args) throws Throwable {
		Path nativeLibPath = Files.createTempDirectory("inking-sample-otd");
		Linker linker = Linker.nativeLinker();
		Arena arena = Arena.ofAuto();
		TabletDriver driver = new OpenTabletDriver(OtdNative.findNative(nativeLibPath, linker, arena));

		driver.getTabletDiscoverEmitter().listen(tablet -> {
			TabletInfo spec = tablet.getInfo();
			System.out.println("New tablet discovered: " + tablet.getTabletId());
			System.out.println("  Device name:   " + spec.getTabletName());
			System.out.println("  Physical size: " + spec.getPhysicalSize().get());
			System.out.println("  Input size:    " + spec.getInputSize().get());

			tablet.getPacketsEmitter().listen(packet -> {
				System.out.println(spec.getTabletName()
					+ ": Input " + packet.getPenPosition()
					+ " Pressure " + (packet.getRawPressure() * 100 / spec.getMaxPressure()) + "%"
					+ " Tilt " + packet.getTilt()
					+ " Hovering " + packet.getRawHoverDistance());
			});
		});

		System.out.println("Ready!");

		// Keep main thread alive
		while (true) Thread.sleep(1000);
	}
}
