package io.github.nahkd123.inking.otd.sample;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.nahkd123.inking.api.TabletDriver;
import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.otd.OpenTabletDriver;
import io.github.nahkd123.inking.otd.netnative.OtdNative;

/**
 * <p>
 * This example list all currently connected tablets.
 * </p>
 */
public class TabletsListMain {
	public static void main(String[] args) throws Throwable {
		Path nativeLibPath = Files.createTempDirectory("inking-sample-otd");
		Linker linker = Linker.nativeLinker();
		Arena arena = Arena.ofAuto();
		TabletDriver driver = new OpenTabletDriver(OtdNative.findNative(nativeLibPath, linker, arena));

		// Wait for driver to collect all tablets
		Thread.sleep(1000);

		for (Tablet tablet : driver.getConnectedTablets()) {
			System.out.println("Tablet: " + tablet.getTabletId() + ": " + tablet.getInfo().getTabletName());
		}
	}
}
